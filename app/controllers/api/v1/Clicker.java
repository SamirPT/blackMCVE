package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.*;
import models.*;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import to.clicker.Alert;
import to.clicker.ClickerState;
import to.ErrorMessage;
import to.ResponseMessage;
import to.clicker.ClickerUpdate;
import util.ClickerHelper;
import util.EventInstanceHelper;
import util.EventsHelper;
import util.exception.IllegalReservationDateException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Created by arkady on 02/04/16.
 */
@Api(value = "/api/v1/clicker", description = "Clicker", tags = "clicker", basePath = "/api/v1/clicker")
public class Clicker extends UserProfileController {

	@ApiOperation(
			nickname = "sendAlert",
			value = "Send alert",
			notes = "Send alert",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "alert", value = "Alert", required = true, dataType = "to.clicker.Alert", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result sendAlert() {
		JsonNode alertJson = request().body().asJson();
		Alert alert;
		try {
			alert = Json.fromJson(alertJson, Alert.class);
		} catch (Exception e) {
			Logger.warn("Bad request from clicker: " + alertJson.asText());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		List<UserVenue> userVenue;
		try {
			userVenue = Ebean.find(UserVenue.class).where().eq("venue.id", alert.getVenueId()).findList();
		} catch (Exception e) {
			Logger.warn("Can't fetch UserVenue list for alert: " + alertJson.textValue(), e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (userVenue == null) {
			Logger.warn("No staff found for venue id=" + alert.getVenueId());
			return internalServerError(Json.toJson(new ErrorMessage("No receivers in this venue")));
		}

		//TODO implement SMS sending. Ex.: "Police flag {venue}"

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "updateStats",
			value = "Update stats",
			notes = "Update stats",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue with given id | No events for this date", response = to.ErrorMessage.class),
					@ApiResponse(code = 406, message = "No events started right now. Try at &lt;time&gt;", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "clickerUpdate", value = "Clicker Update", required = true, dataType = "to.clicker.ClickerUpdate", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result updateCurrentState(@ApiParam(required = true) Long venueId, @ApiParam(required = true) Long eventId) {
		Venue venue = Venue.finder.byId(venueId);

		if (venue == null) {
			Logger.warn("Bad request of clicker stats. VenueId=" + venueId);
			return notFound(Json.toJson(new ErrorMessage("No venue with given id")));
		}

		LocalDate eventDate = ClickerHelper.getCurrentWorkingDay(venue);

		final Event event;
		try {
			event = EventsHelper.isEventAtDate(venueId, eventDate, eventId);
			if (event == null) {
				return notFound(Json.toJson(new ErrorMessage("This Event not available at this date")));
			}
		} catch (Exception e) {
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		final ZoneId zoneId = ZoneId.of(venue.getTimeZone());
		LocalTime localTime = LocalTime.now(zoneId);
		if (localTime.isBefore(event.getStartsAt().minusHours(1L))) {
			return status(406, Json.toJson(new ErrorMessage("This Event closed yet. Try at " + event.getStartsAt().toString())));
		}

		JsonNode clickerUpdateJson = request().body().asJson();
		ClickerUpdate clickerUpdate;
		try {
			clickerUpdate = Json.fromJson(clickerUpdateJson, ClickerUpdate.class);
		} catch (Exception e) {
			Logger.warn("Bad request from clicker: " + clickerUpdateJson.asText());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		EventInstance eventInstance;
		try {
			eventInstance = EventInstanceHelper.getOrCreateEventInstance(venueId, eventDate, eventId);
			eventInstance = ClickerHelper.putUpdateEventInstance(eventInstance, clickerUpdate);
		} catch (IllegalReservationDateException e) {
			return notFound(Json.toJson(new ErrorMessage("No events for this date")));
		} catch (Exception e) {
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		try {
			final short men = eventInstance.getMen().shortValue();
			final short woman = eventInstance.getWomen().shortValue();
			ClickerHelper.updateClickerPoint(localTime, eventDate, venueId, men, woman);
		} catch (Exception e) {
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getCurrentClickerStats",
			value = "Get current stats",
			notes = "Get current stats",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Clicker state", response = ClickerState.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue with given id | No events for this date", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getCurrentState(@ApiParam(required = true) Long venueId, @ApiParam(required = true) Long eventId) {
		Venue venue = Venue.finder.byId(venueId);

		if (venue == null) {
			Logger.warn("Bad request of clicker stats. VenueId=" + venueId);
			return notFound(Json.toJson(new ErrorMessage("No venue with given id")));
		}

		LocalDate eventDate = ClickerHelper.getCurrentWorkingDay(venue);

		DateVenuePk dateVenuePk = new DateVenuePk(venueId, eventDate, eventId);
		EventInstance eventInstance = EventInstance.finder.byId(dateVenuePk);
		ClickerState clickerState = new ClickerState();
		if (eventInstance == null) {
			Event eventAtDate;
			try {
				eventAtDate = EventsHelper.isEventAtDate(venueId, eventDate, eventId);
			} catch (Exception e) {
				Logger.warn("Can't check Event at date", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}
			if (eventAtDate == null) {
				return notFound(Json.toJson(new ErrorMessage("This Event unavailable at this date")));
			}
			clickerState.setMen(0);
			clickerState.setWomen(0);
			clickerState.setTotalIn(0);
			clickerState.setTotalOut(0);
		} else {
			clickerState.setMen(eventInstance.getMen());
			clickerState.setWomen(eventInstance.getWomen());
			clickerState.setTotalIn(eventInstance.getTotalIn());
			clickerState.setTotalOut(eventInstance.getTotalOut());
		}
		clickerState.setCapacity(venue.getCapacity());

		return ok(Json.toJson(clickerState));
	}
}