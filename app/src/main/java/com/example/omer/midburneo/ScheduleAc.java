package com.example.omer.midburneo;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.omer.midburneo.Adapters.MessageAdapter;
import com.example.omer.midburneo.AddNoteActivity;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.NotePreviewActivity;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.AdminAc;
import com.example.omer.midburneo.Tabs.ChatListAc;
import com.example.omer.midburneo.Tabs.MainPageAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.NotePreviewActivity.getFormattedDate;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.ChatListAc.setImgUrlDefault;
import static com.example.omer.midburneo.Tabs.MainPageAc.TABLE_NAME_MESSAGE;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_admin_static;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_camp_static;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_time_calendar_static;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_time_static;

public class ScheduleAc extends AppCompatActivity {
    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    private CalendarView mCalendarView;
    private ArrayList<EventDay> mEventDays = new ArrayList<>();
    public DBHelper db;
    public SQLiteDatabase dbr;
    public String msg, sender, time, msgUid, current_uid, current_admin;
    public Calendar calendar;
    public int mDay, checkTablecount;
    public MyEventDay myEventDay;
    public long countSqlLite;
    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_schedule);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);

        db = new DBHelper(getApplicationContext());
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                current_admin = prefs.getString("admin", null);

                if (current_admin.equals("admin")) {
                    addNote();

                } else {
                    Toast.makeText(ScheduleAc.this, "אתה לא מנהל",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                previewNote(eventDay);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        getRawCountSql();
        if (countSqlLite == 0) {
            getUserTime();

        } else {

            try {
                current_camp_static = prefs.getString("camps", null);
                current_time_calendar_static = prefs.getString("time_calendar", null);
                current_time_static = prefs.getString("time", null);

                getCalendarPickerView();

                UpdateSqliteFromFireBaseCalendar();

            } catch (NullPointerException e) {

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
            MyEventDay myEventDay = data.getParcelableExtra(RESULT);
            try {
                mCalendarView.setDate(myEventDay.getCalendar());
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }
            mEventDays.add(myEventDay);
            mCalendarView.setEvents(mEventDays);

            Log.i("ssssssssssss", "onActivityResult");


        }
    }

    private void addNote() {
        Log.i("ssssssssssss", "addNote");


        long currentDateTime = System.currentTimeMillis();
        DateFormat getTimeHourMintus = new SimpleDateFormat("dd MMMM yyyy");

        String realTime = getTimeHourMintus.format(currentDateTime);

        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("currentDate", realTime);

        startActivityForResult(intent, ADD_NOTE);

    }

    private void previewNote(EventDay eventDay) {
        Log.i("ssssssssssss", "previewNote");

        Intent intent = new Intent(this, NotePreviewActivity.class);
        if (eventDay instanceof MyEventDay) {
            intent.putExtra(EVENT, (MyEventDay) eventDay);
        }
        startActivity(intent);
    }

    public Calendar getCalendar(long time) {

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

//        int mYear = calendar.get(Calendar.YEAR);
//        int mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        return calendar;

    }

    public long getRawCountSql() {
        SQLiteDatabase dbr;
        dbr = db.getWritableDatabase();

        String countQuery = "SELECT  * FROM " + TABLE_NAME_CALENDAR;
        Cursor cursor = dbr.rawQuery(countQuery, null);
        cursor.moveToFirst();
        long count = cursor.getCount();

        Log.e("*******************", String.valueOf(count) + "getrawCount");
        countSqlLite = (int) count;
        cursor.close();

        return countSqlLite;
    }

    public void UpdateSqliteFromFireBaseCalendar() {
        getRawCountSql();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Camps").child(current_camp_static).child("Calendar");

        if (countSqlLite == 0) {

            long currentDateTime = System.currentTimeMillis();
            current_time_static = String.valueOf(currentDateTime);

            Query query = discussionRoomsRef.orderByChild("time").startAt(current_time_static);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        long FBCount = dataSnapshot.getChildrenCount();

                        if (countSqlLite < FBCount) {

                            String msg = ds.child("message").getValue(String.class);
                            String msg_sender = ds.child("message_sender").getValue(String.class);
                            String time = ds.child("time").getValue(String.class);
                            String uid_msg = ds.getKey();

                            Log.d("countSqlLite", String.valueOf(countSqlLite));
                            Log.d("ghghg", String.valueOf(uid_msg));
                            Log.d("FBCount", String.valueOf(FBCount));


                            db.SaveDBSqliteToCalendar(msg, msg_sender, time, uid_msg);

                            getCalendarPickerView();

                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            query.addListenerForSingleValueEvent(valueEventListener);
        } else {

            Query query = discussionRoomsRef.orderByChild("time").startAt(current_time_calendar_static);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        long FBCount = dataSnapshot.getChildrenCount();

                        if (countSqlLite < FBCount) {

                            String msg = ds.child("message").getValue(String.class);
                            String msg_sender = ds.child("message_sender").getValue(String.class);
                            String time = ds.child("time").getValue(String.class);
                            String uid_msg = ds.getKey();

                            Log.d("countSqlLite", String.valueOf(countSqlLite));
                            Log.d("ghghg", String.valueOf(uid_msg));
                            Log.d("FBCount", String.valueOf(FBCount));


                            db.SaveDBSqliteToCalendar(msg, msg_sender, time, uid_msg);

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

    public void getCalendarPickerView() {
        db = new DBHelper(getApplicationContext());

        dbr = db.getWritableDatabase();

        String countQuery = "SELECT  * FROM " + TABLE_NAME_CALENDAR;
        Cursor cursor = dbr.rawQuery(countQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idxAll = cursor.getColumnCount();
                msg = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE));
                sender = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER));
                time = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));
                msgUid = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID));
                String index = cursor.getString(cursor.getColumnIndex("_id"));

                Calendar cal = Calendar.getInstance();
                Calendar cal1 = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");

                long currentDateTime = System.currentTimeMillis();
                String timeCurrent = String.valueOf(currentDateTime);
                try {
                    cal.setTime(sdf.parse(time));// all done
                    cal1.setTime(sdf.parse(timeCurrent));// all done
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                myEventDay = new MyEventDay(cal, R.drawable.ic_send, msg);

                try {
                    mCalendarView.setDate(cal1);

                } catch (OutOfDateRangeException e) {
                    e.printStackTrace();
                }
                mEventDays.add(myEventDay);

                //mEventDays.add(myEventDay);
                mCalendarView.setEvents(mEventDays);


            } while (cursor.moveToNext());

        }
        // close db connection
        dbr.close();
    }

    public void getUserTime() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                current_time_static = dataSnapshot.child("time").getValue().toString();
                current_camp_static = dataSnapshot.child("camps").getValue().toString();
                current_admin_static = dataSnapshot.child("admin").getValue().toString();

                prefs.edit().putString("time", current_time_static).apply();
                prefs.edit().putString("camps", current_camp_static).apply();
                prefs.edit().putString("admin", current_admin_static).apply();


                UpdateSqliteFromFireBaseCalendar();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}