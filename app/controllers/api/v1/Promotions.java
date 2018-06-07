package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import io.swagger.annotations.*;
import models.User;
import models.UserVenue;
import models.VenueRequestStatus;
import modules.fb.FB;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import to.ErrorMessage;
import to.FbUserInfo;
import to.Promotion;
import to.ResponseMessage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by arkady on 05/04/16.
 */
@Api(value = "/api/v1/promotions", description = "Promotions", tags = "promotions", basePath = "/api/v1/promotions")
public class Promotions extends UserProfileController {

	private FB fb;

	@Inject
	public Promotions(FB fb) {
		this.fb = fb;
	}

	@ApiOperation(
			nickname = "getFacebookUsersOfVenue",
			value = "Get facebook users of Venue",
			notes = "Create Get facebook users of Venue, by venue id",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Facebook users list", response = to.FbUserInfo[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getFacebookUsers(Long venueId) {
		List<UserVenue> userVenueList;

		try {
			userVenueList = Ebean.find(UserVenue.class)
					.fetch("user")
					.fetch("user.facebookInfo")
					.where()
					.isNotNull("user.facebookInfo").and(
						Expr.eq("venue.id", venueId),
						Expr.eq("requestStatus", VenueRequestStatus.APPROVED)
					).findList();
		} catch (Exception e) {
			Logger.warn("Can't get UserVenue list for promotion", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		List<FbUserInfo> fbUserInfos;
		try {
			fbUserInfos = userVenueList.stream().map(
					userVenue -> {
						final Short friendsNumber = userVenue.getUser().getFacebookInfo().getFriendsNumber();
						final int numberOfFriends = friendsNumber != null ? friendsNumber.intValue() : 0;
						return new FbUserInfo(
								userVenue.getUser().getId(),
								userVenue.getUser().getFullName(),
								userVenue.getUser().getUserpic(),
								numberOfFriends);
					}
			).collect(Collectors.toList());
		} catch (Exception e) {
			Logger.warn("Can't get UserVenue list for promotion", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}


		return ok(Json.toJson(fbUserInfos));
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
					@ApiResponse(code = 400, message = "Can't get Promotion from request", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "promotion", value = "Promotion", required = true, dataType = "to.Promotion", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result postPromotion() {
		final JsonNode json = request().body().asJson();
		Promotion promotion;
		try {
			promotion = Json.fromJson(json, Promotion.class);
		} catch (Exception e) {
			Logger.warn("Can't get Promotion from request", e);
			return badRequest(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		Akka.system().scheduler().scheduleOnce(Duration.Zero(),
				() -> {
					final List<User> users = Ebean.find(User.class).fetch("facebookInfo").where()
							.and(Expr.isNotNull("facebookInfo"), Expr.isNotNull("facebookInfo.token"))
							.eq("postOnMyBehalf", Boolean.TRUE)
							.in("id", promotion.getUserIds()).findList();
					if (users != null && !users.isEmpty()) {
						final String message = promotion.getMessage() + "\n" + promotion.getUrl();
						users.stream().forEach(user -> fb.postOnTheWall(user, message));
					} else {
						Logger.info("No users found for sharing Promotion");
					}
				}, Akka.system().dispatcher());
		return ok(Json.toJson(new ResponseMessage()));
	}
}
