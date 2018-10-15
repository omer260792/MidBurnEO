package com.example.omer.midburneo.Tabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.omer.midburneo.Adapters.ViewPagerAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;

import com.example.omer.midburneo.Fragments.FragmentHistory;
import com.example.omer.midburneo.Fragments.FragmentMain;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_NOTE;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class NotesAc extends AppCompatActivity {

    private static final String TAG = "NotesAc";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private int countSqlLite;
    public SharedPreferences prefs;
    private FragmentMain fragmentMain;
    private FragmentHistory fragmentHistory;

    private String current_uid, getUid;
    private String getTitle = "getMsg";
    private String getSender = "getSender";
    private String getDate = "getDate";
    private String getDateEnd = "getDateEnd";
    private String getContent = "getContent";
    private String getBool = "getBool";


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_note);

        dbHelper = new DBHelper(this);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new FragmentMain(), "main");
        adapter.AddFragment(new FragmentHistory(), "history");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getRawCountSql();

        UpdateDateFromFireBaseToSQLiteNote();
    }

    public void UpdateDateFromFireBaseToSQLiteNote() {
        fragmentHistory = new FragmentHistory();
        fragmentMain = new FragmentMain();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Camps").child(firebaseUserModel.getChat()).child("Note");

        if (countSqlLite == 0) {
            discussionRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        getUid = snapshot.getKey().toString();

                        getTitle = snapshot.child("title").getValue(String.class);
                        getContent = snapshot.child("content").getValue(String.class);
                        getDate = snapshot.child("date").getValue(String.class);
                        getDateEnd = snapshot.child("dateEnd").getValue(String.class);
                        getBool = snapshot.child("dateBool").getValue(String.class);
                        getSender = snapshot.child("sender").getValue(String.class);


                        Boolean tagUserGroup = snapshot.child(FeedReaderContract.FeedEntry.TAG_USER).exists();
                        if (tagUserGroup.equals(false)){
                            dbHelper.SaveDBSqliteToNote(getTitle, getContent, getDate, getDateEnd, getBool, getSender, current_uid, getUid);

                        }


                        Boolean tagUser = snapshot.child(FeedReaderContract.FeedEntry.TAG_USER).child(current_uid).exists();
                        if (tagUser.equals(true)) {
                            dbHelper.SaveDBSqliteToNote(getTitle, getContent, getDate, getDateEnd, getBool, getSender, current_uid, getUid);

                        }


                    }
                    fragmentMain.getNoteMsg();
                    fragmentHistory.getNoteMsg();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            discussionRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        long FBCount = dataSnapshot.getChildrenCount();

                        if (countSqlLite < FBCount) {

                            getUid = snapshot.getKey().toString();

                            getTitle = snapshot.child("title").getValue(String.class);
                            getContent = snapshot.child("content").getValue(String.class);
                            getDate = snapshot.child("date").getValue(String.class);
                            getDateEnd = snapshot.child("dateEnd").getValue(String.class);
                            getBool = snapshot.child("dateBool").getValue(String.class);
                            getSender = snapshot.child("sender").getValue(String.class);



                            Boolean tagUserGroup = snapshot.child(FeedReaderContract.FeedEntry.TAG_USER).exists();
                            if (tagUserGroup.equals(false)){
                                dbHelper.SaveDBSqliteToNote(getTitle, getContent, getDate, getDateEnd, getBool, getSender, current_uid, getUid);

                            }


                            Boolean tagUser = snapshot.child(FeedReaderContract.FeedEntry.TAG_USER).child(current_uid).exists();
                            if (tagUser.equals(true)) {
                                dbHelper.SaveDBSqliteToNote(getTitle, getContent, getDate, getDateEnd, getBool, getSender, current_uid, getUid);

                            }

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }

    public long getRawCountSql() {
        db = dbHelper.getWritableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_NAME_NOTE;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        long count = cursor.getCount();

        countSqlLite = (int) count;
        cursor.close();

        return countSqlLite;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.item_note) {
            Intent i = new Intent(NotesAc.this, NoteEditAc.class);
            startActivity(i);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickCheckBox(View view) {


    }
}

