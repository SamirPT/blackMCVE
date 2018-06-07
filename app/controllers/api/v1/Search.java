package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import io.swagger.annotations.*;
import models.Reservation;
import models.User;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.ErrorMessage;
import to.SearchUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 17/03/16.
 */
@Api(value = "/api/v1/search", description = "Search anything here", tags = "search", basePath = "/api/v1/search")
public class Search extends UserProfileController<MrBlackUserProfile> {

	@ApiOperation(
			nickname = "searchUsers",
			value = "Search Users",
			notes = "Search Users by name or phone parts",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Short UserInfo list", response = to.SearchUserInfo.class),
					@ApiResponse(code = 400, message = "'namePart' or 'phonePart' should be defined in query params", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result findUsers(String namePart, String phonePart, @ApiParam(required = true) String venueIdparam) {
		final User user = getUserProfile().getUser();
		Long venueId = null;
		if (StringUtils.isNotEmpty(venueIdparam)) {
			try {
				venueId = Long.valueOf(venueIdparam);
			} catch (NumberFormatException e) {
				return badRequest(Json.toJson(new ErrorMessage("Bad 'venueId' value")));
			}
		}
		if (StringUtils.isEmpty(namePart) && StringUtils.isEmpty(phonePart)){
			return badRequest(Json.toJson(new ErrorMessage("'namePart' or 'phonePart' should be defined in query params")));
		}

		List<Reservation> reservationList;
		try {
			final ExpressionList<Reservation> expressionList = Ebean.find(Reservation.class).where()
					.isNotNull("guest_id").eq("booked_by_id", user.getId());
			if (venueId != null) {
				expressionList.eq("venue_id", venueId);
			}
			if (StringUtils.isNotEmpty(namePart)) {
				expressionList.icontains("guest.fullName", "%" + namePart + "%" );
			}
			if (StringUtils.isNotEmpty(phonePart)) {
				expressionList.icontains("guest.phoneNumber", "%" + phonePart + "%" );
			}
			reservationList = expressionList.findList();
		} catch (Exception e) {
			Logger.warn("Something wrong when searching user.. namePart=" + namePart + ", phonePart=" + phonePart, e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		List<SearchUserInfo> userInfos = new ArrayList<>();

		reservationList.stream().map(Reservation::getGuest).distinct().forEach(guest -> {
			SearchUserInfo userInfo = new SearchUserInfo();
			userInfo.setId(guest.getId());
			userInfo.setName(guest.getFullName());
			userInfo.setPhone(guest.getPhoneNumber());
			userInfos.add(userInfo);
		});

		return ok(Json.toJson(userInfos));
	}
}
