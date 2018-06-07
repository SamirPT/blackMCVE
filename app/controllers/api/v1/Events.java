package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.*;
import models.*;
import modules.s3.S3;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.*;
import util.ClickerHelper;
import util.EventsHelper;
import util.RolesHelper;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by arkady on 29/03/16.
 */
@Api(value = "/api/v1/events", description = "Events management", tags = "events", basePath = "/api/v1/events")
public class Events extends UserProfileController<MrBlackUserProfile> {

	private S3 s3;

	@Inject
	public Events(S3 s3) {
		this.s3 = s3;
	}

	@ApiOperation(
			nickname = "getUpcomingEventReservationsNumber",
			value = "Get number of upcoming reservations for Event",
			notes = "Get number of upcoming reservations for Event",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Upcoming reservations number", response = Integer.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No Event with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getUpcomingEventReservationsNumber(@ApiParam(required = true) Long id) {
		final Event event = Event.find.byId(id);

		if (event == null) {
			return notFound(Json.toJson(new ErrorMessage("No Event with given id")));
		}

		final Venue venue = Venue.finder.byId(event.getVenueId());
		final LocalDate currentWorkingDay = ClickerHelper.getCurrentWorkingDay(venue);

		final int reservationsCount;
		try {
			reservationsCount = Reservation.find
					.where()
					.eq("eventInstance.dateVenuePk.venueId", event.getVenueId())
					.eq("eventInstance.dateVenuePk.eventId", event.getId())
					.ge("eventInstance.dateVenuePk.date", currentWorkingDay)
					.findRowCount();
		} catch (Exception e) {
			Logger.warn("Can't count reservations number of Event", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage(String.valueOf(reservationsCount))));
	}

	@ApiOperation(
			nickname = "getEventsCalendar",
			value = "Get events for month of given date",
			notes = "Get events for month of given date",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Calendar", response = to.EventsOfDate[].class),
					@ApiResponse(code = 400, message = "Bad date format", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getEventsCalendar(@ApiParam(required = true, value = "like 2016-03-30") String date,
									@ApiParam(required = true) Long venueId) {
		LocalDate localDate;
		try {
			localDate = LocalDate.parse(date);
		} catch (Exception e) {
			Logger.warn("Bad calendar request. Invalid date: " + date, e);
			return badRequest(Json.toJson(new ErrorMessage("Bad date format")));
		}

		LocalDate start = localDate.withDayOfMonth(1);
		LocalDate end = localDate.withDayOfMonth(localDate.lengthOfMonth());

		//1. Process all of repeatable events entries
		Map<LocalDate, Integer> eventsNumberByDate = new HashMap<>();

		List<Event> eventsList = EventsHelper.getEventsForMonth(start, end, venueId);
		List<Event> recurringEvents = eventsList.stream()
				.filter(Event::getRepeatable)
				.collect(Collectors.toList());

		for (LocalDate day = start; day.isBefore(end); day = day.plusDays(1)) {
			final LocalDate finalDay = day;
			recurringEvents.forEach(event -> {
				if (!finalDay.isAfter(event.getDate()) && isNotDeletedOrDeletedAfter(finalDay, event) &&
						event.getDate().getDayOfWeek().equals(finalDay.getDayOfWeek())) {
					putOrUpdateNumberOfEvents(eventsNumberByDate, finalDay);
				}
			});
		}

		//2. Process all of non-repeatable events entries
		eventsList.stream()
				.filter(event -> !event.getRepeatable())
				.forEach(event -> {
					final LocalDate eventDate = event.getDate();
					putOrUpdateNumberOfEvents(eventsNumberByDate, eventDate);
				});

		List<EventsOfDate> eventsOfDateList = eventsNumberByDate.entrySet()
				.stream()
				.map(EventsOfDate::new)
				.collect(Collectors.toList());

		return ok(Json.toJson(eventsOfDateList));
	}

	private boolean isNotDeletedOrDeletedAfter(LocalDate finalDay, Event event) {
		return !event.isDeleted() || finalDay.isBefore(event.getDeletedAt());
	}

	private void putOrUpdateNumberOfEvents(Map<LocalDate, Integer> eventsNumberByDate, LocalDate finalDay) {
		if (eventsNumberByDate.keySet().contains(finalDay)) {
			Integer eventsNumber = eventsNumberByDate.get(finalDay);
			eventsNumberByDate.replace(finalDay, eventsNumber + 1);
		} else {
			eventsNumberByDate.put(finalDay, 1);
		}
	}

	@ApiOperation(
			nickname = "deleteEvent",
			value = "Delete event",
			notes = "Delete event",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Event with this id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteEvent(Long id) {
		Event event = Event.find.byId(id);
		if (event == null) {
			Logger.warn("Bad delete event request id=" + id);
			return notFound(Json.toJson(new ErrorMessage("Event with this id not found")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), event.getVenueId());
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !RolesHelper.canCreateAndModifyEventInfo(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		Ebean.beginTransaction();
		try {
			Venue venue = Venue.finder.byId(event.getVenueId());
			final LocalDate currentWorkingDay = ClickerHelper.getCurrentWorkingDay(venue);

			List<EventInstance> eventInstances = EventInstance.finder
					.fetch("reservations")
					.fetch("reservations.completionFeedback")
					.where()
					.ge("dateVenuePk.date", currentWorkingDay)
					.eq("venue.id", event.getVenueId())
					.findList();

			if (eventInstances != null && eventInstances.size() > 0) {
				eventInstances.forEach(eventInstance -> {
					final List<Reservation> reservations = eventInstance.getReservations();
					if (reservations != null) {
						reservations.forEach(reservation -> {
							if (reservation.getCompletionFeedback() != null) {
								reservation.setCompletionFeedback(null);
								reservation.update();
							}
						});
					}

					eventInstance.delete();
				});
			}

			event.setDeleted(true);
			event.setDeletedAt(currentWorkingDay);
			event.update();
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't delete event", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}
		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "updateEvent",
			value = "Update event",
			notes = "Update event",
			httpMethod = "UPDATE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Bad time format", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Event with this id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "event", value = "Event info", required = true, dataType = "to.EventInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result updateEvent() {
		JsonNode eventJson = request().body().asJson();
		EventInfo eventInfo;
		try {
			eventInfo = Json.fromJson(eventJson, EventInfo.class);
		} catch (Exception e) {
			Logger.warn("Bad request for update event: " + eventJson.asText());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), eventInfo.getVenueId());
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !RolesHelper.canCreateAndModifyEventInfo(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		Event event = Event.find.byId(eventInfo.getId());
		if (event == null) {
			Logger.warn("Bad update event request, event not found: " + eventInfo);
			return notFound(Json.toJson(new ErrorMessage("Event with this id not found")));
		}

		if (eventInfo.getDescription() != null) event.setDescription(eventInfo.getDescription());
		if (eventInfo.getFbEventUrl() != null) event.setFbEventUrl(eventInfo.getFbEventUrl());
		if (eventInfo.getName() != null) event.setName(eventInfo.getName());
		if (eventInfo.getRepeatable() != null) event.setRepeatable(eventInfo.getRepeatable());
		String oldPictureUrl = null;
		if (eventInfo.getPictureUrl() != null) {
			oldPictureUrl = event.getPictureUrl();
			event.setPictureUrl(eventInfo.getPictureUrl());
		}
		if (eventInfo.getStartsAt() != null) {
			LocalTime localTime;
			try {
				localTime = LocalTime.parse(eventInfo.getStartsAt());
			} catch (Exception e) {
				Logger.warn("Can't update event, bad time format (starts at): " + eventInfo.toString(), e);
				return badRequest(Json.toJson(new ErrorMessage("Bad time format (starts at)")));
			}
			event.setStartsAt(localTime);
		}

		if (eventInfo.getEndsAt() != null) {
			LocalTime localTime;
			try {
				localTime = LocalTime.parse(eventInfo.getEndsAt());
			} catch (Exception e) {
				Logger.warn("Can't update event, bad time format (ends at): " + eventInfo.toString(), e);
				return badRequest(Json.toJson(new ErrorMessage("Bad time format (ends at)")));
			}
			event.setEndsAt(localTime);
		}

		try {
			event.update();
		} catch (Exception e) {
			Logger.warn("Can't update event: " + eventInfo, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (StringUtils.isNotEmpty(oldPictureUrl)) {
			s3.deleteFromS3(oldPictureUrl.substring(oldPictureUrl.lastIndexOf("/") + 1));
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "createEvent",
			value = "Create new Event",
			notes = "Create new Event",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please check sent data | Bad date or time format", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "event", value = "New event info", required = true, dataType = "to.EventInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result createEvent() {
		JsonNode eventJson = request().body().asJson();
		EventInfo eventInfo;
		try {
			eventInfo = Json.fromJson(eventJson, EventInfo.class);
		} catch (Exception e) {
			Logger.warn("Bad request for creating event: " + eventJson.asText());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), eventInfo.getVenueId());
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !RolesHelper.canCreateAndModifyEventInfo(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		Venue venue = Venue.finder.byId(eventInfo.getVenueId());
		if (venue == null) {
			return badRequest(Json.toJson(new ErrorMessage("No venue with this id")));
		} else if (VenueType.EVENT.equals(venue.getVenueType())) {
			List<Event> events = Event.find.where().eq("venueId", venue.getId()).findList();

			if (events != null && events.size() > 0) {
				return badRequest(Json.toJson(new ErrorMessage("You can't create more than one event for this type of venue")));
			}

			if (eventInfo.getRepeatable() != null && eventInfo.getRepeatable()) {
				return badRequest(Json.toJson(new ErrorMessage("You can't create repeatable events for this type of venue")));
			}
		}

		LocalDate localDate;
		LocalTime startsAt;
		LocalTime endsAt;
		try {
			localDate = LocalDate.parse(eventInfo.getDate());
			startsAt = LocalTime.parse(eventInfo.getStartsAt());
			endsAt = LocalTime.parse(eventInfo.getEndsAt());
		} catch (Exception e) {
			Logger.warn("Can't create event: " + eventInfo.toString(), e);
			return badRequest(Json.toJson(new ErrorMessage("Bad date or time format")));
		}

		Event event = new Event();
		event.setDate(localDate);
		event.setDescription(eventInfo.getDescription());
		event.setFbEventUrl(eventInfo.getFbEventUrl());
		event.setName(eventInfo.getName());
		event.setRepeatable(eventInfo.getRepeatable());
		event.setPictureUrl(eventInfo.getPictureUrl());
		event.setStartsAt(startsAt);
		event.setEndsAt(endsAt);
		event.setVenueId(eventInfo.getVenueId());

		try {
			event.save();
		} catch (Exception e) {
			Logger.warn("Can't save new event", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getEvents",
			value = "Get events by venueId and date",
			notes = "Get events by venueId and date",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Events list", response = to.EventsList.class),
					@ApiResponse(code = 400, message = "Bad date format", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getEvents(String date, @ApiParam(required = true) Long venueId) {
		LocalDate localDate;

		if (StringUtils.isNotEmpty(date)) {
			try {
				localDate = LocalDate.parse(date);
			} catch (Exception e) {
				Logger.warn("Can't get events for venue id=" + venueId, e);
				return badRequest(Json.toJson(new ErrorMessage("Bad date format")));
			}
		} else {
			Venue venue = Venue.finder.byId(venueId);
			if (venue == null) {
				return notFound(Json.toJson(new ErrorMessage("Bad venue id")));
			}

			localDate = ClickerHelper.getCurrentWorkingDay(venue);
		}

		List<Event> eventsList;
		try {
			eventsList = EventsHelper.getEventListByDate(venueId, localDate);
		} catch (Exception e) {
			Logger.warn("Can't get events for venue id=" + venueId + ", and date=" + date, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		EventsList events = new EventsList();

		if (eventsList != null) {
			for (Event event : eventsList) {
				EventInfo eventInfo = new EventInfo(event);
				eventInfo.setDate(localDate.toString());
				events.getEventsList().add(eventInfo);
			}
		}

		return ok(Json.toJson(events));
	}
}
