package com.example.omer.midburneo.Class;

import android.content.SharedPreferences;

public class User {

    public String admin;
    public String camps;
    public String chat;
    public String email;
    public String image;
    public String name;
    public String number;
    public String password;
    public String status;
    public String url;
    public String uid;
    public String time;
    public String id;




    public User(String admin, String camps, String chat, String email, String image, String name, String number, String password, String status, String url, String uid, String time, String id) {
        this.admin = admin;
        this.camps = camps;
        this.chat = chat;
        this.email = email;
        this.image = image;
        this.name = name;
        this.number = number;
        this.password = password;
        this.status = status;
        this.url = url;
        this.uid = uid;
        this.time = time;
        this.id = id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getCamps() {
        return camps;
    }

    public void setCamps(String camps) {
        this.camps = camps;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

