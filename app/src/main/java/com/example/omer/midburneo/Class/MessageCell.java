package com.example.omer.midburneo.Class;


public class MessageCell {
    String messageSender;
    String messageText;
    String messageDateTime;
    Boolean isSender;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;

    public MessageCell(String messageSender, String messageText, String messageDateTime, Boolean isSender, String image){
        this.messageSender = messageSender;
        this.messageText = messageText;
        this.messageDateTime = messageDateTime;
        this.isSender = isSender;
        this.image = image;
    }

    public String getMessageDateTime() {
        return messageDateTime;
    }

    public void setMessageDateTime(String messageDateTime) {
        this.messageDateTime = messageDateTime;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Boolean getSender() {
        return isSender;
    }

    public void setSender(Boolean sender) {
        isSender = sender;
    }
}