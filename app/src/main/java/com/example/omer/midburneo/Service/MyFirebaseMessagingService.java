package com.example.omer.midburneo.Service;




import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.omer.midburneo.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    NotificationManager notificationManager;

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }



//
//        Map<String, String> map = new HashMap<>();
//        map.put("title", "hello from me");
//
//
//        notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
////        //Setting up Notification channels for android O and above
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            setupChannels();
////        }
//        int notificationId = new Random().nextInt(60000);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Common.current_token)
//                .setSmallIcon(R.drawable.ic_arrow_left)  //a resource for your custom small icon
//                .setContentTitle(remoteMessage.getData().get("title")) //the "title" value you sent in your notification
//                .setContentText(remoteMessage.getData().get("message")) //ditto
//                .setAutoCancel(true)  //dismisses the notification on click
//                .setSound(defaultSoundUri);
//
////        notificationManager =
////                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }

    private void handleNow() {
    }

    private void scheduleJob() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {


            CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
            String adminChannelDescription = getString(R.string.notifications_admin_channel_descripti);

            NotificationChannel adminChannel;
            adminChannel = new NotificationChannel(Common.current_token, adminChannelName, NotificationManager.IMPORTANCE_LOW);
            adminChannel.setDescription(adminChannelDescription);
            adminChannel.enableLights(true);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(adminChannel);

        }
    }
}




//
//
//package com.example.omer.midburneo.Service;
//
//        import android.app.NotificationChannel;
//        import android.app.NotificationManager;
//        import android.app.PendingIntent;
//        import android.content.Context;
//        import android.graphics.Color;
//        import android.media.RingtoneManager;
//        import android.net.Uri;
//        import android.os.Build;
//        import android.support.annotation.RequiresApi;
//        import android.support.v4.app.NotificationCompat;
//
//        import com.example.omer.midburneo.R;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.messaging.FirebaseMessagingService;
//        import com.google.firebase.messaging.RemoteMessage;
//
//        import java.util.HashMap;
//        import java.util.Map;
//        import java.util.Random;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = "MyFirebaseMessagingService";
//
//    NotificationManager notificationManager;
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        Map<String, String> map = new HashMap<>();
//        map.put("title", "hello from me");
//
//
//        notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        //Setting up Notification channels for android O and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            setupChannels();
//        }
//        int notificationId = new Random().nextInt(60000);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Common.current_token)
//                .setSmallIcon(R.drawable.ic_arrow_left)  //a resource for your custom small icon
//                .setContentTitle(remoteMessage.getData().put("title","erel")) //the "title" value you sent in your notification
//                .setContentText(remoteMessage.getData().get("message")) //ditto
//                .setAutoCancel(true)  //dismisses the notification on click
//                .setSound(defaultSoundUri);
//
////        notificationManager =
////                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
//    }
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void setupChannels() {
//
//
//        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
//        String adminChannelDescription = getString(R.string.notifications_admin_channel_descripti);
//
//        NotificationChannel adminChannel;
//        adminChannel = new NotificationChannel(Common.current_token, adminChannelName, NotificationManager.IMPORTANCE_LOW);
//        adminChannel.setDescription(adminChannelDescription);
//        adminChannel.enableLights(true);
//        adminChannel.setLightColor(Color.RED);
//        adminChannel.enableVibration(true);
//        if (notificationManager != null) {
//            notificationManager.createNotificationChannel(adminChannel);
//
//        }
//    }
//}