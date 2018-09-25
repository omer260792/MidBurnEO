package com.example.omer.midburneo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.NotePreviewActivity.getFormattedDate;
import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_camp_static;

public class AddNoteActivity extends AppCompatActivity {

    public DBHelper db;
    public String current_uid, get_msg_uid;
    public String msg = "hello";
    public MyEventDay myEventDay;
    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_activity);
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        get_msg_uid = UUID.randomUUID().toString();
        db = new DBHelper(getApplicationContext());
        Log.i("ssssssssssss", "AddNoteActivity");


        final CalendarView datePicker = (CalendarView) findViewById(R.id.datePicker);
        Button button = (Button) findViewById(R.id.addNoteButton);
        final EditText noteEditText = (EditText) findViewById(R.id.noteEditText);

        String currentTimeIntent = getIntent().getStringExtra("currentDate");


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        try {
            cal.setTime(sdf.parse(currentTimeIntent));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }

        myEventDay = new MyEventDay(cal, R.drawable.ic_send, msg);

        try {
            datePicker.setDate(cal);

        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                MyEventDay myEventDay = new MyEventDay(datePicker.getSelectedDate(),
                        R.drawable.ic_send, noteEditText.getText().toString());


                returnIntent.putExtra(ScheduleAc.RESULT, myEventDay);

                long currentDateTime = System.currentTimeMillis();
                String time = String.valueOf(currentDateTime);
                String msg = myEventDay.getNote();

                String calendar = getFormattedDate(myEventDay.getCalendar().getTime());


                db.SaveDBSqliteToCalendar(msg, current_uid, calendar, get_msg_uid);
                SaveInFireBase(msg,current_uid,calendar,get_msg_uid);

                prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                prefs.edit().putString("time_calendar", calendar).apply();

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    public void SaveInFireBase(String msg, String sender, String time, String msguid) {

        try {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(current_camp_static).child("Calendar").child(msguid);

            Map<String, Object> mapCampsUpdates = new HashMap<>();
            mapCampsUpdates.put("message", msg);
            mapCampsUpdates.put("message_sender", sender);
            mapCampsUpdates.put("time", time);

            mUserDatabase.updateChildren(mapCampsUpdates);
        } catch (Exception e) {

        }

    }

}