package com.example.omer.midburneo.Class;

public class MessageNote {

    public String msg;
    public String time;
    public String sender;
    public String uidMsg;
    public String camp;



    public MessageNote(String msg, String time, String sender, String uidMsg, String camp) {
        this.msg = msg;
        this.time = time;
        this.sender = sender;
        this.uidMsg = uidMsg;
        this.camp = camp;

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUidMsg() {
        return uidMsg;
    }

    public void setUidMsg(String uidMsg) {
        this.uidMsg = uidMsg;
    }

    public String getCamp() {
        return camp;
    }

    public void setCamp(String camp) {
        this.camp = camp;
    }

}
