package com.example.omer.midburneo;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

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
import com.example.omer.midburneo.Tabs.ChatListAc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;

public class ScheduleAc extends AppCompatActivity {
    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    private CalendarView mCalendarView;
    private ArrayList<EventDay> mEventDays = new ArrayList<>();
    private ArrayList mEventDayss = new ArrayList<>();
    public DBHelper db;
    public SQLiteDatabase dbr;
    public String msg, sender, time, msgUid;
    public Calendar calendar;
    public int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_schedule);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);


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

                Log.e("*******************", "ScheduleAc: " + msg);
                Log.e("*******************", "ScheduleAc: " + time);

                long timeMill = Long.parseLong(time);
                int idx = Integer.parseInt(index);
                Log.e("*******************", "ScheduleAc: " + idx);

                getCalendar(timeMill);

                //mCalendarView.setEvents(db.getAllCalendar());

                MyEventDay myEventDay = new MyEventDay(calendar,R.drawable.ic_send,msg);

                //mEventDaysCalendar = (Collection) new MyEventDay(calendar,R.drawable.ic_send,msg);
                try {
                    mCalendarView.setDate(calendar);

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


        //  MyEventDay myEventDay = new MyEventDay()
//        mEventDaysCalendar.addAll(db.getAllCalendar());

//        messageList.addAll(dbHelper.getAllMsg());
//        messageAdapter = new MessageAdapter(ChatListAc.this, messageList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(ChatListAc.this));
//        recyclerView.setAdapter(messageAdapter);

//
//        Intent returnIntent = new Intent();
//        MyEventDay myEventDay = new MyEventDay()
//
//        returnIntent.putExtra(ScheduleAc.RESULT, myEventDay);
//        setResult(Activity.RESULT_OK, returnIntent);
//        //finish();


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

            Log.i("ssssssssssss", "onActivityResult");


        }
    }

    private void addNote() {
        Log.i("ssssssssssss", "addNote");

        Intent intent = new Intent(this, AddNoteActivity.class);
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
}