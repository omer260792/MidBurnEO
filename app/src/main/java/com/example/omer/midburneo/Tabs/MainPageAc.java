package com.example.omer.midburneo.Tabs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import com.example.omer.midburneo.Adapters.ImageAdapter;
import com.example.omer.midburneo.Class.FirebaseUserModel;
import com.example.omer.midburneo.Class.User;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.ScheduleAc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import static com.example.omer.midburneo.DataBase.DBHelper.DATABASE_NAME;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_MESSAGE;


// ToDo push notification -- All Project
// ToDo picture's users in AlertDailog -- All Project
// ToDo change name's group from Camp-AllCamps-group   -- AdminAc
// ToDo refresh the recyclerview   -- ANoteAc
// ToDo save database to sqlite  -- ChatListAc
// ToDo save record path in stroge device directory  -- ChatListAc
// ToDo arranging folders in storage  -- ChatListAc
// ToDo permission excel app  -- All Project

public class MainPageAc extends AppCompatActivity {

    private static final String TAG = "MainPageAc";

    public static SharedPreferences prefs;
    public static String SHPRF = "User";

    private DatabaseReference mUserDatabase;
    private DatabaseReference mCurrentUser;
    private FirebaseAuth mAuth;

    public String current_uid, current_name, image, current_camp, current_admin, timeString;
    public String tokenUser = "no token";
    public String current_uid_camp = "";
    public static FirebaseUserModel firebaseUserModel;
    private boolean isConnected;
    private DBHelper dbHelper;
    public SQLiteDatabase db;
    private ProgressDialog mprogress;


    static User user = User.getInstance();
    public static final String setImgUrlDefault = "https://firebasestorage.googleapis.com/v0/b/midburneo-6d072.appspot.com/o/profile_images%2Fcropped5081028198796683166.jpg?alt=media&token=8c49a7b9-2ee5-4ea6-b7c2-52199ef167f8";


    protected void onCreate(@Nullable Bundle savedInstanceState) {


        // Determine if you have an internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mprogress = new ProgressDialog(MainPageAc.this);
        mprogress.setMessage("מוריד מידע");
        mprogress.show();


        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        firebaseUserModel = new FirebaseUserModel();


        if (isConnected) {
            Log.e(TAG, "internet connection" + "=" + String.valueOf(isConnected));

            if (firebaseUserModel.getChat() != null) {
                FirebaseUserModel.getSPToFirebaseUserModel(firebaseUserModel, getApplicationContext());
                DATABASE_NAME = firebaseUserModel.getChat();

            }
            current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mAuth = FirebaseAuth.getInstance();


            getUserDBFromFireBase();
            UpdateUserOnline();

        } else {
            Log.e(TAG, "internet connection" + "=" + String.valueOf(isConnected));

            FirebaseUserModel.getSPToFirebaseUserModel(firebaseUserModel, getApplicationContext());
            DATABASE_NAME = firebaseUserModel.getChat();
            TABLE_NAME_MESSAGE = firebaseUserModel.getUidReceiver();
            dbHelper = new DBHelper(getApplicationContext());

        }


        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        current_admin = firebaseUserModel.getAdmin();
                        startActivity(new Intent(MainPageAc.this, AdminAc.class));

                        break;
                    case 1:
                        Intent intent = new Intent(MainPageAc.this, ChatAc.class);
                        startActivity(intent);
                        break;
                    case 2:
                        startActivity(new Intent(MainPageAc.this, EquipmentAc.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainPageAc.this, ScheduleAc.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainPageAc.this, NotesAc.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainPageAc.this, ProfileAc.class));
                        break;


                }
            }
        });


    }


    public void getUserDBFromFireBase() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String uid = dataSnapshot.getKey();

                current_admin = dataSnapshot.child("admin").getValue().toString();
                current_name = dataSnapshot.child("name").getValue().toString();
                current_camp = dataSnapshot.child("camps").getValue().toString();
                current_uid_camp = dataSnapshot.child("chat").getValue().toString();
                String deviceUserIntent = dataSnapshot.child("device_id").getValue().toString();
                //tokenUser = dataSnapshot.child("device_token").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String number = dataSnapshot.child("number").getValue().toString();
                String time = dataSnapshot.child("time").getValue().toString();
                String role = dataSnapshot.child("role").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();


                firebaseUserModel.setName(current_name);
                firebaseUserModel.setDeviceId(deviceUserIntent);
                firebaseUserModel.setImage(image);
                firebaseUserModel.setRole(role);
                firebaseUserModel.setDeviceId(deviceUserIntent);
                firebaseUserModel.setAdmin(current_admin);
                firebaseUserModel.setNumber(number);
                firebaseUserModel.setCamp(current_camp);
                firebaseUserModel.setPhone(phone);
                firebaseUserModel.setChat(current_uid_camp);
                firebaseUserModel.setStatus("status");
                firebaseUserModel.setTime(time);
                firebaseUserModel.setEmail(email);
                firebaseUserModel.setUidReceiver(uid);
                DATABASE_NAME = firebaseUserModel.getChat();

                UpdateUserOnline();


                FirebaseUserModel.saveDataInSharedPre(firebaseUserModel, getApplicationContext());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void UpdateUserOnline() {


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            // Get new Instance ID token
                            tokenUser = task.getResult().getToken();

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
                            mUserDatabase.keepSynced(true);

                            Map<String, Object> mapCampsUpdates = new HashMap<>();
                            mapCampsUpdates.put("online", "true");
                            mapCampsUpdates.put("device_token", tokenUser);

                            mUserDatabase.updateChildren(mapCampsUpdates);

                            mprogress.dismiss();
                        } else {
                            Log.w(TAG, "getInstanceId failed", task.getException());

                        }

                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isConnected) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
            mUserDatabase.keepSynced(true);

            current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            long currentDateTime = System.currentTimeMillis();
            timeString = String.valueOf(currentDateTime);
        }
        // before on destroy take all the data

    }

    @Override
    protected void onDestroy() {

        if (isConnected) {

            Map<String, Object> mapCampsUpdates = new HashMap<>();
            mapCampsUpdates.put("time", timeString);
            mapCampsUpdates.put("online", "false");

            mUserDatabase.updateChildren(mapCampsUpdates);

            super.onDestroy();
        }

    }

}


