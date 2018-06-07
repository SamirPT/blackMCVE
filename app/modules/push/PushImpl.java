package modules.push;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.exceptions.NetworkIOException;
import play.Configuration;
import play.Logger;
import play.api.Environment;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by arkady on 01/03/16.
 */
public class PushImpl implements Push {

//	public static final String ANDROID_TOKEN = "push.gcm.token";
//	private HttpClient client;
	private ApnsService service;

	@Inject
	public PushImpl(Environment environment, Configuration configuration) {
		final File file = environment.getFile("/app/cert/cert.p12");
		if (file != null) {
			String appleCertPath = file.getAbsolutePath();
			service = APNS.newService()
							.withCert(appleCertPath, "123321")
							.withSandboxDestination()
//							.withProductionDestination()
							.build();
		}
	}

	public void broadcastIOSPush (String message) {
		Akka.system().scheduler().scheduleOnce(Duration.Zero(),
				() -> {
//					List<PushToken> tokenList = PushToken.finder.where().eq("type", PushTokenType.IOS).findList();
//					if (tokenList != null) {
//						tokenList.stream().forEach(pushToken -> sendIOSPush(pushToken.getToken(), message));
//					}
				}, Akka.system().dispatcher());
	}

	public void sendIOSPush(String token, String title, String message) {
		Akka.system().scheduler().scheduleOnce(Duration.Zero(), () -> {
			String payload = APNS.newPayload()
					.alertBody(title)
					.alertTitle("MrBlack Industry")
					.customField("messageData", message)
					.sound("default")
					.build();

			try {
				service.push(token, payload);
			} catch (NetworkIOException e) {
				Logger.warn("Can't send push: " + payload, e);
			}
		}, Akka.system().dispatcher());
	}

	private void sendAndroidPush(List<String> token, String message, URI uri) throws IOException {
//		HttpPost request = new HttpPost(uri);
//		request.setHeader("Content-type", "application/json");
//		request.addHeader("Authorization", "key=" + androidGCMToken);
//
//		AndroidPushNotification pushNotification = new AndroidPushNotification("Exodus Las Vegas", message, token);
//
//		StringEntity postingString = new StringEntity(Json.toJson(pushNotification).toString());
//		request.setEntity(postingString);
//
//		HttpResponse response = client.execute(request);
//		HttpEntity responseEntity = response.getEntity();
//		String resp = EntityUtils.toString(responseEntity);
	}

	public void broadcastAndroidPush (String message) throws URISyntaxException {
//		Akka.system().scheduler().scheduleOnce(Duration.Zero(),
//				() -> {
//					URIBuilder builder = new URIBuilder();
//					builder.setScheme("https" )
//							.setHost("gcm-http.googleapis.com")
//							.setPort(443)
//							.setPath("/gcm/send" );
//					try {
//						URI uri = builder.build();
//
//						List<PushToken> androidTokenList = PushToken.finder.where().eq("type", PushTokenType.ANDROID).findList();
//						if (androidTokenList != null) {
//							int BATCH = 1000;
//							IntStream.range(0, (androidTokenList.size()+BATCH-1)/BATCH)
//									.mapToObj(i -> androidTokenList.subList(i*BATCH, Math.min(androidTokenList.size(), (i+1)*BATCH)))
//									.forEach(batch -> {
//										List<String> tokensBatch = androidTokenList.stream()
//												.map(PushToken::getToken)
//												.collect(Collectors.toList());
//										try {
//											sendAndroidPush(tokensBatch, message, uri);
//										} catch (IOException e) {
//											Logger.warn("Can't send android push notification", e);
//										}
//									});
//						}
//					} catch (URISyntaxException e) {
//						Logger.warn("Can't send android push notification", e);
//					}
//				}, Akka.system().dispatcher()
//		);
	}

}
