package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.omer.midburneo.Adapters.FriendsAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.FirebaseMessageModel;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_MESSAGE;
import static com.example.omer.midburneo.RegisterAc.REQUEST_PHONE_CALL;
;import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class ChatAc extends AppCompatActivity {

    private final String TAG = "ChatAc";
    private Button imageButtonSearchUser;
    private RecyclerView recyclerView;
    private TextView nameCampTv;
    private CircleImageView imageView;
    private EditText etSearchUser;
    public SharedPreferences prefs;


    public String current_uid, getUid, getUidUsers, get_lastmsg, get_phone, get_device, get_token, get_chat,
            getNameReceiver, get_image, name, get_role, get_email, get_status, get_admin, get_chatRooms;
    public long countSqlLite, getCountSqlLiteMessage;

    public int num = 1;
    public int countSqLiteUpdate;
    public Boolean boolRecycler = false;

    public FriendsAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    public DBHelper dbHelper;
    public DatabaseReference mUserDatabase, mUserDatabaseMessage;
    public SQLiteDatabase db;
    public int numBtn = 1;
    public int numCount = 1;
    public long numOfMessage = 1;
    public int lastCountMessage = 0;



    private List<Friend> personUtilsList = new ArrayList<>();
    private List<FirebaseMessageModel> messages = new ArrayList<FirebaseMessageModel>();


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_user);

        nameCampTv = findViewById(R.id.nameCampUser);
        imageView = findViewById(R.id.imgChatUser);
        recyclerView = findViewById(R.id.recycler_Expand);
        etSearchUser = findViewById(R.id.etSearchUser);
        imageButtonSearchUser = findViewById(R.id.imageButtonSearchUser);

        final ProgressDialog dialog = new ProgressDialog(ChatAc.this);

        nameCampTv.setText(firebaseUserModel.getCamp());

        dbHelper = new DBHelper(getApplicationContext());

        try {
            Picasso.get().load(firebaseUserModel.getImage()).error(R.drawable.midburn_logo).into(imageView);


        } catch (NullPointerException e) {
            Picasso.get().load(R.drawable.midburn_logo).error(R.drawable.midburn_logo).into(imageView);
        }

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ChatAc.this);
        recyclerView.setLayoutManager(layoutManager);


        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);


        getProfilesCount();

        if (countSqlLite == 0) {
            num = 2;

            UpdateSqliteFromFireBase();


        } else {
            num = 1;
            getmsg();

            UpdateSqliteFromFireBase();

        }


        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
                String test = etSearchUser.getText().toString() + "";

                if (!test.equals("")) {

                    boolRecycler = false;
                    Log.e(TAG, "boolRecycler = false;" + test);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ChatAc.this);
                    layoutManager.setStackFromEnd(boolRecycler);
                    recyclerView.setLayoutManager(layoutManager);

                } else {
                    boolRecycler = false;
                    Log.e(TAG, "boolRecycler = true;" + test);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ChatAc.this);
                    layoutManager.setStackFromEnd(boolRecycler);
                    recyclerView.setLayoutManager(layoutManager);

                }
            }
        });


    }


    public void getmsg() {

        personUtilsList.addAll(dbHelper.getAllFriend());
        mAdapter = new FriendsAdapter(ChatAc.this, personUtilsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        getProfilesCount();

    }

    public long getProfilesCount() {
        String countQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;
        SQLiteDatabase dbr = dbHelper.getReadableDatabase();
        Cursor cursor = dbr.rawQuery(countQuery, null);
        long count = cursor.getCount();
        countSqlLite = (long) count;
        cursor.close();

        return countSqlLite;
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


                    long FBCount = dataSnapshot.getChildrenCount();

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
                            } else {
                                saveUserChatFirebase(getMyKeyMsg);
                            }
                        } else {
                            getMyKeyMsg = ds.child(current_uid).getValue(String.class);
                        }
                    } catch (Exception e) {
                    }

                    // #### Check if exsist new User in the camp
                    try {
                        Boolean check = query(getUidUsers, FeedReaderContract.FeedEntry.UID);
                        if (getUidUsers.equals(firebaseUserModel.getChat())) {

                            if (check.equals(false)) {
                                getMyKeyMsg = get_chat;
                                dbHelper.SaveDBSqliteUser(getNameReceiver, firebaseUserModel.getCamp(), getUidUsers, get_image, get_lastmsg, get_phone, get_device, get_token, getMyKeyMsg);

                            } else {


                                Boolean CheckUid = ds.child(getUidUsers).exists();
                                if (CheckUid.equals(false)) {

                                    // todo cancel from sqlLite


                                } else {


                                    UpdateSqliteFromFireBaseMessage(getMyKeyMsg);

//                                    String countString = prefs.getString(getMyKeyMsg, null);
//
//                                    if (!countString.equals(null)) {
//
//                                        int countCon = Integer.parseInt(countString);
//
//                                        int lastcount = countCon - lastCountMessage;
//                                        Log.e(TAG + "Camp", String.valueOf(countCon));
//                                        Log.e(TAG + "Camp", String.valueOf(lastCountMessage));
//
//                                        String lastcountString = String.valueOf(lastcount);
//                                        Log.e(TAG + "camp", String.valueOf(lastCountMessage));
//                                        //  lastCountMessage = 2;
//
//                                        Log.e(TAG + "Camp", getMyKeyMsg);
//                                        Log.e(TAG + "campr", String.valueOf(countCon));
//                                        Log.e(TAG + "camp", String.valueOf(lastcount));
//                                        Log.e(TAG + "camp", String.valueOf(lastcountString));

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
//                                        data.put(FeedReaderContract.FeedEntry.STATUS, lastcountString);
//                                        data.put(FeedReaderContract.FeedEntry.LASTMSG, lastcountString);
                                    data.put(FeedReaderContract.FeedEntry.ROLE, get_role);
                                    data.put(FeedReaderContract.FeedEntry.PHONE, get_phone);
                                    data.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, get_device);
                                    data.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, get_token);
                                    data.put(FeedReaderContract.FeedEntry.CHAT_ROOMS, getMyKeyMsg);
                                    db.update(TABLE_NAME, data, "_id=" + countSqLiteUpdate, null);
                                }


                            }
                        } else {
                            if (check.equals(false)) {
                                if (!current_uid.equals(getUidUsers)) {
                                    dbHelper.SaveDBSqliteUser(getNameReceiver, firebaseUserModel.getCamp(), getUidUsers, get_image, get_lastmsg, get_phone, get_device, get_token, getMyKeyMsg);
                                }

                            } else {


                                UpdateSqliteFromFireBaseMessage(getMyKeyMsg);

//                                String countString = prefs.getString(getMyKeyMsg, null);
//
//                                if (countString != null) {
//
//                                    int countCon = Integer.parseInt(countString);
//
//                                    int lastcount = countCon - lastCountMessage;
//                                    String lastcountString = String.valueOf(lastcount);
//                                    Log.e(TAG + "User", String.valueOf(lastCountMessage));
//                                    Log.e(TAG + "User", String.valueOf(countCon));
//
//                                    //lastCountMessage = 0;
//
//                                    Log.e(TAG + "User", getMyKeyMsg);
//                                    Log.e(TAG + "User", String.valueOf(countCon));
//                                    Log.e(TAG + "User", String.valueOf(lastcount));
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

                                        Log.e("ddddddd",
                                                "3");

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

                        Log.e("ddddddd",
                                "4");
                        Boolean CheckUid = ds.child(FeedReaderContract.FeedEntry.GROUP).exists();
                        if (CheckUid.equals(true)) {
                            Log.e("ddddddd",
                                    "5");
                            for (DataSnapshot dataSnapshot1 : ds.child(FeedReaderContract.FeedEntry.GROUP).getChildren()) {
                                String key = dataSnapshot1.getKey();

                                String nameGroup = String.valueOf(dataSnapshot1.getValue());
                                Boolean check = query(key, FeedReaderContract.FeedEntry.CHAT_ROOMS);


                                if (check.equals(false)) {

                                    dbHelper.SaveDBSqliteUser(nameGroup, firebaseUserModel.getCamp(), current_uid, get_image, "0", "default", "default", "default", key);
                                } else {
//

                                    UpdateSqliteFromFireBaseMessage(key);

//                                    String countString = prefs.getString(key, null);
//
//                                    if (countString != null) {
//
//                                        int countCon = Integer.parseInt(countString);
//
//                                        int lastcount = countCon - lastCountMessage;
//                                        String lastcountString = String.valueOf(lastcount);
//                                        Log.e(TAG + "Group", String.valueOf(lastCountMessage));
//                                        //  lastCountMessage = 0;
//
//                                        Log.e(TAG + "Group", key);
//                                        Log.e(TAG + "Group", String.valueOf(countCon));
//                                        Log.e(TAG + "Group", String.valueOf(lastcount));

                                    try {
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
                if (num == 2) {
                    getmsg();
                    num = 1;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);


    }

    //    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onStart() {
        super.onStart();
        etSearchUser.setVisibility(View.GONE);
        // UpdateSqliteFromFireBase();


        imageButtonSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (numBtn == 1) {
                    etSearchUser.setVisibility(View.VISIBLE);
                    numBtn = 2;
                } else {
                    etSearchUser.setVisibility(View.GONE);
                    numBtn = 1;
                }
            }
        });
       // getmsg();

        Log.e(TAG, "onstart");
    }

    public static void callPhoneChatAc(String phone, Context context) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            String uri = "tel:" + phone.trim();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);

        }


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.item_setting_user) {

            Intent i = new Intent(ChatAc.this, AddGroupAc.class);
            startActivity(i);


            return true;
        }


        return super.onOptionsItemSelected(item);
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

    private void filter(String text) {
        ArrayList<Friend> filteredList = new ArrayList<>();

        for (Friend item : personUtilsList) {
            if (item.name.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);

            }

        }

        mAdapter.filterList(filteredList);
    }

    public void UpdateSqliteFromFireBaseMessage(String chatRooms) {


        mUserDatabaseMessage = FirebaseDatabase.getInstance().getReference("ChatRooms").child(chatRooms);
        mUserDatabaseMessage.addValueEventListener(commentValueEventListener);


    }

    final com.google.firebase.database.ValueEventListener commentValueEventListener = new com.google.firebase.database.ValueEventListener() {

        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {


            messages.clear();


            // count sqlite messages == ount firebase on server
            for (com.google.firebase.database.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                FirebaseMessageModel firebaseMessageModel = postSnapshot.getValue(FirebaseMessageModel.class);
                firebaseMessageModel.setId(postSnapshot.getKey());


                String image = firebaseMessageModel.setImage((postSnapshot.child("image").getValue().toString()));
                String sender = firebaseMessageModel.setSenderId(postSnapshot.child("senderId").getValue().toString());
                String createDate = firebaseMessageModel.setCreatedDate(Long.valueOf(postSnapshot.child("createdDate").getValue().toString()));

                String status = firebaseMessageModel.setStatus(postSnapshot.child("status").getValue().toString());
                String record = firebaseMessageModel.setRecord(postSnapshot.child("record").getValue().toString());


                firebaseMessageModel.setId(postSnapshot.getKey());

                messages.add(firebaseMessageModel);

                numOfMessage = dataSnapshot.getChildrenCount();
                get_chatRooms = dataSnapshot.getKey();

            }

            String chatRooms = String.valueOf(numOfMessage);
            Log.e(TAG + "sssss", chatRooms);


            String countStringSP = prefs.getString(get_chatRooms, null);
            Boolean check = query(get_chatRooms, FeedReaderContract.FeedEntry.CHAT_ROOMS);
            if (check) {
                if (!current_uid.equals(get_chatRooms)) {
                    if (countStringSP != null) {
                        Log.e(TAG + "Camp", get_chatRooms);


                        numCount = (int) numOfMessage;


                        int countFromSP = Integer.parseInt(countStringSP);


                        int lastcount = numCount - countFromSP;
                        try {
                            String countStringTotal = String.valueOf(lastcount);
                            ContentValues data = new ContentValues();
                            data.put(FeedReaderContract.FeedEntry.LASTMSG, countStringTotal);
                            db.update(TABLE_NAME, data, "_id=" + countSqLiteUpdate, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }

            } else {


            }


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            messages.clear();


            System.out.println("The read failed: " + databaseError.getMessage());
        }
    };


}
