package com.example.omer.midburneo.Class;

public class Calendar {

    public String msg;
    public String sender;
    public String time;
    public String msguid;

    public Calendar(String msg, String sender, String time, String msguid) {
        this.msg = msg;
        this.sender = sender;
        this.time = time;
        this.msguid = msguid;
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

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsguid() {
        return msguid;
    }

    public void setMsguid(String msguid) {
        this.msguid = msguid;
    }

}
