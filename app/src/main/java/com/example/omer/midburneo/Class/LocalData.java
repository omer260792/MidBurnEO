package com.example.omer.midburneo.Class;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import static com.example.omer.midburneo.RegisterAc.SHPRF;


public class LocalData {

    private static final String APP_SHARED_PREFS = "RemindMePref";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private static final String reminderStatus = "reminderStatus";
    private static final String hour = "hour";
    private static final String min = "min";
    private static final String titlePush = "titlePush";
    private static final String bodyPush = "bodyPush";
    private static final String registration_ids_static = "jsonArray";


    public LocalData(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(SHPRF, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    // Settings Page Set Reminder

    public boolean getReminderStatus() {
        return appSharedPrefs.getBoolean(reminderStatus, false);
    }

    public void setReminderStatus(boolean status) {
        prefsEditor.putBoolean(reminderStatus, status);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Hour)

    public int get_hour() {
        return appSharedPrefs.getInt(hour, 20);
    }

    public void set_hour(int h) {
        prefsEditor.putInt(hour, h);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Minutes)

    public int get_min() {
        return appSharedPrefs.getInt(min, 0);
    }

    public String getTitlePush() {
        return appSharedPrefs.getString(titlePush, "titlePush");
    }

    public String getBodyPush() {
        return appSharedPrefs.getString(bodyPush, "titlePush");
    }

    public void setBodyPush(String body) {
        prefsEditor.putString(bodyPush, body);
        prefsEditor.commit();
    }

    public void setTitlePush(String title) {
        prefsEditor.putString(titlePush, title);
        prefsEditor.commit();
    }

    public void set_min(int m) {
        prefsEditor.putInt(min, m);
        prefsEditor.commit();
    }

    public void reset() {
        prefsEditor.clear();
        prefsEditor.commit();

    }

    public String getRegistration_ids_static() {
        return appSharedPrefs.getString(registration_ids_static, "pushJsonArray");
    }

    public void set_Registration_ids_static(String jsonArray) {
        prefsEditor.putString(registration_ids_static, jsonArray);
        prefsEditor.commit();

    }


}
