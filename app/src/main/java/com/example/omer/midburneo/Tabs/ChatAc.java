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
import android.nfc.Tag;
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

import com.annimon.stream.operator.LongGenerate;
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
    private int countSqLiteUpdateMessage;
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


        String test = etSearchUser.getText().toString() + "";


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
        dbHelper = new DBHelper(getApplicationContext());

        getmsg();

        if (countSqlLite != 0 && personUtilsList != null) {
            for (int i = 0; i < personUtilsList.size(); i++) {


                String chatRoom = personUtilsList.get(i).getChatRoom();
                countSqLiteUpdateMessage = Integer.parseInt(personUtilsList.get(i).uidCount);
                mUserDatabaseMessage = FirebaseDatabase.getInstance().getReference("ChatRooms").child(chatRoom);
                mUserDatabaseMessage.addValueEventListener(commentValueEventListener);


            }


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

                    Log.e(TAG, "boolRecycler = false;" + test);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ChatAc.this);
                    layoutManager.setStackFromEnd(false);
                    recyclerView.setLayoutManager(layoutManager);

                } else {
                    Log.e(TAG, "boolRecycler = true;" + test);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ChatAc.this);
                    layoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(layoutManager);

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        etSearchUser.setVisibility(View.GONE);

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

        Log.e(TAG, "onstart");
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
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        long count = cursor.getCount();
        countSqlLite = (long) count;
        cursor.close();

        return countSqlLite;
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
        Log.i(TAG, "query " + selectionArgss);
        Log.i(TAG, "query " + selectio);


        try {
            Cursor cursor = builder.query(dbHelper.getReadableDatabase(),
                    projection, selection, selectionArgs, null, null, null);
            if (cursor == null) {
                return null;
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                return false;
            }

            countSqLiteUpdateMessage = cursor.getInt(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID));

            Log.e(TAG, "countSqLiteUpdate" + countSqLiteUpdateMessage);

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
                Log.e(TAG,"ssuuuccccccc");

            }
            Log.e(TAG,"ssuuuccccccc3");

        }

        mAdapter.filterList(filteredList);
        mAdapter.notifyDataSetChanged();
    }


    final com.google.firebase.database.ValueEventListener commentValueEventListener = new com.google.firebase.database.ValueEventListener() {

        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {


            if (countSqlLite == 0) {
                return;
            } else {
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

                String countStringSP = prefs.getString(get_chatRooms, null);
                Boolean check = query(get_chatRooms, FeedReaderContract.FeedEntry.CHAT_ROOMS);

                if (check) {
                    if (!current_uid.equals(get_chatRooms)) {
                        if (countStringSP != null) {


                            numCount = (int) numOfMessage;


                            int countFromSP = Integer.parseInt(countStringSP);


                            int lastcount = numCount - countFromSP;
                            String time = String.valueOf(System.currentTimeMillis());

                            String countStringTotal = String.valueOf(lastcount);
                            try {
                                db = dbHelper.getWritableDatabase();

                                ContentValues data = new ContentValues();
                                data.put(FeedReaderContract.FeedEntry.LASTMSG, countStringTotal);
                                data.put(FeedReaderContract.FeedEntry.TIME, time);
                                db.update(TABLE_NAME, data, "_id=" + countSqLiteUpdateMessage, null);
                               // int count = countSqLiteUpdateMessage - 1;
                                personUtilsList.get(countSqLiteUpdateMessage - 1).setLastMsg(countStringTotal);
                                personUtilsList.get(countSqLiteUpdateMessage - 1).setTime(time);
                                recyclerView.setAdapter(new FriendsAdapter(ChatAc.this,personUtilsList));
                                mAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }


                } else {
                    Log.e(TAG + "false", get_chatRooms + check);


                }
            }

            // count sqlite messages == ount firebase on server


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            messages.clear();


            System.out.println("The read failed: " + databaseError.getMessage());
        }
    };


}
