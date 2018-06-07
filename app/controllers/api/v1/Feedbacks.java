package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import io.swagger.annotations.*;
import models.*;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.*;
import util.ReservationsHelper;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by arkady on 24/03/16.
 */
@Api(value = "/api/v1/feedbacks", description = "Feedbacks", tags = "feedbacks", basePath = "/api/v1/feedbacks")
public class Feedbacks extends UserProfileController<MrBlackUserProfile> {

	private ReservationsHelper reservationsHelper;

	@Inject
	public Feedbacks(ReservationsHelper reservationsHelper) {
		this.reservationsHelper = reservationsHelper;
	}

	@ApiOperation(
			nickname = "createFeedback",
			value = "Create new feedback",
			notes = "Create new feedback",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please check parameters of reservations", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "feedback", value = "Feedback info", required = true, dataType = "to.FeedbackInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result createFeedback() {
		JsonNode feedbackJson = request().body().asJson();
		FeedbackInfo feedbackInfo = Json.fromJson(feedbackJson, FeedbackInfo.class);

		final FeedbackReservationInfo shortReservationInfo = feedbackInfo.getShortReservationInfo();
		if ((StringUtils.isEmpty(feedbackInfo.getMessage()) && feedbackInfo.getRating() == null )
				|| shortReservationInfo == null
				|| shortReservationInfo.getReservationId() == null) {
			return badRequest(Json.toJson(new ErrorMessage("Please check parameters of reservations")));
		}

		final User currentUser = getUserProfile().getUser();

		Feedback feedback = new Feedback();
		feedback.setAuthor(currentUser);
		feedback.setMessage(feedbackInfo.getMessage());

		final Long reservationId = feedbackInfo.getShortReservationInfo().getReservationId();
		Reservation reservation = reservationsHelper.getReservationWithFetchedVenue(reservationId);
		if (reservation == null) {
			Logger.warn("Can't create feedback for reservation with given id" + reservationId);
			return badRequest("Can't find reservation with given id" + reservationId);
		}

		List<String> tags = feedbackInfo.getTags();
		final Venue venue = reservation.getEventInstance().getVenue();
		if (tags != null && tags.size() > 0) {
			final List<String> venueTags = venue.getTags();
			List<String> resultTagsList = tags.stream().filter(venueTags::contains).collect(Collectors.toList());
			feedback.setTags(resultTagsList);

			final List<String> reservationTags = reservation.getTags();
			List<String> reservationNewTags = resultTagsList.stream().filter(
					s -> !reservationTags.contains(s)
			).collect(Collectors.toList());

			if (reservationNewTags != null && reservationNewTags.size() > 0) {
				reservationTags.addAll(reservationNewTags);
				reservation.setTags(reservationTags);
				try {
					reservation.update();
				} catch (Exception e) {
					Logger.warn("Can't put new tags into Reservation", e);
				}
			}
		}

		feedback.setReservation(reservation);

		if (reservation.getBottleService() != null) {
			final List<ReservationUser> reservationStaff = ReservationUser.finder.fetch("user").where()
					.eq("reservation.id", reservationId)
					.findList();
			StringBuilder stringBuilder = new StringBuilder();
			if (reservationStaff != null) {
				for (ReservationUser staff : reservationStaff) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}
					stringBuilder.append(staff.getUser().getFullName());

					if (staff.getUser().getId().equals(currentUser.getId())) {
						feedback.setAuthorRole(staff.getPk().getRole());
					}
				}
			}

			feedback.setStaffAssignment(stringBuilder.toString());
		}

		feedback.setStars(feedbackInfo.getRating());
		feedback.setTime(LocalTime.now(ZoneId.of(venue.getTimeZone())));
		feedback.setUser(reservation.getGuest());

		if (feedback.getAuthorRole() == null) {
			UserVenue userVenue = Ebean.find(UserVenue.class).where().and(
					Expr.eq("pk.userId", currentUser.getId()),
					Expr.eq("pk.venueId", venue.getId())
			).findUnique();

			if (userVenue != null && userVenue.getRoles().size() > 0) {
				feedback.setAuthorRole(userVenue.getRoles().get(0));
			} else {
				Logger.info("Strange attempt to leave feedback by user id=" + currentUser.getId() +
						", into reservation id=" + reservation.getId());
			}
		}

		try {
			feedback.save();
		} catch (Exception e) {
			Logger.warn("Can't create feedback", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "deleteFeedback",
			value = "Delete feedback",
			notes = "Delete feedback",
			httpMethod = "DELETE",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteFeedback(Long id) {
		Feedback feedback = Feedback.find.byId(id);
		try {
			feedback.delete();
		} catch (Exception e) {
			Logger.warn("Can't delete feedback", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}
		return ok(Json.toJson(new ErrorMessage("Ok")));
	}

	@ApiOperation(
			nickname = "updateFeedback",
			value = "Update feedback",
			notes = "Update feedback",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "Feedback with given id not found", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "feedback", value = "Feedback info", required = true, dataType = "to.FeedbackInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result updateFeedback() {
		JsonNode feedbackJson = request().body().asJson();
		FeedbackInfo feedbackInfo = Json.fromJson(feedbackJson, FeedbackInfo.class);
		Feedback feedback = Ebean.find(Feedback.class)
				.fetch("reservation")
				.fetch("reservation.eventInstance")
				.fetch("reservation.eventInstance.venue")
				.where().eq("id", feedbackInfo.getId()).findUnique();

		if (feedback == null) {
			return notFound(Json.toJson(new ErrorMessage("Feedback with given id not found")));
		}

		if (StringUtils.isNotEmpty(feedbackInfo.getMessage())) feedback.setMessage(feedbackInfo.getMessage());
		if (feedbackInfo.getRating() != null) feedback.setStars(feedbackInfo.getRating());

		List<String> tags = feedbackInfo.getTags();
		if (tags != null && tags.size() > 0) {
			Reservation reservation = feedback.getReservation();
			Venue venue = reservation.getEventInstance().getVenue();
			final List<String> venueTags = venue.getTags();
			List<String> resultTagsList = tags.stream().filter(venueTags::contains).collect(Collectors.toList());
			feedback.setTags(resultTagsList);

			final List<String> reservationTags = reservation.getTags();
			List<String> reservationNewTags = resultTagsList.stream().filter(
					s -> !reservationTags.contains(s)
			).collect(Collectors.toList());

			if (reservationNewTags != null && reservationNewTags.size() > 0) {
				reservationTags.addAll(reservationNewTags);
				reservation.setTags(reservationTags);
				try {
					reservation.update();
				} catch (Exception e) {
					Logger.warn("Can't put new tags into Reservation", e);
				}
			}
		}

		try {
			feedback.update();
		} catch (Exception e) {
			Logger.warn("Can't update feedback", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getFeedbacks",
			value = "Get feedbacks of reservation",
			notes = "Get feedbacks of reservation by reservation id. Should be defined only one of parameters: reservationId or userId",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Feedbacks list", response = to.FeedbacksList.class),
					@ApiResponse(code = 400, message = "'reservationId' or 'userId' should be defined", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getFeedbacks(String reservationId, String userId) {
		if (StringUtils.isEmpty(reservationId) && StringUtils.isEmpty(userId)) {
			return badRequest(ErrorMessage.getJsonErrorMessage("'reservationId' or 'userId' should be defined"));
		}

		List<Feedback> feedbackList;
		try {
			final ExpressionList<Feedback> feedbackExpressionList = Feedback.find.where().eq("meta", false);

			if (StringUtils.isNotEmpty(reservationId)) {
				feedbackExpressionList.eq("reservation.id", Long.valueOf(reservationId));
			} else {
				feedbackExpressionList.eq("user.id", Long.valueOf(userId));
			}

			feedbackList = feedbackExpressionList.findList();
		} catch (Exception e) {
			Logger.warn("Can't get feedbacks for reservation=" + reservationId + ", user=" + userId, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		//TODO remove proxy class FeedbacksList and move it into simple List<FeedbackInfo>
		FeedbacksList feedbacks = new FeedbacksList();

		if (feedbackList != null) {
			for (Feedback feedback : feedbackList) {
				FeedbackInfo feedbackInfo = new FeedbackInfo(feedback);
				feedbacks.getFeedbackList().add(feedbackInfo);
			}
		}

		return ok(Json.toJson(feedbacks));
	}

}
