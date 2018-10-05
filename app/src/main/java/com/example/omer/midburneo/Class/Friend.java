package com.example.omer.midburneo.Class;

public class Friend {

    public String name;
    public String camp;
    public String uidReceiver;
    public String lastMsg;
    public String status;
    public String image;
    public String uidCount;
    public String role;
    public String online;
    public String phone;
    public String device;
    public String token;
    public String chatRoom;

    public String time;

    public String getDevice() {
        return device;
    }

    public String setDevice(String device) {
        this.device = device;
        return device;
    }

    public String getToken() {
        return token;
    }

    public String setToken(String token) {
        this.token = token;
        return token;
    }

    public Friend() {

    }


    public String getPhone() {
        return phone;
    }

    public String setPhone(String phone) {
        this.phone = phone;
        return phone;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public String setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
        return chatRoom;
    }

    public Friend(String name, String camp, String uidReceiver, String image, String lastMsg, String status, String uidCount, String role, String online, String phone, String chatRoom) {
        this.name = name;
        this.camp = camp;
        this.uidReceiver = uidReceiver;
        this.image = image;
        this.lastMsg = lastMsg;
        this.status = status;
        this.uidCount = uidCount;
        this.role = role;
        this.online = online;
        this.phone = phone;
        this.chatRoom = chatRoom;


    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }


    public String getRole() {
        return role;
    }

    public String setRole(String role) {
        this.role = role;
        return role;
    }

    public String setUidCount(String uidCount) {
        this.uidCount = uidCount;
        return uidCount;
    }

    public String getUidCount() {

        return uidCount;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        this.name = name;
        return name;
    }

    public String setCamp(String camp) {
        this.camp = camp;
        return camp;
    }

    public String getCamp() {
        return camp;
    }

    public String setUidReceiver(String uidReceiver) {
        this.uidReceiver = uidReceiver;
        return uidReceiver;
    }

    public String getUidReceiver() {

        return uidReceiver;
    }


    public String setImage(String image) {
        this.image = image;
        return image;
    }

    public String getImage() {

        return image;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public String setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
        return lastMsg;
    }

    public String getStatus() {
        return status;
    }

    public String setStatus(String status) {
        this.status = status;
        return status;
    }


    public String getTime() {
        return time;
    }

    public String setTime(String time) {
        this.time = time;
        return time;
    }


}

