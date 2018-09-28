package com.example.omer.midburneo.Tabs;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omer.midburneo.Adapters.EquipmentAdapter;
import com.example.omer.midburneo.Adapters.FriendsAdapter;
import com.example.omer.midburneo.Adapters.MessageNoteAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.Class.MessageNote;
import com.example.omer.midburneo.DataBase.DBEquipment;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_NOTE;
import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_camp_static;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_name_static;

public class NotesAc extends AppCompatActivity {

    private static final String TAG = "NotesAc";


    private Button addNoteButton;
    private CheckBox checkBox;

    private DatabaseReference mUserDatabase;

    public RecyclerView.Adapter mAdapterNote;
    public RecyclerView.LayoutManager layoutManagerNote;
    public RecyclerView recyclerViewNote;
    private List<MessageNote> messageNoteList = new ArrayList<>();


    public SharedPreferences prefs;

    public String current_camp, current_uid, getUid, date, dateEnd, current_admin;

    private String getTitle = "getMsg";
    private String getSender = "getSender";
    private String getDate = "getDate";
    private String getDateEnd = "getDateEnd";
    private String getContent = "getContent";
    private String getBool = "getBool";
    private String getUidMsg = "getUidMsg";
    private long timeLong, timeEndLong;

    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private int countSqlLite;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_note);

        Log.e(TAG, "onCreate");

        recyclerViewNote = findViewById(R.id.recyclerViewNote);
        addNoteButton = findViewById(R.id.addNoteButton);
        checkBox = findViewById(R.id.checkBoxNote);

        //checkBox.setChecked(true);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbHelper = new DBHelper(this);

        recyclerViewNote.setHasFixedSize(true);
        layoutManagerNote = new LinearLayoutManager(NotesAc.this);
        recyclerViewNote.setLayoutManager(layoutManagerNote);


        try {

            getRawCountSql();

            current_camp_static = prefs.getString("camps", null);
            current_name_static = prefs.getString("name", null);

            Log.e(TAG, current_name_static);
            Log.e(TAG, current_camp_static);

        } catch (NullPointerException e) {

        }

        if (countSqlLite == 0) {
            Log.e(TAG, "oncreat if " + String.valueOf(countSqlLite));

            UpdateDateFromFireBaseToSQLiteNote();

        } else {
            getNoteMsg();

        }


        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NotesAc.this, NoteEditAc.class);
                startActivity(i);
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public void getNoteMsg() {

        Log.e(TAG, "getNoteMsg");

        try {
            messageNoteList.addAll(dbHelper.getAllNote());
            mAdapterNote = new MessageNoteAdapter(this, messageNoteList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(NotesAc.this);
            layoutManager.setStackFromEnd(true);
            recyclerViewNote.setLayoutManager(layoutManager);
            recyclerViewNote.setAdapter(mAdapterNote);
            mAdapterNote.notifyDataSetChanged();


        } catch (Exception e) {
            e.printStackTrace();
            e.getStackTrace();
            e.getMessage();

        }

    }


    public void UpdateDateFromFireBaseToSQLiteNote() {
        if (current_camp_static.equals(null)) {

            Log.e(TAG, "UpdateDateFromFireBaseToSQLiteNote After + if");

            return;
        } else {
            getRawCountSql();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference discussionRoomsRef = rootRef.child("Camps").child(current_camp_static).child("Note");
            Log.e(TAG, "UpdateDateFromFireBaseToSQLiteEquipment After + else");

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

                            dbHelper.SaveDBSqliteToNote(getTitle, getContent, getDate, getDateEnd, getBool, getSender, current_uid, getUid);
                            Log.e(TAG, "UpdateDateFromFireBaseToSQLiteNote After + countSqlLite == 0");

                        }
                        getNoteMsg();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }


    }

    public long getRawCountSql() {
        db = dbHelper.getWritableDatabase();

        String countQuery = "SELECT  * FROM " + TABLE_NAME_NOTE;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        long count = cursor.getCount();

        Log.e(TAG, "getrawCount:" + String.valueOf(count));
        countSqlLite = (int) count;
        cursor.close();

        return countSqlLite;
    }

    public void onClickCheckBox(View view) {


    }
}
