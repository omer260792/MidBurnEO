package com.example.omer.midburneo.Class;

import android.widget.EditText;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Camp {


    public String user;
    public String camptheme;

   // public Map<String, Boolean> stars = new HashMap<>();

    public Camp() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Camp(String user, String camptheme) {
        this.user = user;
        this.camptheme = camptheme;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("camptheme", camptheme);



        return result;
    }
}

