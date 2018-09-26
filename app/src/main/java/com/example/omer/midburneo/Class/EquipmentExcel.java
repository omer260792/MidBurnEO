package com.example.omer.midburneo.Class;

public class EquipmentExcel {


    public String image;
    public String nameProd;
    public String content;
    public String time;
    public String mount;
    public String mountCurrent;
    public String sender;

    public EquipmentExcel(String image, String nameProd, String content, String time, String mount, String mountCurrent, String sender) {
        this.image = image;
        this.nameProd = nameProd;
        this.content = content;
        this.time = time;
        this.mount = mount;
        this.mountCurrent = mountCurrent;
        this.sender = sender;
    }

    public EquipmentExcel() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNameProd() {
        return nameProd;
    }

    public void setNameProd(String nameProd) {
        this.nameProd = nameProd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

    public String getMountCurrent() {
        return mountCurrent;
    }

    public void setMountCurrent(String mountCurrent) {
        this.mountCurrent = mountCurrent;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
