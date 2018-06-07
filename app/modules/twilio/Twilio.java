package modules.twilio;

import com.twilio.sdk.TwilioRestException;

/**
 * Created by arkady on 05/02/16.
 */
public interface Twilio {
	void sendSMS(String phoneNumber, String messageText) throws TwilioRestException;
}
