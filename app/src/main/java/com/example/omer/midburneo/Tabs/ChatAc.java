package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.omer.midburneo.Adapters.FriendsAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
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
import static com.example.omer.midburneo.RegisterAc.REQUEST_PHONE_CALL;
;import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class ChatAc extends AppCompatActivity {

    private final String TAG = "ChatAc";

    private RecyclerView recyclerView;
    private TextView nameCampTv;
    private CircleImageView imageView;

    public String current_uid, getUid, getUidUsers, get_lastmsg, get_phone, get_device, get_token, get_chat, getNameReceiver, get_image, name;
    public long countSqlLite;
    public int num = 1;

    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    public DBHelper dbHelper;
    private DatabaseReference mUserDatabase;
    public SQLiteDatabase db;
    List<Friend> personUtilsList;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_user);

        nameCampTv = findViewById(R.id.nameCampUser);
        imageView = findViewById(R.id.imgChatUser);
        recyclerView = findViewById(R.id.recycler_Expand);

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
        personUtilsList = new ArrayList<>();


        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getProfilesCount();

        if (countSqlLite == 0) {
            num = 2;
            UpdateSqliteFromFireBase();
            Log.d("onStrartCahtAc", String.valueOf(num));


        } else {
            num = 1;
            getmsg();
            UpdateSqliteFromFireBase();
            Log.d("onStrartCahtAc", String.valueOf(num));

        }

    }


    public void getmsg() {

        personUtilsList.addAll(dbHelper.getAllFriend());
        mAdapter = new FriendsAdapter(this, personUtilsList);
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
        Log.d(TAG, String.valueOf(countSqlLite));

        return countSqlLite;
    }


    public void UpdateSqliteFromFireBase() {


        if (firebaseUserModel.getCamp() == null) {
        } else {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference discussionRoomsRef = rootRef.child("Users");

            Query query = discussionRoomsRef.orderByChild("chat").equalTo(firebaseUserModel.getChat());
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        long FBCount = dataSnapshot.getChildrenCount();

                        getUidUsers = ds.getKey();


                        if (countSqlLite < FBCount) {

                            getNameReceiver = ds.child(FeedReaderContract.FeedEntry.NAME).getValue(String.class);
                            get_image = ds.child(FeedReaderContract.FeedEntry.IMAGE).getValue(String.class);
                            get_lastmsg = ds.child(FeedReaderContract.FeedEntry.LASTMSG).getValue(String.class);
                            get_phone = ds.child(FeedReaderContract.FeedEntry.PHONE).getValue(String.class);
                            get_device = ds.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID).getValue(String.class);
                            get_chat = ds.child(FeedReaderContract.FeedEntry.CHAT).getValue(String.class);
                            get_token = ds.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN).getValue(String.class);
                            getUidUsers = ds.getKey();


//                            if (getUidUsers.equals(current_uid)){
//
//                                Boolean CheckUid = ds.child(FeedReaderContract.FeedEntry.GROUP).exists();
//
//                                if (CheckUid.equals(true)){
//
//                                    for (DataSnapshot dataSnapshot1 : ds.child(FeedReaderContract.FeedEntry.GROUP).getChildren()){
//
//                                      //  String test = ds.child(FeedReaderContract.FeedEntry.NAME).getValue(String.class);
//
//                                        String checkObject = dataSnapshot1.child(FeedReaderContract.FeedEntry.GROUP).getValue(String.class);
//
//
//
//                                    }
//
//
//                                }
//
//                            }

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


                            try {

                                Boolean check = query(getUidUsers, FeedReaderContract.FeedEntry.UID);

                                if (getNameReceiver.equals(firebaseUserModel.getCamp())) {

                                    if (check.equals(false)){
                                        getMyKeyMsg = get_chat;
                                        dbHelper.SaveDBSqliteUser(getNameReceiver, firebaseUserModel.getCamp(), getUidUsers, get_image, get_lastmsg, get_phone, get_device, get_token, getMyKeyMsg);

                                    }


                                } else {
                                    if (check.equals(false)) {
                                        dbHelper.SaveDBSqliteUser(getNameReceiver, firebaseUserModel.getCamp(), getUidUsers, get_image, get_lastmsg, get_phone, get_device, get_token, getMyKeyMsg);

                                    }


                                }


                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }


                        } else {

                            if (getUidUsers.equals(current_uid)) {

                                Boolean CheckUid = ds.child(FeedReaderContract.FeedEntry.GROUP).exists();

                                if (CheckUid.equals(true)) {



                                    for (DataSnapshot dataSnapshot1 : ds.child(FeedReaderContract.FeedEntry.GROUP).getChildren()) {

                                        String key = dataSnapshot1.getKey();
                                        String nameGroup = String.valueOf(dataSnapshot1.getValue());

                                        Boolean check =  query(key, FeedReaderContract.FeedEntry.CHAT_ROOMS);

                                        if (check.equals(false)) {

                                            get_image = "default";
                                            dbHelper.SaveDBSqliteUser(nameGroup, firebaseUserModel.getCamp(), current_uid, get_image, "default", "default", "default", "default", key);


                                        }


                                    }


                                }

                            }
                        }


                    }
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


    public boolean CheckIsDataAlreadyInDBorNot(String TableName, String dbfield, String fieldValue) {

        db = dbHelper.getReadableDatabase();


        try {
            String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
            Cursor cursor = db.rawQuery(Query, null);
            if (cursor == null) {
                cursor.close();
                return false;
            }
            cursor.close();
        } catch (Exception e) {
            return false;

        }

        return true;
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

        Cursor cursor = builder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        return true;
    }


}
