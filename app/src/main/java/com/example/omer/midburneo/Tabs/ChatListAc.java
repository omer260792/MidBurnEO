package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
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
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
import static com.example.omer.midburneo.DataBase.DBHelper.BoolRefresh;
import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;
import static com.example.omer.midburneo.Tabs.MainPageAc.TABLE_NAME_MESSAGE;


public class ChatListAc extends AppCompatActivity {

    private static final String TAG = "ChatListAc";

    public RecyclerView recyclerView;
    public Button imgBtnSendMsg;
    public ImageView btnCamera;
    public EditText edittxtMsg;
    public TextView tvFriendUser, statusFriendUser;
    public CircleImageView imgUser;

    public String nameUserIntent, campUserIntent, uidUserIntent, imageUserIntent, statusUserIntent, countUserIntent, timeUserIntent, current_image, timeExitSP, current_uid, current_name, realTime, nameCampSP, childGroupName, TestStatuName, last_msg, stringUrl, StringCurrentMil, currentTime;
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
    public int checkTablecount, countSqlLite;
    public int num = 1;
    private long currentDateTime;
    private Uri resultUri;

    private final List<Message> messageList = new ArrayList<>();
    RecyclerView.Adapter messageAdapter;
    private DatabaseReference mUserDatabase;
    private StorageReference mImageStorage, filePath;
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    public SharedPreferences prefs;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nameUserIntent = getIntent().getStringExtra("nameUidFriend");
        campUserIntent = getIntent().getStringExtra("campUidFriend");
        uidUserIntent = getIntent().getStringExtra("receiverUidFriend");
        imageUserIntent = getIntent().getStringExtra("imageUidFriend");
        statusUserIntent = getIntent().getStringExtra("statusUidFriend");
        countUserIntent = getIntent().getStringExtra("countUidFriend");
        timeUserIntent = getIntent().getStringExtra("timeUidFriend");

        dbHelper = new DBHelper(getApplicationContext());
        mImageStorage = FirebaseStorage.getInstance().getReference();
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        imgBtnSendMsg = findViewById(R.id.imgBtnSendMsg);
        btnCamera = findViewById(R.id.btnCamera);
        edittxtMsg = findViewById(R.id.edittxtMsg);
        imgUser = findViewById(R.id.imgUser);
        statusFriendUser = findViewById(R.id.statusFriendUser);
        tvFriendUser = findViewById(R.id.tvFriendUser);
        tvFriendUser.setText(nameUserIntent);


        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        current_image = prefs.getString("image", null);
        current_name = prefs.getString("name", null);
        nameCampSP = prefs.getString("camps", null);
        timeExitSP = prefs.getString("time_msg", null);

        getRawCountSql();
        TEST = current_uid + uidUserIntent;

        if (imageUserIntent == null || imageUserIntent == "default") {
            Picasso.get().load(R.drawable.midburn_logo).error(R.drawable.midburn_logo).into(imgUser);
        } else {
            Picasso.get().load(imageUserIntent).error(R.drawable.midburn_logo).into(imgUser);
        }

        if (nameUserIntent.equals(nameCampSP)) {
            TABLE_NAME_MESSAGE = current_uid;
        } else {
            TABLE_NAME_MESSAGE = uidUserIntent;
        }

        try {
            CreateUserTableSqlite();
            tableExists(db, TABLE_NAME_MESSAGE);
            Log.e(TAG, "try-CheckifTableExsits:");
        } catch (Exception e) {
            Log.e(TAG, "CATCH-CheckifTableExsits:");

        }

        if (checkTablecount == 0) {
            Log.e(TAG, "1");
            num = 2;
            CreateUserTableSqlite();

            if (checkTablecount != 0) {
                Log.e(TAG, "1+2");
                updateDBFireBaseToSqlLite();
            }
        } else {
            if (countSqlLite == 0) {
                Log.e(TAG, "2");
                num = 2;
                updateDBFireBaseToSqlLite();

            } else {
                Log.e(TAG, "2+1");
                getmsg();
            }
        }

        imgBtnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = edittxtMsg.getText().toString();


                if (!text.equals("")) {

                    getRealTime();

                    get_msg_uid = UUID.randomUUID().toString();

                    if (current_image.equals("default")) {
                        current_image = setImgUrlDefault;
                    }

                    messageList.add(new Message(current_image, text, realTime, current_uid, uidUserIntent, nameUserIntent, current_name, get_msg_uid, TEST));

                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatListAc.this));
                    messageAdapter = new MessageAdapter(ChatListAc.this, messageList);
                    recyclerView.setAdapter(messageAdapter);

                    Log.e("*******************", "BTN-Send MSG");


                    if (nameUserIntent.equals(nameCampSP)) {
                        childGroupName = uidUserIntent;

                        SaveDataToFireBase();


                        dbHelper.SaveDBSqliteGroup(text, uidUserIntent, current_uid, current_name, StringCurrentMil, current_image, get_msg_uid, TEST, current_uid);


                    } else {
                        childGroupName = "default";

                        SaveDataToFireBase();

                        dbHelper.SaveDBSqliteUser(text, uidUserIntent, current_uid, current_name, StringCurrentMil, current_image, get_msg_uid, TEST, uidUserIntent);

                    }


                } else {
                    Toast.makeText(ChatListAc.this, "please type something", Toast.LENGTH_LONG).show();
                }

                edittxtMsg.setText("");


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");


        if (checkTablecount == 0) {

            Log.e(TAG, "onStart:" + 1);

            num = 2;
            CreateUserTableSqlite();
            if (checkTablecount != 0) {
                Log.e(TAG, "onStart:" + 2);

                updateDBFireBaseToSqlLite();
            }

        } else {

        }

        CheckUserIfOnline();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLastMsg();

    }



    public void CreateUserTableSqlite() {
        try {

            Log.e(TAG, "CreateUserTableSqlite Funk ");

            db = dbHelper.getWritableDatabase();
            dbHelper.onCreate(db);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void SaveDataToFireBase() {

        Log.e("*******************", "SaveDataToFireBase");

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(childGroupName).child(get_msg_uid);

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

        Log.e("*******************", "getmsg");

        try {
            messageList.addAll(dbHelper.getAllMsg());
            messageAdapter = new MessageAdapter(ChatListAc.this, messageList);
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatListAc.this));
            recyclerView.setAdapter(messageAdapter);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void updateDBFireBaseToSqlLite() {

        if (nameUserIntent.equals(nameCampSP)) {

            if (timeUserIntent.equals("default")) {

                if (countSqlLite == 0) {

                    num = 2;

                    long currentDateTime = System.currentTimeMillis();
                    currentTime = String.valueOf(currentDateTime);
                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(uidUserIntent);
                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {


                                image = ds.child("image").getValue(String.class);
                                nameSender = ds.child("nameSender").getValue(String.class);
                                sender = ds.child("sender").getValue(String.class);
                                receiver = ds.child("receiver").getValue(String.class);
                                text = ds.child("message").getValue(String.class);
                                time = ds.child("time").getValue(String.class);
                                get_msg_uid = ds.getKey();


                                TestStatuName = uidUserIntent + current_uid;

                                dbHelper.SaveDBSqliteGroup(text, receiver, sender, nameSender, time, image, get_msg_uid, TestStatuName, current_uid);

                            }
                            if (num == 2) {
                                getmsg();
                                num = 1;

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {


                    db = dbHelper.getWritableDatabase();

                    String countQuery = "SELECT  * FROM " + current_uid;
                    Cursor cursor = db.rawQuery(countQuery, null);
                    cursor.moveToLast();

                    time = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));

                    long lastTimeUser = Long.parseLong(time);
                    currentTime = String.valueOf(lastTimeUser);

                    Log.e("*******************", "GroupCheckMsgFirebase - 1");
                    Log.e(TAG, "GroupCheckMsgFirebase - 1" + realTime);

                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(uidUserIntent);
                    mUserDatabase.orderByChild("time").startAt(currentTime).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG + "test", dataSnapshot.getChildren().toString());

                            int FBCount = (int) dataSnapshot.getChildrenCount();

                            if (FBCount > countSqlLite) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    image = ds.child("image").getValue(String.class);
                                    nameSender = ds.child("nameSender").getValue(String.class);
                                    sender = ds.child("sender").getValue(String.class);
                                    receiver = ds.child("receiver").getValue(String.class);
                                    text = ds.child("message").getValue(String.class);
                                    time = ds.child("time").getValue(String.class);
                                    get_msg_uid = ds.getKey();

                                    Log.e(TAG, "GroupCheckMsgFirebase - 2");

                                    TestStatuName = uidUserIntent + current_uid;

                                    dbHelper.SaveDBSqliteGroup(text, receiver, sender, nameSender, time, image, get_msg_uid, TestStatuName, current_uid);

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

        } else {


            TestStatuName = uidUserIntent + current_uid;

            Log.e(TAG, "DefaultCheckMsgFirebase - 1");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child("default");
            databaseReference.orderByChild("status").equalTo(TestStatuName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        int FBCount = (int) dataSnapshot.getChildrenCount();
                        Log.e(TAG, FBCount + "FBCount - 2");

                        image = ds.child("image").getValue(String.class);
                        text = ds.child("message").getValue(String.class);
                        time = ds.child("time").getValue(String.class);
                        get_msg_uid = ds.getKey();


                        Log.e(TAG, "DefaultCheckMsgFirebase - 2");

                        dbHelper.SaveDBSqliteUser(text, current_uid, uidUserIntent, nameUserIntent, time, image, get_msg_uid, TestStatuName, uidUserIntent);

                        int test = countSqlLite + 1;
                        Log.e(TAG, String.valueOf(test));


                        int lastCountSql = countSqlLite;
                        Log.e(TAG, String.valueOf(lastCountSql));


                        tableExists(db, TABLE_NAME_MESSAGE);

                        if (test == lastCountSql) {
                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child("default").child(get_msg_uid);


                            Log.e(TAG, String.valueOf(get_msg_uid));

                            Map<String, Object> stringObjectHashMap = new HashMap<>();

                            stringObjectHashMap.put("status", "true");

                            mUserDatabase.updateChildren(stringObjectHashMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }



    public void getLastMsg() {

        try {

            db = dbHelper.getWritableDatabase();

            String countQuery = "SELECT  * FROM " + TABLE_NAME_MESSAGE;
            Cursor cursor = db.rawQuery(countQuery, null);
            cursor.moveToLast();
            last_msg = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE));
            time = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));


            Log.e(TAG, "onDestroy_lastMsg: " + last_msg);
            Log.e(TAG, "onDestroy_lastMsg: " + time);
            cursor.close();

            int count = Integer.parseInt(countUserIntent);

            ContentValues cv = new ContentValues();
            cv.put("lastmsg", last_msg);
            cv.put("time", time);

            db.update(TABLE_NAME, cv, "_id=" + count, null);

        } catch (Exception e) {
            Log.e(TAG, "Exception - Error get_Last_msg");

        }


    }


    public long getRawCountSql() {
        db = dbHelper.getWritableDatabase();


        try {
            String countQuery = "SELECT  * FROM " + TABLE_NAME_MESSAGE;
            Cursor cursor = db.rawQuery(countQuery, null);
            cursor.moveToFirst();
            long count = cursor.getCount();

            Log.e(TAG, "getrawCount:" + String.valueOf(count));
            countSqlLite = (int) count;
            cursor.close();

        } catch (Exception e) {

        }

        return countSqlLite;
    }

    public boolean tableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", TABLE_NAME_MESSAGE});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        checkTablecount = cursor.getInt(0);
        cursor.close();
        Log.e(TAG, "tableExists" + String.valueOf(checkTablecount));

        return checkTablecount > 0;
    }


    public void CheckUserIfOnline() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uidUserIntent);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String status = dataSnapshot.child("time").getValue().toString();

                if (!status.equals("default")) {
                    DateFormat getTimeDmY = new SimpleDateFormat("dd:MM:yyyy:HH:mm");
                    long timeMilLong = Long.parseLong(status);

                    String realTime = getTimeDmY.format(timeMilLong);

                    if (status.equals("true")) {
                        statusFriendUser.setText("מחובר");
                    } else {
                        statusFriendUser.setText("last seen:" + realTime);

                    }
                } else {
                    statusFriendUser.setText("");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void loadImagebtn(View view) {

        PermissionManager.check(ChatListAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(ChatListAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(ChatListAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Profile Photo");
        alertDialog.setMessage("Please Pick A Method");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Gallery", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                gallery();

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                return;

            }
        });

        alertDialog.show();
    }

    private void gallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                filePath = mImageStorage.child("msg_images").child(resultUri.getLastPathSegment());

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                stringUrl = String.valueOf(uri);

                                if (stringUrl != null) {

                                    getRealTime();
                                    text = "image";
                                    get_msg_uid = UUID.randomUUID().toString();


                                    if (nameUserIntent.equals(nameCampSP)) {
                                        childGroupName = uidUserIntent;

                                        SaveDataToFireBase();


                                        dbHelper.SaveDBSqliteGroup(text, uidUserIntent, current_uid, current_name, StringCurrentMil, stringUrl, get_msg_uid, TEST, current_uid);


                                    } else {
                                        childGroupName = "default";

                                        SaveDataToFireBase();

                                        dbHelper.SaveDBSqliteUser(text, uidUserIntent, current_uid, current_name, StringCurrentMil, stringUrl, get_msg_uid, TEST, uidUserIntent);

                                    }


                                }

                            }
                        });
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(ChatListAc.this, "error", Toast.LENGTH_LONG).show();

            }
        }


    }


    public void getRealTime() {

        currentDateTime = System.currentTimeMillis();
        StringCurrentMil = String.valueOf(currentDateTime);
        time = String.valueOf(currentDateTime);
        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
        DateFormat getTimeDmY = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");

        realTime = getTimeHourMintus.format(currentDateTime);
    }
}




