package com.example.omer.midburneo.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.ChatAc;
import com.example.omer.midburneo.Tabs.ChatListAc;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by haripal on 7/25/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "From: " + remoteMessage.getSentTime());
        Log.d(TAG, "From: " + remoteMessage.getNotification().getTitle());
        Log.d(TAG, "From: " + remoteMessage.getNotification().getBodyLocalizationKey());
        Log.d(TAG, "From: " + remoteMessage.getNotification().getTitleLocalizationArgs());
        Log.d(TAG, "From: " + remoteMessage.getNotification().getTag());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        //Calling method to show notification
        showNotification(remoteMessage.getNotification().getBody(), remoteMessage.getFrom());
    }

    private void showNotification(String messageBody, String sender) {
        Intent intent = new Intent(this, ChatListAc.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_midburn_app)
                .setContentTitle(sender)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }

}
