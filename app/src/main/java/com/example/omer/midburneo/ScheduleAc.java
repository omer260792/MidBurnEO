package com.example.omer.midburneo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;

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
import java.util.Date;
import java.util.Locale;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.SHPRF;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class ScheduleAc extends AppCompatActivity {
    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    private CalendarView mCalendarView;
    private ArrayList<EventDay> mEventDays = new ArrayList<>();
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    public String msg, sender, time, msgUid, current_uid, current_time, current_camp, current_admin, uid_msg, msg_sender, setTime, count, name_sender, image;
    public Calendar calendar;
    public MyEventDay myEventDay;
    public long countSqlLite;
    private DatabaseReference mUserDatabase, discussionRoomsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_schedule);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);

        dbHelper = new DBHelper(getApplicationContext());
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getDateCalendar();

        getRawCountSql();
        if (countSqlLite == 0) {
            getUserTime();

        } else {

            try {
                getCalendarPickerView();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        if (countSqlLite == 0) {


                            getCalnderFromFireBase();

                        } else {


                            getCalnderFromFireBase();

                        }

                    }
                }, 2000);   //




            } catch (NullPointerException e) {

            }
        }


        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    addNote();



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


        }
    }

    private void addNote() {


        long currentDateTime = System.currentTimeMillis();
        DateFormat getTimeHourMintus = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

        String realTime = getTimeHourMintus.format(currentDateTime);

        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("currentDate", realTime);

        startActivityForResult(intent, ADD_NOTE);

    }

    private void previewNote(EventDay eventDay) {

        Intent intent = new Intent(this, NotePreviewActivity.class);
        if (eventDay instanceof MyEventDay) {
            intent.putExtra(EVENT, (MyEventDay) eventDay);
            intent.putExtra("countIntent", count);
            intent.putExtra("timeIntent", time);
        }
        startActivity(intent);
    }


    public long getRawCountSql() {

        db = dbHelper.getReadableDatabase();

        String countQuery = "SELECT  * FROM " + TABLE_NAME_CALENDAR;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        long count = cursor.getCount();

        countSqlLite = (int) count;
        cursor.close();

        return countSqlLite;
    }

    public void UpdateSqliteFromFireBaseCalendar() {

        if (countSqlLite == 0) {


            getCalnderFromFireBase();

        } else {


            getCalnderFromFireBase();

        }
    }

    public void getCalendarPickerView() {

        db = dbHelper.getReadableDatabase();


        String countQuery = "SELECT  * FROM " + TABLE_NAME_CALENDAR;
        Cursor cursor = db.rawQuery(countQuery, null);

        com.example.omer.midburneo.Class.Calendar calendar = new com.example.omer.midburneo.Class.Calendar();


        if (cursor.moveToFirst()) {
            do {
                int idxAll = cursor.getColumnCount();
                msg = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE));
                sender = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER));
                time = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));
                msgUid = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID));
                setTime = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME__SET));
                image = calendar.setImage(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.IMAGE)));
                count = calendar.setCountRaw(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID)));


                Calendar cal = Calendar.getInstance();
                Calendar cal1 = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    try {
                        cal.setTime(sdf.parse(time));// all done
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    Date date = null;
                    try {
                        date = sdf.parse(time);
                        cal.setTime(date);// all done

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }


                long currentDateTime = System.currentTimeMillis();
                String timeCurrent = String.valueOf(currentDateTime);
                //String realTime = sdf.format(time);
                cal1.setTimeInMillis(currentDateTime);

                myEventDay = new MyEventDay(cal, R.drawable.midcamp_logo, msg);

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

        db.close();
    }

    public void getUserTime() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                current_time = dataSnapshot.child("time").getValue().toString();
                current_camp = dataSnapshot.child("camps").getValue().toString();
                current_admin = dataSnapshot.child("admin").getValue().toString();


                UpdateSqliteFromFireBaseCalendar();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCalnderFromFireBase() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Calendar");
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    long FBCount = dataSnapshot.getChildrenCount();


                    msg = ds.child("message").getValue(String.class);
                    msg_sender = ds.child("message_sender").getValue(String.class);
                    time = ds.child("time").getValue(String.class);
                    setTime = ds.child("timeSet").getValue(String.class);
                    name_sender = ds.child("name").getValue(String.class);
                    image = ds.child("image").getValue(String.class);
                    uid_msg = ds.getKey();

                    Boolean tagUserGroup = ds.child(FeedReaderContract.FeedEntry.TAG_USER).exists();
                    if (tagUserGroup.equals(false)) {
                        Boolean check = query(setTime, FeedReaderContract.FeedEntry.TIME__SET);
                        if (check.equals(false)){
                            dbHelper.SaveDBSqliteToCalendar(msg, msg_sender, time, uid_msg, setTime, name_sender, image);

                        }

                    }

                    Boolean tagUser = ds.child(FeedReaderContract.FeedEntry.TAG_USER).child(current_uid).exists();
                    if (tagUser.equals(true)) {

                        Boolean check = query(setTime, FeedReaderContract.FeedEntry.TIME__SET);
                        if (check.equals(false)){
                            dbHelper.SaveDBSqliteToCalendar(msg, msg_sender, time, uid_msg, setTime, name_sender, image);

                        }
                    }

                    getCalendarPickerView();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getDateCalendar() {

        Calendar cal = Calendar.getInstance();

        long currentDateTime = System.currentTimeMillis();

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            String realTime = sdf.format(currentDateTime);

            cal.setTime(sdf.parse(realTime));
            mCalendarView.setDate(cal);
            //mCalendarView.setEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Boolean query(String selectionArgss, String selectio) {

        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.MESSAGE,
                FeedReaderContract.FeedEntry.MESSAGE_SENDER,
                FeedReaderContract.FeedEntry.TIME,
                FeedReaderContract.FeedEntry.MESSAGE_UID,
                FeedReaderContract.FeedEntry.TIME__SET,
                FeedReaderContract.FeedEntry.NAME,
                FeedReaderContract.FeedEntry.IMAGE
        };

        String selection = selectio + " = ?";
        String[] selectionArgs = {selectionArgss};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME_CALENDAR);

        Cursor cursor = builder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        cursor.close();

        return true;
    }


}