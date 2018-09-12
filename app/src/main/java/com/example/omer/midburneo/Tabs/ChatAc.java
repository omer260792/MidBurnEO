package com.example.omer.midburneo.Tabs;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.RegisterAc.SHPRF;

public class ChatAc extends AppCompatActivity {


    private final String TAG = "ChatAc";

    private RecyclerView recyclerView;
    private TextView nameCampTv;
    private CircleImageView imageView;

    public String current_uid, currentImageSP, currentNameSP,currentstatusSp, currentCampSP, getUid, getUidUsers, get_lastmsg, getNameReceiver, get_image, get_time , name;
    public long countSqlLite;

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

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        currentNameSP = prefs.getString("name", null);
        currentImageSP = prefs.getString("image", null);
        currentCampSP = prefs.getString("camps", null);
        currentstatusSp = prefs.getString("status", null);

        nameCampTv.setText(currentCampSP);

        db = new DBHelper(getApplicationContext());

        try {
            Picasso.get().load(currentImageSP).error(R.drawable.midburn_logo).into(imageView);


        } catch (NullPointerException e) {
            Picasso.get().load(R.drawable.midburn_logo).error(R.drawable.midburn_logo).into(imageView);
        }

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ChatAc.this);
        recyclerView.setLayoutManager(layoutManager);
        personUtilsList = new ArrayList<>();

        getmsg();


    }


    @Override
    protected void onStart() {
        super.onStart();

        UpdateSqliteFromFireBase();

    }



    public void getmsg() {

        personUtilsList.addAll(db.getAllFriend());

        mAdapter = new FriendsAdapter(this, personUtilsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    public long getProfilesCount() {
        String countQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;
        SQLiteDatabase dbr = db.getReadableDatabase();
        Cursor cursor = dbr.rawQuery(countQuery, null);
        long count = cursor.getCount();
        countSqlLite = (long) count;
        cursor.close();

        return countSqlLite;
    }


    public void UpdateSqliteFromFireBase() {

        getProfilesCount();

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
                        getUidUsers = ds.getKey();

                        Log.d("countSqlLite", String.valueOf(countSqlLite));
                        Log.d("FBCount", String.valueOf(FBCount));


                        db.SaveDBSqlite(getNameReceiver, currentCampSP, getUidUsers, get_image, get_lastmsg);

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

    }


}
