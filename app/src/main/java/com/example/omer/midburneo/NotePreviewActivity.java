package com.example.omer.midburneo;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.applandeo.materialcalendarview.EventDay;
import com.example.omer.midburneo.Adapters.CalendarAdapter;
import com.example.omer.midburneo.Adapters.EquipmentAdapter;
import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.Tabs.EquipmentAc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class NotePreviewActivity extends AppCompatActivity {

    private static final String TAG = "NotePreviewActivity";


    private RecyclerView recyclerNotePreview;
    private TextView tvNote;
    private SQLiteDatabase db;
    public DBHelper dbHelper;
    private CalendarAdapter mAdapter;
    private ArrayList<Calendar> calendarArrayList = new ArrayList<>();
    private String count, time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_preview_activity);


        Intent intent = getIntent();
        count = getIntent().getStringExtra("coumtIntent");
        time = getIntent().getStringExtra("timeIntent");


        recyclerNotePreview = findViewById(R.id.recycler_note_preview);

        dbHelper = new DBHelper(getApplicationContext());
        // getNotePreview();

        if (intent != null) {
            Object event = intent.getParcelableExtra(ScheduleAc.EVENT);
            if (event instanceof MyEventDay) {

                MyEventDay myEventDay = (MyEventDay) event;

                getSupportActionBar().setTitle(getFormattedDate(myEventDay.getCalendar().getTime()));
                //tvNote.setText(myEventDay.getNote());

                String time = String.valueOf(getFormattedDate(myEventDay.getCalendar().getTime()));
                Log.e(TAG, time);

                getNotePreview(time);
                return;
            }
            if (event instanceof EventDay) {
                EventDay eventDay = (EventDay) event;
                getSupportActionBar().setTitle(getFormattedDate(eventDay.getCalendar().getTime()));
            }
        }
    }

    public void getNotePreview(String time) {


        try {

            calendarArrayList.addAll(dbHelper.getAllCalnderNotePreview(time));
            mAdapter = new CalendarAdapter(NotePreviewActivity.this, calendarArrayList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(NotePreviewActivity.this);
            layoutManager.setStackFromEnd(false);
            recyclerNotePreview.setLayoutManager(layoutManager);
            recyclerNotePreview.setAdapter(mAdapter);


        } catch (Exception e) {
            e.printStackTrace();
            e.getStackTrace();
            e.getMessage();

        }
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        return sdf.format(date);
    }
}
