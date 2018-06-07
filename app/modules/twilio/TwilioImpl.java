package modules.twilio;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 04/02/16.
 */

class TwilioImpl implements Twilio {
	private static final String TWILIO_ACCOUNT_SID = "twilio.account.sid";
	private static final String TWILIO_AUTH_TOKEN = "twilio.auth.token";
	private static final String TWILIO_SENDER_PHONE = "twilio.sender.phone";

	private String accountSid;
	private String authToken;
	private String senderPhone;

	@Inject
	public TwilioImpl(ApplicationLifecycle lifecycle, Configuration configuration) {
		accountSid = configuration.getString(TWILIO_ACCOUNT_SID);
		authToken = configuration.getString(TWILIO_AUTH_TOKEN);
		senderPhone = configuration.getString(TWILIO_SENDER_PHONE);
//		lifecycle.addStopHook( () -> {
//			// previous contents of Plugin.onStop
//			return F.Promise.pure(null);
//		});
	}

	@Override
	public void sendSMS(String phoneNumber, String messageText) throws TwilioRestException {
		if (StringUtils.isEmpty(phoneNumber) || StringUtils.isEmpty(messageText)) return;

		try {
			TwilioRestClient client = new TwilioRestClient(accountSid, authToken);
			Account account = client.getAccount();
			MessageFactory messageFactory = account.getMessageFactory();
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("To", phoneNumber));
			params.add(new BasicNameValuePair("From", senderPhone));
			params.add(new BasicNameValuePair("Body", messageText));
			com.twilio.sdk.resource.instance.Message sms = messageFactory.create(params);
		} catch (TwilioRestException e) {
			throw e;
		} catch (Exception e) {
			Logger.warn("An error occurred trying to append the message verb to the response verb.", e);
		}
	}
}
