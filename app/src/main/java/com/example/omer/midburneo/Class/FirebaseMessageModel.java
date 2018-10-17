package com.example.omer.midburneo.Class;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

public class FirebaseMessageModel {

    private String senderId;
    private String receiverId;
    private String status;
    private String text;
    private Long createdDate;
    private String senderName;
    private String Id;
    private String image;
    private String record;

    public FirebaseMessageModel() {
        /*Blank default constructor essential for Firebase*/
    }

    public String getRecord() {
        return record;
    }

    public String setRecord(String record) {
        this.record = record;
        return record;
    }

    public String getImage() {
        return image;
    }

    public String setImage(String image) {
        this.image = image;
        return image;
    }


    public String getReceiverId() {
        return receiverId;
    }

    public String setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return receiverId;
    }

    public String getStatus() {
        return status;
    }

    public String setStatus(String status) {
        this.status = status;
        return status;
    }


    public String getSenderId() {
        return senderId;
    }

    public String setSenderId(String senderDeviceId) {
        this.senderId = senderDeviceId;
        return senderDeviceId;
    }

    public String getText() {
        return text;
    }

    public String setText(String text) {
        this.text = text;
        return text;
    }

    public java.util.Map<String, String> getCreatedDate() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreatedDateLong() {
        return createdDate;
    }

    public String setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
        return null;
    }

    public String getSenderName() {
        return senderName;
    }

    public String setSenderName(String senderName) {
        this.senderName = senderName;
        return senderName;
    }

    public String getId() {
        return Id;
    }

    public String setId(String id) {
        Id = id;
        return id;
    }
}