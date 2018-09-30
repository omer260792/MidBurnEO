package com.example.omer.midburneo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;

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

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.RegisterAc.prefs;
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
    public String msg, sender, time, msgUid, current_uid, current_admin, uid_msg, msg_sender, setTime;
    public Calendar calendar;
    public int mDay, checkTablecount;
    public MyEventDay myEventDay;
    public long countSqlLite;
    private DatabaseReference mUserDatabase, discussionRoomsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_schedule);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);

        db = new DBHelper(getApplicationContext());
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


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
        discussionRoomsRef = rootRef.child("Camps").child(current_camp_static).child("Calendar");

        if (countSqlLite == 0) {

            long currentDateTime = System.currentTimeMillis();
            current_time_static = String.valueOf(currentDateTime);

            getCalnderFromFireBase(current_time_calendar_static);

        } else {
            getCalnderFromFireBase(current_time_static);

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
                setTime = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME__SET));
                String index = cursor.getString(cursor.getColumnIndex("_id"));

                Calendar cal = Calendar.getInstance();
                Calendar cal1 = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");

//                try {
//                    long testL = Long.parseLong(time);
//                    String test = String.valueOf(sdf.parse(setTime));
//                    String test2 = String.valueOf(sdf.parse(time));
//                    String test1 = String.valueOf(sdf.parse(String.valueOf(testL)));
//
//                    Log.e("fbhgbhfgbgghghghg",test + test2 + test1);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

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

    public void getCalnderFromFireBase(String ChildTime) {

        long currentDateTime = System.currentTimeMillis();
        current_time_static = String.valueOf(currentDateTime);

        Query query = discussionRoomsRef.orderByChild("time").startAt(ChildTime);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    long FBCount = dataSnapshot.getChildrenCount();

                    if (countSqlLite < FBCount) {
                        msg = ds.child("message").getValue(String.class);
                        msg_sender = ds.child("message_sender").getValue(String.class);
                        time = ds.child("time").getValue(String.class);
                        setTime = ds.child("timeSet").getValue(String.class);
                        uid_msg = ds.getKey();

                        Log.d("countSqlLite", String.valueOf(countSqlLite));
                        Log.d("ghghg", String.valueOf(uid_msg));
                        Log.d("FBCount", String.valueOf(FBCount));


                        db.SaveDBSqliteToCalendar(msg, msg_sender, time, uid_msg,setTime);

                        getCalendarPickerView();
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