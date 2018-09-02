package com.example.omer.midburneo.Tabs;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.example.omer.midburneo.Adapters.MessageAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.Message;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Service.Common;
import com.example.omer.midburneo.Service.IRequestListener;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
import static com.example.omer.midburneo.DataBase.DBHelper.TABLE_NAME_MESSAGE;
import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_uid_camp_static;


public class ChatListAc extends AppCompatActivity implements IRequestListener {

    private static final String TAG = "ChatListAc";

    public RecyclerView recyclerView;
    public Button imgBtnSendMsg;
    public ImageView btnCamera;
    public EditText edittxtMsg;
    public TextView tvFriendUser, statusFriendUser;
    public CircleImageView imgUser;

    public String nameUserIntent, campUserIntent, uidUserIntent, imageUserIntent, statusUserIntent, current_image, UidRandom, current_uid, current_name, realTime, getMsgUid;
    public String time = "time";
    public String image = "image";
    public String text = "text";
    public String receiver = "text";
    public String sender = "text";
    public String nameSender = "text";
    public String get_msg_uid = "text";
    public String status = "false";
    public String TEST;

    public static final String setImgUrlDefault = "https://firebasestorage.googleapis.com/v0/b/midburneo-6d072.appspot.com/o/profile_images%2Fcropped5081028198796683166.jpg?alt=media&token=8c49a7b9-2ee5-4ea6-b7c2-52199ef167f8";

    private String num = "1";
    private long newRowId;

    private long countSqlLite;


    private long currentDateTime;

    private final List<Message> messageList = new ArrayList<>();
    RecyclerView.Adapter messageAdapter;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mUserDatabase;
    public DBHelper dbHelper;
    public SQLiteDatabase db;

    SharedPreferences prefs;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nameUserIntent = getIntent().getStringExtra("nameUidFriend");
        campUserIntent = getIntent().getStringExtra("campUidFriend");
        uidUserIntent = getIntent().getStringExtra("receiverUidFriend");
        imageUserIntent = getIntent().getStringExtra("imageUidFriend");
        statusUserIntent = getIntent().getStringExtra("statusUidFriend");

        tvFriendUser = findViewById(R.id.tvFriendUser);
        tvFriendUser.setText(nameUserIntent);

        dbHelper = new DBHelper(getApplicationContext());

        SaveUserTableSqlite();


        recyclerView = findViewById(R.id.recyclerView);
        imgBtnSendMsg = findViewById(R.id.imgBtnSendMsg);
        btnCamera = findViewById(R.id.btnCamera);
        edittxtMsg = findViewById(R.id.edittxtMsg);
        imgUser = findViewById(R.id.imgUser);
        statusFriendUser = findViewById(R.id.statusFriendUser);


        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        current_image = prefs.getString("image", null);
        String getStatus = prefs.getString("status", null);
        current_name = prefs.getString("name", null);
        // current_uid_camp_static = prefs.getString("camp", null);


        Common.current_token = FirebaseInstanceId.getInstance().getToken();

        TABLE_NAME_MESSAGE = uidUserIntent + "";

        SaveUserTableSqlite();

        Log.d("MY TOKEN", Common.current_token);


        if (getStatus.equals("true")) {
            statusFriendUser.setText("מחובר");
        } else {

        }

        if (imageUserIntent == null || imageUserIntent == "default") {

            Picasso.get().load(R.drawable.midburn_logo).error(R.drawable.midburn_logo).into(imgUser);

        } else {
            Picasso.get().load(imageUserIntent).error(R.drawable.midburn_logo).into(imgUser);

        }

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ChatListAc.this);
        recyclerView.setLayoutManager(layoutManager);

        getmsg();
        updateDBFireBaseToSqlLite();


        FirebaseAuth.getInstance();

        TEST = current_uid + uidUserIntent;

        imgBtnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = edittxtMsg.getText().toString();

                if (!text.equals("")) {

                    currentDateTime = System.currentTimeMillis();
                    time = String.valueOf(currentDateTime);
                    DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
                    DateFormat getTimeDmY = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");

                    realTime = getTimeHourMintus.format(currentDateTime);

                    get_msg_uid = UUID.randomUUID().toString();

                    if (current_image.equals("default")) {
                        current_image = setImgUrlDefault;
                    }

                    messageList.add(new Message(current_image, text, realTime, current_uid, uidUserIntent, nameUserIntent, current_name, get_msg_uid, TEST));
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(ChatListAc.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatListAc.this));
                    messageAdapter = new MessageAdapter(ChatListAc.this, messageList);
                    recyclerView.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    messageAdapter.notifyItemInserted(messageList.size());


                    num = "2";

                    SaveDBFireBase();


                    dbHelper.SaveDBSqlitee(text, nameUserIntent, current_uid, current_name, realTime, current_image, get_msg_uid, TEST);


                } else {
                    Toast.makeText(ChatListAc.this, "please type something", Toast.LENGTH_LONG).show();
                }

                edittxtMsg.setText("");

                String token = FirebaseInstanceId.getInstance().getToken();
                Toast.makeText(ChatListAc.this, "", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void SaveUserTableSqlite() {

        db = dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();

        try {
            dbHelper.onCreate(db);

            // db.execSQL(TABLE_NAME_MESSAGE);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void SaveDBFireBase() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(current_uid_camp_static).child(get_msg_uid);

        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("message", text);
        stringObjectHashMap.put("image", current_image);
        stringObjectHashMap.put("time", time);
        stringObjectHashMap.put("receiver", uidUserIntent);
        stringObjectHashMap.put("sender", current_uid);
        stringObjectHashMap.put("nameSender", current_name);
        stringObjectHashMap.put("status", TEST);
        mUserDatabase.updateChildren(stringObjectHashMap);

    }


    public void getmsg() {


        String TableName = uidUserIntent + "";

        try {
            messageList.addAll(dbHelper.getAllMsg(TableName));

            messageAdapter = new MessageAdapter(this, messageList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(messageAdapter);

            num = "2";

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void updateDBFireBaseToSqlLite() {

        //  getProfilesCount();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(current_uid_camp_static);

        String test = uidUserIntent + current_uid;
        Log.d(TAG, "UpdateDfireBase" + campUserIntent);

        mUserDatabase.orderByChild("status").equalTo(test).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.getChildren().toString());

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

//                    status = ds.child("status").getValue().toString();

                    image = ds.child("image").getValue(String.class);
                    nameSender = ds.child("nameSender").getValue(String.class);
                    sender = ds.child("sender").getValue(String.class);
                    receiver = ds.child("receiver").getValue(String.class);
                    text = ds.child("message").getValue(String.class);
                    time = ds.child("time").getValue(String.class);
                    get_msg_uid = ds.getKey();


                    dbHelper.SaveDBSqlitee(text, current_uid, nameUserIntent, current_name, time, image, get_msg_uid, status);

                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(current_uid_camp_static).child(get_msg_uid);

                    Map<String, Object> stringObjectHashMap = new HashMap<>();
      ;
                    stringObjectHashMap.put("status", "true");
                    mUserDatabase.updateChildren(stringObjectHashMap);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//
//        Query query = mUserDatabase.orderByChild("status").equalTo("false");
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    long FBCount = dataSnapshot.getChildrenCount();
//
//                    status = ds.child("status").getValue(String.class);
//
//
//                    if (status.equals(false)) {
//                        image = ds.child("image").getValue(String.class);
//                        nameSender = ds.child("nameSender").getValue(String.class);
//                        sender = ds.child("sender").getValue(String.class);
//                        receiver = ds.child("receiver").getValue(String.class);
//                        text = ds.child("message").getValue(String.class);
//                        time = ds.child("time").getValue(String.class);
//                        get_msg_uid = ds.getKey();
//
//                        status.equals(true);
//
//
//
//                    }
//
//                    dbHelper.SaveDBSqlitee(text, current_uid, nameUserIntent, current_name, time, image, get_msg_uid, status);
//
//
//                    Log.d(TAG, image);
//                    Log.d(TAG, nameSender);
//                    Log.d(TAG, sender);
//                    Log.d(TAG, text);
//                    Log.d(TAG, time);
//                    Log.d(TAG, String.valueOf(FBCount));
//                    Log.d("countSqlLite", String.valueOf(countSqlLite));
//                    Log.d("FBCount", String.valueOf(FBCount));
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "DBError");
//
//            }
//        };
//        query.addListenerForSingleValueEvent(valueEventListener);

    }


    public void SendPushNotification() {

        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(ChatListAc.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onComplete() {
        Log.d(TAG, "Token registered successfully in the DB");

    }

    @Override
    public void onError(String message) {
        Log.d(TAG, "Error trying to register the token in the DB: " + message);
    }

    public long getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME_MESSAGE;
        SQLiteDatabase dbr = dbHelper.getReadableDatabase();
        Cursor cursor = dbr.rawQuery(countQuery, null);
        long count = cursor.getCount();
        countSqlLite = (long) count;
        cursor.close();

        return countSqlLite;
    }

}




