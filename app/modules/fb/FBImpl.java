package modules.fb;

import com.restfb.*;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.FacebookType;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

/**
 * Created by arkady on 09/04/16.
 */
public class FBImpl implements FB {

	public void postOnTheWall(User user, String message) {
		if (!hasFbToken(user) || !Boolean.TRUE.equals(user.getPostOnMyBehalf())) return;

		final String accessToken = user.getFacebookInfo().getToken();
		FacebookClient client = new DefaultFacebookClient(accessToken, Version.LATEST);
		try {
			FacebookType publishMessageResponse = client.publish("me/feed", FacebookType.class,
							Parameter.with("message", message));
		} catch (Exception e) {
			Logger.warn("Can't post on the wall", e);
		}
	}

	public void postEvent(User user, String eventId) {
		if (!hasFbToken(user) || !Boolean.TRUE.equals(user.getPostOnMyBehalf())) return;

		final String accessToken = user.getFacebookInfo().getToken();
		FacebookClient client = new DefaultFacebookClient(accessToken, Version.LATEST);
		try {
			Boolean publishRSVPResponse = client.publish(eventId + "/attending", Boolean.class);
		} catch (Exception e) {
			Logger.warn("Can't attend to event", e);
		}
	}

	public void checkIn(User user, String placeId) {
		if (!hasFbToken(user) || !Boolean.TRUE.equals(user.getPostOnMyBehalf()) || StringUtils.isEmpty(placeId)) return;

		FacebookClient client = new DefaultFacebookClient(user.getFacebookInfo().getToken(), Version.LATEST);
		try {
			FacebookType publishCheckinResponse = client.publish("me/feed",
					FacebookType.class, Parameter.with("place", placeId));
		} catch (Exception e) {
			Logger.warn("Can't check in", e);
		}
	}

	@Override
	public int getNumberOfFriends(String token) {
		if (StringUtils.isEmpty(token)) return 0;
		int result = 0;
		FacebookClient client = new DefaultFacebookClient(token, Version.LATEST);
		try {
			Connection<com.restfb.types.User> friendsList = client.fetchConnection("me/friends", com.restfb.types.User.class);
			result = friendsList.getData().size();
		} catch (FacebookOAuthException e) {
			throw e;
		} catch (Exception e) {
			Logger.warn("Can't fetch friends list", e);
		}

		return result;
	}

	private static boolean hasFbToken(User user) {
		return user != null && user.getFacebookInfo() != null && StringUtils.isNotEmpty(user.getFacebookInfo().getToken());
	}
}
