package com.example.omer.midburneo.Class;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.example.omer.midburneo.RegisterAc.SHPRF;

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
        String deviceId;
        String admin = "";
        String number = "";
        String camp = "";
        String phone = "";
        String chat = "";
        String deviceToken = "";
        String name = "";
        String time = "";
        String email = "";

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


        prefs = context.getSharedPreferences(SHPRF, MODE_PRIVATE);

        prefs.edit().putString(image, firebaseUserModel.getImage()).apply();
        prefs.edit().putString(role , firebaseUserModel.getRole()).apply();
        prefs.edit().putString(deviceId, firebaseUserModel.getDeviceId()).apply();
        prefs.edit().putString(admin, firebaseUserModel.getAdmin()).apply();
        prefs.edit().putString(number, firebaseUserModel.getNumber()).apply();
        prefs.edit().putString(camp, firebaseUserModel.getCamp()).apply();
        prefs.edit().putString(phone, firebaseUserModel.getPhone()).apply();
        prefs.edit().putString(chat, firebaseUserModel.getChat()).apply();
        prefs.edit().putString(deviceToken, firebaseUserModel.getDeviceToken()).apply();
        prefs.edit().putString(name, firebaseUserModel.getName()).apply();
        prefs.edit().putString(time, firebaseUserModel.getTime()).apply();
        prefs.edit().putString(email, firebaseUserModel.getEmail()).apply();

        return true;
    }


    public static Boolean getSPToFirebaseUserModel(FirebaseUserModel firebaseUserModel, Context context) {

        SharedPreferences prefs;
        prefs = context.getSharedPreferences(SHPRF, MODE_PRIVATE);


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



        firebaseUserModel.setName(prefs.getString(image, null));
        firebaseUserModel.setDeviceId(prefs.getString(role, null));
        firebaseUserModel.setDeviceToken(prefs.getString(deviceId, null));
        firebaseUserModel.setImage(prefs.getString(admin, null));
        firebaseUserModel.setRole(prefs.getString(number, null));
        firebaseUserModel.setDeviceId(prefs.getString(camp, null));
        firebaseUserModel.setAdmin(prefs.getString(phone, null));
        firebaseUserModel.setNumber(prefs.getString(chat, null));
        firebaseUserModel.setCamp(prefs.getString(deviceToken, null));
        firebaseUserModel.setPhone(prefs.getString(name, null));
        firebaseUserModel.setChat(prefs.getString(time, null));
        firebaseUserModel.setStatus(prefs.getString(email, null));



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

    public void setName(String name) {
        this.name = name;
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
