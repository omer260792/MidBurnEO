package com.example.omer.midburneo.Tabs;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteQueryBuilder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


import com.example.omer.midburneo.Adapters.ImageAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
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
    private DBHelper dbHelper;
    public SQLiteDatabase db;

    public String current_uid, current_name, image, current_camp, current_admin, timeString, getUid, getUidUsers, get_lastmsg, get_phone, get_device, get_token, get_chat,
            getNameReceiver, get_image, name, get_role, get_email, get_status, get_admin;
    public int countSqLiteUpdate;
    public String tokenUser = "no token";
    public String current_uid_camp = "";
    private boolean isConnected;


    public static FirebaseUserModel firebaseUserModel;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private ProgressDialog mprogress;

    protected void onCreate(@Nullable Bundle savedInstanceState) {


        // Determine if you have an internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        //on create
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //Progress bar for loading
        mprogress = new ProgressDialog(MainPageAc.this);
        mprogress.setMessage("מוריד מידע");
        mprogress.show();
        Log.e(TAG, "mprogress mprogress" + "=" + String.valueOf(isConnected));

        //SharedPreferences
        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        firebaseUserModel = new FirebaseUserModel();



        //true first
        if (isConnected) {
            Log.e(TAG, "internet connection true" + "=" + String.valueOf(isConnected));

            if (firebaseUserModel.getChat() != null) {
                FirebaseUserModel.getSPToFirebaseUserModel(firebaseUserModel, getApplicationContext());

            }
            current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mAuth = FirebaseAuth.getInstance();


//            getUserDBFromFireBase();
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
        Log.e(TAG, "internet gridview" + "=" + String.valueOf(isConnected));


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

    @Override
    protected void onStart() {
        super.onStart();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        com.google.firebase.database.ValueEventListener userValueEventListener = new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String uid = dataSnapshot.getKey();

                Log.e(TAG, "getUserDBFromFireBase" + "=" + String.valueOf(isConnected));


                current_admin = dataSnapshot.child("admin").getValue().toString();
                current_name = dataSnapshot.child("name").getValue().toString();
                current_camp = dataSnapshot.child("camps").getValue().toString();
                current_uid_camp = dataSnapshot.child("chat").getValue().toString();
                String deviceUserIntent = dataSnapshot.child("device_id").getValue().toString();
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



                dbHelper = new DBHelper(getApplicationContext());


                UpdateUserOnline();


                FirebaseUserModel.saveDataInSharedPre(firebaseUserModel, getApplicationContext());
                UpdateSqliteFromFireBase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mUserDatabase.addListenerForSingleValueEvent(userValueEventListener);

    }



    public void UpdateSqliteFromFireBase() {

        db = dbHelper.getWritableDatabase();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Users");
        Query query = discussionRoomsRef.orderByChild("chat").equalTo(firebaseUserModel.getChat());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    getUidUsers = ds.getKey();
                    getNameReceiver = ds.child(FeedReaderContract.FeedEntry.NAME).getValue(String.class);
                    get_image = ds.child(FeedReaderContract.FeedEntry.IMAGE).getValue(String.class);
                    get_lastmsg = ds.child(FeedReaderContract.FeedEntry.LASTMSG).getValue(String.class);
                    get_phone = ds.child(FeedReaderContract.FeedEntry.PHONE).getValue(String.class);
                    get_device = ds.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID).getValue(String.class);
                    get_chat = ds.child(FeedReaderContract.FeedEntry.CHAT).getValue(String.class);
                    get_token = ds.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN).getValue(String.class);
                    get_role = ds.child(FeedReaderContract.FeedEntry.ROLE).getValue(String.class);
                    get_status = ds.child(FeedReaderContract.FeedEntry.STATUS).getValue(String.class);
                    get_email = ds.child(FeedReaderContract.FeedEntry.EMAIL).getValue(String.class);
                    get_admin = ds.child(FeedReaderContract.FeedEntry.ADMIN).getValue(String.class);

                    // #### Check if exsist ChatRooms Uid Between Users in the camp
                    Boolean CheckUidEx;
                    String getMyKeyMsg = UUID.randomUUID().toString();
                    try {
                        CheckUidEx = ds.child(current_uid).exists();
                        if (CheckUidEx.equals(false)) {

                            if (getNameReceiver.equals(firebaseUserModel.getCamp())) {
                                getMyKeyMsg = get_chat;
                                saveUserChatFirebase(getMyKeyMsg);
                                Log.e(TAG, "Save uid group");

                            } else {
                                saveUserChatFirebase(getMyKeyMsg);
                                Log.e(TAG, "Save uid user");

                            }
                        } else {
                            getMyKeyMsg = ds.child(current_uid).getValue(String.class);
                        }
                    } catch (Exception e) {
                    }

                    // #### Check in Sqlite if exsist new User in the camp
                    try {
                        Boolean check = query(getUidUsers, FeedReaderContract.FeedEntry.UID);
                        if (getUidUsers.equals(firebaseUserModel.getChat())) {

                            if (check.equals(false)) {
                                getMyKeyMsg = get_chat;
                                dbHelper.SaveDBSqliteUser(getNameReceiver, firebaseUserModel.getCamp(), getUidUsers, get_image, get_lastmsg, get_phone, get_device, get_token, getMyKeyMsg);
                                Log.e(TAG, "Save Group");

                            } else {
                                    if (get_role.equals("default")) {
                                        get_role = "קבוצה רשמית";
                                    }
                                    String camps = firebaseUserModel.getCamp();
                                    String chat = firebaseUserModel.getChat();
                                    ContentValues data = new ContentValues();
                                    data.put(FeedReaderContract.FeedEntry.ADMIN, get_admin);
                                    data.put(FeedReaderContract.FeedEntry.CAMPS, camps);
                                    data.put(FeedReaderContract.FeedEntry.CHAT, chat);
                                    data.put(FeedReaderContract.FeedEntry.EMAIL, get_email);
                                    data.put(FeedReaderContract.FeedEntry.IMAGE, get_image);
                                    data.put(FeedReaderContract.FeedEntry.NAME, getNameReceiver);
                                    data.put(FeedReaderContract.FeedEntry.ROLE, get_role);
                                    data.put(FeedReaderContract.FeedEntry.PHONE, get_phone);
                                    data.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, get_device);
                                    data.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, get_token);
                                    data.put(FeedReaderContract.FeedEntry.CHAT_ROOMS, getMyKeyMsg);
                                    db.update(TABLE_NAME, data, "_id=" + countSqLiteUpdate, null);

                            }
                        } else {
                            if (check.equals(false)) {
                                if (!current_uid.equals(getUidUsers)) {
                                    dbHelper.SaveDBSqliteUser(getNameReceiver, firebaseUserModel.getCamp(), getUidUsers, get_image, get_lastmsg, get_phone, get_device, get_token, getMyKeyMsg);
                                    Log.e(TAG, "Save User");

                                }

                            } else {

                                if (!current_uid.equals(getUidUsers)) {
                                    if (get_role.equals("default")) {
                                        get_role = "אין תפקיד";
                                    }

                                    try {
                                        String camps = firebaseUserModel.getCamp();
                                        String chat = firebaseUserModel.getChat();
                                        ContentValues data = new ContentValues();
                                        data.put(FeedReaderContract.FeedEntry.ADMIN, get_admin);
                                        data.put(FeedReaderContract.FeedEntry.CAMPS, camps);
                                        data.put(FeedReaderContract.FeedEntry.CHAT, chat);
                                        data.put(FeedReaderContract.FeedEntry.EMAIL, get_email);
                                        data.put(FeedReaderContract.FeedEntry.IMAGE, get_image);
                                        data.put(FeedReaderContract.FeedEntry.NAME, getNameReceiver);

                                        data.put(FeedReaderContract.FeedEntry.ROLE, get_role);
                                        data.put(FeedReaderContract.FeedEntry.PHONE, get_phone);
                                        data.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, get_device);
                                        data.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, get_token);
                                        data.put(FeedReaderContract.FeedEntry.CHAT_ROOMS, getMyKeyMsg);
                                        db.update(TABLE_NAME, data, "_id=" + countSqLiteUpdate, null);

                                        Log.e(TAG, "Upload User");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    // #### Check if exsist Groups Uid Between Users in the camp
                    if (getUidUsers.equals(current_uid)) {

                        Boolean CheckUid = ds.child(FeedReaderContract.FeedEntry.GROUP).exists();
                        if (CheckUid.equals(true)) {

                            for (DataSnapshot dataSnapshot1 : ds.child(FeedReaderContract.FeedEntry.GROUP).getChildren()) {

                                String key = dataSnapshot1.getKey();
                                String nameGroup = String.valueOf(dataSnapshot1.getValue());

                                Boolean check = query(key, FeedReaderContract.FeedEntry.CHAT_ROOMS);
                                if (check.equals(false)) {
                                    dbHelper.SaveDBSqliteUser(nameGroup, firebaseUserModel.getCamp(), current_uid, get_image, "0", "default", "default", "default", key);
                                    Log.e(TAG, "Save user Groups");

                                } else {
                                    try {
                                        Log.e(TAG, "update user Groups");

                                        ContentValues data = new ContentValues();
                                        data.put(FeedReaderContract.FeedEntry.IMAGE, get_image);
                                        data.put(FeedReaderContract.FeedEntry.NAME, nameGroup);
                                        data.put(FeedReaderContract.FeedEntry.ROLE, "");
                                        db.update(TABLE_NAME, data, "_id=" + countSqLiteUpdate, null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        }
                    }


                }// end for Loop

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);


    }

    public void UpdateUserOnline() {


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        Log.e(TAG, "UpdateUserOnline" + "=" + String.valueOf(isConnected));

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

    private Boolean query(String selectionArgss, String selectio) {


        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.ADMIN,
                FeedReaderContract.FeedEntry.CAMPS,
                FeedReaderContract.FeedEntry.CHAT,
                FeedReaderContract.FeedEntry.EMAIL,
                FeedReaderContract.FeedEntry.IMAGE,
                FeedReaderContract.FeedEntry.NAME,
                FeedReaderContract.FeedEntry.NUMBER,
                FeedReaderContract.FeedEntry.PASSWORD,
                FeedReaderContract.FeedEntry.STATUS,
                FeedReaderContract.FeedEntry.TIME,
                FeedReaderContract.FeedEntry.UID,
                FeedReaderContract.FeedEntry.LASTMSG,
                FeedReaderContract.FeedEntry.ROLE,
                FeedReaderContract.FeedEntry.PHONE,
                FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID,
                FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN,
                FeedReaderContract.FeedEntry.CHAT_ROOMS

        };

        String selection = selectio + " = ?";
        String[] selectionArgs = {selectionArgss};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);
        Log.i(TAG, "3333" + selectionArgss);
        Log.i(TAG, "3333" + selectio);


        try {
            Cursor cursor = builder.query(dbHelper.getReadableDatabase(),
                    projection, selection, selectionArgs, null, null, null);
            if (cursor == null) {
                return null;
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
            countSqLiteUpdate = cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;


    }

    public void saveUserChatFirebase(String uid) {
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(getUidUsers);

        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put(current_uid, uid);

        mUserDatabase.updateChildren(stringObjectHashMap);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        Map<String, Object> stringObjectHashMap1 = new HashMap<>();
        stringObjectHashMap1.put(getUidUsers, uid);

        mUserDatabase.updateChildren(stringObjectHashMap1);


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


