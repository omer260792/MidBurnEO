package com.example.omer.midburneo.Class;

public class MessageNote {

    public String msg;
    public String content;
    public String date;
    public String dateEnd;
    public String sender;
    public String uidMsg;
    public String uid;
    public String dateBool;


    public String count;


    public MessageNote(String msg, String content, String date, String dateEnd, String sender, String uidMsg, String uid, String dateBool) {
        this.msg = msg;
        this.content = content;
        this.date = date;
        this.dateEnd = dateEnd;
        this.sender = sender;
        this.uidMsg = uidMsg;
        this.uid = uid;
        this.dateBool = dateBool;

    }

    public String getCount() {
        return count;
    }

    public String setCount(String count) {
        this.count = count;
        return count;
    }


    public MessageNote(){

    }

    public String getContent() {
        return content;
    }

    public String setContent(String content) {
        this.content = content;
        return content;
    }

    public String getDate() {
        return date;
    }

    public String setDate(String date) {
        this.date = date;
        return date;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public String setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
        return dateEnd;
    }

    public String getDateBool() {
        return dateBool;
    }

    public String setDateBool(String dateBool) {
        this.dateBool = dateBool;
        return dateBool;
    }


    public String getTime() {
        return date;
    }

    public void setTime(String date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public String setSender(String sender) {
        this.sender = sender;
        return sender;
    }

    public String getMsg() {

        return msg;
    }

    public String setMsg(String msg) {
        this.msg = msg;
        return msg;
    }

    public String getUidMsg() {
        return uidMsg;
    }

    public String setUidMsg(String uidMsg) {
        this.uidMsg = uidMsg;
        return uidMsg;
    }
    public String getUid() {
        return uid;
    }

    public String setUid(String uid) {
        this.uid = uid;
        return uid;
    }


}
