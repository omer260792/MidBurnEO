package com.example.omer.midburneo.Class;

public class Message {

    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";
    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";


    public String image;
    public String msg;
    public String time;
    public String sender;
    public String receiver;
    public String name_receiver;
    public String name_sender;
    public String uidMsg;
    public String status;




    public Message(String image, String msg, String time, String sender, String receiver, String name_receiver, String name_sender, String uidMsg, String status){

        this.image = image;
        this.msg = msg;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
        this.name_receiver = name_receiver;
        this.name_sender = name_sender;
        this.uidMsg = uidMsg;
        this.status = status;
    }

    public Message() {

    }

    public String getImage() {
        return image;
    }

    public String setImage(String image) {
        this.image = image;
        return image;
    }

    public String getMsg() {
        return msg;
    }


    public String setMsg(String msg) {
        this.msg = msg;
        return msg;
    }



    public String getTime() {
        return time;
    }

    public String setTime(String time) {
        return time;
    }

    public String getSender() {
        return sender;
    }

    public String setSender(String sender) {
        this.sender = sender;
        return sender;
    }

    public String setReceiver(String receiver) {
        this.receiver = receiver;
        return receiver;
    }

    public String getReceiver() {

        return receiver;
    }

    public String getName_receiver() {
        return name_receiver;
    }

    public String setName_receiver(String name_receiver) {
        this.name_receiver = name_receiver;
        return name_receiver;
    }

    public String getName_sender() {
        return name_sender;
    }

    public void setName_sender(String name_sender) {
        this.name_sender = name_sender;
    }

    public String getUidMsg() {
        return uidMsg;
    }

    public String setUidMsg(String uidMsg) {
        this.uidMsg = uidMsg;
        return uidMsg;
    }

    public String getStatus() {
        return status;
    }

    public String setStatus(String status) {
        this.status = status;
        return status;
    }

}
