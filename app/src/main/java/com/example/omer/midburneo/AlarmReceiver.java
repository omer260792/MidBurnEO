package com.example.omer.midburneo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.omer.midburneo.Class.LocalData;
import com.example.omer.midburneo.Tabs.MainPageAc;


public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LocalData localData = new LocalData(context);

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min());
                return;
            }
        }

        Log.d(TAG, "onReceive: ");

       // Trigger the notification
        NotificationScheduler.showNotification(context, MainPageAc.class,
                localData.getTitlePush(), localData.getTitlePush());

    }
}

