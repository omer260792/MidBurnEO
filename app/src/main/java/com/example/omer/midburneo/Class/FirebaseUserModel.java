package com.example.omer.midburneo.Class;

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
