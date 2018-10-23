package com.example.omer.midburneo.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.ChatAc;
import com.example.omer.midburneo.Tabs.ChatListAc;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by haripal on 7/25/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String yourAwesomeUnicodeString = "problemInAcMYFirebaseMsg";
    private String Stringname = "problemInAcMYFirebaseMsg";
    private String nameGroup;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.d(TAG, "From1: " + remoteMessage.getFrom());
        Log.d(TAG, "From2: " + remoteMessage.getSentTime());
        Log.d(TAG, "From3: " + remoteMessage.getMessageId());
        Log.d(TAG, "From4: " + remoteMessage.getData().get("Room"));
        Log.d(TAG, "From5: " + remoteMessage.getData().get("uidSender"));
        Log.d(TAG, "From5: " + remoteMessage.getData().get("name"));
        Log.d(TAG, "From6: " + remoteMessage.getData().get("deviceId"));
        Log.d(TAG, "From7: " + remoteMessage.toIntent().getStringExtra("name"));
        Log.d(TAG, "From8: " + remoteMessage.toIntent().getStringExtra("msg"));
        Log.d(TAG, "From10: " + remoteMessage.toIntent().getStringExtra("chat"));
        Log.d(TAG, "From9: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "From11: " + remoteMessage.getNotification().getTitle());


        try {
            yourAwesomeUnicodeString= URLDecoder.decode(remoteMessage.toIntent().getStringExtra("name"),"UTF-8");
            Log.e(TAG+3,yourAwesomeUnicodeString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            Stringname= URLDecoder.decode(remoteMessage.toIntent().getStringExtra("msg"),"UTF-8");
            Log.e(TAG+2,Stringname);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String name = remoteMessage.getNotification().getTitle();
        try {
            String nmaeAfterDecode = URLDecoder.decode(name,"UTF-8");

            if (!nmaeAfterDecode.equals(yourAwesomeUnicodeString)){
                yourAwesomeUnicodeString = nmaeAfterDecode;
                nameGroup = "קבוצה";

            }
            else {
                nameGroup = "";

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Calling method to show notification
        showNotification(Stringname,yourAwesomeUnicodeString);

        ChatListAc.getDataRemoteFromPush(remoteMessage);
    }

    private void showNotification(String messageBody, String sender) {



        Intent intent = new Intent(this, ChatListAc.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_midcamp_logo_big);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.midcamp_logo_app)
                .setStyle(new NotificationCompat.BigPictureStyle().bigLargeIcon(icon))
                .setContentTitle(sender)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSubText(nameGroup)
                .setContentIntent(pendingIntent);

        //.setStyle(new NotificationCompat.BigPictureStyle().bigLargeIcon(icon))
        //notificationObjectData

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }

}
