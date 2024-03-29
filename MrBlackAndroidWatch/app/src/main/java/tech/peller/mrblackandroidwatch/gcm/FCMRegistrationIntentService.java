package tech.peller.mrblackandroidwatch.gcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import tech.peller.mrblackandroidwatch.GlobalActivity;


/**
 * Created by Sam (samir@peller.tech) on 28.02.2016
 */
public class FCMRegistrationIntentService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        GlobalActivity activity = GlobalActivity.instance;
        if(activity != null) {
            activity.setToken(refreshedToken);
        }

//        Intent brSenderIntent = new Intent(GlobalActivity.BROADCAST_TOKEN_ACTION);
//        brSenderIntent.putExtra(GlobalActivity.EXTRA_BROADCAST_TOKEN, refreshedToken);
//        sendBroadcast(brSenderIntent);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}