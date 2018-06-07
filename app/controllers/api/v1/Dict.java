package controllers.api.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import models.BottleServiceType;
import models.GroupType;
import models.PromotionCompany;
import models.VenueRole;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import to.ErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arkady on 15/02/16.
 */
@Api(value = "/api/v1/dict", description = "Contains dictionaries", tags = "dict", basePath = "/api/v1/dict")
public class Dict extends UserProfileController {

	@ApiOperation(
			nickname = "getPromotionCompanies",
			value = "Get Promotion Companies",
			notes = "Get list of available Promotion Companies",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "PromotionCompany list", response = PromotionCompany[].class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getPromotersList() {
		List<PromotionCompany> promotionCompanyList;
		try {
			promotionCompanyList = PromotionCompany.finder.all();
		} catch (Exception e) {
			Logger.warn("Can't obtain promoters list", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}
		return ok(Json.toJson(promotionCompanyList));
	}

	@ApiOperation(
			nickname = "getRoles",
			value = "Get Roles",
			notes = "Get user roles list",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "List of defined user roles"),
			}
	)
	public Result roles() {
		Map<String, String> map = new HashMap<>();
		map.putAll(VenueRole.all());
		map.remove(VenueRole.PROMOTER.name());
		return ok(Json.toJson(map));
	}

	@ApiOperation(
			nickname = "getBottleServiceTypes",
			value = "Get Bottle Service types",
			notes = "Get Bottle Service types list",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "List of Bottle Service types"),
			}
	)
	public Result bottleServiceTypesList() {
		return ok(Json.toJson(BottleServiceType.all()));
	}

	@ApiOperation(
			nickname = "getReservationGroupTypes",
			value = "Get Reservation Group types",
			notes = "Get Reservation Group types list",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "List of Reservation Group types"),
			}
	)
	public Result reservationGroupTypesList() {
		return ok(Json.toJson(GroupType.all()));
	}
}
