package modules.push;

//import java.net.URISyntaxException;

/**
 * Created by arkady on 01/03/16.
 */
public interface Push {
//	void broadcastAndroidPush(String message) throws URISyntaxException;
	void broadcastIOSPush(String message);
	void sendIOSPush(String token, String title, String message);
}
