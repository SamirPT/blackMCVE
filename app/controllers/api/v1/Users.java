package controllers.api.v1;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.restfb.exception.FacebookOAuthException;
import com.twilio.sdk.TwilioRestException;
import io.swagger.annotations.*;
import models.*;
import modules.fb.FB;
import modules.s3.S3;
import modules.twilio.Twilio;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import security.MrBlackUserProfile;
import to.*;
import util.PayInfoHelper;
import util.PhoneHelper;
import util.ReservationsHelper;
import util.RolesHelper;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by arkady on 18/01/16.
 */

@Api(value = "/api/v1/user", description = "Operations with Users", tags = "users", basePath = "/api/v1/user")
public class Users extends UserProfileController<MrBlackUserProfile> {

	private Twilio twilio;
	private S3 s3;
	private FB fb;
	private ReservationsHelper reservationsHelper;

	@Inject
	public Users(Twilio twilio, S3 s3, FB fb, ReservationsHelper reservationsHelper) {
		this.twilio = twilio;
		this.s3 = s3;
		this.fb = fb;
		this.reservationsHelper = reservationsHelper;
	}

	@ApiOperation(
			nickname = "addTokenToUser",
			value = "Add token to User",
			notes = "Add token to User",
			httpMethod = "PUT",
			consumes = "text/html"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.CustomerInfo.class),
					@ApiResponse(code = 400, message = "Bad token type", response = to.ErrorMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No Users with this id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result addToken(Long id, String token, String tokenTypeName) {
		PushTokenType tokenType;
		try {
			tokenType = PushTokenType.valueOf(tokenTypeName);
		} catch (IllegalArgumentException e) {
			return badRequest(Json.toJson(ErrorMessage.getJsonErrorMessage("Bad token type")));
		}

		if (StringUtils.isEmpty(token)) {
			return badRequest(Json.toJson(ErrorMessage.getJsonErrorMessage("Token shouldn't be empty")));
		}

		User user = User.finder.byId(id);
		if (user == null) {
			return notFound(ErrorMessage.getJsonErrorMessage("No Users with this id"));
		}

		if (PushTokenType.IOS.equals(tokenType)) {
			user.setIOSToken(token);
		}

		try {
			user.update();
		} catch (Exception e) {
			Logger.warn("Can't add token to user", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok();
	}

	@ApiOperation(
			nickname = "deleteTokenFromUser",
			value = "Delete User's token",
			notes = "Delete one of User's tokens",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.CustomerInfo.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No Users with this id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteToken(Long id, String tokenTypeName) {
		PushTokenType tokenType;
		try {
			tokenType = PushTokenType.valueOf(tokenTypeName);
		} catch (IllegalArgumentException e) {
			return badRequest(Json.toJson(ErrorMessage.getJsonErrorMessage("Bad token type")));
		}

		User user = User.finder.byId(id);
		if (user == null) {
			return notFound(ErrorMessage.getJsonErrorMessage("No Users with this id"));
		}

		if (PushTokenType.IOS.equals(tokenType)) {
			user.setIOSToken(null);
		}

		try {
			user.update();
		} catch (Exception e) {
			Logger.warn("Can't add token to user", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok();
	}

	@ApiOperation(
			nickname = "updateUsersCustomerProfile",
			value = "Update customer info of user",
			notes = "Update customer info of user",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Ok", response = to.CustomerInfo.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 403, message = "You don't have access to call this method", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No Customers with this id | No Venue with this id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "customerInfo", value = "CustomerInfo", required = true, dataType = "to.CustomerInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result updateCustomerInfo(Long venueId) {
		JsonNode customerInfoJson = request().body().asJson();
		CustomerInfo customerInfo;
		try {
			customerInfo = Json.fromJson(customerInfoJson, CustomerInfo.class);
		} catch (Exception e) {
			Logger.warn("Bad CustomerInfo in request : " + customerInfoJson.toString());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		if (customerInfo.getVisitorInfo() == null || customerInfo.getVisitorInfo().getId() == null ) {
			return badRequest(Json.toJson(new ErrorMessage("Please define customer id in visitor info")));
		}

		User customer = User.finder.fetch("payInfo").where().idEq(customerInfo.getVisitorInfo().getId()).findUnique();
		if (customer == null) {
			return notFound(Json.toJson(new ErrorMessage("No Customers with this id")));
		}

		UserVenuePK customerVenuePk = new UserVenuePK(customer.getId(), venueId);
		CustomerVenue customerVenue = CustomerVenue.finder.byId(customerVenuePk);
		if (customerVenue == null) {
			customerVenue = new CustomerVenue();
		}

		boolean customerVenueChanged = false;
		if (customerInfo.getCustomerNote() != null && !customerInfo.getCustomerNote().equals(customerVenue.getCustomerNote())) {
			customerVenue.setCustomerNote(customerInfo.getCustomerNote());
			customerVenueChanged = true;
		}

		if (customerInfo.getTags() != null) {
			Venue venue = Venue.finder.byId(venueId);

			if (venue == null) {
				return notFound(Json.toJson(new ErrorMessage("No Venue with this id")));
			}

			final List<String> venueTags = venue.getTags();

			List<String> resultTagsList = customerInfo.getTags().stream()
					.filter(venueTags::contains)
					.collect(Collectors.toList());
			customerVenue.setTags(resultTagsList);
			customerVenueChanged = true;
		}

		final PayInfoTO contactInfo = customerInfo.getContactInfo();
		PayInfo payInfo = null;
		if (contactInfo != null && !contactInfo.toNewPayInfo().equals(customer.getPayInfo())) {
			if (customer.getPayInfo() == null) {
				payInfo = new PayInfo();
				payInfo.setUser(customer);
				payInfo.setPhoneNumber(PhoneHelper.normalizePhoneNumber(customer.getPhoneNumber()));
			} else {
				payInfo = customer.getPayInfo();
			}

			if (StringUtils.isNotEmpty(contactInfo.getFirstName())) payInfo.setFirstName(contactInfo.getFirstName());
			if (StringUtils.isNotEmpty(contactInfo.getLastName())) payInfo.setLastName(contactInfo.getLastName());
			if (StringUtils.isNotEmpty(contactInfo.getAddress())) payInfo.setAddress(contactInfo.getAddress());
			if (StringUtils.isNotEmpty(contactInfo.getCity())) payInfo.setCity(contactInfo.getCity());
			if (StringUtils.isNotEmpty(contactInfo.getState())) payInfo.setState(contactInfo.getState());
			if (StringUtils.isNotEmpty(contactInfo.getCountry())) payInfo.setCountry(contactInfo.getCountry());
			if (StringUtils.isNotEmpty(contactInfo.getPostalCode())) payInfo.setPostalCode(contactInfo.getPostalCode());
			if (StringUtils.isNotEmpty(contactInfo.getCompanyName())) payInfo.setCompanyName(contactInfo.getCompanyName());
			if (StringUtils.isNotEmpty(contactInfo.getEmail())) payInfo.setEmail(contactInfo.getEmail());

			if (StringUtils.isNotEmpty(contactInfo.getDateOfBirth())) {
				try {
					payInfo.setDateOfBirth(LocalDate.parse(contactInfo.getDateOfBirth()));
				} catch (DateTimeParseException e) {
					final ErrorMessage errorMessage = new ErrorMessage("Bad D.O.B. format in contact info");
					errorMessage.addField("dateOfBirth");
					return badRequest(Json.toJson(errorMessage));
				}
			}
		}

		Ebean.beginTransaction();
		try {
			if (customerVenueChanged) {
				if (customerVenue.getPk() == null) {
					customerVenue.setPk(customerVenuePk);
					customerVenue.save();
				} else {
					customerVenue.update();
				}
			}

			if (payInfo != null && PayInfoHelper.isAnyFieldPresent(new PayInfoTO(payInfo))) {
				if (payInfo.getId() == null) {
					payInfo.save();
				} else {
					payInfo.update();
				}
			}
			Ebean.commitTransaction();
		} catch (Exception e) {
			Logger.warn("Can't update Customer Info", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage()));
	}

	@ApiOperation(
			nickname = "getUsersCustomerProfile",
			value = "Get customer info of user",
			notes = "Get customer info of user",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Customer info", response = to.CustomerInfo.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 403, message = "You don't have access to call this method", response = to.ErrorMessage.class),
					@ApiResponse(code = 404, message = "No venue with this id | No customers with this id", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getCustomerInfo(@ApiParam(required = true) Long customerId,
								  @ApiParam(required = true) Long venueId) {
		final User currentUser = getUserProfile().getUser();
		if (!currentUser.isAdmin()) {
			UserVenue userVenue = UserVenue.finder.byId(new UserVenuePK(currentUser.getId(), venueId));
			if (userVenue == null || !RolesHelper.canViewClientsSection(userVenue.getRoles())) {
				Logger.info("Attempt to receive CustomerInfo with bad permissions by userId=" + currentUser.getId());
				return forbidden(ErrorMessage.getJsonForbiddenMessage());
			}
		}

		User user = User.finder.fetch("payInfo").where().idEq(customerId).findUnique();
		if (user == null) {
			return notFound(Json.toJson(new ErrorMessage("No customers with this id")));
		}

		Venue venue = Venue.finder.byId(venueId);
		if (venue == null) {
			return notFound(Json.toJson(new ErrorMessage("No venue with this id")));
		}

		UserVenuePK pk = new UserVenuePK(customerId, venueId);
		CustomerVenue customerVenue = CustomerVenue.finder.byId(pk);

		if (customerVenue == null) {
			customerVenue = new CustomerVenue();
			customerVenue.setPk(pk);
		}

		CustomerInfo customerInfo = new CustomerInfo();

		customerInfo.setCustomerNote(customerVenue.getCustomerNote());
		if (customerVenue.getFirstBookedBy() != null) {
			customerInfo.setFirstBookedBy(new UserInfo(customerVenue.getFirstBookedBy()));
		}

		try {
			List<Reservation> reservations = reservationsHelper.getAllReservationsOfUserInVenue(venueId, customerId);
			List<Feedback> feedbacks = reservationsHelper.getFeedbacksOfUserInVenue(venueId, customerId);
			customerInfo.setVisitorInfo(reservationsHelper.getDetailedVisitorInfo(venueId, user, reservations, feedbacks));

			final List<Reservation> pastReservations = new ArrayList<>();
			final List<Reservation> comingUpReservations = new ArrayList<>();

			reservations.forEach(reservation -> {
				if (LocalDate.now(ZoneId.of(venue.getTimeZone())).isAfter(reservation.getEventInstance().getDate())) {
					pastReservations.add(reservation);
				} else {
					comingUpReservations.add(reservation);
				}
			});

			customerInfo.setPastReservations(reservationsHelper.getEodReservationInfos(pastReservations));
			customerInfo.setComingUpReservations(reservationsHelper.getEodReservationInfos(comingUpReservations));
			List<FeedbackInfo> feedbackList = feedbacks.stream().map(FeedbackInfo::new).collect(Collectors.toList());
			customerInfo.setNotes(feedbackList);
			customerInfo.setTags(customerVenue.getTags());
		} catch (Exception e) {
			Logger.warn("Can't obtain Customer Info", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (user.getPayInfo() != null) {
			customerInfo.setContactInfo(new PayInfoTO(user.getPayInfo()));
		}

		return ok(Json.toJson(customerInfo));
	}

	@ApiOperation(
			nickname = "getUserSettings",
			value = "Get current User settings",
			notes = "Get current User settings",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "User Settings", response = to.UserSettings.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result getSettings() {
		final User user = getUserProfile().getUser();

		UserSettings userSettings = new UserSettings();
		userSettings.setCheckInOnMyBehalf(user.getCheckInOnMyBehalf());
		userSettings.setInAppNotification(user.getInAppNotification());
		userSettings.setPostOnMyBehalf(user.getPostOnMyBehalf());
		userSettings.setRsvpOnMyBehalf(user.getRsvpOnMyBehalf());
		userSettings.setSmsMessages(user.getSmsMessages());

		return ok(Json.toJson(userSettings));
	}

	@ApiOperation(
			nickname = "setUserSettings",
			value = "Set current User settings",
			notes = "Set current User settings",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "User Settings", response = to.UserSettings.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "settings", value = "User's settings", required = true, dataType = "to.UserSettings", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result setSettings() {
		final User user = getUserProfile().getUser();

		JsonNode userSettingsJson = request().body().asJson();
		UserSettings userSettings;
		try {
			userSettings = Json.fromJson(userSettingsJson, UserSettings.class);
		} catch (Exception e) {
			Logger.warn("Bad UserSettings in request : " + userSettingsJson.toString());
			return badRequest(Json.toJson(new ErrorMessage("Please check sent data")));
		}

		if (userSettings.getSmsMessages() != null) user.setSmsMessages(userSettings.getSmsMessages());
		if (userSettings.getCheckInOnMyBehalf() != null) user.setCheckInOnMyBehalf(userSettings.getCheckInOnMyBehalf());
		if (userSettings.getInAppNotification() != null) user.setInAppNotification(userSettings.getInAppNotification());
		if (userSettings.getPostOnMyBehalf() != null) user.setPostOnMyBehalf(userSettings.getPostOnMyBehalf());
		if (userSettings.getRsvpOnMyBehalf() != null) user.setRsvpOnMyBehalf(userSettings.getRsvpOnMyBehalf());

		try {
			user.update();
		} catch (Exception e) {
			Logger.warn("Can't update user's settings", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Ok")));
	}

	@ApiOperation(
			nickname = "getUser",
			value = "Get current User",
			notes = "Get current User profile",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "User profile", response = to.UserInfo.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result get() {
		final User user = getUserProfile().getUser();

		UserInfo userInfo = new UserInfo();
		userInfo.setId(user.getId());
		userInfo.setFullName(user.getFullName());
		userInfo.setEmail(user.getEmail());
		if (user.getBirthday() != null) userInfo.setBirthday(user.getBirthday().toString());
		userInfo.setPhoneNumber(user.getPhoneNumber());
		userInfo.setUserpic(user.getUserpic());
		userInfo.setPreferredRoles(user.getPreferredRoles());
		userInfo.setHasFacebookProfile(user.getFacebookInfo() != null);
		userInfo.setPromotionCompany(user.getPromotionCompany());
		userInfo.setIsAdmin(Boolean.TRUE.equals(user.isAdmin()));

		return ok(Json.toJson(userInfo));
	}

	@ApiOperation(
			nickname = "createUser",
			value = "Create new User",
			notes = "Create new User, or generates security code for sign in, and sends it via SMS. " +
					"For new user fields 'email', 'name' and 'birthday' shouldn't be empty. " +
					"For 'sign in' phone number is enough",
			httpMethod = "POST"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "User created", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "Email, phoneNumber, name and birthday fields shouldn't be empty", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "user", value = "Essential User's info", required = true, dataType = "models.User", paramType = "body")
	})
	public Result create() {
			JsonNode userJson = request().body().asJson();
			String phoneNumber = userJson.findValue("phoneNumber") != null ? userJson.findValue("phoneNumber").asText() : null;
			String birthday = userJson.findValue("birthday") != null ? userJson.findValue("birthday").asText() : null;
			String email = userJson.findValue("email") != null ?  userJson.findValue("email").asText() : null;
			String fullName = userJson.findValue("fullName") != null ? userJson.findValue("fullName").asText() : null;

			if (StringUtils.isEmpty(birthday) || StringUtils.isEmpty(email) || !EmailValidator.getInstance().isValid(email) ||
					StringUtils.isEmpty(fullName) || StringUtils.isEmpty(phoneNumber)) {
				final ErrorMessage errorMessage = new ErrorMessage("Email, phoneNumber, name and birthday fields shouldn't be empty");
				if (StringUtils.isEmpty(phoneNumber)) errorMessage.addField("phoneNumber");
				if (StringUtils.isEmpty(email) || !EmailValidator.getInstance().isValid(email)) errorMessage.addField("email");
				if (StringUtils.isEmpty(fullName)) errorMessage.addField("fullName");
				if (StringUtils.isEmpty(birthday)) errorMessage.addField("birthday");
				return badRequest(Json.toJson(errorMessage));
			}

		Ebean.beginTransaction();
		try {
			User user = User.finder.where().eq("phoneNumber", PhoneHelper.normalizePhoneNumber(phoneNumber)).findUnique();
			if (user == null) {
				UserInfo userInfo = play.libs.Json.fromJson(userJson, UserInfo.class);
				if (userInfo.getFacebookInfo() != null) {
					FacebookInfo fbInfo = FacebookInfo.finder.byId(userInfo.getFacebookInfo().getId());
					if (fbInfo != null) {
						if (!fbInfo.equals(userInfo.getFacebookInfo())) {
							fillEmptyFBInfoFields(userInfo.getFacebookInfo(), fbInfo);
							fbInfo.save();
						}
						userInfo.setFacebookInfo(fbInfo);
						if (StringUtils.isEmpty(userInfo.getUserpic())) userInfo.setUserpic(fbInfo.getPictureUrl());
					}
				}
				user = userInfo.toUser();
			}

			setSecurityCode(user);
			user.setAdmin(Boolean.FALSE);
			user.save();
			twilio.sendSMS(user.getPhoneNumber(), "Mr. Black says Hello. Your code is " + user.getSecurityCode());
			Ebean.commitTransaction();
		} catch (TwilioRestException | NumberFormatException e) {
			final ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
			errorMessage.addField("phoneNumber");
			return internalServerError(Json.toJson(errorMessage));
		} catch (Exception e) {
			Logger.warn("Can't process user creation", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.toJson(new ResponseMessage("Security code has been sent via SMS")));
	}

	@ApiOperation(
			nickname = "getSecurityCode",
			value = "Get security code via SMS for sign in",
			notes = "Generates security code for sign in, and sends it via SMS",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Security code has been sent via SMS", response = ResponseMessage.class),
					@ApiResponse(code = 400, message = "Phone number required", response = ErrorMessage.class),
					@ApiResponse(code = 404, message = "This phone number is not registered", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	public Result getSecurityCode(@ApiParam(required = true, value = "Phone number") String phoneNumber) {
		Logger.info("Received security code request for phoneNumber: " + phoneNumber);
		if (StringUtils.isEmpty(phoneNumber)) {
			final ErrorMessage errorMessage = new ErrorMessage("Phone number required");
			errorMessage.addField("phoneNumber");
			return badRequest(Json.toJson(errorMessage));
		}

		try {
			phoneNumber = PhoneHelper.normalizePhoneNumber(phoneNumber);
		} catch (NumberFormatException e) {
			final ErrorMessage errorMessage = new ErrorMessage("Invalid phone number");
			errorMessage.addField("phoneNumber");
			return badRequest(Json.toJson(errorMessage));
		}

		if (isDemoUser(phoneNumber)) return ok(Json.toJson(new ResponseMessage("Security code for demo user is 111111")));

		User user = User.finder.where().eq("phoneNumber", phoneNumber).findUnique();
		if (user == null) {
			final ErrorMessage errorMessage = new ErrorMessage("This phone number is not registered");
			errorMessage.addField("phoneNumber");
			return notFound(Json.toJson(errorMessage));
		}

		setSecurityCode(user);
		try {
			user.save();
		} catch (Exception e) {
			Logger.warn("Can't update user", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		try {
			twilio.sendSMS(user.getPhoneNumber(), "Mr. Black says Hello. Your code is " + user.getSecurityCode());
		} catch (TwilioRestException e) {
			final ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
			errorMessage.addField("phoneNumber");
			return internalServerError(Json.toJson(errorMessage));
		} catch (Exception e) {
			Logger.warn("Can't process user creation", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok(Json.toJson(new ResponseMessage("Security code has been sent via SMS")));
	}

	public static boolean isDemoUser(String phoneNumber) {
		return "+11111111111".equals(phoneNumber);
	}

	private void setSecurityCode(User user) {
		final Integer securityCode = (int)(Math.floor(Math.random() * 900000) + 99999);
		user.setSecurityCode(String.valueOf(securityCode));
	}

	@ApiOperation(
			nickname = "getApiKey",
			value = "Get API Key",
			notes = "Exchanges security code and phone number to API key",
			httpMethod = "GET"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 400, message = "Phone number and security code must be not empty", response = ErrorMessage.class),
					@ApiResponse(code = 403, message = "Wrong security code", response = ErrorMessage.class),
					@ApiResponse(code = 404, message = "Unknown phone number", response = ErrorMessage.class),
					@ApiResponse(code = 500, message = "Can't create session api_key. Please contact application team", response = ErrorMessage.class),
					@ApiResponse(code = 200, message = "Ok", response = ApiKey.class)
			}
	)
	public Result getApiKey(@ApiParam(required = true, value = "Registered phone number") String phoneNumber,
							@ApiParam(required = true, value = "Security code, received via SMS") String securityCode) {

		User user;
		try {
			user = User.finder.where().eq("phoneNumber", PhoneHelper.normalizePhoneNumber(phoneNumber)).findUnique();
		} catch (NumberFormatException e) {
			final ErrorMessage errorMessage = new ErrorMessage("Invalid phone number");
			errorMessage.addField("phoneNumber");
			return badRequest(Json.toJson(errorMessage));
		}

		if (StringUtils.isEmpty(phoneNumber) || StringUtils.isEmpty(securityCode)) {
			return badRequest(Json.toJson(new ErrorMessage("Phone number and security code must be not empty")));
		}
		if (user == null) {
			final ErrorMessage errorMessage = new ErrorMessage("Unknown phone number");
			errorMessage.addField("phoneNumber");
			return notFound(Json.toJson(errorMessage));
		}
		if (!securityCode.equals(user.getSecurityCode())) {
			final ErrorMessage errorMessage = new ErrorMessage("Wrong security code");
			errorMessage.addField("securityCode");
			return forbidden(Json.toJson(errorMessage));
		}

		final String newApiKey;
		try {
			newApiKey = UUID.randomUUID().toString();
			user.addApiKey(newApiKey);
			if (!isDemoUser(phoneNumber)) user.setSecurityCode(null);
			user.save();
		} catch (Exception e) {
			Logger.warn("Can't create session", e);
			return internalServerError(Json.toJson(new ErrorMessage("Can't create api_key. Please contact service administrator")));
		}

		return ok(Json.toJson(new ApiKey(newApiKey)));
	}

	private void fillEmptyFBInfoFields(FacebookInfo from, FacebookInfo to) {
		if (to.getGender() == null) to.setGender(from.getGender());
		if (to.getName() == null) to.setName(from.getName());
		if (to.getPicture() == null) to.setPicture(from.getPicture());
		if (to.getEmail() == null) to.setEmail(from.getEmail());
		if (to.getBirthday() == null) to.setBirthday(from.getBirthday());
		if (to.getRelationshipStatus() == null) to.setRelationshipStatus(from.getRelationshipStatus());
		if (to.getLocation() == null) to.setLocation(from.getLocation());
	}

	@ApiOperation(
			nickname = "updateUser",
			value = "Update User",
			notes = "Update existing User. Available for own account or any account with admin permissions",
			httpMethod = "PUT"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "Updated", response = ResponseMessage.class),
					@ApiResponse(code = 401, message = "Authentication required", response = to.ErrorMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "user", value = "User's entity", required = true, dataType = "to.UserInfo", paramType = "body")
	})
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result update() {
		final UserInfo userInfo = Json.fromJson(request().body().asJson(), UserInfo.class);
		User user = getUserProfile().getUser();

		if (StringUtils.isNotEmpty(userInfo.getEmail())) user.setEmail(userInfo.getEmail());
		if (userInfo.getPreferredRoles() != null) user.setPreferredRoles(userInfo.getPreferredRoles());
		if (StringUtils.isNotEmpty(userInfo.getFullName())) user.setFullName(userInfo.getFullName());
		if (StringUtils.isNotEmpty(userInfo.getBirthday())) {
			try {
				user.setBirthday(LocalDate.parse(userInfo.getBirthday()));
			} catch (Exception e) {
				return badRequest(ErrorMessage.getJsonErrorMessage("Bad DOB format"));
			}
		}
		final FacebookInfo newFacebookInfo = userInfo.getFacebookInfo();
		if (newFacebookInfo != null) {
			FacebookInfo facebookInfo;
			if (user.getFacebookInfo() == null) {
				facebookInfo = new FacebookInfo();
			} else {
				facebookInfo = user.getFacebookInfo();
			}
			if (newFacebookInfo.getBirthday() != null) facebookInfo.setBirthday(newFacebookInfo.getBirthday());
			if (newFacebookInfo.getEmail() != null) facebookInfo.setEmail(newFacebookInfo.getEmail());
			if (newFacebookInfo.getGender() != null) facebookInfo.setGender(newFacebookInfo.getGender());
			if (newFacebookInfo.getId() != null) facebookInfo.setId(newFacebookInfo.getId());
			if (newFacebookInfo.getLocation() != null) facebookInfo.setLocation(newFacebookInfo.getLocation());
			if (newFacebookInfo.getName() != null) facebookInfo.setName(newFacebookInfo.getName());
			if (newFacebookInfo.getPicture() != null) facebookInfo.setPicture(newFacebookInfo.getPicture());
			if (newFacebookInfo.getRelationshipStatus() != null) facebookInfo.setRelationshipStatus(newFacebookInfo.getRelationshipStatus());
			if (newFacebookInfo.getToken() != null) facebookInfo.setToken(newFacebookInfo.getToken());

			boolean isValidFbToken = true;
			try {
				facebookInfo.setFriendsNumber(Integer.valueOf(fb.getNumberOfFriends(facebookInfo.getToken())).shortValue());
			} catch (FacebookOAuthException e) {
				isValidFbToken = false;
				Logger.info("Facebook with invalid token has been received. This account will not be connected to user " +
						"with id=" + user.getId());
			}

			if (isValidFbToken) {
				facebookInfo.save();
				user.setFacebookInfo(newFacebookInfo);
			}
		}

		String oldUserpic = null;
		if (StringUtils.isNotEmpty(userInfo.getUserpic())) {
			oldUserpic = user.getUserpic();
			user.setUserpic(userInfo.getUserpic());
		}

		if (userInfo.getPromotionCompany() != null) {
			user.setPromotionCompany(userInfo.getPromotionCompany());
		}

		try {
			user.update();
		} catch (Exception e) {
			Logger.warn("Can't update user", e);
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		if (StringUtils.isNotEmpty(oldUserpic) && oldUserpic.contains("cloudfront.net")) {
			try {
				s3.deleteFromS3(oldUserpic.substring(oldUserpic.lastIndexOf("/") + 1));
			} catch (Exception e) {
				Logger.warn("Can't delete image from s3: " + oldUserpic, e);
			}
		}

		return ok(Json.toJson(new ResponseMessage("Updated")));
	}

	@ApiOperation(
			nickname = "deleteCurrentUser",
			value = "Delete User by api_key",
			notes = "Delete current user by api_key",
			httpMethod = "DELETE"
	)
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "User deleted", response = ResponseMessage.class),
					@ApiResponse(code = 500, message = "Something went wrong. Please contact application team", response = ErrorMessage.class)
			}
	)
	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result deleteCurrentUser() {
		return TODO;
	}
}
