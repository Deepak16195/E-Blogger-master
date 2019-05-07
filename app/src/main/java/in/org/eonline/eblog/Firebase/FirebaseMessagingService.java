package in.org.eonline.eblog.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import in.org.eonline.eblog.HomeActivity;
import in.org.eonline.eblog.R;

/**
 * Created by Dignity on 04-12-2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage == null)
            return;

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //handleNotification(remoteMessage.getNotification().getBody());
        }

        String data = String.valueOf(remoteMessage.getData().size());
// Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                Map<String, String> params = remoteMessage.getData();
                JSONObject json = new JSONObject(params);
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }


//        //showNotification(remoteMessage.getData().get("message"));
//         String Message = remoteMessage.getNotification().getBody();
//        String Tital = remoteMessage.getNotification().getTitle();
//        if(Tital == null)
//        {
//            Tital ="mDairy";
//        }


    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            // JSONObject data = json.getJSONObject("data");
            JSONObject data = json;
            String title = data.getString("title");
            String message = data.getString("body");
            //String imageUrl = data.getString("image");
            //String timestamp = data.getString("timestamp");
            //JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "body: " + message);

            // Log.e(TAG, "payload: " + payload.toString());
            // Log.e(TAG, "imageUrl: " + imageUrl);
            // Log.e(TAG, "timestamp: " + timestamp);

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            ShowNotification(getApplicationContext(), title, message, intent);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void ShowNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-05";
        String channelName = "E-Bloggger";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(R.color.colorAccent);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.blogger)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setSound(alarmSound)
                .setOnlyAlertOnce(true)
                .setContentText(body);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId, mBuilder.build());
    }


}
