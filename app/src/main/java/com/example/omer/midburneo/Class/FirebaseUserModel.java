package com.example.omer.midburneo.Class;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.example.omer.midburneo.Tabs.MainPageAc.SHPRF;

public class FirebaseUserModel {


    String image = "";
    String role = "";
    String deviceId;
    String admin = "";
    String number = "";
    String uidReceiver = "";
    String camp = "";
    String pass = "";
    String phone = "";
    String chat = "";
    String deviceToken = "";
    String name = "";
    String lastMsg = "";
    String online = "";
    String time = "";
    String email = "";
    String status = "";





    public static Boolean saveDataInSharedPre(FirebaseUserModel firebaseUserModel, Context context) {

        SharedPreferences prefs;
        String SHPRF = "User";
        String image = "";
        String role = "";
        String deviceId = "";
        String admin = "";
        String number = "";
        String camp = "";
        String phone = "";
        String chat = "";
        String deviceToken = "";
        String name = "";
        String time = "";
        String email = "";
        String uid = "";

        image = firebaseUserModel.getImage();
        role = firebaseUserModel.getRole();
        deviceId = firebaseUserModel.getDeviceId();
        admin = firebaseUserModel.getAdmin();
        number = firebaseUserModel.getNumber();
        camp = firebaseUserModel.getCamp();
        phone = firebaseUserModel.getPhone();
        chat = firebaseUserModel.getChat();
        deviceToken = firebaseUserModel.getDeviceToken();
        name = firebaseUserModel.getName();
        time = firebaseUserModel.getTime();
        email = firebaseUserModel.getEmail();
        uid = firebaseUserModel.getUidReceiver();


        prefs = context.getSharedPreferences(SHPRF, MODE_PRIVATE);

        prefs.edit().putString(FeedReaderContract.FeedEntry.IMAGE, image).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.ROLE , role).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID,deviceId).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.ADMIN, admin).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.CAMPS, camp).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.PHONE, phone).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.NUMBER, number).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.CHAT, chat).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, deviceToken).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.NAME, name).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.TIME, time).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.EMAIL, email).apply();
        prefs.edit().putString(FeedReaderContract.FeedEntry.UID, uid).apply();

        return true;
    }


    public static Boolean getSPToFirebaseUserModel(FirebaseUserModel firebaseUserModel, Context context) {

        SharedPreferences prefs;
        prefs = context.getSharedPreferences(SHPRF, MODE_PRIVATE);






       String ss = firebaseUserModel.setName(prefs.getString(FeedReaderContract.FeedEntry.NAME, null));
        firebaseUserModel.setDeviceId(prefs.getString(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, null));
        firebaseUserModel.setDeviceToken(prefs.getString(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, null));
        firebaseUserModel.setImage(prefs.getString(FeedReaderContract.FeedEntry.IMAGE, null));
        firebaseUserModel.setRole(prefs.getString(FeedReaderContract.FeedEntry.ROLE, null));
        firebaseUserModel.setAdmin(prefs.getString(FeedReaderContract.FeedEntry.ADMIN, null));
        firebaseUserModel.setNumber(prefs.getString(FeedReaderContract.FeedEntry.NUMBER, null));
        firebaseUserModel.setCamp(prefs.getString(FeedReaderContract.FeedEntry.CAMPS, null));
        firebaseUserModel.setPhone(prefs.getString(FeedReaderContract.FeedEntry.PHONE, null));
        firebaseUserModel.setChat(prefs.getString(FeedReaderContract.FeedEntry.CHAT, null));
        firebaseUserModel.setStatus(prefs.getString(FeedReaderContract.FeedEntry.STATUS, null));



        return true;
    }


    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }


    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public FirebaseUserModel() {
        /*Blank default constructor essential for Firebase*/
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        this.name = name;
        return name;
    }

    public String getCamp() {
        return camp;
    }

    public void setCamp(String camp) {
        this.camp = camp;
    }

    public String getUidReceiver() {
        return uidReceiver;
    }

    public void setUidReceiver(String uidReceiver) {
        this.uidReceiver = uidReceiver;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
        return deviceToken;
    }


}
