package com.example.omer.midburneo;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyFirebaseApp","MyFirebaseApp");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //To re-enable FCM, make a runtime call:
      //  FirebaseMessaging.getInstance().setAutoInitEnabled(true);

    }
}
//"AIzaSyBsYvy_Ycs2DLw6T_77-ddtJ_sXzVGg0EY"