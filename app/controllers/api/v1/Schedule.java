package controllers.api.v1;

import com.avaje.ebean.annotation.Transactional;
import com.google.inject.Inject;
import io.swagger.annotations.*;
import models.*;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.*;
import util.ClickerHelper;
import util.EventsHelper;
import util.ReservationsHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by arkady on 04/04/16.
 */
@Api(value = "/api/v1/schedule", description = "User's schedule", tags = "schedule", basePath = "/api/v1/schedule")
public class Schedule extends UserProfileController<MrBlackUserProfile> {

	private ReservationsHelper reservationsHelper;

	@Inject
	public Schedule(ReservationsHelper reservationsHelper) {
		this.reservationsHelper = reservationsHelper;
	}

	@ApiOperation(
			nickname = "getUserSchedule",
			value = "Get current user's schedule",
			notes = "Get current user's schedule",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "User's schedule", response = ScheduleInfo.class),
					@ApiResponse(code = 400, message = "Bad date value", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue with given id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	@Transactional
	public Result my(@ApiParam(required = true) Long venueId) {
		final User currentUser = getUserProfile().getUser();
		final Venue venue = Venue.finder.byId(venueId);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("No venue with given id")));
		}

		final LocalDate currentWorkingDay = ClickerHelper.getCurrentWorkingDay(venue);
		LocalDate closestDateForSchedule;
		try {
			closestDateForSchedule = getClosestDateForSchedule(venue.getId(), currentUser.getId(),
					currentWorkingDay);
		} catch (Exception e) {
			Logger.warn("Can't get closestDateForSchedule", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		ScheduleInfo schedule = new ScheduleInfo();

		if (closestDateForSchedule != null) {
			schedule.setDate(closestDateForSchedule.toString());

			List<ReservationUser> assignments;
			List<Event> eventsList;
			try {
				assignments = getUserAssignments(venue.getId(), currentUser.getId(), closestDateForSchedule);
				eventsList = EventsHelper.getEventListByDate(venueId, closestDateForSchedule);
			} catch (Exception e) {
				Logger.warn("Can't get reservation or events for user's schedule", e);
				return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
			}

			List<ReservationInfo> reservationInfoList = new ArrayList<>();
			if (assignments != null) {
				for (ReservationUser assignment : assignments) {
					ReservationInfo reservationInfo = reservationsHelper.reservationToInfo(assignment.getReservation(), true);
					reservationInfoList.add(reservationInfo);
				}
			}
			schedule.setReservations(reservationInfoList);

			List<EventInfo> eventInfos = new ArrayList<>();
			if (eventsList != null) {
				for (Event e : eventsList) {
					EventInfo eventInfo = new EventInfo(e);
					eventInfos.add(eventInfo);
				}
			}
			schedule.setEventInfos(eventInfos);
		} else {
			schedule.setDate(currentWorkingDay.toString());
		}

		return ok(Json.toJson(schedule));
	}

	static List<ReservationUser> getUserAssignments(Long venueId, Long currentUserId, LocalDate date) {
		List<ReservationUser> assignments = ReservationUser.finder
				.fetch("reservation")
				.fetch("reservation.guest")
				.fetch("reservation.guest.payInfo")
				.fetch("reservation.photos")
				.fetch("reservation.staff")
				.fetch("reservation.staff.user")
				.where()
				.eq("reservation.eventInstance.dateVenuePk.date", date)
				.eq("reservation.eventInstance.venue.id", venueId)
				.eq("reservation.staff.user.id", currentUserId)
				.findList();

		if (assignments != null && !assignments.isEmpty()) {
			Map<Long, ReservationUser> distinctResos = new HashMap<>();
			assignments.forEach(reservationUser -> {
				if (!distinctResos.containsKey(reservationUser.getReservation().getId())) {
					distinctResos.put(reservationUser.getReservation().getId(), reservationUser);
				}
			});

			assignments = new ArrayList<>(distinctResos.values());
		}

		return assignments;
	}

	private LocalDate getClosestDateForSchedule(Long venueId, Long currentUserId, LocalDate fromDate) {
		final List<ReservationUser> list = ReservationUser.finder
				.fetch("reservation")
				.where()
				.ge("reservation.eventInstance.dateVenuePk.date", fromDate)
				.eq("reservation.eventInstance.venue.id", venueId)
				.eq("reservation.staff.user.id", currentUserId)
				.orderBy("reservation.eventInstance.dateVenuePk.date asc")
				.findPagedList(0, 1).getList();

		ReservationUser closestReservation = null;
		if (list != null && !list.isEmpty()) {
			closestReservation = list.get(0);
		}

		return closestReservation != null ? closestReservation.getReservation().getEventInstance().getDate() : null;
	}
}
