package com.example.omer.midburneo.Service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.omer.midburneo.Class.FirebaseUserModel;
import com.example.omer.midburneo.Class.UserTest;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by haripal on 7/25/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    UserTest user = UserTest.getInstance();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = null;//FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken != null) {
            Log.d(TAG, "Refreshed token: " + refreshedToken);


           try {

               sendTokenToServer(refreshedToken);
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }

    public void sendTokenToServer(final String strToken) {
        // API call to send token to Server

        database =  FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        usersRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                for (com.google.firebase.database.DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    FirebaseUserModel firebaseUserModel = userSnapshot.getValue(FirebaseUserModel.class);

                    if (strToken != null && firebaseUserModel.getDeviceId().equals(user.deviceId) && !strToken.equals(firebaseUserModel.getDeviceToken())) {
                        user.deviceToken = strToken;

                        usersRef.child(userSnapshot.getKey()).child("device_token").setValue(strToken, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.i(TAG,  databaseError.toString());
                                } else {
                                    System.out.println("Refreshed Token Updated");
                                }
                            }
                        });
                        usersRef.child(userSnapshot.getKey()).child("deviceToken").setValue(strToken, new Firebase.CompletionListener() {

                            @Override
                            public void onComplete(com.firebase.client.FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Log.i(TAG, firebaseError.toString());
                                } else {
                                    System.out.println("Refreshed Token Updated");
                                }
                            }

                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

    }
}