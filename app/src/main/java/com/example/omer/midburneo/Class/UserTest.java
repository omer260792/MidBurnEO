package com.example.omer.midburneo.Class;

import android.content.SharedPreferences;


public class UserTest {


    private static final UserTest user = new UserTest();

    public static UserTest getInstance() {
        return user;
    }

    public String firebaseKey = "firebaseKey";
    public String name = "Owner";
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


    public static final String appPreferences = "ChattingAppPreferences";

    public static final String Key = "keyKey";
    public static final String Name = "nameKey";
    public static final String DeviceToken = "deviceTokenKey";
    public static final String DeviceId = "deviceIdKey";

    public SharedPreferences sharedpreferences;

    private UserTest() {
    }

    public Boolean login(FirebaseUserModel firebaseUserModel) {


        image = firebaseUserModel.getImage();
        role = firebaseUserModel.getRole();
        deviceId = firebaseUserModel.getDeviceId();
        admin = firebaseUserModel.getAdmin();
        number = firebaseUserModel.getNumber();
        uidReceiver = firebaseUserModel.getUidReceiver();
        camp = firebaseUserModel.getCamp();
        pass = firebaseUserModel.getPass();
        phone = firebaseUserModel.getPhone();
        chat = firebaseUserModel.getChat();
        deviceToken = firebaseUserModel.getDeviceToken();
        name = firebaseUserModel.getName();
        lastMsg = firebaseUserModel.getLastMsg();
        online = firebaseUserModel.getOnline();
        time = firebaseUserModel.getTime();
        email = firebaseUserModel.getEmail();
        status = firebaseUserModel.getStatus();



        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Key, firebaseKey);
        editor.putString(Name, name);
        editor.putString(DeviceId, deviceId);

        editor.apply();

        return true;
    }

    public void saveFirebaseKey(String key) {
        this.firebaseKey = key;

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Key, firebaseKey);
        editor.apply();
    }

    public void logout() {
        firebaseKey = "";
        name = "";
        deviceId = "";
        deviceToken = "";

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Key, firebaseKey);
        editor.putString(Name, name);
        editor.putString(DeviceId, deviceId);

        editor.apply();
    }
}