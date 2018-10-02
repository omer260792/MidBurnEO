package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.Adapters.FriendsAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Utils.UtilHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.RegisterAc.REQUEST_PHONE_CALL;
import static com.example.omer.midburneo.RegisterAc.SHPRF;

public class ChatAc extends AppCompatActivity {




    private final String TAG = "ChatAc";

    private RecyclerView recyclerView;
    private TextView nameCampTv;
    private CircleImageView imageView;
    private UtilHelper utilHelper;

    public String current_uid, currentImageSP, currentNameSP, currentstatusSp, currentCampSP, getUid, getUidUsers, get_lastmsg, get_phone, get_device, get_token, getNameReceiver, get_image, get_time, name;
    public long countSqlLite;
    public int num = 1;

    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    public DBHelper db;
    private DatabaseReference mUserDatabase;
    public SQLiteDatabase dbSql;
    List<Friend> personUtilsList;
    SharedPreferences prefs;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_chat);

        nameCampTv = findViewById(R.id.nameCampUser);
        imageView = findViewById(R.id.imgChatUser);
        recyclerView = findViewById(R.id.recycler_Expand);


        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        currentNameSP = prefs.getString("name", null);
        currentImageSP = prefs.getString("image", null);
        currentCampSP = prefs.getString("camps", null);
        currentstatusSp = prefs.getString("status", null);

        nameCampTv.setText(currentCampSP);

        db = new DBHelper(getApplicationContext());
        //utilHelper = new UtilHelper();

        try {
            Picasso.get().load(currentImageSP).error(R.drawable.midburn_logo).into(imageView);


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

        personUtilsList.addAll(db.getAllFriend());
        mAdapter = new FriendsAdapter(this, personUtilsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        getProfilesCount();

    }

    public long getProfilesCount() {
        String countQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;
        SQLiteDatabase dbr = db.getReadableDatabase();
        Cursor cursor = dbr.rawQuery(countQuery, null);
        long count = cursor.getCount();
        countSqlLite = (long) count;
        cursor.close();
        Log.d("getProfilesCount", String.valueOf(countSqlLite));


        return countSqlLite;
    }


    public void UpdateSqliteFromFireBase() {


        if (currentCampSP.equals(null)) {
            return;
        } else {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference discussionRoomsRef = rootRef.child("Users");

            Query query = discussionRoomsRef.orderByChild("camps").equalTo(currentCampSP);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        long FBCount = dataSnapshot.getChildrenCount();

                        if (countSqlLite < FBCount) {

                            getNameReceiver = ds.child("name").getValue(String.class);
                            get_image = ds.child("image").getValue(String.class);
                            get_lastmsg = ds.child("lastmsg").getValue(String.class);
                            get_phone = ds.child("phone").getValue(String.class);
                            get_device = ds.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID).getValue(String.class);
                            get_token = ds.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN).getValue(String.class);
                            getUidUsers = ds.getKey();

                            Log.d("countSqlLite", String.valueOf(countSqlLite));
                            Log.d("FBCount", String.valueOf(FBCount));

                            db.SaveDBSqliteUser(getNameReceiver, currentCampSP, getUidUsers, get_image, get_lastmsg, get_phone, get_device, get_token);

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
            query.addValueEventListener(valueEventListener);

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


}
