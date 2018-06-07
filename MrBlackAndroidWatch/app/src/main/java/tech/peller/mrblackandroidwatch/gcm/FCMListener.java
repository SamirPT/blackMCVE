package tech.peller.mrblackandroidwatch.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.GlobalActivity;

/**
 * Created by Sam (samir@peller.tech) on 28.02.2017
 */
public class FCMListener extends FirebaseMessagingService {
    public final static String NOTIFICATION_EXTRA = "notificationTO";
    private final static String TITLE_NAME = "title";
    private final static String MESSAGE_NAME = "message";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(remoteMessage.getData().get(TITLE_NAME).toString())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(remoteMessage.getData().get(MESSAGE_NAME).toString()))
                        .setContentText(remoteMessage.getData().get(MESSAGE_NAME).toString())
                        .setAutoCancel(true)
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.rolha));


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, GlobalActivity.class);

        //передаем данные, управляющие направлением приложения к соответствующему нотификации экрану
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (remoteMessage.getData().get(NOTIFICATION_EXTRA) != null) {
            resultIntent.putExtra(NOTIFICATION_EXTRA, remoteMessage.getData().get(NOTIFICATION_EXTRA).toString());
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(GlobalActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        Random r = new Random();
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        r.nextInt(10000),
                        0
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int notificationId = r.nextInt(100000);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }
}
