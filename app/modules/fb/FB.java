package modules.fb;

import com.restfb.exception.FacebookOAuthException;
import models.User;

/**
 * Created by arkady on 09/04/16.
 */
public interface FB {
	void postOnTheWall(User user, String message);
	void postEvent(User user, String eventId);
	void checkIn(User user, String placeId);
	int getNumberOfFriends(String token) throws FacebookOAuthException;
}
