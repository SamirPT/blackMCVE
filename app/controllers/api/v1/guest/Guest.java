package controllers.api.v1.guest;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import io.swagger.annotations.*;
import models.EventInstance;
import models.Reservation;
import models.User;
import models.Venue;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.ErrorMessage;
import to.ResponseMessage;
import to.UserSettings;
import to.VisitorInfo;
import to.guest.GuestReservationInfo;
import to.guest.GuestReservationTicket;
import to.guest.GuestReservations;
import util.GuestAppHelper;
import util.ReservationsHelper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Created by arkady on 16/06/16.
 */
@Api(value = "/api/v1/guest", description = "Guest app endpoints", tags = "guest", basePath = "/api/v1/guest")
public class Guest extends UserProfileController<MrBlackUserProfile> {

	private ReservationsHelper reservationsHelper;

	@Inject
	public Guest(ReservationsHelper reservationsHelper) {
		this.reservationsHelper = reservationsHelper;
	}

	@ApiOperation(
			nickname = "getGuestReservationTicket",
			value = "Get reservation ticket",
			notes = "Get reservation ticket for current Guest",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Guest reservation ticket", response = GuestReservationTicket.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 403, message = "You can view only your own reservations", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getReservationTicket(@ApiParam(required = true) Long id) {
		GuestReservationTicket ticket;
		try {
			ticket = reservationsHelper.getGuestReservationTicket(id);
		} catch (Exception e) {
			Logger.warn("Can't get guests's reservations", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (ticket == null) {
			return notFound(Json.toJson(new ErrorMessage("No reservations with given id")));
		}

		if (!getUserProfile().getUser().getId().equals(ticket.getGuestId())) {
			return forbidden(Json.toJson(new ErrorMessage("You can view only yours reservations")));
		}

		return ok(Json.toJson(ticket));
	}

	@ApiOperation(
			nickname = "getGuestReservations",
			value = "Get current Guest reservations",
			notes = "Get all reservations of current Guest",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Guest reservations", response = GuestReservations.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getReservations() {
		User user = getUserProfile().getUser();
		List<Reservation> reservations;
		try {
			reservations = Reservation.find.fetch("eventInstance").fetch("eventInstance.venue").where().eq("guest_id", user.getId()).findList();
		} catch (Exception e) {
			Logger.warn("Can't get guests's reservations", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		GuestReservations guestReservations = new GuestReservations();
		if (reservations != null) {
			reservations.forEach(reservation -> {
				final EventInstance eventInstance = reservation.getEventInstance();
				final Venue venue = eventInstance.getVenue();
				GuestReservationInfo reservationInfo = new GuestReservationInfo(reservationsHelper.reservationToInfo(reservation, false, false));
				reservationInfo.setVenueName(venue.getName());
				reservationInfo.setEventName(eventInstance.getEvent().getName());
				reservationInfo.setEventStartsAt(eventInstance.getEvent().getStartsAt().toString());
				reservationInfo.setEventImage(eventInstance.getEvent().getPictureUrl());
				reservationInfo.setGuestsNumber(reservation.getGuestsNumber());
				if (LocalDate.now(ZoneId.of(venue.getTimeZone())).isAfter(eventInstance.getDate())) {
					guestReservations.getPast().add(reservationInfo);
				} else {
					guestReservations.getComingUp().add(reservationInfo);
				}
			});
		}

		return ok(Json.toJson(guestReservations));
	}

	@ApiOperation(
			nickname = "setGuestSettings",
			value = "Set current Guest settings",
			notes = "Set current Guest settings",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Guest Settings", response = to.UserSettings.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "settings", value = "Guest's settings", required = true, dataType = "to.UserSettings", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result setSettings() {
		final User user = getUserProfile().getUser();

		JsonNode userSettingsJson = request().body().asJson();
		UserSettings userSettings;
		try {
			userSettings = Json.fromJson(userSettingsJson, UserSettings.class);
		} catch (Exception e) {
			Logger.warn("Bad UserSettings in request : " + userSettingsJson.asText());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		if (userSettings.getSmsMessages() != null) user.setGuestSmsMessages(userSettings.getSmsMessages());
		if (userSettings.getCheckInOnMyBehalf() != null) user.setGuestCheckInOnMyBehalf(userSettings.getCheckInOnMyBehalf());
		if (userSettings.getInAppNotification() != null) user.setGuestInAppNotification(userSettings.getInAppNotification());
		if (userSettings.getPostOnMyBehalf() != null) user.setGuestPostOnMyBehalf(userSettings.getPostOnMyBehalf());
		if (userSettings.getRsvpOnMyBehalf() != null) user.setGuestRsvpOnMyBehalf(userSettings.getRsvpOnMyBehalf());

		try {
			user.update();
		} catch (Exception e) {
			Logger.warn("Can't update user's settings", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getGuestSettings",
			value = "Get current Guest settings",
			notes = "Get current Guest settings",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Guest Settings", response = to.UserSettings.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getSettings() {
		final User user = getUserProfile().getUser();

		UserSettings userSettings = new UserSettings();
		userSettings.setCheckInOnMyBehalf(user.getGuestCheckInOnMyBehalf());
		userSettings.setInAppNotification(user.getGuestInAppNotification());
		userSettings.setPostOnMyBehalf(user.getGuestPostOnMyBehalf());
		userSettings.setRsvpOnMyBehalf(user.getGuestRsvpOnMyBehalf());
		userSettings.setSmsMessages(user.getGuestSmsMessages());

		return ok(Json.toJson(userSettings));
	}

	@ApiOperation(
			nickname = "getGuestProfile",
			value = "Get current Guest profile",
			notes = "Get current User profile",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "User profile", response = to.UserInfo.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getProfile() {
		User user = getUserProfile().getUser();
		Short rating;
		try {
			rating = GuestAppHelper.getOverallGuestRating(user.getId());
		} catch (Exception e) {
			Logger.warn("Can't fetch user's settings", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		VisitorInfo visitorInfo = new VisitorInfo(user);
		visitorInfo.setRating(rating);

		return ok(Json.toJson(visitorInfo));
	}
}
