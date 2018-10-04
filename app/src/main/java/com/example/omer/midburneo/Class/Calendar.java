package com.example.omer.midburneo.Class;

public class Calendar {

    public String msg;
    public String sender;
    public String time;
    public String msguid;
    public String timeSet;

    public String getCountRaw() {
        return countRaw;
    }

    public String setCountRaw(String countRaw) {
        this.countRaw = countRaw;
        return countRaw;
    }

    public String countRaw;


    public Calendar(String msg, String sender, String time, String msguid, String timeSet) {
        this.msg = msg;
        this.sender = sender;
        this.time = time;
        this.msguid = msguid;
        this.timeSet = timeSet;
    }

    public Calendar(){

    }

    public String getMsg() {
        return msg;
    }

    public String setMsg(String msg) {
        this.msg = msg;
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public String setSender(String sender) {
        this.sender = sender;
        return sender;
    }

    public String getTime() {
        return time;
    }

    public String setTime(String time) {
        this.time = time;
        return time;
    }

    public String getMsguid() {
        return msguid;
    }

    public void setMsguid(String msguid) {
        this.msguid = msguid;
    }


    public String getTimeSet() {
        return timeSet;
    }

    public String setTimeSet(String timeSet) {
        this.timeSet = timeSet;
        return timeSet;
    }

}
