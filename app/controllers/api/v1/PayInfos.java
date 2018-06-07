package controllers.api.v1;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.*;
import models.PayInfo;
import models.User;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.ErrorMessage;
import to.PayInfoTO;
import to.ResponseMessage;
import util.PhoneHelper;

/**
 * Created by arkady on 01/08/16.
 */
@Api(value = "/api/v1/payinfo", description = "Operations with User's Pay Info", tags = "payinfo", basePath = "/api/v1/payinfo")
public class PayInfos extends UserProfileController<MrBlackUserProfile> {

	@ApiOperation(
			nickname = "updatePayInfo",
			value = "Update PayInfo",
			notes = "Update existing PayInfo",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please check sent data", response = ErrorMessage.class),
					@ApiResponse(code = 404, message = "No PayInfo with given id", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "payInfo", value = "Updated PayInfo", required = true, dataType = "to.PayInfoTO", paramType = "body")
	})
	public Result update() {
		return play.mvc.Results.TODO;
	}

	@ApiOperation(
			nickname = "createPayInfo",
			value = "Create PayInfo",
			notes = "Create new PayInfo and attach it to existing user by user's id or phone number." +
					"Required fileds: firstName, lastName, address, city, country, postalCode, dateOfBirth",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "Please check sent data | Please fill all required fileds |" +
							"User with this phone number already has payment info", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "payInfo", value = "New PayInfo", required = true, dataType = "to.PayInfoTO", paramType = "body")
	})
	public Result create() {
		JsonNode userJson = request().body().asJson();
		PayInfoTO payInfoTO;
		try {
			payInfoTO = Json.fromJson(userJson, PayInfoTO.class);
		} catch (Exception e) {
			Logger.warn("Bad request to create new PayInfo", e);
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		PayInfo payInfo = payInfoTO.toNewPayInfo();
		if (payInfo.getUser() == null && StringUtils.isNotEmpty(payInfo.getPhoneNumber())) {
			final String normalizedPhoneNumber = PhoneHelper.normalizePhoneNumber(payInfo.getPhoneNumber());
			User user = User.finder.fetch("payInfo").where().eq("phoneNumber", normalizedPhoneNumber).findUnique();
			if (user != null && user.getPayInfo() == null) {
				payInfo.setUser(user);
			} else {
				return badRequest(Json.toJson(new ErrorMessage("User with this phone number already has payment info")));
			}
		}

		try {
			payInfo.save();
		} catch (Exception e) {
			Logger.warn("Can't create PayInfo", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage()));
	}
}
