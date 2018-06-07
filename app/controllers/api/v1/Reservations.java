package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import io.swagger.annotations.*;
import models.*;
import modules.fb.FB;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import security.MrBlackUserProfile;
import to.*;
import to.notifications.NotificationType;
import to.venue.EODReservationInfo;
import util.*;
import util.exception.IllegalReservationDateException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by arkady on 01/03/16.
 */
@Api(value = "/api/v1/reservation", description = "Reservations management", tags = "reservations", basePath = "/api/v1/reservation")
public class Reservations extends UserProfileController<MrBlackUserProfile> {

	private FB fb;
	private ReservationsHelper reservationsHelper;
	private NotificationsHelper notificationsHelper;

	@Inject
	public Reservations(FB fb, ReservationsHelper reservationsHelper, NotificationsHelper notificationsHelper) {
		this.fb = fb;
		this.reservationsHelper = reservationsHelper;
		this.notificationsHelper = notificationsHelper;
	}

	@ApiOperation(
			nickname = "attachReservationPicture",
			value = "Attach picture to Reservation",
			notes = "Attach picture to Reservation",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Unknown reservation id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result attachImageToReservation(@ApiParam(required = true) Long reservationId,
												  @ApiParam(required = true) String url) {
		Reservation reservation = Reservation.find.byId(reservationId);
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("Unknown reservation id")));
		}

		reservation.getPhotos().add(new Picture(url));

		try {
			reservation.update();
		} catch (Exception e) {
			Logger.warn("Can't attach image to reservation", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "deleteReservationPicture",
			value = "Delete picture from reservation",
			notes = "Delete picture from reservation",
			httpMethod = "DELETE",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No pictures with this url", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteImageFromReservation(Long reservationId, @ApiParam(required = true) String url) {
		Picture picture = Picture.find.where().eq("link", url).findUnique();

		if (picture == null) {
			return notFound(Json.toJson(new ErrorMessage("No pictures with this url")));
		}

		try {
			picture.delete();
		} catch (Exception e) {
			Logger.warn("Can't delete reservation picture", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "assignStaffBatch",
			value = "Assign staff batch",
			notes = "Assign Staff to Reservation",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Unknown reservation id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "staffInfo", value = "Array of StaffAssignment", required = true, dataType = "to.StaffAssignment", allowMultiple = true, paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result assignStaffBatch(Long reservationId) {
		List<StaffAssignment> assignments;
		try {
			assignments = Arrays.asList(Json.fromJson(request().body().asJson(), StaffAssignment[].class));
		} catch (Exception e) {
			Logger.warn("Can't parse staff assignments: " + request().body().asJson().toString(), e);
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		Reservation reservation = Reservation.find.byId(reservationId);
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("Unknown reservation id")));
		}

		final List<ReservationUser> assignmentsList = new ArrayList<>();
		Ebean.beginTransaction();
		for (StaffAssignment staffAssignment : assignments) {
			final String role = staffAssignment.getRole().name();
			final Long staffAssignmentId = staffAssignment.getId();
			final UserVenue userVenue = Ebean.find(UserVenue.class).where()
					.eq("venue_id", reservation.getEventInstance().getDateVenuePk().getVenueId())
					.eq("user_id", staffAssignmentId)
					.eq("request_status", VenueRequestStatus.APPROVED.name())
					.raw("roles ?? '" + role + "'").findUnique();
			if (userVenue == null) {
				Ebean.rollbackTransaction();
				Logger.info("User has no permissons to work in this venue as " + role + ". User id: " + staffAssignmentId);
				return badRequest(Json.toJson(new ErrorMessage(staffAssignment.getName() + " has no permissons to work in this venue as " + role)));
			}

			ReservationUserPK assignmentPk = new ReservationUserPK(reservationId, staffAssignmentId, staffAssignment.getRole());
			ReservationUser assignment = new ReservationUser();
			assignment.setPk(assignmentPk);
			assignment.setReservation(reservation);
			assignment.setUser(new User(staffAssignmentId));

			try {
				assignment.save();
				assignmentsList.add(assignment);
			} catch (Exception e) {
				Ebean.rollbackTransaction();
				if (e.getMessage().contains("duplicate key value violates unique constraint")) {
					return badRequest(Json.toJson(staffAssignment.getName() + " already assigned as " +
							role + " to this reservation"));
				} else {
					Logger.warn("Can't assign staff to reservation", e);
					return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
				}
			}
		}
		Ebean.commitTransaction();

		Akka.system().scheduler().scheduleOnce(Duration.Zero(),
				() -> assignmentsList.forEach(staffAssignment -> {
					final Long userId = staffAssignment.getUser().getId();
					Reservation r = Ebean.find(Reservation.class)
							.fetch("eventInstance")
							.fetch("eventInstance.event")
							.fetch("eventInstance.venue")
							.where()
							.eq("id", staffAssignment.getReservation().getId()).findUnique();

					if (r != null) {
						final Venue venue = r.getEventInstance().getVenue();
						UserVenuePK pk = new UserVenuePK(userId, venue.getId());
						NotificationsSettings settings = NotificationsSettings.finder.byId(pk);

						if (settings == null || settings.getNotifyMeArrival()) {
							User staff = User.finder.byId(userId);
							if (staff != null && StringUtils.isNotEmpty(staff.getIOSToken())) {
								Notification notification = new Notification();

								notification.setVenueId(venue.getId());
								notification.setDate(LocalDate.now(ZoneId.of(venue.getTimeZone())));
								notification.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
								notification.setUserId(staff.getId());
								notification.setType(NotificationType.NEW_ASSIGNMENT);
								notification.setEventId(r.getEventInstance().getEvent().getId());
								notification.setEventDate(r.getEventInstance().getDate());

								final Map<String, String> data = notificationsHelper.getNewAssignmentNotificationData(r);
								NotificationData notificationData = new NotificationData();
								notificationData.setData(data);
								notification.setData(notificationData);

								try {
									notification.save();

									NotificationStats stats = NotificationStats.finder.byId(pk);
									if (stats == null) {
										stats = new NotificationStats(pk);
										stats.setUnreadAssignments(1);
										stats.save();
									} else {
										stats.setUnreadAssignments(stats.getUnreadAssignments() + 1);
										stats.update();
									}
								} catch (Exception e) {
									Logger.warn("Can't save notification, or update User's stats", e);
								}

								try {
									notificationsHelper.sendNewAssignmentNotification(notification, staff.getIOSToken());
								} catch (Exception e) {
									Logger.warn("Can't send push", e);
								}
							}
						}
					} else {
						Logger.warn("Can't find reservation with id=" + staffAssignment.getReservation().getId());
					}
				}), Akka.system().dispatcher());

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "setTags",
			value = "Set reservation tags",
			notes = "Set reservation tags",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Can't parse list of tags", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Unknown reservation id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "tags", value = "List of tags, ex. [\"tag1\",\"tag2\"]", required = true, dataType = "array", allowMultiple = true, paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result setTags(@ApiParam(required = true) Long id) {
		Reservation reservation = reservationsHelper.getReservationWithFetchedVenue(id);
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("Unknown reservation id")));
		}

		List<String> tags;
		try {
			final JsonNode json = request().body().asJson();
			tags = Arrays.asList(Json.fromJson(json, String[].class));
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Can't parse list of tags: " + request().body().toString())));
		}

		Venue venue = reservation.getEventInstance().getVenue();
		final List<String> venueTags = venue.getTags();

		List<String> resultTagsList = tags.stream().filter(venueTags::contains).collect(Collectors.toList());
		reservation.setTags(resultTagsList);

		try {
			reservation.update();
		} catch (Exception e) {
			Logger.warn("Can't update reservation with new tags", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "changeReservationState",
			value = "Changes reservation state",
			notes = "Change Reservation state",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Nothing to approve. BottleServiceType is not defined | Unknown ReservationStatus value", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "There is no reservation with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result changeReservationState(@ApiParam(required = true) Long id,
										 @ApiParam(required = true, allowableValues = "PENDING,APPROVED,REJECTED,ARRIVED,PRE_RELEASED,RELEASED,COMPLETED,NO_SHOW,CONFIRMED_COMPLETE") String newState) {
		ReservationStatus newStatus;
		try {
			newStatus = ReservationStatus.valueOf(newState);
		} catch (IllegalArgumentException e) {
			return badRequest(Json.toJson(new ErrorMessage("Unknown ReservationStatus value")));
		}

		Reservation reservation = Ebean.find(Reservation.class)
				.fetch("guest")
				.fetch("staff")
				.fetch("staff.user")
				.fetch("eventInstance")
				.fetch("eventInstance.venue")
				.where()
				.eq("id", id).findUnique();
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("There is no reservation with given id")));
		}

		if (newStatus.equals(reservation.getStatus())) {
			return ok(Json.toJson(new ResponseMessage("Already " + newStatus.name())));
		}

		final User currentUser = getUserProfile().getUser();
		Logger.info("Trying to change reservation id=" + id + " status to " + newState + ". User_id=" + currentUser.getId());
		UserVenuePK key = new UserVenuePK(currentUser.getId(), reservation.getEventInstance().getDateVenuePk().getVenueId());

		if (ReservationStatus.APPROVED.equals(newStatus)) {
			if (!currentUser.isAdmin()) {
				UserVenue userVenue = UserVenue.finder.byId(key);
				if (userVenue == null || !RolesHelper.canViewAndModifyAllReservations(userVenue.getRoles())) {
					return forbidden(ErrorMessage.getJsonForbiddenMessage());
				}
			}

			if (reservation.getBottleService() == null) {
				return badRequest(Json.toJson(new ErrorMessage("Nothing to approve. BottleServiceType is not defined")));
			}

			if (reservation.getStatus().equals(ReservationStatus.PENDING)) {
				reservationsHelper.notifyGuestBySMS(reservation);
			}
		}

		Venue venue = reservation.getEventInstance().getVenue();
		if (ReservationStatus.ARRIVED.equals(newStatus)) {
			reservation.setArrivalTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
			reservationsHelper.notifyOnArrival(reservation, venue.getName());

			final User bookedBy = reservation.getBookedBy();
			final String iosToken = bookedBy.getIOSToken();
			if (StringUtils.isNotEmpty(iosToken)) {
				Notification notification = new Notification();
				notification.setVenueId(venue.getId());
				notification.setEventId(reservation.getEventInstance().getEvent().getId());
				notification.setEventDate(reservation.getEventInstance().getDate());
				notification.setReservationId(reservation.getId());
				notification.setUserId(bookedBy.getId());
				notification.setDate(LocalDate.now(ZoneId.of(venue.getTimeZone())));
				notification.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
				notification.setType(NotificationType.NOTIFY_ME_ARRIVAL);

				Map<String, String> data = notificationsHelper.getNotifyMeArrivalNotificationData(reservation);
				NotificationData notificationData = new NotificationData();
				notificationData.setData(data);
				notification.setData(notificationData);

				try {
					notification.save();
				} catch (Exception e) {
					Logger.warn("Can't save notification", e);
				}

				try {
					notificationsHelper.sendNotifyMeArrivalNotification(notification, iosToken);
				} catch (Exception e) {
					Logger.warn("Can't send push", e);
				}

				try {
					UserVenuePK pk = new UserVenuePK(bookedBy.getId(), venue.getId());
					NotificationStats stats = NotificationStats.finder.byId(pk);
					if (stats == null) {
						stats = new NotificationStats(pk);
						stats.setNotifyMeReservationArrival(1);
						stats.save();
					} else {
						stats.setNotifyMeReservationArrival(stats.getNotifyMeReservationArrival() + 1);
						stats.update();
					}
				} catch (Exception e) {
					Logger.warn("Can't update notification stats", e);
				}
			}

			User guest = reservation.getGuest();
			if (guest != null) {
				fb.checkIn(guest, venue.getPlaceId());
			}
		}

		if (!currentUser.isAdmin() && (ReservationStatus.COMPLETED.equals(newStatus) ||
				ReservationStatus.NO_SHOW.equals(newStatus) ||
				ReservationStatus.RELEASED.equals(newStatus) ||
				ReservationStatus.PRE_RELEASED.equals(newStatus))) {

			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}

			if (!RolesHelper.canReleaseAndCompleteAllTables(userVenue.getRoles())) {
				if (RolesHelper.canReleaseAndCompleteTablesAssignedToMe(userVenue.getRoles())) {
					boolean currentUserInStaff = false;
					for (ReservationUser assignment : reservation.getStaff()) {
						if (assignment.getUser().getId().equals(currentUser.getId())) {
							currentUserInStaff = true;
							break;
						}
					}

					if (!currentUserInStaff) {
						return forbidden(ErrorMessage.getJsonForbiddenMessage());
					}

					if (ReservationStatus.RELEASED.equals(newStatus)) newStatus = ReservationStatus.PRE_RELEASED;
				} else {
					return forbidden(ErrorMessage.getJsonForbiddenMessage());
				}
			}
		}

		if (!currentUser.isAdmin() && ReservationStatus.CONFIRMED_COMPLETE.equals(newStatus)) {
			UserVenue userVenue = UserVenue.finder.byId(key);

			if (userVenue == null || !RolesHelper.canViewEODStatementAndConfirm(userVenue.getRoles())) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		if (ReservationStatus.NO_SHOW.equals(newStatus) || ReservationStatus.RELEASED.equals(newStatus) ||
				ReservationStatus.COMPLETED.equals(newStatus)) {
			if (reservation.getTable() != null) reservation.setReleasedFrom(reservation.getTable());
			reservation.setTable(null);

			if (ReservationStatus.COMPLETED.equals(newStatus)) {
				reservation.setCompletedBy(currentUser);
			}
		}

		if (ReservationStatus.CONFIRMED_COMPLETE.equals(newStatus)) {
			reservation.setConfirmedBy(currentUser);
		}

		try {
			reservation.setStatus(newStatus);
			reservation.setStatusChangeTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
			reservation.update();
		} catch (Exception e) {
			Logger.warn("Can't set reservation newStatus to " + newStatus + " for reservation with id=" + id, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		// Everything smooth. Send notifications
		if (ReservationStatus.PRE_RELEASED.equals(newStatus)) {
			List<UserVenue> managersUserVenue = UserVenue.finder
					.fetch("user")
					.where()
					.eq("venue.id", venue.getId())
					.raw("roles ?? '" + VenueRole.MANAGER.name() + "'")
					.findList();

			if (managersUserVenue != null) managersUserVenue.forEach(uv -> {
				final User manager = uv.getUser();
				final String iosToken = manager.getIOSToken();
				if (StringUtils.isNotEmpty(iosToken)) {
					Notification notification = new Notification();
					notification.setVenueId(venue.getId());
					notification.setEventId(reservation.getEventInstance().getEvent().getId());
					notification.setEventDate(reservation.getEventInstance().getDate());
					notification.setUserId(manager.getId());
					notification.setReservationId(reservation.getId());
					notification.setDate(LocalDate.now(ZoneId.of(venue.getTimeZone())));
					notification.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
					notification.setType(NotificationType.TABLE_RELEASED);

					Map<String, String> data = notificationsHelper.getTableReleasedNotificationData(reservation, currentUser.getFullName());
					NotificationData notificationData = new NotificationData();
					notificationData.setData(data);
					notification.setData(notificationData);

					try {
						notification.save();

						UserVenuePK pk = new UserVenuePK(manager.getId(), venue.getId());
						NotificationStats stats = NotificationStats.finder.byId(pk);
						if (stats == null) {
							stats = new NotificationStats(pk);
							stats.setUnreadTableReleased(1);
							stats.save();
						} else {
							stats.setUnreadTableReleased(stats.getUnreadTableReleased() + 1);
							stats.update();
						}
					} catch (Exception e) {
						Logger.warn("Can't save notification, or update User's stats", e);
					}

					try {
						notificationsHelper.sendTableReleasedNotification(notification, iosToken);
					} catch (Exception e) {
						Logger.warn("Can't send push", e);
					}
				}
			});
		}

		if (ReservationStatus.APPROVED.equals(newStatus)) {
			final User bookedBy = reservation.getBookedBy();
			final String iosToken = bookedBy.getIOSToken();
			if (StringUtils.isNotEmpty(iosToken)) {
				Notification notification = new Notification();
				notification.setVenueId(venue.getId());
				notification.setEventId(reservation.getEventInstance().getEvent().getId());
				notification.setEventDate(reservation.getEventInstance().getDate());
				notification.setReservationId(reservation.getId());
				notification.setUserId(bookedBy.getId());
				notification.setDate(LocalDate.now(ZoneId.of(venue.getTimeZone())));
				notification.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
				notification.setType(NotificationType.RESERVATION_I_CREATED_APPROVED);

				Map<String, String> data = notificationsHelper.getReservationICreatedApprovedNotificationData(reservation);
				NotificationData notificationData = new NotificationData();
				notificationData.setData(data);
				notification.setData(notificationData);

				try {
					notification.save();
				} catch (Exception e) {
					Logger.warn("Can't save notification", e);
				}

				try {
					notificationsHelper.sendReservationICreatedApprovedNotification(notification, iosToken);
				} catch (Exception e) {
					Logger.warn("Can't send push", e);
				}

				try {
					UserVenuePK pk = new UserVenuePK(bookedBy.getId(), venue.getId());
					NotificationStats stats = NotificationStats.finder.byId(pk);
					if (stats == null) {
						stats = new NotificationStats(pk);
						stats.setUnreadReservationsApproved(1);
						stats.save();
					} else {
						stats.setUnreadReservationsApproved(stats.getUnreadReservationsApproved() + 1);
						stats.update();
					}
				} catch (Exception e) {
					Logger.warn("Can't update notification stats", e);
				}
			}
		}

		if (ReservationStatus.REJECTED.equals(newStatus)) {
			final User bookedBy = reservation.getBookedBy();
			final String iosToken = bookedBy.getIOSToken();
			if (StringUtils.isNotEmpty(iosToken)) {
				Notification notification = new Notification();
				notification.setVenueId(venue.getId());
				notification.setEventId(reservation.getEventInstance().getEvent().getId());
				notification.setEventDate(reservation.getEventInstance().getDate());
				notification.setReservationId(reservation.getId());
				notification.setUserId(bookedBy.getId());
				notification.setDate(LocalDate.now(ZoneId.of(venue.getTimeZone())));
				notification.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
				notification.setType(NotificationType.RESERVATION_I_CREATED_REJECTED);

				Map<String, String> data = notificationsHelper.getReservationICreatedRejectedNotificationData(reservation);
				NotificationData notificationData = new NotificationData();
				notificationData.setData(data);
				notification.setData(notificationData);

				try {
					notification.save();
				} catch (Exception e) {
					Logger.warn("Can't save notification", e);
				}

				try {
					notificationsHelper.sendReservationICreatedRejectedNotification(notification, iosToken);
				} catch (Exception e) {
					Logger.warn("Can't send push", e);
				}

				try {
					UserVenuePK pk = new UserVenuePK(bookedBy.getId(), venue.getId());
					NotificationStats stats = NotificationStats.finder.byId(pk);
					if (stats == null) {
						stats = new NotificationStats(pk);
						stats.setUnreadReservationsRejected(1);
						stats.save();
					} else {
						stats.setUnreadReservationsRejected(stats.getUnreadReservationsRejected() + 1);
						stats.update();
					}
				} catch (Exception e) {
					Logger.warn("Can't update notification stats", e);
				}
			}
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "unassign",
			value = "Unassign table",
			notes = "Unassign table from Reservation",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Unknown reservation id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result unassignTable(Long id) {
		Reservation reservation = Reservation.find.byId(id);
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("Unknown reservation id")));
		}

		reservation.setTable(null);
		List<ReservationUser> staff = ReservationUser.finder.where().eq("reservation.id", id).findList();

		Ebean.beginTransaction();
		try {
			if (staff != null) {
				staff.stream().forEach(Model::delete);
			}
			reservation.update();
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't unassign table from reservation", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "removeStaffBatch",
			value = "Remove (Unassign) staff batch",
			notes = "Remove (Unassign) staff batch from Reservation",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Unknown 'role' value | Please check sent data | ", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Unknown reservation id | No staff assignment with given params", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "staffInfo", value = "Reservation staff info", required = true, dataType = "to.SearchUserInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result removeStaffBatch(Long reservationId) {
		List<StaffAssignment> assignments;
		try {
			assignments = Arrays.asList(Json.fromJson(request().body().asJson(), StaffAssignment[].class));
		} catch (Exception e) {
			Logger.warn("Can't parse staff assignments: " + request().body().asJson().toString(), e);
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		Ebean.beginTransaction();
		for (StaffAssignment staffAssignment : assignments) {
			final VenueRole role = staffAssignment.getRole();
			ReservationUserPK assignmentPk = new ReservationUserPK(reservationId, staffAssignment.getId(), role);
			ReservationUser assignment = ReservationUser.finder.byId(assignmentPk);

			if (assignment == null) {
				Ebean.rollbackTransaction();
				return notFound(Json.toJson(new ErrorMessage("No assignment for " + staffAssignment.getName()) + " as " + role));
			}

			try {
				assignment.delete();
			} catch (Exception e) {
				Ebean.rollbackTransaction();
				Logger.warn("Can't remove staff from reservation", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}
		}
		Ebean.commitTransaction();

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "reactivateReservation",
			value = "Reactivate reservation",
			notes = "Reactivate Reservation",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "There is no reservation with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result reactivateReservation(Long id) {
		Reservation reservation = Reservation.find.byId(id);
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("There is no reservation with given id")));
		}

		reservation.setDeleted(false);
		try {
			reservation.update();
		} catch (Exception e) {
			Logger.warn("Can't reactivate reservation with id=" + id, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}


	@ApiOperation(
			nickname = "moveToDate",
			value = "Move reservation to another date",
			notes = "Moves reservation to another date. Venue must have at least one Event at this date",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Bad date format | Reservation already closed | No events at this date", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "There is no reservation with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result moveToDate(@ApiParam(required = true) Long id, @ApiParam(required = true) String newDate,
							 @ApiParam(required = true) Long eventId) {
		Reservation reservation = reservationsHelper.getReservationWithFetchedVenue(id);
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("There is no reservation with given id")));
		}

		LocalDate date;
		try {
			date = LocalDate.parse(newDate);
		} catch (Exception e) {
			Logger.info("Attempt to move reservation with bad newDate value:" + newDate);
			return badRequest(Json.toJson(new ErrorMessage("Bad date format")));
		}

		final Venue venue = reservation.getEventInstance().getVenue();
		final String timeZone = venue.getTimeZone();
		final LocalDate now = LocalDate.now(ZoneId.of(timeZone));

		if (reservation.getEventInstance().getDate().isBefore(now)) {
			return badRequest(Json.toJson(new ErrorMessage("Reservation already closed")));
		}

		if (date.isBefore(now)) {
			return badRequest(Json.toJson(new ErrorMessage("It's Impossible to move reservation to past. Please put it back to the future.")));
		}

		EventInstance eventInstance;
		try {
			eventInstance = EventInstanceHelper.getOrCreateEventInstance(venue.getId(), date, eventId);
		} catch (IllegalReservationDateException e) {
			Logger.warn("Can't create event instance", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}
		reservation.setEventInstance(eventInstance);

		try {
			reservation.update();
		} catch (Exception e) {
			Logger.warn("Can't update reservation with new date:" + newDate, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "getReservationsForAssignment",
			value = "Get ReservationsForAssigment",
			notes = "Get list",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Reservation info list", response = to.SearchReservationInfo[].class),
					@ApiResponse(code = 400, message = "Date and venueId should be consistent", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "There are no reservations for this date", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getReservationWithBsInfoslist(@ApiParam(required = true) String venueIdString,
												@ApiParam(required = true) String dateString,
												@ApiParam(required = true, allowableValues = "TABLE,STANDUP,BAR") String bsType,
												@ApiParam(required = true) Long eventId) {
		final Long venueId;
		final LocalDate date;
		final BottleServiceType bottleServiceType;

		try {
			venueId = Long.parseLong(venueIdString);
			date = LocalDate.parse(dateString);
			bottleServiceType = BottleServiceType.valueOf(bsType);
		} catch (NumberFormatException e) {
			Logger.warn("Can't parse date(" + dateString + ") or venueId (" + venueIdString + ")", e);
			return badRequest(Json.toJson(new ErrorMessage("Date and venueId should be consistent")));
		}

		List<Reservation> reservations;
		try {
			reservations = reservationsHelper.getReservationsWithBS(venueId, date, bottleServiceType, eventId);
		} catch (Exception e) {
			Logger.warn("Can't fetch reservations list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		List<ReservationInfo> reservationInfos;
		if (reservations == null || reservations.isEmpty()) {
			reservationInfos = new ArrayList<>();
		} else {
			reservationInfos = reservations.stream()
					.filter(reservation ->
							ReservationStatus.APPROVED.equals(reservation.getStatus()) &&
							reservation.getTable() == null)
					.map(reservation -> reservationsHelper.reservationToInfo(reservation, false, false))
					.collect(Collectors.toList());
		}

		return ok(Json.toJson(reservationInfos));
	}

	@ApiOperation(
			nickname = "assignTable",
			value = "Assign table",
			notes = "Assign table to Reservation",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please check seating place info", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Unknown reservation id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "tableInfo", value = "Table info", required = true, dataType = "to.TableInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result assignTable(Long reservationId) {
		Reservation reservation = Reservation.find.byId(reservationId);
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("Unknown reservation id")));
		}

		TableInfo tableInfo = Json.fromJson(request().body().asJson(), TableInfo.class);
		if (tableInfo == null || tableInfo.getId() == null) {
			return badRequest(Json.toJson(new ErrorMessage("Please check seating place info")));
		}

		ClosedTablePk closedTablePk = new ClosedTablePk(reservation.getEventInstance().getDateVenuePk().getVenueId(),
				tableInfo.getId(), reservation.getEventInstance().getDate());

		ClosedTable closedTable = ClosedTable.find.byId(closedTablePk);
		if (closedTable != null) {
			return badRequest(Json.toJson(new ErrorMessage("This table is closed today")));
		}

		Reservation reservationOnThisTable;
		try {
			reservationOnThisTable = Reservation.find.where()
					.eq("table.id", tableInfo.getId())
					.eq("eventInstance.event.id", reservation.getEventInstance().getEvent().getId())
					.eq("eventInstance.dateVenuePk.date", reservation.getEventInstance().getDate())
					.findUnique();
		} catch (Exception e) {
			Logger.warn("Can't find table with id=" + tableInfo.getId(), e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		List<ReservationUser> staffOfOldReservation = null;
		List<ReservationUser> staffOfNewReservation;
		if (reservationOnThisTable != null) {
			reservationOnThisTable.setTable(null);
			staffOfOldReservation = ReservationUser.finder.where()
					.eq("reservation.id", reservationOnThisTable.getId())
					.findList();
		}
		reservation.setTable(new Place(tableInfo.getId()));
		staffOfNewReservation = ReservationUser.finder.where()
				.eq("reservation.id", reservation.getId())
				.findList();

		Ebean.beginTransaction();
		try {
			if (reservationOnThisTable != null) {
				if (staffOfOldReservation != null) {
					staffOfOldReservation.stream().forEach(Model::delete);
				}
				reservationOnThisTable.update();
			}

			if (staffOfNewReservation != null) {
				staffOfNewReservation.stream().forEach(Model::delete);
			}

			if (ReservationStatus.NO_SHOW.equals(reservation.getStatus()) ||
					ReservationStatus.RELEASED.equals(reservation.getStatus()) ||
					ReservationStatus.PENDING.equals(reservation.getStatus())) {
				reservation.setStatus(ReservationStatus.APPROVED);
			}

			reservation.update();
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't assign table to reservation", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "changeNumberOfSeatedGuests",
			value = "Increase or decrease number of seated guests",
			notes = "Increase or decrease number of seated guests in Reservation",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "'action' parameter is required and should be equals to 'inc' or 'dec'", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "There is no reservation with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 406, message = "Reservation should be accepted to change number of guests", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result changeNumberOfSeatedGuests(@ApiParam(required = true) Long id, @ApiParam(allowableValues = "INC,DEC", required = true) String action,
											 @ApiParam(allowableValues = "MALE,FEMALE", required = true) String gender) {
		//TODO refactor this method with ClickerUpdate
		final String INC_ACTION = "INC";
		final String DEC_ACTION = "DEC";
		final String GENDER_M = "MALE";
		final String GENDER_F = "FEMALE";
		if (StringUtils.isEmpty(action) || !INC_ACTION.equalsIgnoreCase(action) && !DEC_ACTION.equalsIgnoreCase(action)) {
			return badRequest(Json.toJson(new ErrorMessage("'action' parameter is required and should be equals to 'INC' or 'DEC'")));
		}

		if (!GENDER_F.equals(gender) && !GENDER_M.equals(gender)) {
			return badRequest(Json.toJson(new ErrorMessage("Bad gender value")));
		}

		final Reservation reservation = reservationsHelper.getReservationWithFetchedVenue(id);
		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("There is no reservation with given id")));
		}

		if (reservation.getBottleService() != null && ReservationStatus.PENDING.equals(reservation.getStatus())) {
			return status(406, Json.toJson(new ErrorMessage("Reservation with BS should be APPROVED to change number of guests")));
		}
		if (reservation.getBottleService() != null && !ReservationStatus.ARRIVED.equals(reservation.getStatus())) {
			return status(406, Json.toJson(new ErrorMessage("Reservation with BS should be ARRIVED to change number of guests")));
		}

		Ebean.beginTransaction();
		try {
			Logger.info("Changing number of seated guests (" + action + "," + gender + "): " + reservation.toString());
			Short girlsSeated = reservation.getGirlsSeated() != null ? reservation.getGirlsSeated() : 0;
			Short guysSeated = reservation.getGuysSeated() != null ? reservation.getGuysSeated() : 0;

			final EventInstance eventInstance = reservation.getEventInstance();
			final Venue venue = eventInstance.getVenue();
			final ZoneId zoneId = ZoneId.of(venue.getTimeZone());
			final LocalTime localTimeNow = LocalTime.now(zoneId);

			if (INC_ACTION.equalsIgnoreCase(action)) {
				if (reservation.getArrivalTime() == null) {
					reservation.setArrivalTime(localTimeNow);
					if (reservation.getBottleService() == null) {
						reservationsHelper.notifyOnArrival(reservation, venue.getName());
					}
				}
				if (GENDER_F.equalsIgnoreCase(gender)) {
					reservation.setGirlsSeated(++girlsSeated);
				} else if (GENDER_M.equalsIgnoreCase(gender)) {
					reservation.setGuysSeated(++guysSeated);
				}
			} else if (DEC_ACTION.equalsIgnoreCase(action)) {
				if (GENDER_F.equalsIgnoreCase(gender) && girlsSeated > 0){
					reservation.setGirlsSeated(--girlsSeated);
				} else if (GENDER_M.equalsIgnoreCase(gender) && guysSeated > 0) {
					reservation.setGuysSeated(--guysSeated);
				}
			}

			//TODO Do it only when data has been changed above
			reservation.update();

			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't update number of seated guests", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "deleteReservation",
			value = "Delete reservation",
			notes = "Delete Reservation",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "There is no reservation with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteReservation(Long id) {
		Reservation reservation = Reservation.find
				.fetch("completionFeedback")
				.where()
				.idEq(id)
				.findUnique();

		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("There is no reservation with given id")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!canModyfyReservation(reservation, currentUser)) return forbidden(ErrorMessage.getJsonForbiddenMessage());

		Ebean.beginTransaction();
		try {
			if (reservation.getCompletionFeedback() != null) {
				reservation.setCompletionFeedback(null);
				reservation.update();
			}

			reservation.delete();
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't delete reservation with id=" + id, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	private boolean canModyfyReservation(Reservation reservation, User currentUser) {
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), reservation.getEventInstance().getDateVenuePk().getVenueId());
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null) return false;

			final boolean canModifyGL = RolesHelper.canViewAndModifyAllGuestListResos(userVenue.getRoles());
			final boolean isGL = (reservation.getBottleService() == null);

			if (!(canModifyGL && isGL) && !RolesHelper.canViewAndModifyAllReservations(userVenue.getRoles())) {
				return false;
			}
		}
		return true;
	}

	@ApiOperation(
			nickname = "updateReservation",
			value = "Update Reservation",
			notes = "Update Reservation",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Reservation updated", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please select existing guest, or enter name and phone of new person", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Unknown reservation id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "reservation", value = "Reservation info", required = true, dataType = "to.ReservationInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result updateReservation() {
		EODReservationInfo reservationInfo;

		final Http.RequestBody body = request().body();
		Logger.info("Attempt to update reservation: " + request().body().asJson().toString());
		try {
			reservationInfo = Json.fromJson(body.asJson(), EODReservationInfo.class);
		} catch (Exception e) {
			return badRequest(Json.toJson(new ErrorMessage("Bad reservation format")));
		}

		Reservation reservation = Ebean.find(Reservation.class)
				.fetch("eventInstance")
				.fetch("eventInstance.venue")
				.fetch("staff")
				.fetch("staff.user")
				.where()
				.eq("id", reservationInfo.getId()).findUnique();

		if (reservation == null) {
			return notFound(Json.toJson(new ErrorMessage("Unknown reservation id")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!canModyfyReservation(reservation, currentUser)) {

			UserVenuePK key = new UserVenuePK(currentUser.getId(), reservation.getEventInstance().getDateVenuePk().getVenueId());
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null) {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}

			if (RolesHelper.canReleaseAndCompleteTablesAssignedToMe(userVenue.getRoles())) {
				boolean currentUserInStaff = false;
				for (ReservationUser assignment : reservation.getStaff()) {
					if (assignment.getUser().getId().equals(currentUser.getId())) {
						currentUserInStaff = true;
						break;
					}
				}

				if (!currentUserInStaff) {
					return forbidden(ErrorMessage.getJsonForbiddenMessage());
				}
			} else {
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		if (reservationInfo.getBookingNote() != null) reservation.setBookingNote(reservationInfo.getBookingNote());

		List<ReservationUser> removedStaff = null;
		if (reservationInfo.getBottleService() != null && !reservationInfo.getBottleService().equals(reservation.getBottleService())) {
			if (BottleServiceType.NONE.equals(reservationInfo.getBottleService())) {
				reservation.setBottleService(null);
				reservation.setStatus(null);
			} else {
				reservation.setStatus(ReservationStatus.PENDING);
				reservation.setBottleService(reservationInfo.getBottleService());
			}
			reservation.setTable(null);
			removedStaff = ReservationUser.finder.where().eq("reservation.id", reservation.getId()).findList();
		}

		if (reservation.getBottleService() != null && !ReservationStatus.COMPLETED.equals(reservationInfo.getStatus()) &&
				!ReservationStatus.CONFIRMED_COMPLETE.equals(reservationInfo.getStatus())) {
			final EventInstance eventInstance = reservation.getEventInstance();

			boolean validMinSpend = true;
			boolean validMinBolltes = true;

			if (eventInstance.getMinSpend() != null && reservationInfo.getMinSpend() != null ) {
				validMinSpend = reservationInfo.getMinSpend() >= eventInstance.getMinSpend();
			}

			if (eventInstance.getMinBottles() != null && reservationInfo.getBottleMin() != null) {
				validMinBolltes = reservationInfo.getBottleMin() >= eventInstance.getMinBottles();
			}

			if (eventInstance.getMinSpend() != null && eventInstance.getMinBottles() != null && !validMinSpend && !validMinBolltes) {
					return badRequest(Json.toJson(new ErrorMessage("Min spend value should be at least " + eventInstance.getMinSpend() +
							", or min bottles value should be " + eventInstance.getMinBottles() + " or more")));
			} else {
				if (eventInstance.getMinSpend() != null && !validMinSpend) {
					return badRequest(Json.toJson(new ErrorMessage("Min spend value should be " + eventInstance.getMinSpend() + " or more")));
				}
				if (eventInstance.getMinBottles() != null && !validMinBolltes) {
					return badRequest(Json.toJson(new ErrorMessage("Min bottles value should be " + eventInstance.getMinBottles() + " or more")));
				}
			}

			if (reservationInfo.getBottleMin() != null) {
				reservation.setBottleMin(reservationInfo.getBottleMin() != 0 ? reservationInfo.getBottleMin() : null);
			}

			if (reservationInfo.getMinSpend() != null) {
				reservation.setMinSpend(reservationInfo.getMinSpend() != 0 ? reservationInfo.getMinSpend() : null);
			}
		}

		if (reservationInfo.getComplimentGirls() != null) reservation.setComplimentGirls(reservationInfo.getComplimentGirls());
		if (reservationInfo.getComplimentGuys() != null) reservation.setComplimentGuys(reservationInfo.getComplimentGuys());
		if (reservationInfo.getComplimentGuysQty() != null) reservation.setComplimentGuysQty(reservationInfo.getComplimentGuysQty());
		if (reservationInfo.getComplimentGirlsQty() != null) reservation.setComplimentGirlsQty(reservationInfo.getComplimentGirlsQty());
		if (reservationInfo.getComplimentGroupQty() != null) reservation.setComplimentGroupQty(reservationInfo.getComplimentGroupQty());

		if (reservationInfo.getGroupType() != null) {
			if (GroupType.NONE.equals(reservationInfo.getGroupType())) {
				reservation.setGroupType(null);
			} else {
				reservation.setGroupType(reservationInfo.getGroupType());
			}
		}
		if (reservationInfo.getGuestsNumber() != null) reservation.setGuestsNumber(reservationInfo.getGuestsNumber());
		if (reservationInfo.getMustEnter() != null) reservation.setMustEnter(reservationInfo.getMustEnter());
		if (reservationInfo.getNotifyMgmtOnArrival() != null) reservation.setNotifyMgmtOnArrival(reservationInfo.getNotifyMgmtOnArrival());

		if (reservationInfo.getReducedGirls() != null) reservation.setReducedGirls(reservationInfo.getReducedGirls());
		if (reservationInfo.getReducedGuys() != null) reservation.setReducedGuys(reservationInfo.getReducedGuys());
		if (reservationInfo.getReducedGuysQty() != null) reservation.setReducedGuysQty(reservationInfo.getReducedGuysQty());
		if (reservationInfo.getReducedGirlsQty() != null) reservation.setReducedGirlsQty(reservationInfo.getReducedGirlsQty());
		if (reservationInfo.getReducedGroupQty() != null) reservation.setReducedGroupQty(reservationInfo.getReducedGroupQty());

		if (StringUtils.isNotEmpty(reservationInfo.getEstimatedArrivalTime())) {
			try {
				reservation.setEstimatedArrivalTime(LocalTime.parse(reservationInfo.getEstimatedArrivalTime()));
			} catch (Exception e) {
				return badRequest(Json.toJson(new ErrorMessage("Invalid 'Estimated arrival time'" )));
			}
		}

		if (reservationInfo.getTotalSpent() != null) reservation.setTotalSpent(reservationInfo.getTotalSpent());

		final VisitorInfo guestInfo = reservationInfo.getGuestInfo();
		if (guestInfo != null) {
			if (guestInfo.getId() != null) {
				if (!guestInfo.getId().equals(reservation.getGuest().getId())) {
					reservation.setGuest(new User(guestInfo.getId()));
				}
			} else {
				if (StringUtils.isNotEmpty(guestInfo.getPhoneNumber())) {
					final String guestPhoneNumber;
					try {
						guestPhoneNumber = PhoneHelper.normalizePhoneNumber(guestInfo.getPhoneNumber());
					} catch (NumberFormatException e) {
						return badRequest(Json.toJson(new ErrorMessage("Invalid phone number")));
					}

					if (reservation.getGuest() == null || !guestPhoneNumber.equals(reservation.getGuest().getPhoneNumber())) {
						User newUser = User.finder.where().eq("phoneNumber", guestPhoneNumber).findUnique();
						if (newUser != null) {
							reservation.setGuest(newUser);
						} else if (StringUtils.isNotEmpty(guestInfo.getFullName())) {
							newUser = new User();
							newUser.setFullName(guestInfo.getFullName());
							newUser.setPhoneNumber(guestPhoneNumber);
							try {
								newUser.save();
							} catch (Exception e) {
								Logger.warn("Can't assign existent reservation to NEW user", e);
								return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
							}
							reservation.setGuest(newUser);
						} else {
							return badRequest(Json.toJson(new ErrorMessage("Please select existing guest, or enter name and phone of new person")));
						}
					}
				} else if (StringUtils.isNotEmpty(guestInfo.getFullName())) {
					reservation.setGuestFullName(guestInfo.getFullName());
					reservation.setGuest(null);
				}
			}
		}

		if (reservationInfo.getCompletionFeedback() != null) {
			final Venue venue = reservation.getEventInstance().getVenue();
			final FeedbackInfo completionFeedback = reservationInfo.getCompletionFeedback();

			Feedback feedback = reservation.getCompletionFeedback();
			if (feedback == null) {
				feedback = new Feedback();
				feedback.setMeta(true);
				feedback.setId(completionFeedback.getId());
				feedback.setReservation(reservation);
				feedback.setUser(reservation.getGuest());
				reservation.setCompletionFeedback(feedback);
			}

			feedback.setAuthor(currentUser);
			feedback.setMessage(completionFeedback.getMessage());
			feedback.setStars(completionFeedback.getRating() != null ? completionFeedback.getRating() : 0);
			feedback.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
		}

		Ebean.beginTransaction();
		if (reservationInfo.getPayees() != null && reservationInfo.getPayees().size() > 0) {
			List<PayInfo> payInfos;
			try {
				payInfos = getPayInfos(reservationInfo.getPayees());
			} catch (NumberFormatException e) {
				return badRequest(Json.toJson(new ErrorMessage("Invalid phone number in payment info")));
			} catch (Exception e) {
				Logger.warn("Can't process pay infos", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}
			reservation.setPayees(payInfos);
		}

		try {
			if (removedStaff != null) {
				removedStaff.forEach(Model::delete);
			}
			reservation.update();
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't update reservation", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage("Reservation updated")));
	}

	private List<PayInfo> getPayInfos(List<PayInfoTO> payInfoTOList) throws NumberFormatException {
		List<PayInfo> payInfos = new ArrayList<>();

		for (PayInfoTO payInfoTO : payInfoTOList) {
			if (payInfoTO.getId() == null) {
				PayInfo payInfo = payInfoTO.toNewPayInfo();
				payInfo.save();

				if (StringUtils.isNotEmpty(payInfo.getPhoneNumber())) {
					final User user = User.finder.fetch("payInfo")
							.where()
							.eq("phoneNumber", payInfo.getPhoneNumber())
							.findUnique();
					if (user != null && (user.getPayInfo() == null ||
							!PayInfoHelper.isAnyFieldPresent(new PayInfoTO(user.getPayInfo())))) {
						user.setPayInfo(payInfo);
						user.update();
					}
				}

				payInfos.add(payInfo);
			} else {
				payInfos.add(new PayInfo(payInfoTO.getId()));
			}
		}

		return payInfos;
	}

	@ApiOperation(
			nickname = "createReservation",
			value = "Create Reservation",
			notes = "Create new Reservation",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Reservation created", response = to.ReservationInfo.class),
					@ApiResponse(code = 400, message = "No events at this date | No guest info | Guest id or at least guest name required", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 406, message = "Required fields shouldn't be empty", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "reservation", value = "Reservation info", required = true, dataType = "to.ReservationInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result createReservation(boolean approved) {
		ReservationInfo reservationInfo;
		try {
			reservationInfo = Json.fromJson(request().body().asJson(), ReservationInfo.class);
		} catch (Exception e) {
			Logger.warn("Bad createReservation request: " + request().body().asJson(), e);
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		final Long venueId = reservationInfo.getVenueId();
		if (venueId == null) {
			return badRequest(Json.toJson(new ErrorMessage("Venue id required")));
		}

		final User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
			UserVenue userVenue = UserVenue.finder.byId(key);
			if (userVenue == null) return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		if (StringUtils.isEmpty(reservationInfo.getReservationDate())) {
			return badRequest(Json.toJson(new ErrorMessage("Reservation date required")));
		}

		if (reservationInfo.getEventId() == null) {
			return badRequest(Json.toJson(new ErrorMessage("eventId required")));
		}

		final LocalDate reservationDate = LocalDate.parse(reservationInfo.getReservationDate());

		EventInstance eventInstance;
		try {
			eventInstance = EventInstanceHelper.getOrCreateEventInstance(venueId, reservationDate, reservationInfo.getEventId());
		} catch (IllegalReservationDateException e) {
			Logger.warn("Attempt to create reservation at wrong date:" + reservationDate + ", venueId:" + venueId, e);
			return badRequest(Json.toJson(new ErrorMessage("No events at this date")));
		} catch (Exception e) {
			Logger.warn("Can't get or create EventInstance", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		Reservation reservation = new Reservation();
		reservation.setToken(UUID.randomUUID());
		reservation.setGuysSeated((short) 0);
		reservation.setGirlsSeated((short) 0);
		reservation.setDeleted(false);
		reservation.setEventInstance(eventInstance);
		reservation.setBookedBy(currentUser);
		reservation.setBookingNote(reservationInfo.getBookingNote());

		reservation.setComplimentGirls(reservationInfo.getComplimentGirls());
		reservation.setComplimentGuys(reservationInfo.getComplimentGuys());
		reservation.setComplimentGuysQty(reservationInfo.getComplimentGuysQty());
		reservation.setComplimentGirlsQty(reservationInfo.getComplimentGirlsQty());
		reservation.setComplimentGroupQty(reservationInfo.getComplimentGroupQty());

		if (StringUtils.isNotEmpty(reservationInfo.getEstimatedArrivalTime())) {
			try {
				reservation.setEstimatedArrivalTime(LocalTime.parse(reservationInfo.getEstimatedArrivalTime()));
			} catch (Exception e) {
				return badRequest(Json.toJson(new ErrorMessage("Invalid 'Estimated arrival time'" )));
			}
		}

		if (!GroupType.NONE.equals(reservationInfo.getGroupType())) {
			reservation.setGroupType(reservationInfo.getGroupType());
		}

		final VisitorInfo guestInfo = reservationInfo.getGuestInfo();

		if (guestInfo == null) {
			return badRequest(Json.toJson(new ErrorMessage("No guest info")));
		}

		reservation.setGuestsNumber(reservationInfo.getGuestsNumber() != null ? reservationInfo.getGuestsNumber() : 0);
		reservation.setMustEnter(reservationInfo.getMustEnter());
		reservation.setNotifyMgmtOnArrival(reservationInfo.getNotifyMgmtOnArrival());
		reservation.setReducedGirls(reservationInfo.getReducedGirls());
		reservation.setReducedGuys(reservationInfo.getReducedGuys());
		reservation.setReducedGuysQty(reservationInfo.getReducedGuysQty());
		reservation.setReducedGirlsQty(reservationInfo.getReducedGirlsQty());
		reservation.setReducedGroupQty(reservationInfo.getReducedGroupQty());

		if (reservationInfo.getBottleService() != null && !BottleServiceType.NONE.equals(reservationInfo.getBottleService())) {
			reservation.setBottleService(reservationInfo.getBottleService());

			if (eventInstance.getBsClosed()) {
				return badRequest(Json.toJson(new ErrorMessage("Bottle service closed")));
			}

			boolean validMinSpend = true;
			if (eventInstance.getMinSpend() != null) {
				validMinSpend = reservationInfo.getMinSpend() != null && reservationInfo.getMinSpend() >= eventInstance.getMinSpend();
			}

			boolean validMinBolltes = true;
			if (eventInstance.getMinBottles() != null) {
				validMinBolltes = reservationInfo.getBottleMin() != null && reservationInfo.getBottleMin() >= eventInstance.getMinBottles();
			}

			if (eventInstance.getMinSpend() != null && eventInstance.getMinBottles() != null) {
				if (!validMinSpend && !validMinBolltes) {
					return badRequest(Json.toJson(new ErrorMessage("Min spend value should be at least " + eventInstance.getMinSpend() +
							", or min bottles value should be " + eventInstance.getMinBottles() + " or more")));
				}
			} else {
				if (eventInstance.getMinSpend() != null && validMinSpend) {
					return badRequest(Json.toJson(new ErrorMessage("Min spend value should be " + eventInstance.getMinSpend() + " or more")));
				}
				if (eventInstance.getMinBottles() != null && validMinBolltes) {
					return badRequest(Json.toJson(new ErrorMessage("Min bottles value should be " + eventInstance.getMinBottles() + " or more")));
				}
			}

			reservation.setStatus(approved ? ReservationStatus.APPROVED : ReservationStatus.PENDING);
			reservation.setMinSpend(reservationInfo.getMinSpend());
			reservation.setBottleMin(reservationInfo.getBottleMin());
		} else {
			if (eventInstance.getGlClosed()) {
				return badRequest(Json.toJson(new ErrorMessage("Guest list closed")));
			}
		}

		Ebean.beginTransaction();
		try {
			Venue venue = Venue.finder.byId(venueId);
			reservation.setCreationDate(LocalDate.now(ZoneId.of(venue.getTimeZone())));
			reservation.setCreationTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));

			if (guestInfo.getId() != null) {
				reservation.setGuest(new User(guestInfo.getId()));
			} else {
				final String guestFullName = guestInfo.getFullName();
				if (StringUtils.isNotEmpty(guestFullName)) {
					if (StringUtils.isNotEmpty(guestInfo.getPhoneNumber())) {
						String guestPhoneNumber;
						try {
							guestPhoneNumber = PhoneHelper.normalizePhoneNumber(guestInfo.getPhoneNumber());
						} catch (NumberFormatException e) {
							Logger.info("Attempt to create reservation with invalid phone number of guest: " + guestInfo.getPhoneNumber());
							return badRequest(Json.toJson(new ErrorMessage("Invalid phone number" )));
						}

						try {
							User guest = User.finder.where().eq("phoneNumber", guestPhoneNumber).findUnique();
							if (guest == null) {
								guest = new User();
								guest.setFullName(guestFullName);
								guest.setPhoneNumber(guestPhoneNumber);
								guest.save();
							}
							reservation.setGuest(guest);
						} catch (Exception e) {
							Logger.warn("Can't save guest as new user from reservation", e);
							return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
						}
					} else {
						reservation.setGuestFullName(guestFullName);
					}
				} else {
					return badRequest(Json.toJson(new ErrorMessage("Guest id or at least guest name required" )));
				}
			}

			if (reservation.getGuest() != null) {
				UserVenuePK pk = new UserVenuePK(reservation.getGuest().getId(), venueId);
				CustomerVenue customerVenue = CustomerVenue.finder.fetch("firstBookedBy").where().idEq(pk).findUnique();
				if (customerVenue == null) {
					customerVenue = new CustomerVenue(pk);
					customerVenue.setFirstBookedBy(currentUser);
					customerVenue.save();
				} else if (customerVenue.getFirstBookedBy() == null) {
					customerVenue.setFirstBookedBy(currentUser);
					customerVenue.update();
				}
			}

			reservation.save();
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't save reservation: " + reservation.toString(), e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		if (ReservationStatus.APPROVED.equals(reservation.getStatus())) {
			reservationsHelper.notifyGuestBySMS(reservation);
		} else {
			final Venue venue = eventInstance.getVenue();
			List<UserVenue> userVenue = UserVenue.finder
					.fetch("user")
					.where()
					.eq("venue.id", venue.getId())
					.raw("roles ?? '" + VenueRole.MANAGER.name() + "'")
					.findList();

			if (userVenue != null) userVenue.forEach(userVenue1 -> {
				final User user = userVenue1.getUser();
				final String iosToken = user.getIOSToken();
				if (StringUtils.isNotEmpty(iosToken)) {
					Notification notification = new Notification();
					notification.setVenueId(venue.getId());
					notification.setEventId(eventInstance.getEvent().getId());
					notification.setEventDate(reservation.getEventInstance().getDate());
					notification.setUserId(user.getId());
					notification.setReservationId(reservation.getId());
					final LocalDate localDate = LocalDate.now(ZoneId.of(venue.getTimeZone()));
					notification.setDate(localDate);
					notification.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
					notification.setType(NotificationType.NEW_APPROVAL_REQUEST_BS);

					Map<String, String> data = notificationsHelper.getNewApprovalRequestBSNotificationData(reservation, localDate);
					NotificationData notificationData = new NotificationData();
					notificationData.setData(data);
					notification.setData(notificationData);

					try {
						notification.save();

						UserVenuePK pk = new UserVenuePK(user.getId(), venue.getId());
						NotificationStats stats = NotificationStats.finder.byId(pk);
						if (stats == null) {
							stats = new NotificationStats(pk);
							stats.setUnreadReservationsApprovalRequest(1);
							stats.save();
						} else {
							stats.setUnreadReservationsApprovalRequest(stats.getUnreadReservationsApproved() + 1);
							stats.update();
						}
					} catch (Exception e) {
						Logger.warn("Can't save notification, or update User's stats", e);
					}

					try {
						notificationsHelper.sendNewApprovalRequestBSNotification(reservation, notification, iosToken);
					} catch (Exception e) {
						Logger.warn("Can't send push", e);
					}
				}
			});
		}

		return ok(Json.toJson(reservationsHelper.reservationToInfo(reservation, false)));
	}

	@ApiOperation(
			nickname = "getReservation",
			value = "Get Reservation",
			notes = "Get detailed Reservation info",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Reservation info", response = to.ReservationInfo.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "There is no reservation with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getReservation(@ApiParam(required = true) Long id) {
		ReservationInfo reservationInfo;
		try {
			Reservation reservation = Ebean.find(Reservation.class)
					.fetch("staff").fetch("staff.user")
					.where()
					.eq("id", id).findUnique();
			if (reservation == null) {
				return notFound(Json.toJson(new ErrorMessage("There is no reservation with given id")));
			}

			final User currentUser = getUserProfile().getUser();
			if (!currentUser.isAdmin()) {
				UserVenuePK key = new UserVenuePK(currentUser.getId(), reservation.getEventInstance().getDateVenuePk().getVenueId());
				UserVenue userVenue = UserVenue.finder.byId(key);
				if (userVenue == null) return forbidden(ErrorMessage.getJsonForbiddenMessage());

				if (!RolesHelper.canViewAndModifyAllReservations(userVenue.getRoles()) &&
						!RolesHelper.canOnlyViewAllResos(userVenue.getRoles())) {

					final boolean canViewGL = RolesHelper.canViewAndModifyAllGuestListResos(userVenue.getRoles());
					final boolean isGL = (reservation.getBottleService() == null);

					if (!(canViewGL && isGL) && !reservation.getBookedBy().getId().equals(currentUser.getId())) {
						return forbidden(ErrorMessage.getJsonForbiddenMessage());
					}
				}
			}

			reservationInfo = reservationsHelper.reservationToInfo(reservation, true);
		} catch (Exception e) {
			Logger.warn("Can't fetch reservation info", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}
		return ok(Json.toJson(reservationInfo));
	}

	@ApiOperation(
			nickname = "getReservations",
			value = "Get Reservations",
			notes = "Get Reservations list",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Reservation list", response = to.ReservationsLists.class),
					@ApiResponse(code = 400, message = "Date and venueId should be consistent", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	@Transactional
	public Result list(@ApiParam(required = true) String venueIdString,
					   @ApiParam(required = true) String dateString,
					   @ApiParam(required = true) Long eventId) {
		Long venueId;
		LocalDate date;

		try {
			venueId = Long.parseLong(venueIdString);
			date = LocalDate.parse(dateString);
		} catch (NumberFormatException e) {
			Logger.warn("Can't parse date(" + dateString + ") or venueId (" + venueIdString + ")", e);
			return badRequest(Json.toJson(new ErrorMessage("Date and venueId should be consistent")));
		}

		final User currentUser = getUserProfile().getUser();
		UserVenuePK key = new UserVenuePK(currentUser.getId(), venueId);
		UserVenue userVenue = UserVenue.finder.byId(key);

		if (!currentUser.isAdmin() && userVenue == null) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		}

		List<Reservation> reservations;
		try {
			final ExpressionList<Reservation> where = Ebean.find(Reservation.class).fetch("staff").fetch("staff.user")
					.fetch("guest").where();

			if (!currentUser.isAdmin()) {
				if (!RolesHelper.canViewAndModifyAllReservations(userVenue.getRoles()) &&
						!RolesHelper.canOnlyViewAllResos(userVenue.getRoles())) {
					if (RolesHelper.canViewAndModifyAllGuestListResos(userVenue.getRoles())) {
						where.or(Expr.eq("bookedBy.id", currentUser.getId()), Expr.isNull("bottleService"));
					} else {
						where.eq("bookedBy.id", currentUser.getId());
					}
				}
			}

			reservations = where
					.eq("venue_id", venueId)
					.eq("event_id", eventId)
					.eq("date", date)
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't fetch reservations list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		ReservationsLists reservationList = new ReservationsLists();

		for (Reservation reservation : reservations) {
			final ReservationInfo reservationInfo = reservationsHelper.reservationToInfo(reservation, false);
			if (reservationsHelper.isReservationInApprovedList(reservation))
				reservationList.getApproved().add(reservationInfo);
			else if (ReservationStatus.PENDING.equals(reservation.getStatus()) ||
					ReservationStatus.REJECTED.equals(reservation.getStatus()))
				reservationList.getPending().add(reservationInfo);
			else reservationList.getGuestlist().add(reservationInfo);
		}

		return ok(Json.toJson(reservationList));
	}
}
