package controllers.api.v1;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.twilio.sdk.TwilioRestException;
import io.swagger.annotations.*;
import models.*;
import modules.twilio.Twilio;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.*;
import to.admin.VenueTO;
import to.notifications.NotificationTO;
import to.notifications.NotificationType;
import to.venue.*;
import util.*;
import util.exception.IllegalReservationDateException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by arkady on 15/02/16.
 */
@Api(value = "/api/v1/venue", description = "Venues management", tags = "venues", basePath = "/api/v1/venue")
public class Venues extends UserProfileController<MrBlackUserProfile> {

	private ReservationsHelper reservationsHelper;
	private NotificationsHelper notificationsHelper;
	private Twilio twilio;

	@Inject
	public Venues(ReservationsHelper reservationsHelper, NotificationsHelper notificationsHelper, Twilio twilio) {
		this.reservationsHelper = reservationsHelper;
		this.notificationsHelper = notificationsHelper;
		this.twilio = twilio;
	}

	@ApiOperation(
			nickname = "Get Venue's notification settings",
			value = "Get Venue's notification settings of current user",
			notes = "Get Venue's notification settings of current user",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Settings", response = NotificationsSettingsTO.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getSettings(Long venueId) {
		final User user = getUserProfile().getUser();
		UserVenuePK pk = new UserVenuePK(user.getId(), venueId);
		NotificationsSettings settings = NotificationsSettings.finder.byId(pk);

		if (settings == null) {
			settings = new NotificationsSettings();
		}

		return ok(Json.toJson(new NotificationsSettingsTO(settings)));
	}

	@ApiOperation(
			nickname = "Update Venue's notification settings",
			value = "Update Venue's notification settings of current user",
			notes = "Update Venue's notification settings of current user",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result setSettings(Long venueId) {
		final User user = getUserProfile().getUser();

		JsonNode userSettingsJson = request().body().asJson();
		NotificationsSettingsTO settingsTO;
		try {
			settingsTO = Json.fromJson(userSettingsJson, NotificationsSettingsTO.class);
		} catch (Exception e) {
			Logger.warn("Bad UserSettings in request : " + userSettingsJson.asText());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		UserVenuePK pk = new UserVenuePK(user.getId(), venueId);
		NotificationsSettings settings = NotificationsSettings.finder.byId(pk);

		boolean isNewSettings = false;
		if (settings == null) {
			isNewSettings = true;
			settings = new NotificationsSettings(pk);
		}

		if (settingsTO.getNewApprovalEmployee() != null) settings.setNewApprovalEmployee(settingsTO.getNewApprovalEmployee());
		if (settingsTO.getNewApprovalRequestBs() != null) settings.setNewApprovalRequestBs(settingsTO.getNewApprovalRequestBs());
		if (settingsTO.getNewAssignment() != null) settings.setNewAssignment(settingsTO.getNewAssignment());
		if (settingsTO.getNotifyMeArrival() != null) settings.setNotifyMeArrival(settingsTO.getNotifyMeArrival());
		if (settingsTO.getReservationICreatedApproved() != null) settings.setReservationICreatedApproved(settingsTO.getReservationICreatedApproved());
		if (settingsTO.getReservationICreatedRejected() != null) settings.setReservationICreatedRejected(settingsTO.getReservationICreatedRejected());
		if (settingsTO.getTableReleased() != null) settings.setTableReleased(settingsTO.getTableReleased());

		try {
			if (isNewSettings) {
				settings.save();
			} else {
				settings.update();
			}
		} catch (Exception e) {
			Logger.warn("Can't update user's settings", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "Get EOD report",
			value = "Get EOD report",
			notes = "Get EOD report",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = NotificationTO[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getEndOfDayStatementReport(@ApiParam(required = true) Long venueId,
											 @ApiParam(required = true) Long eventId,
											 @ApiParam(required = true) String date) {
		LocalDate localDate;
		try {
			localDate = LocalDate.parse(date);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Bad date format")));
		}

		final User currentUser = getUserProfile().getUser();
		UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
		UserVenue userVenue = UserVenue.finder.byId(key);

		if (!currentUser.isAdmin() && (userVenue == null || !RolesHelper.canViewEODStatementAndConfirm(userVenue.getRoles()))) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		EventInstance eventInstance;
		try {
			DateVenuePk pk = new DateVenuePk(venueId, localDate, eventId);
			eventInstance = EventInstance.finder
					.fetch("event")
					.fetch("venue")
					.fetch("reservations")
					.where()
					.idEq(pk)
					.findUnique();
		} catch (Exception e) {
			Logger.warn("Can't fetch reservations list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (eventInstance == null) {
			return badRequest(ErrorMessage.getJsonErrorMessage("Please check date, event, and venue parameters"));
		}

		final Integer[] sales = {0};
		if (eventInstance.getReservations() != null && eventInstance.getReservations().size() > 0) {
			eventInstance.getReservations().stream()
					.filter(reservation -> ReservationStatus.CONFIRMED_COMPLETE.equals(reservation.getStatus()) &&
							(reservation.getTable() != null || reservation.getReleasedFrom() != null)
					)
					.forEach(reservation -> sales[0] = sales[0] + reservation.getTotalSpent());
		}

		final Integer men = eventInstance.getMen();
		final Integer women = eventInstance.getWomen();

		Integer total = men + women;
		String report;
		if (total > 0) {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMM d, YYYY");
			final String dateString = eventInstance.getDate().format(formatter);

			report = eventInstance.getVenue().getName() + ", " + eventInstance.getEvent().getName() + ", " + dateString + ":\n" +
					"- Total Net sales (" + sales[0] + ")\n- Clicker (" + total +
					", " + Math.round((100 * women)/total) +"% females / " +
					Math.round((100 * men)/total) + "% males)";
		} else {
			report = "- Total Net sales (" + sales[0] + ")\n- Clicker (0)";
		}

		List<UserVenue> userVenues = UserVenue.finder
				.fetch("user")
				.where()
				.eq("venue.id", venueId)
				.raw("roles ?? '" + VenueRole.MANAGER.name() + "'")
				.findList();

		if (userVenue != null) userVenues.forEach(userVenue1 -> {
			final String phoneNumber = userVenue1.getUser().getPhoneNumber();
			try {
				twilio.sendSMS(phoneNumber, report);
			} catch (TwilioRestException e) {
				Logger.warn("Can't send SMS report", e);
			}
		});

		eventInstance.setReportSent(true);
		try {
			eventInstance.update();
		} catch (Exception e) {
			Logger.warn("Can't update eventInstance", e);
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "mark Notifications as read",
			value = "mark Notifications as read by id",
			notes = "mark Notifications as read by id",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = NotificationTO[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result markNotificationAsReadById(@ApiParam(required = true) Long venueId,
											 @ApiParam(required = true) Long notificationId) {
		final User user = getUserProfile().getUser();

		Ebean.beginTransaction();
		try {
			Notification notification = Notification.find.byId(notificationId);
			if (notification == null) {
				return notFound(ErrorMessage.getJsonErrorMessage("Bad notification id"));
			}

			UserVenuePK pk = new UserVenuePK(user.getId(), venueId);
			NotificationStats stats = NotificationStats.finder.byId(pk);
			if (stats != null) {
				if (NotificationType.NEW_ASSIGNMENT.equals(notification.getType())) {
					stats.setUnreadAssignments(stats.getUnreadAssignments() - 1);
				} else if (NotificationType.NEW_APPROVAL_EMPLOYEE.equals(notification.getType())) {
					stats.setUnreadEmployees(stats.getUnreadEmployees() - 1);
				} else if (NotificationType.NEW_APPROVAL_REQUEST_BS.equals(notification.getType())) {
					stats.setUnreadReservationsApprovalRequest(stats.getUnreadReservationsApprovalRequest() - 1);
				} else if (NotificationType.RESERVATION_I_CREATED_APPROVED.equals(notification.getType())) {
					stats.setUnreadReservationsApproved(stats.getUnreadReservationsApproved() - 1);
				} else if (NotificationType.RESERVATION_I_CREATED_REJECTED.equals(notification.getType())) {
					stats.setUnreadReservationsRejected(stats.getUnreadReservationsRejected() - 1);
				} else if (NotificationType.TABLE_RELEASED.equals(notification.getType())) {
					stats.setUnreadTableReleased(stats.getUnreadTableReleased() - 1);
				}
				stats.update();
			}

			notification.delete();
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Error when updating notification stats or deleting notification", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "mark Notifications as read",
			value = "mark Notifications as read by type",
			notes = "mark Notifications as read by type",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = NotificationTO[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result markNotificationAsReadByType(@ApiParam(required = true) Long venueId,
											   @ApiParam(required = true) String typeName) {
		final User user = getUserProfile().getUser();

		NotificationType type;
		try {
			type = NotificationType.valueOf(typeName);
		} catch (IllegalArgumentException e) {
			return badRequest(ErrorMessage.getJsonErrorMessage("Bad notification type"));
		}

		Ebean.beginTransaction();
		try {
			List<Notification> notifications = Notification.find
					.where()
					.eq("userId", user.getId())
					.eq("venueId", venueId)
					.eq("type", type)
					.findList();

			if (notifications != null) {
				notifications.forEach(Model::delete);
			}

			UserVenuePK pk = new UserVenuePK(user.getId(), venueId);
			NotificationStats stats = NotificationStats.finder.byId(pk);
			if (stats != null) {
				if (NotificationType.NEW_ASSIGNMENT.equals(type)) {
					stats.setUnreadAssignments(0);
				} else if (NotificationType.NEW_APPROVAL_EMPLOYEE.equals(type)) {
					stats.setUnreadEmployees(0);
				} else if (NotificationType.NEW_APPROVAL_REQUEST_BS.equals(type)) {
					stats.setUnreadReservationsApprovalRequest(0);
				} else if (NotificationType.RESERVATION_I_CREATED_APPROVED.equals(type)) {
					stats.setUnreadReservationsApproved(0);
				} else if (NotificationType.RESERVATION_I_CREATED_REJECTED.equals(type)) {
					stats.setUnreadReservationsRejected(0);
				} else if (NotificationType.TABLE_RELEASED.equals(type)) {
					stats.setUnreadTableReleased(0);
				}
				stats.update();
			}
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Error when deleting notifications by type", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "getVenueFeed",
			value = "Returns VenueFeed of this Venue",
			notes = "Returns VenueFeed of this Venue",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = NotificationTO[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getVenueFeed(@ApiParam(required = true) Long venueId) {
		final User currentUser = getUserProfile().getUser();

		UserVenuePK pk = new UserVenuePK(currentUser.getId(), venueId);
		List<Notification> notificationList = Notification.find
				.where()
				.eq("venueId", venueId)
				.eq("userId", currentUser.getId())
				.findList();

		List<NotificationTO> notifications;
		if (notificationList != null) {
			notifications = notificationList.stream().map(NotificationTO::new).collect(Collectors.toList());
		} else {
			notifications = new ArrayList<>();
		}

		return ok(Json.toJson(notifications));
	}

	@ApiOperation(
			nickname = "updatePromoterVenueRequest",
			value = "Update Venue request for Promoter",
			notes = "Update Venue request by venue id for Promoter",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 403, message = "You don't have permissions for this action", response = ErrorMessage.class),
					@ApiResponse(code = 404, message = "Request of Promoter for this Venue not found", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result approvePromoterRequest(@ApiParam(required = true) Long venueId,
										 @ApiParam(required = true) Long promoterId) {
		final User currentUser = getUserProfile().getUser();

		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !RolesHelper.canViewAndManageEmployeeSection(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		PromoterVenue promoterVenue = Ebean.find(PromoterVenue.class)
				.where().eq("promoter.id", promoterId)
				.where().eq("venue.id", venueId)
				.findUnique();

		if (promoterVenue == null) {
			return notFound(Json.toJson(new ResponseMessage("Request of Promoter for this Venue not found")));
		}

		try {
			promoterVenue.setRequestStatus(VenueRequestStatus.APPROVED);
			promoterVenue.update();
		} catch (Exception e) {
			Logger.warn("Can't approve venue request for Promoter", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "deletePromoterVenueRequest",
			value = "Delete Venue request for Promoter",
			notes = "Delete Venue request by venue id for Promoter",
			httpMethod = "DELETE",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venue request created", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 403, message = "You don't have permissions for this action", response = ErrorMessage.class),
					@ApiResponse(code = 404, message = "Request of Promoter for this Venue not found", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deletePromoterRequest(@ApiParam(required = true) Long venueId,
										@ApiParam(required = true) Long promoterId) {
		final User currentUser = getUserProfile().getUser();

		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), promoterId);
			UserVenue userVenue = UserVenue.finder.byId(key);

			if (userVenue == null || userVenue.getRoles() == null || !RolesHelper.canViewAndManageEmployeeSection(userVenue.getRoles())) {
				key = new UserVenuePK(currentUser.getId(), venueId);
				userVenue = UserVenue.finder.byId(key);
				if (userVenue == null || userVenue.getRoles() == null || !RolesHelper.canViewAndManageEmployeeSection(userVenue.getRoles())) {
					return forbidden(ErrorMessage.getJsonForbiddenMessage());
				}
			}
		}

		PromoterVenue promoterVenue = Ebean.find(PromoterVenue.class)
				.where().eq("promoter.id", promoterId)
				.where().eq("venue.id", venueId)
				.findUnique();

		if (promoterVenue == null) {
			return notFound(Json.toJson(new ResponseMessage("Request of Promoter for this Venue not found")));
		}

		if (VenueRequestStatus.APPROVED.equals(promoterVenue.getRequestStatus())) {
			try {
				promoterVenue.setRequestStatus(VenueRequestStatus.PREVIOUS);
				promoterVenue.update();
			} catch (Exception e) {
				Logger.warn("Can't mark venue request for Promoter as PREVIOUS", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}
		} else if (VenueRequestStatus.REQUESTED.equals(promoterVenue.getRequestStatus())) {
			try {
				promoterVenue.delete();
			} catch (Exception e) {
				Logger.warn("Can't delete venue request for Promoter", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "createPromoterVenueRequest",
			value = "Create Venue request for Promoter",
			notes = "Create Venue request by venue id for Promoter",
			httpMethod = "POST",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venue request created", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "Bad promoterId. This is not promotion company", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Venue with this id is not found | " +
							"Promoter company with this id is not found", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result createPromoterRequest(@ApiParam(required = true) Long venueId,
										@ApiParam(required = true) Long promoterId) {
		final User currentUser = getUserProfile().getUser();

		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), promoterId);
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || userVenue.getRoles() == null || !userVenue.getRoles().contains(VenueRole.MANAGER)) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		if (venueId.equals(promoterId)) {
			return badRequest(ErrorMessage.getJsonErrorMessage("You can't send request to yourself"));
		}

		Venue venue = Venue.finder.byId(venueId);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("Venue with this id is not found")));
		}

		if (VenueType.PROMOTER.equals(venue.getVenueType())) {
			return badRequest(ErrorMessage.getJsonErrorMessage("You can't send request to another promoter"));
		}

		Venue promoter = Venue.finder.byId(promoterId);
		if (promoter == null) {
			return notFound(Json.toJson(new ErrorMessage("Promoter company with this id is not found")));
		}

		if (!VenueType.PROMOTER.equals(promoter.getVenueType())) {
			return badRequest(Json.toJson(new ErrorMessage("Bad promoter id. This is not promotion company")));
		}

		PromoterVenue promoterVenue = Ebean.find(PromoterVenue.class)
				.where().eq("promoter.id", promoterId)
				.where().eq("venue.id", venueId)
				.findUnique();

		if (promoterVenue != null) {
			if (VenueRequestStatus.PREVIOUS.equals(promoterVenue.getRequestStatus())) {
				promoterVenue.setRequestStatus(VenueRequestStatus.REQUESTED);

				try {
					promoterVenue.update();
				} catch (Exception e) {
					Logger.warn("Can't recreate venue request for Promoter", e);
					return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
				}
			} else {
				return ok(Json.toJson(new ResponseMessage("Request already created")));
			}
		} else {
			PromoterVenuePK pk = new PromoterVenuePK(promoterId, venueId);
			promoterVenue = new PromoterVenue(pk);
			promoterVenue.setSince(LocalDate.now(ZoneId.of(venue.getTimeZone())));
			promoterVenue.setRequestStatus(VenueRequestStatus.REQUESTED);

			try {
				promoterVenue.save();
			} catch (Exception e) {
				Logger.warn("Can't save venue request for Promoter", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}
		}

		return ok(Json.toJson(new ResponseMessage("Venue request created")));
	}

	@ApiOperation(
			nickname = "endOfDayStatement",
			value = "Get End of Day statement",
			notes = "Get End of Day statement by date and eventId",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "End of day statement", response = EndOfDayStatement.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 403, message = "Operation not permitted. Please create request to work in this Venue", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	@Transactional
	public Result getEndOfDayStatement(@ApiParam(required = true) Long venueId, @ApiParam(required = true) Long eventId,
									   @ApiParam(required = true) String date) {
		LocalDate localDate;
		try {
			localDate = LocalDate.parse(date);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Bad date format")));
		}

		final User currentUser = getUserProfile().getUser();
		UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
		UserVenue userVenue = UserVenue.finder.byId(key);

		if (!currentUser.isAdmin() && (userVenue == null || !RolesHelper.canViewEODStatementAndConfirm(userVenue.getRoles()))) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		EventInstance eventInstance;
		try {
			DateVenuePk pk = new DateVenuePk(venueId, localDate, eventId);
			eventInstance = EventInstance.finder
					.fetch("reservations")
					.fetch("reservations.completedBy")
					.fetch("reservations.confirmedBy")
					.fetch("reservations.guest")
					.fetch("reservations.payees")
					.fetch("reservations.staff")
					.fetch("reservations.staff.user")
					.fetch("reservations.staff.user.payInfo")
					.where()
					.or(Expr.isNotNull("reservations.table"), Expr.isNotNull("reservations.releasedFrom"))
					.idEq(pk)
					.findUnique();
		} catch (Exception e) {
			Logger.warn("Can't fetch reservations list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		final EndOfDayStatement statement = new EndOfDayStatement();
		if (eventInstance != null && eventInstance.getReservations() != null) {
			final Map<Place, List<Reservation>> placeListMap = new HashMap<>();
			for (Reservation reservation : eventInstance.getReservations()) {
				Place place = reservation.getTable() != null ? reservation.getTable() : reservation.getReleasedFrom();
				if (!placeListMap.keySet().contains(place)) placeListMap.put(place, new ArrayList<>());
				placeListMap.get(place).add(reservation);
			}

			if (!placeListMap.isEmpty()) {
				try {
					placeListMap.forEach((place, reservationList) -> {
						final List<EODReservationInfo> reservationInfoList =
								reservationsHelper.getEodReservationInfos(reservationList);
						statement.getEndOfDayItems().add(new EndOfDayItem(new TableInfo(place), reservationInfoList));
					});
				} catch (Exception e) {
					Logger.warn("Can't map results to EoD Items list", e);
					return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
				}
			}

			statement.setReportSent(eventInstance.getReportSent());
		}

		return ok(Json.toJson(statement));
	}

	@ApiOperation(
			nickname = "upcomingReservations",
			value = "Get upcoming reservations",
			notes = "Get paged list of upcoming reservations",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Guest info", response = UpcomingReservations.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue with this id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getUpcomingReservations(@ApiParam(required = true) Long id,
										  @ApiParam(required = true) Integer pageIndex,
										  @ApiParam(required = true) Integer pageSize) {
		Venue venue = Venue.finder.byId(id);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("No venue with this id")));
		}

		LocalDate date = LocalDate.now(ZoneId.of(venue.getTimeZone()));

		List<Reservation> reservationList;
		try {
			reservationList= Reservation.find
					.fetch("eventInstance")
					.fetch("eventInstance.event")
					.where()
					.eq("eventInstance.venue.id", id)
					.ge("eventInstance.dateVenuePk.date", date)
					.orderBy("id asc")
					.findPagedList(pageIndex, pageSize).getList();
		} catch (Exception e) {
			Logger.warn("Can't get upcoming reservations", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		final UpcomingReservations upcomingReservations = new UpcomingReservations();
		if (reservationList != null) {
			reservationList.forEach(reservation -> {
				upcomingReservations.getReservationInfos().add(reservationsHelper.reservationToInfo(reservation, false, false));
				final EventInfo eventInfo = new EventInfo(reservation.getEventInstance().getEvent());
				eventInfo.setDate(reservation.getEventInstance().getDate().toString());
				upcomingReservations.getEventInfos().add(eventInfo);
			});
		}

		return ok(Json.toJson(upcomingReservations));
	}

	@ApiOperation(
			nickname = "getGuestInfo",
			value = "Returns guest info",
			notes = "Returns guest info by guestId and venueId",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Guest info", response = VisitorInfo.class),
					@ApiResponse(code = 400, message = "'venueId' and 'guestId' required | Guest with this id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getGuestInfo(@ApiParam(required = true) Long venueId, @ApiParam(required = true) Long guestId) {
		if (venueId == null || guestId == null) {
			return badRequest(Json.toJson(new ErrorMessage("'venueId' and 'guestId' required")));
		}
		User guest = User.finder.byId(guestId);
		if (guest == null) {
			return notFound(Json.toJson(new ErrorMessage("Guest with this id not found")));
		}
		final VisitorInfo visitorInfo = reservationsHelper.getDetailedVisitorInfo(venueId, guest);
		return ok(Json.toJson(visitorInfo));
	}

	@ApiOperation(
			nickname = "setVenueState",
			value = "Set GL and BS open or closed, and set/remove table minimums",
			notes = "Set GL and BS open or closed, and set/remove table minimums (bottles and min.spend)",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Venue state", value = "State", required = true, dataType = "to.venue.State", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result setEventInstanceState(@ApiParam(required = true) Long venueId, @ApiParam(required = true) String date,
										@ApiParam(required = true) Long eventId) {
		LocalDate localDate;
		try {
			localDate = LocalDate.parse(date);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Bad date format")));
		}

		State state;
		try {
			state = Json.fromJson(request().body().asJson(), State.class);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Bad state format")));
		}

		EventInstance eventInstance;
		try {
			eventInstance = EventInstanceHelper.getOrCreateEventInstance(venueId, localDate, eventId);
		} catch (IllegalReservationDateException e) {
			Logger.warn("Can't get or create EventInstance", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		setEventInstanceState(state, eventInstance);

		try {
			eventInstance.update();
		} catch (Exception e) {
			Logger.warn("Can't update EventInstance", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	private void setEventInstanceState(State state, EventInstance eventInstance) {
		if (state.getBsClosed() != null) eventInstance.setBsClosed(state.getBsClosed());
		if (state.getGlClosed() != null) eventInstance.setGlClosed(state.getGlClosed());
		if (state.getMinBottles() != null) {
			if (state.getMinBottles() == 0) {
				eventInstance.setMinBottles(null);
			} else {
				eventInstance.setMinBottles(state.getMinBottles());
			}
		}
		if (state.getMinSpend() != null) {
			if (state.getMinSpend() == 0) {
				eventInstance.setMinSpend(null);
			} else {
				eventInstance.setMinSpend(state.getMinSpend());
			}
		}
	}

	@ApiOperation(
			nickname = "getSnapshot",
			value = "Get Venue Snapshot",
			notes = "Get Venue Snapshot",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venue snapshot", response = Snapshot.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	@Transactional
	public Result getSnapshot(@ApiParam(required = true) Long venueId, @ApiParam(required = true) String date,
							  @ApiParam(required = true) Long eventId) {
		Snapshot snapshot = new Snapshot();

		LocalDate localDate;
		try {
			localDate = LocalDate.parse(date);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Bad date format")));
		}

		EventInstance eventInstance;
		try {
			eventInstance = EventInstanceHelper.getOrCreateEventInstance(venueId, localDate, eventId, true, true, true);
		} catch (IllegalReservationDateException e) {
			return notFound(Json.toJson(new ErrorMessage(e.getMessage())));
		} catch (Exception e) {
			Logger.warn("Can't get or create EventInstance", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		Event event = eventInstance.getEvent();
		Venue venue = eventInstance.getVenue();

		try {
			snapshot.setEventInfo(new EventInfo(event));
		} catch (EntityNotFoundException e) {
			Logger.warn("Something wrong with event...", e);
			return internalServerError(Json.toJson(new ErrorMessage("Something wrong with event...")));
		}

		final LocalDate currentWorkingDay = ClickerHelper.getCurrentWorkingDay(venue);
		final boolean showClicker = ClickerHelper.isShowClicker(currentWorkingDay, eventId, venueId, ZoneId.of(venue.getTimeZone()));
		snapshot.setShowClicker(showClicker);

		State state = new State();
		state.setGlClosed(eventInstance.getGlClosed());
		state.setBsClosed(eventInstance.getBsClosed());
		state.setMinBottles(eventInstance.getMinBottles());
		state.setMinSpend(eventInstance.getMinSpend());
		snapshot.setState(state);

		List<Reservation> reservations = eventInstance.getReservations();

		snapshot.getClickerState().setTotalIn(eventInstance.getTotalIn());
		snapshot.getClickerState().setTotalOut(eventInstance.getTotalOut());
		snapshot.getClickerState().setCapacity(venue.getCapacity());
		snapshot.getClickerState().setMen(eventInstance.getMen());
		snapshot.getClickerState().setWomen(eventInstance.getWomen());

		//TODO remove
		snapshot.setOuts(eventInstance.getTotalOut());
		snapshot.setIns(eventInstance.getTotalIn());
		snapshot.setCapacity(venue.getCapacity());

		final User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin() && reservations != null) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !(RolesHelper.canViewFullSnapshotInfo(userVenue.getRoles()) ||
					RolesHelper.canOnlyViewAllResos(userVenue.getRoles()))) {

				final List<ReservationUser> userAssignments = Schedule.getUserAssignments(venue.getId(), currentUser.getId(), localDate);
				snapshot.setMyReservations(userAssignments != null ? userAssignments.size() : 0);

				final Stream<Reservation> stream;
				if (RolesHelper.canViewAndModifyAllGuestListResos(userVenue.getRoles())) {
					stream = reservations.stream().filter(reservation -> reservation.getBottleService() == null ||
							currentUser.getId().equals(reservation.getBookedBy().getId()));
				} else {
					stream = reservations.stream().filter(reservation -> currentUser.getId().equals(reservation.getBookedBy().getId()));
				}

				reservations = stream.collect(Collectors.toList());
				snapshot.setOuts(0); //Don't show outs for basic staff
			}
		}

		if (reservations != null) {
			for (Reservation reservation : reservations) {
				if (reservation.getBottleService() != null) {
					if (reservation.getTable() != null) {
						snapshot.setSeated(snapshot.getSeated() + 1);
					}

					if (reservationsHelper.isReservationInApprovedList(reservation)) {
							snapshot.setApproved(snapshot.getApproved() + 1);
							snapshot.setBsGirlsActual(reservation.getGirlsSeated() + snapshot.getBsGirlsActual());
							snapshot.setBsGuysActual(reservation.getGuysSeated() + snapshot.getBsGuysActual());
							snapshot.setBsMinSpend(snapshot.getBsMinSpend() + reservation.getMinSpend());
					} else if (ReservationStatus.PENDING.equals(reservation.getStatus())) {
						snapshot.setQueue(snapshot.getQueue() + 1);
					}

					snapshot.setBsBooked(snapshot.getBsBooked() + reservation.getGuestsNumber() + 1); //+1 for owner of reservation
				} else {
					snapshot.setGuestList(snapshot.getGuestList() + 1);
					if (reservation.getArrivalTime() != null) {
						snapshot.setGlGirlsActual(reservation.getGirlsSeated() + snapshot.getGlGirlsActual());
						snapshot.setGlGuysActual(reservation.getGuysSeated() + snapshot.getGlGuysActual());
					}

					snapshot.setGlBooked(snapshot.getGlBooked() + reservation.getGuestsNumber() + 1); //+1 for owner of reservation
				}
			}
		}

		UserVenuePK pk = new UserVenuePK(currentUser.getId(), venueId);
		NotificationStats stats = NotificationStats.finder.byId(pk);
		if (stats != null) {
			snapshot.setUnreadEmployees(stats.getUnreadEmployees());
			snapshot.setUnreadTableReleased(stats.getUnreadTableReleased());
			snapshot.setUnreadAssignments(stats.getUnreadAssignments());
			snapshot.setUnreadReservationsApprovalRequest(stats.getUnreadReservationsApprovalRequest());
			snapshot.setUnreadReservationsRejected(stats.getUnreadReservationsRejected());
			snapshot.setUnreadReservationsApproved(stats.getUnreadReservationsApproved());
		}

		return ok(Json.toJson(snapshot));
	}

	@ApiOperation(
			nickname = "getCustomers",
			value = "Get Customers list",
			notes = "Get Customers list of Venue. Tags separated by commas. Ex.: tag1,tag2,",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Customers list", response = VisitorInfo[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getCustomers(@ApiParam(required = true) Long venueId, @ApiParam(value = "Ex.: tag1,tag2") String tagsString,
							   @ApiParam(required = true, value = "starts from 0") Integer pageIndex,
							   @ApiParam(required = true) Integer pageSize) {

		final User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !RolesHelper.canViewClientsSection(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		final ExpressionList<Reservation> expressionList = Reservation.find.where();

		if (StringUtils.isNotEmpty(tagsString)) {
			List<String> tags = Arrays.asList(tagsString.split(","));
			String result = tags.stream()
					.map((s) -> "'" + s.trim() + "'")
					.collect(Collectors.joining(","));
			expressionList.raw("tags ??| array[" + result + "]");
		}
		expressionList.and(Expr.eq("eventInstance.dateVenuePk.venueId", venueId), Expr.isNotNull("guest"));

		List<Reservation> reservations;
		try {
			reservations = expressionList.findList();
		} catch (Exception e) {
			Logger.warn("Can't fetch Customers list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		Set<Long> guestIdSet = null;
		if (reservations != null) {
			guestIdSet = reservations.stream()
					.filter(reservation -> reservation.getGuest() != null)
					.map(reservation -> reservation.getGuest().getId()).collect(Collectors.toSet());
		}

		List<VisitorInfo> visitorInfos = new ArrayList<>();
		if (guestIdSet != null) {
			List<User> guests = Ebean.find(User.class).where()
					.in("id", guestIdSet)
					.orderBy("id asc")
					.findPagedList(pageIndex, pageSize).getList();

			if (guests != null && guests.size() > 0){
				guests.forEach(guest -> {
					VisitorInfo detailedVisitorInfo;
					detailedVisitorInfo = reservationsHelper.getDetailedVisitorInfo(venueId, guest);
					visitorInfos.add(detailedVisitorInfo);
				});
			}
		}

		return ok(Json.toJson(visitorInfos));
	}

	@ApiOperation(
			nickname = "getTagsList",
			value = "Get all tags of Venue",
			notes = "Get all tags of Venue",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Tags list", response = String[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue found with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getTagsList(@ApiParam(required = true) Long venueId) {
		Venue venue = Venue.finder.byId(venueId);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("No venue found with given id")));
		}

		return ok(Json.toJson(venue.getTags()));
	}

	@ApiOperation(
			nickname = "addTagToVenue",
			value = "Add tag to venue",
			notes = "Add single tag to venue",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue found with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "request params", value = "TagTO", required = true, dataType = "to.venue.TagTO", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result addTagToVenue(@ApiParam(required = true) Long venueId) {
		Venue venue = Venue.finder.byId(venueId);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("No venue found with given id")));
		}

		TagTO tagTO;
		try {
			tagTO = Json.fromJson(request().body().asJson(), TagTO.class);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Bad tag format")));
		}

		final List<String> tags = venue.getTags();
		tags.add(tagTO.getTag());
		venue.setTags(tags);

		try {
			venue.update();
		} catch (Exception e) {
			Logger.warn("Can't populate venue with new tag", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "deleteTagFromVenue",
			value = "Delete tag from venue",
			notes = "Delete tag from venue",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "Tag should be not empty", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue found with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteTagFromVenue(@ApiParam(required = true) Long venueId, @ApiParam(required = true) String tag) {
		Venue venue = Venue.finder.byId(venueId);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("No venue found with given id")));
		}

		if (StringUtils.isEmpty(tag)) {
			return badRequest(Json.toJson(new ErrorMessage("Tag should be not empty")));
		}

		final List<String> tags = venue.getTags();
		if (!tags.remove(tag)) {
			return badRequest(Json.toJson(new ErrorMessage("Unknown tag")));
		}
		venue.setTags(tags);

		List<Reservation> reservations = Reservation.find.where().raw("tags ?? '" + tag + "'").findList();
		List<CustomerVenue> customerVenues = CustomerVenue.finder.where().raw("tags ?? '" + tag + "'").findList();

		Ebean.beginTransaction();
		try {
			venue.update();
			if (reservations != null) {
				reservations.forEach(reservation -> {
					final List<String> reservationTags = reservation.getTags();
					reservationTags.remove(tag);
					reservation.setTags(reservationTags);
					reservation.update();
				});
			}

			if (customerVenues != null) {
				customerVenues.forEach(customerVenue -> {
					final List<String> customerVenueTags = customerVenue.getTags();
					customerVenueTags.remove(tag);
					customerVenue.setTags(customerVenueTags);
					customerVenue.update();
				});
			}

			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't remove tag from Venue", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "getPrevEventDate",
			value = "Get prev event date",
			notes = "Get prev event date of this venue",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Previous date", response = DateStringValue.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No events before this date", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getPrevAvailableDate( @ApiParam(required = true) Long venueId, @ApiParam(required = true) String from) {
		LocalDate nextDate;
		try {
			Venue venue = Venue.finder.byId(venueId);
			if (venue == null) {
				return notFound(Json.toJson(new ErrorMessage("No venue found with given id")));
			}

			nextDate = EventsHelper.getClosestEventBeforeDate(venueId, LocalDate.parse(from));
		} catch (Exception e) {
			Logger.warn("Can't get closest previous date", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (nextDate == null) {
			return notFound(Json.toJson(new ErrorMessage("No events before this date")));
		}

		return ok(Json.toJson(new DateStringValue(nextDate.toString())));
	}

	@ApiOperation(
			nickname = "getAvailableSeatingLists",
			value = "Get available seating lists",
			notes = "Get available seating places lists(TABLE, STANDUP, BAR)",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.SeatingPlaceList.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getAvailableSeating(Long id) {
		SeatingPlaceList seatingPlaceList = new SeatingPlaceList();
		try {
			List<Place> seatingPlaces = Ebean.find(Place.class).where().eq("venue.id", id).findList();
			if (seatingPlaces != null) {
				for (Place place : seatingPlaces) {
					if (place.getBottleServiceType().equals(BottleServiceType.BAR)) {
						seatingPlaceList.getBar().add(new TableInfo(place));
					} else if (place.getBottleServiceType().equals(BottleServiceType.STANDUP)) {
						seatingPlaceList.getStandup().add(new TableInfo(place));
					} else if (place.getBottleServiceType().equals(BottleServiceType.TABLE)) {
						seatingPlaceList.getTable().add(new TableInfo(place));
					}
				}
			}
		} catch (Exception e) {
			Logger.warn("Can't get Staff list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(seatingPlaceList));
	}

	@ApiOperation(
			nickname = "getStaffLists",
			value = "Get available staff for reservation",
			notes = "Get HOSTS, BUSSERS and SERVERS available for reservation",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "HOSTS, BUSSERS and SERVERS list", response = StaffList.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getStaffLists(Long id) {
		List<UserVenue> userVenueList;
		try {
			userVenueList = Ebean.find(UserVenue.class)
					.fetch("user")
					.where().and(Expr.eq("venue.id", id), Expr.eq("requestStatus", VenueRequestStatus.APPROVED.name()))
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't get Staff list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		StaffList staffList = new StaffList();
		for (UserVenue userVenue : userVenueList) {
			VenueStaffInfo venueStaffInfo = getVenueStaffInfo(userVenue);
			if (venueStaffInfo.getPreferredRoles().contains(VenueRole.SERVER)) {
				staffList.getServers().add(venueStaffInfo);
			}
			if (venueStaffInfo.getPreferredRoles().contains(VenueRole.BUSSER)) {
				staffList.getBussers().add(venueStaffInfo);
			}
			if (venueStaffInfo.getPreferredRoles().contains(VenueRole.VIP_HOST)) {
				staffList.getHosts().add(venueStaffInfo);
			}
		}

		return ok(Json.toJson(staffList));
	}

	@ApiOperation(
			nickname = "getNextEventDate",
			value = "Get next event date",
			notes = "Get next event date of this venue",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Next date", response = DateStringValue.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No events after this date", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getNextAvailableDate(@ApiParam(required = true) Long venueId, @ApiParam(required = true) String from) {
		LocalDate nextDate;
		try {
			nextDate = EventsHelper.getClosestEventAfterDate(venueId, LocalDate.parse(from));
		} catch (Exception e) {
			Logger.warn("Can't get closest date", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (nextDate == null) {
			return notFound(Json.toJson(new ErrorMessage("No events after this date")));
		}

		return ok(Json.toJson(new DateStringValue(nextDate.toString())));
	}

	@ApiOperation(
			nickname = "getVenuePeople",
			value = "Get Venue requests and employees",
			notes = "Get Venue requests and current employees by venue id",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Requests and employees list", response = VenuePeopleList.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public  Result getPeople(Long venueId) {
		User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !RolesHelper.canViewAndManageEmployeeSection(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		List<UserVenue> userVenueList;
		try {
			userVenueList = Ebean.find(UserVenue.class)
					.fetch("user")
					.where()
					.and(Expr.eq("venue.id", venueId), Expr.ne("user.id", currentUser.getId()))
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't get UserVenue list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		VenuePeopleList venuePeopleList = new VenuePeopleList();

		if (userVenueList != null) {
			for (UserVenue userVenue : userVenueList) {
				VenueStaffInfo venueStaffInfo = getVenueStaffInfo(userVenue);
				if (VenueRequestStatus.APPROVED.equals(venueStaffInfo.getRequestStatus()) ||
						VenueRequestStatus.PREVIOUS.equals(venueStaffInfo.getRequestStatus())) {
					venuePeopleList.getApprovedPeoples().add(venueStaffInfo);
				} else if (VenueRequestStatus.REQUESTED.equals(venueStaffInfo.getRequestStatus())) {
					venuePeopleList.getPeoplesRequests().add(venueStaffInfo);
				}
			}
		}

		List<PromoterVenue> promoterVenueList;
		try {
			promoterVenueList = Ebean.find(PromoterVenue.class)
					.fetch("promoter")
					.where()
					.eq("venue.id", venueId)
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't get UserVenue list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (promoterVenueList != null) {
			for (PromoterVenue promoterVenue : promoterVenueList) {
				PromoterRequest promoterRequest = getPromoterRequest(promoterVenue);
				if (VenueRequestStatus.APPROVED.equals(promoterRequest.getRequestStatus()) ||
						VenueRequestStatus.PREVIOUS.equals(promoterRequest.getRequestStatus())) {
					venuePeopleList.getApprovedPromoters().add(promoterRequest);
				} else if (VenueRequestStatus.REQUESTED.equals(promoterRequest.getRequestStatus())) {
					venuePeopleList.getPromotersRequests().add(promoterRequest);
				}
			}
		}

		return ok(Json.toJson(venuePeopleList));
	}

	private PromoterRequest getPromoterRequest(PromoterVenue promoterVenue) {
		PromoterRequest request = new PromoterRequest(promoterVenue.getPromoter());
		request.setRequestStatus(promoterVenue.getRequestStatus());
		request.setSince(promoterVenue.getSince().toString());
		return request;
	}

	@ApiOperation(
			nickname = "getVenueRequests",
			value = "Get Venue requests",
			notes = "Get Venue requests by venue id",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Requests list", response = UserInfo[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No requests found", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getRequests(@ApiParam(value = "Venue id") Long id) {
		//TODO check for Manager's permissions for getting venue requests status
		final User user = getUserProfile().getUser();

		List<UserVenue> userVenueList;
		try {
			userVenueList = Ebean.find(UserVenue.class)
					.fetch("user")
					.where().eq("requestStatus", "REQUESTED")
					.and(Expr.eq("venue.id", id), Expr.ne("user.id", user.getId()))
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't get UserVenue list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (userVenueList == null) {
			return notFound(Json.toJson(new ErrorMessage("No requests found")));
		}

		List<VenueStaffInfo> requestList = new ArrayList<>();
		for (UserVenue userVenue : userVenueList) {
			VenueStaffInfo venueStaffInfo = getVenueStaffInfo(userVenue);
			requestList.add(venueStaffInfo);
		}

		return ok(Json.toJson(requestList));
	}

	@NotNull
	private VenueStaffInfo getVenueStaffInfo(UserVenue userVenue) {
		VenueStaffInfo venueStaffInfo = new VenueStaffInfo(userVenue.getUser());
		venueStaffInfo.setPreferredRoles(userVenue.getRoles());
		venueStaffInfo.setSince(userVenue.getSince().toString());
		venueStaffInfo.setRequestStatus(userVenue.getRequestStatus());
		return venueStaffInfo;
	}

	@ApiOperation(
			nickname = "createVenueRequest",
			value = "Create Venue request",
			notes = "Create Venue request by venue id for current user",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venue request created", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "No available roles for promotion company. " +
							"Please choose Manager, Promoter, or both", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Venue with this id is not found", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "request params", value = "Venue request info", required = true, dataType = "to.VenueRequest", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result createRequest(@ApiParam(value = "Venue id") Long id) {
		final User user = getUserProfile().getUser();

		Venue venue = Venue.finder.byId(id);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("Venue with this id is not found")));
		}

		VenueRequest venueRequest;
		try {
			venueRequest = Json.fromJson(request().body().asJson(), VenueRequest.class);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		if (VenueType.PROMOTER.equals(venue.getVenueType())) {
			final List<VenueRole> normalizedPromoterRoles = RolesHelper.normalizePromoterRoles(venueRequest.getPreferredRoles());
			venueRequest.setPreferredRoles(normalizedPromoterRoles);

			if (normalizedPromoterRoles == null || normalizedPromoterRoles.isEmpty()) {
				return badRequest(Json.toJson(new ErrorMessage("No available roles for promotion company. " +
						"Please choose Manager, Promoter, or both")));
			}
		}

		UserVenue userVenue = Ebean.find(UserVenue.class)
				.where().eq("user.id", user.getId())
				.where().eq("venue.id", id)
				.findUnique();

		if (userVenue == null) {
			userVenue = new UserVenue();
			userVenue.setVenue(venue);
			userVenue.setUser(user);
		}

		userVenue.setSince(LocalDate.now(ZoneId.of(venue.getTimeZone())));
		userVenue.setRoles(venueRequest.getPreferredRoles());
		userVenue.setRequestStatus(VenueRequestStatus.REQUESTED);

		try {
			userVenue.save();
		} catch (Exception e) {
			Logger.warn("Can't save venue request", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		List<UserVenue> managersUserVenues = UserVenue.finder
				.fetch("user")
				.where()
				.eq("venue.id", venue.getId())
				.raw("roles ?? '" + VenueRole.MANAGER.name() + "'")
				.findList();

		final UserVenue finalUserVenue = userVenue;
		if (managersUserVenues != null) managersUserVenues.forEach(managersUserVenue -> {
			final User manager = managersUserVenue.getUser();
			final String iosToken = manager.getIOSToken();
			if (StringUtils.isNotEmpty(iosToken)) {
				Notification notification = new Notification();
				notification.setVenueId(venue.getId());
				notification.setUserId(manager.getId());
				notification.setDate(LocalDate.now(ZoneId.of(venue.getTimeZone())));
				notification.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
				notification.setType(NotificationType.NEW_APPROVAL_EMPLOYEE);

				Map<String, String> data = notificationsHelper.getNewApprovalEmployeeNotificationData(finalUserVenue);
				NotificationData notificationData = new NotificationData();
				notificationData.setData(data);
				notification.setData(notificationData);

				try {
					notification.save();

					UserVenuePK pk = new UserVenuePK(manager.getId(), venue.getId());
					NotificationStats stats = NotificationStats.finder.byId(pk);
					if (stats == null) {
						stats = new NotificationStats(pk);
						stats.setUnreadEmployees(1);
						stats.save();
					} else {
						stats.setUnreadEmployees(stats.getUnreadEmployees() + 1);
						stats.update();
					}
				} catch (Exception e) {
					Logger.warn("Can't save notification, or update User's stats", e);
				}

				try {
					notificationsHelper.sendNewApprovalEmployeeNotification(notification, iosToken);
				} catch (Exception e) {
					Logger.warn("Can't send push", e);
				}
			}
		});

		return ok(Json.toJson(new ResponseMessage("Venue request created")));
	}

	@ApiOperation(
			nickname = "updateVenueRequest",
			value = "Update state of Venue request",
			notes = "Updates state of Venue requests by venue id",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Updated", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 402, message = "userId and preferredRoles should be defined in VenueRequest", response = ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue request for this user", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "request params", value = "Venue request info", required = true, dataType = "to.VenueRequest", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result updateRequest(@ApiParam(value = "Venue id", required = true) Long venueId) {
		User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !RolesHelper.canManageVenueInfo(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		final JsonNode venueRequestJson = request().body().asJson();
		if (venueRequestJson.findValue("userId") == null || venueRequestJson.findValue("preferredRoles") == null) {
			return badRequest(Json.toJson(new ErrorMessage("userId and preferredRoles should be defined in VenueRequest")));
		}

		VenueRequest venueRequest = Json.fromJson(venueRequestJson, VenueRequest.class);

		UserVenue userVenue = Ebean.find(UserVenue.class).fetch("venue")
				.where()
				.eq("user.id", venueRequest.getUserId())
				.eq("venue.id", venueId)
				.findUnique();

		if (userVenue == null) {
			return notFound(Json.toJson(new ErrorMessage("No venue request for this user")));
		}

		final Venue venue = userVenue.getVenue();
		if (VenueType.PROMOTER.equals(venue.getVenueType())) {
			final List<VenueRole> normalizedPromoterRoles = RolesHelper.normalizePromoterRoles(venueRequest.getPreferredRoles());
			venueRequest.setPreferredRoles(normalizedPromoterRoles);
		}

		userVenue.setRoles(venueRequest.getPreferredRoles());
		userVenue.setRequestStatus(VenueRequestStatus.APPROVED);
		userVenue.setSince(LocalDate.now(ZoneId.of(venue.getTimeZone())));

		try {
			userVenue.save();
		} catch (Exception e) {
			Logger.warn("Can't update Venue Request", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Updated")));
	}

	@ApiOperation(
			nickname = "getVenueStaff",
			value = "Get Venue staff",
			notes = "Get Venue staff by venue id",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Users list", response = UserInfo[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Not found", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getStaff(Long venueId) {
		final User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null || !RolesHelper.canViewAndManageEmployeeSection(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		List<UserVenue> userVenues;
		try {
			userVenues = Ebean.find(UserVenue.class)
					.fetch("user")
					.where().eq("requestStatus", VenueRequestStatus.APPROVED)
					.and(Expr.eq("venue.id", venueId), Expr.ne("user.id", currentUser.getId()))
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't get staff list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (userVenues == null) {
			return notFound(Json.toJson(new ErrorMessage("Not found")));
		}

		List<VenueStaffInfo> userInfos = new ArrayList<>();
		for (UserVenue userVenue : userVenues) {
			VenueStaffInfo userInfo = getVenueStaffInfo(userVenue);
			userInfos.add(userInfo);
		}

		return ok(Json.toJson(userInfos));
	}

	@ApiOperation(
			nickname = "getVenue",
			value = "Get Venue",
			notes = "Get Venue by id",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venue", response = Venue.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Venue with given id is not found", response = to.ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result get(Long id) {
		Venue venue = Venue.finder.byId(id);
		if (venue == null) return notFound(Json.toJson(new ErrorMessage("Venue not found")));
		return ok(Json.toJson(new VenueTO(venue)));
	}

	@ApiOperation(
			nickname = "createVenue",
			value = "Create Venue",
			notes = "Create new Venue",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venue created", response = Venue.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 406, message = "Required fields shouldn't be empty", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "venue", value = "Venue info", required = true, dataType = "models.Venue", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient", authorizerName = "admin")
	public Result create() {
		final JsonNode json = request().body().asJson();
		VenueTO venueTO;
		try {
			venueTO = Json.fromJson(json, VenueTO.class);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		if (StringUtils.isEmpty(venueTO.getName()) || StringUtils.isEmpty(venueTO.getAddress()) || venueTO.getCapacity() == 0) {
			final ErrorMessage errorMessage = new ErrorMessage("Name, address and capacity shouldn't be empty");
			if (StringUtils.isEmpty(venueTO.getName())) errorMessage.addField("name");
			if (StringUtils.isEmpty(venueTO.getAddress())) errorMessage.addField("address");
			if (venueTO.getCapacity() == 0) errorMessage.addField("capacity");
			return status(406, Json.toJson(errorMessage));
		}

		Venue venue = new Venue();
		venue.setAddress(venueTO.getAddress());
		venue.setCapacity(venueTO.getCapacity());
		venue.setCoverUrl(venueTO.getCoverUrl());
		venue.setLogoUrl(venueTO.getLogoUrl());
		venue.setName(venueTO.getName());
		venue.setPlaceId(venueTO.getFbPlaceId());
		venue.setTags(new ArrayList<>());

		try {
			venue.save();
		} catch (Exception e) {
			Logger.warn("Can't save Venue", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(venue));
	}

	@ApiOperation(
			nickname = "updateVenue",
			value = "Update Venue",
			notes = "Update Venue",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Updated", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "id must be number", response = ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Venue with given id not found", response = ErrorMessage.class),
					@ApiResponse(code = 406, message = "Required fields shouldn't be empty", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "venue", value = "New Venue info", required = true, dataType = "models.Venue", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient", authorizerName = "admin")
	public Result update() {
		final JsonNode json = request().body().asJson();
		VenueTO venueTO;
		try {
			venueTO = Json.fromJson(json, VenueTO.class);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		if (venueTO.getId() == null) {
			final ErrorMessage errorMessage = new ErrorMessage("id shouldn't be empty");
			errorMessage.addField("id");
			return status(406, Json.toJson(errorMessage));
		}

		Venue venue;
		try {
			venue = Venue.finder.byId(venueTO.getId());
		} catch (NumberFormatException e) {
			Logger.warn("Can't update Venue", e);
			return badRequest(Json.toJson(new ErrorMessage("id must be number")));
		}

		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("Venue with given id not found")));
		}

		if (StringUtils.isNotEmpty(venueTO.getName())) venue.setName(venueTO.getName());
		if (StringUtils.isNotEmpty(venueTO.getAddress())) venue.setAddress(venueTO.getAddress());
		if (StringUtils.isNotEmpty(venueTO.getCoverUrl())) venue.setCoverUrl(venueTO.getCoverUrl());
		if (StringUtils.isNotEmpty(venueTO.getLogoUrl())) venue.setLogoUrl(venueTO.getLogoUrl());
		if (venueTO.getCapacity() != null && venueTO.getCapacity() != 0) venue.setCapacity(venueTO.getCapacity());
		if ((StringUtils.isNotEmpty(venue.getPlaceId()) && StringUtils.isEmpty(venueTO.getFbPlaceId())) ||
				StringUtils.isNotEmpty(venueTO.getFbPlaceId())) {
			venue.setPlaceId(venueTO.getFbPlaceId());
		}

		try {
			venue.update();
		} catch (Exception e) {
			Logger.warn("Can't save Venue", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Updated")));
	}

	@ApiOperation(
			nickname = "deleteVenue",
			value = "Delete Venue",
			notes = "Delete Venue with given id",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Deleted", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Venue with given id not found", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient", authorizerName = "admin")
	public Result delete(Long id) {
		Venue venue = Venue.finder.byId(id);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("Venue with given id not found")));
		}

		try {
			venue.delete();
		} catch (Exception e) {
			Logger.warn("Can't delete venue", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Deleted")));
	}

	@ApiOperation(
			nickname = "venuesList",
			value = "Venues list",
			notes = "List of all Venues",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venues list", response = Venue[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result list() {
		//TODO it's definently not what we need! Refactor!
		List<Venue> venueList = Venue.finder.all();

		try {
			final User user = getUserProfile().getUser();
			List<UserVenue> userVenueList = Ebean.find(UserVenue.class)
					.fetch("user")
					.where().eq("user.id", user.getId())
					.findList();

			Map<Long, UserVenue> userVenueMap = userVenueList.stream().collect(Collectors.toMap(
					userVenue -> userVenue.getVenue().getId(),
					userVenue -> userVenue));

			Predicate<Venue> checkForVenueNotExistNorApproved = venue -> !userVenueMap.keySet().contains(venue.getId()) ||
					!userVenueMap.get(venue.getId()).getRequestStatus().equals(VenueRequestStatus.APPROVED);

			venueList = venueList.stream().parallel()
					.filter(checkForVenueNotExistNorApproved)
					.peek(venue -> Optional.ofNullable(userVenueMap.get(venue.getId()))
							.ifPresent(userVenue -> {
								venue.setRequestStatus(userVenue.getRequestStatus());
								venue.setPreferredRoles(userVenue.getRoles());
							}))
					.collect(Collectors.toList());
		} catch (Exception e) {
			Logger.warn("Can't fetch venues list", e);
			return badRequest(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(venueList));
	}

	@ApiOperation(
			nickname = "userVenuesList",
			value = "Venues list of current user",
			notes = "List of current User's Venues",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venues list", response = Venue[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result myList() {
		final User user = getUserProfile().getUser();

		List<Venue> resultList = new ArrayList<>();
		try {
			List<UserVenue> userVenueList = Ebean.find(UserVenue.class)
					.fetch("venue")
					.where().eq("requestStatus", VenueRequestStatus.APPROVED)
					.where().eq("user.id", user.getId())
					.findList();

			if (userVenueList != null && !userVenueList.isEmpty()) {
				List<Venue> venueList = userVenueList.stream()
						.peek(userVenue -> {
							userVenue.getVenue().setRequestStatus(userVenue.getRequestStatus());
							userVenue.getVenue().setPreferredRoles(userVenue.getRoles());
						})
						.map(UserVenue::getVenue)
						.collect(Collectors.toList());

				resultList.addAll(venueList);

				final List<Long> promotersIdsList = venueList.stream()
						.filter(venue -> VenueType.PROMOTER.equals(venue.getVenueType()))
						.filter(venue -> venue.getPreferredRoles() != null && venue.getPreferredRoles().contains(VenueRole.PROMOTER))
						.map(Venue::getId).collect(Collectors.toList());

				if (promotersIdsList != null && !promotersIdsList.isEmpty()) {
					final List<PromoterVenue> promoterVenues = Ebean.find(PromoterVenue.class).fetch("venue")
							.where()
							.in("promoter.id", promotersIdsList)
							.eq("requestStatus", VenueRequestStatus.APPROVED)
							.findList();

					List<Venue> promotedVenueList = promoterVenues.stream()
							.peek(promoterVenue -> {
								promoterVenue.getVenue().setRequestStatus(VenueRequestStatus.APPROVED);
								promoterVenue.getVenue().setPreferredRoles(Collections.singletonList(VenueRole.PROMOTER));
							})
							.map(PromoterVenue::getVenue)
							.collect(Collectors.toList());
					if (promotedVenueList != null && !promotedVenueList.isEmpty()) {
						promotedVenueList.forEach(venue -> {
							if (!resultList.contains(venue)) {
								resultList.add(venue);
							} {
								final int index = resultList.indexOf(venue);
								final Venue venue1 = resultList.get(index);
								final List<VenueRole> preferredRoles = venue1.getPreferredRoles();
								if (!preferredRoles.contains(VenueRole.PROMOTER)) preferredRoles.add(VenueRole.PROMOTER);
							}
						});
					}
				}
			}

			List<NotificationStats> notificationStats = NotificationStats.finder
					.where()
					.eq("pk.userId", user.getId())
					.findList();

			if (notificationStats != null) {
				Map<Long, Integer> unreadNotificationsByVenueId = new HashMap<>();
				notificationStats.forEach(stats -> {
					Integer count = stats.getUnreadAssignments() +
							stats.getUnreadEmployees() +
							stats.getUnreadReservationsApprovalRequest() +
							stats.getUnreadReservationsApproved() +
							stats.getUnreadReservationsRejected() +
							stats.getUnreadTableReleased() +
							stats.getNotifyMeReservationArrival();
					unreadNotificationsByVenueId.put(stats.getPk().getVenueId(), count);
				});

				resultList.forEach(venue -> {
					if (unreadNotificationsByVenueId.containsKey(venue.getId())) {
						venue.setUnreadNotifications(unreadNotificationsByVenueId.get(venue.getId()));
					}
				});
			}
		} catch (Exception e) {
			Logger.warn("Can't fetch venues list", e);
			return badRequest(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(resultList));
	}

	@ApiOperation(
			nickname = "discardVenueRequest",
			value = "Discard Venue request",
			notes = "Discard Venue request for current user or for user with given userId",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Successfully discarded", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "Bad value of userId: <userId>", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Venue request not found", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteRequest(@ApiParam(value = "Venue id", required = true) Long venueId,
								@ApiParam(value = "UserId of request (optional)") String userIdParam) {
		Long userId;
		if (StringUtils.isNotEmpty(userIdParam)) {
			try {
				userId = Long.valueOf(userIdParam);

				User currentUser = getUserProfile().getUser();
				if (!currentUser.isAdmin()) {
					UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
					UserVenue userVenue = UserVenue.finder.byId(key);
					if (userVenue == null || !RolesHelper.canManageVenueInfo(userVenue.getRoles())) {
						return forbidden(ErrorMessage.getJsonForbiddenMessage());
					}
				}
			} catch (NumberFormatException e) {
				Logger.warn("Bad userId in deleteRequest params: " + userIdParam, e);
				return badRequest(Json.toJson(new ErrorMessage("Bad value of userId: " + userIdParam)));
			}
		} else {
			final User user = getUserProfile().getUser();
			userId = user.getId();
		}

		UserVenue userVenue = Ebean.find(UserVenue.class)
				.where().eq("venue.id", venueId)
				.where().eq("user.id", userId)
				.findUnique();

		if (userVenue == null) {
			return notFound(Json.toJson(new ErrorMessage("Venue request not found")));
		}

		if (VenueRequestStatus.APPROVED.equals(userVenue.getRequestStatus())) {
			userVenue.setRequestStatus(VenueRequestStatus.PREVIOUS);
			try {
				userVenue.update();
			} catch (Exception e) {
				Logger.warn("Can't mark venue request as PREVIOUS user.id=" + userId + ", venue.id=" + venueId, e);
				return badRequest(ErrorMessage.getJsonInternalServerErrorMessage());
			}
		} else if (VenueRequestStatus.REQUESTED.equals(userVenue.getRequestStatus())) {
			try {
				userVenue.delete();
			} catch (Exception e) {
				Logger.warn("Can't delete venue request user.id=" + userId + ", venue.id=" + venueId, e);
				return badRequest(ErrorMessage.getJsonInternalServerErrorMessage());
			}
		}

		return ok(Json.toJson(new ResponseMessage("Successfully discarded")));
	}
}
