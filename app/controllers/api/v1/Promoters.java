package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import io.swagger.annotations.*;
import models.*;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.ErrorMessage;
import to.PromoterRequest;
import util.RolesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by arkady on 09/08/16.
 */
@Api(value = "/api/v1/promoter", description = "Promoters management", tags = "promoters", basePath = "/api/v1/promoter")
public class Promoters extends UserProfileController<MrBlackUserProfile> {

	@ApiOperation(
			nickname = "notAssociatedVenues",
			value = "Venues list for associating with Promoter",
			notes = "List of Venues for associating with promoter",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venues list", response = PromoterRequest[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 403, message = "Your role in this venue doesn't permit this action, consult your manager", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result notAssociatedVenues(@ApiParam(required = true) Long promoterId) {
		final User currentUser = getUserProfile().getUser();
		if (!canViewVenuesLists(promoterId, currentUser)) return forbidden(ErrorMessage.getJsonForbiddenMessage());

		List<PromoterVenue> associatedAndRequestedVenuesList;
		try {
			final List<VenueRequestStatus> requestedStatuses = Arrays.asList(
					VenueRequestStatus.APPROVED,
					VenueRequestStatus.REQUESTED
			);

			associatedAndRequestedVenuesList = Ebean.find(PromoterVenue.class)
					.fetch("venue")
					.where()
					.eq("promoter.id", promoterId)
					.in("requestStatus", requestedStatuses)
					.ne("venue.venueType", VenueType.PROMOTER)
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't get assiciated venues for Promoter", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		List<Long> associatedVenueIds = new ArrayList<>();
		if (associatedAndRequestedVenuesList != null) {
			final List<Long> venueIds = associatedAndRequestedVenuesList.stream()
					.map(promoterVenue -> promoterVenue.getVenue().getId())
					.collect(Collectors.toList());
			associatedVenueIds.addAll(venueIds);
		}

		List<Venue> notAssociatedVenues = Venue.finder
				.where()
				.ne("venueType", VenueType.PROMOTER)
				.not(Expr.in("id", associatedVenueIds))
				.findList();

		List<PromoterRequest> requestList = new ArrayList<>();
		if (notAssociatedVenues != null) {
			final List<PromoterRequest> promoterRequests = notAssociatedVenues
					.stream()
					.map(PromoterRequest::new)
					.collect(Collectors.toList());
			requestList.addAll(promoterRequests);
		}

		if (associatedAndRequestedVenuesList != null) {
			final List<PromoterRequest> requestedVenues = associatedAndRequestedVenuesList.stream()
					.filter(promoterVenue -> VenueRequestStatus.REQUESTED.equals(promoterVenue.getRequestStatus()))
					.map(PromoterRequest::new)
					.collect(Collectors.toList());
			requestList.addAll(requestedVenues);
		}

		return ok(Json.toJson(requestList));
	}

	@ApiOperation(
			nickname = "associatedVenues",
			value = "Associated Venues list of Promoter",
			notes = "Associated list of Venues with promoter",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Venues list", response = PromoterRequest[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 403, message = "Your role in this venue doesn't permit this action, consult your manager", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result associatedVenues(@ApiParam(required = true) Long promoterId) {
		final User currentUser = getUserProfile().getUser();
		if (!canViewVenuesLists(promoterId, currentUser)) return forbidden(ErrorMessage.getJsonForbiddenMessage());

		List<PromoterVenue> promoterVenueList;
		try {
			promoterVenueList = Ebean.find(PromoterVenue.class)
					.fetch("venue")
					.where()
					.eq("promoter.id", promoterId)
					.eq("requestStatus", VenueRequestStatus.APPROVED.name())
					.findList();
		} catch (Exception e) {
			Logger.warn("Can't get assiciated venues for Promoter", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		List<PromoterRequest> associatedVenues = new ArrayList<>();
		if (promoterVenueList != null) {
			final List<PromoterRequest> venueList = promoterVenueList.stream()
					.map(PromoterRequest::new)
					.collect(Collectors.toList());
			associatedVenues.addAll(venueList);
		}

		return ok(Json.toJson(associatedVenues));
	}

	private boolean canViewVenuesLists(Long promoterId, User currentUser) {
		if (currentUser.isAdmin()) return true;

		UserVenuePK key = new UserVenuePK(currentUser.getId(), promoterId);
		UserVenue userVenue = UserVenue.finder.byId(key);
		return userVenue != null && RolesHelper.canManageVenueInfo(userVenue.getRoles());
	}
}
