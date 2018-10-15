package com.example.omer.midburneo.Class;

import android.content.SharedPreferences;

public class User {


    private static final User user = new User();

    public static User getInstance() {
        return user;
    }

    public String name = "";
    public String deviceId = "";
    public String deviceToken = "";
    public String admin = "";
    public String camp = "";
    public String uidReceiver = "";
    public String lastMsg = "";
    public String status = "";
    public String image = "";
    public String uidCount = "";
    public String role = "";
    public String online = "";
    public String phone = "";
    public String time = "";
    public String chat = "";
    public String email = "";
    public String number = "";
    public String pass = "";


    public static final String appPreferences = "User";

    public static final String Key = "keyKey";
    public static final String Name = "nameKey";
    public static final String DeviceToken = "deviceTokenKey";
    public static final String DeviceId = "deviceIdKey";

    public SharedPreferences sharedpreferences;

    public static SharedPreferences prefs;
    public static String SHPRF = "User";

    private User() {
    }

    public Boolean login(FirebaseUserModel firebaseUserModel) {


        deviceId = firebaseUserModel.getDeviceId();
        deviceToken = firebaseUserModel.getDeviceToken();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        // editor.putString(Name, name);
        editor.putString(DeviceId, deviceId);

        editor.apply();

        return true;

    }


}