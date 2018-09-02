package com.example.omer.midburneo.Class;

public class Friend {

    public String name;
    public String camp;
    public String uidReceiver;
    public String lastMsg;
    public String status;
    public String image;


    public Friend() {

    }


    public Friend(String name, String camp, String uidReceiver, String image, String lastMsg, String status) {
        this.name = name;
        this.camp = camp;
        this.uidReceiver = uidReceiver;
        this.image = image;
        this.lastMsg = lastMsg;
        this.status = status;


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



}

