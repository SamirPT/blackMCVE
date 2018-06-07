package controllers;

import com.google.inject.Inject;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.verbs.Message;
import com.twilio.sdk.verbs.TwiMLResponse;
import modules.twilio.Twilio;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import to.ErrorMessage;
import to.ReservationInfo;

/**
 * Created by arkady on 27/01/16.
 */
public class Sms extends UserProfileController {

	@Inject
	Twilio twilio;

	@RequiresAuthentication(clientName = "IndirectCookieClient,ParameterClient")
	public Result reservation() {
		ReservationInfo reservationInfo = Json.fromJson(request().body().asJson(), ReservationInfo.class);

		if (reservationInfo.getGuestInfo() == null || StringUtils.isEmpty(reservationInfo.getGuestInfo().getPhoneNumber())) {
			return badRequest(Json.toJson(new Error("Guest's phone number required")));
		}

		String message = "Your reservation at {venue} on {date} is confirmed. {link}";

		//TODO replace tokens (link - web view with barcode)

		try {
			twilio.sendSMS(reservationInfo.getGuestInfo().getPhoneNumber(), message);
		} catch (TwilioRestException e) {
			Logger.warn("Can't send reservation details");
			return internalServerError(ErrorMessage.getJsonInternalServerErrorMessage());
		}

		return ok();
	}

	public Result reply() {
		TwiMLResponse twiml = new TwiMLResponse();
		Message message = new Message("Mr. Black does not directly respond to messages. Check your reservation details for relevant contact information. Enjoy your night!");
		try {
			twiml.append(message);
		}
		catch (Exception e) {
			Logger.error("An error occurred trying to append the message verb to the response verb.", e);
		}
		return ok(twiml.toXML()).as("text/xml");
	}
}
