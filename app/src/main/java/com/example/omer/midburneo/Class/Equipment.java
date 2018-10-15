package com.example.omer.midburneo.Class;

public class Equipment {


    public String image;
    public String nameProd;
    public String content;
    public String time;
    public String mount;
    public String mountCurrent;
    public String sender;
    public String uid;
    public String count;


    public Equipment(String image, String nameProd, String content, String time, String mount, String mountCurrent, String sender, String uid) {
        this.image = image;
        this.nameProd = nameProd;
        this.content = content;
        this.time = time;
        this.mount = mount;
        this.mountCurrent = mountCurrent;
        this.sender = sender;
        this.uid = uid;
    }

    public Equipment() {

    }

    public String getImage() {
        return image;
    }

    public String setImage(String image) {
        this.image = image;
        return image;
    }

    public String getNameProd() {
        return nameProd;
    }

    public String setNameProd(String nameProd) {
        this.nameProd = nameProd;
        return nameProd;
    }

    public String getContent() {
        return content;
    }

    public String setContent(String content) {
        this.content = content;
        return content;
    }

    public String getTime() {
        return time;
    }

    public String setTime(String time) {
        this.time = time;
        return time;
    }

    public String getMount() {
        return mount;
    }

    public String setMount(String mount) {
        this.mount = mount;
        return mount;
    }

    public String getMountCurrent() {
        return mountCurrent;
    }

    public String setMountCurrent(String mountCurrent) {
        this.mountCurrent = mountCurrent;
        return mountCurrent;
    }

    public String getSender() {
        return sender;
    }

    public String setSender(String sender) {
        this.sender = sender;
        return sender;
    }

    public String getUid() {
        return uid;
    }

    public String setUid(String uid) {
        this.uid = uid;
        return uid;
    }

    public String getCount() {
        return count;
    }

    public String setCount(String count) {
        this.count = count;
        return count;
    }

}
