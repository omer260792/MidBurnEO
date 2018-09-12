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
import com.example.omer.midburneo.DataBase.DBHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class AddNoteActivity extends AppCompatActivity {

    public DBHelper db;
    public String current_uid,get_msg_uid;

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
                Log.i("ssssssssssss", msg);
                Log.i("ssssssssssss", current_uid);
                Log.i("ssssssssssss", String.valueOf(myEventDay.getCalendar().getTime()));
                Log.i("ssssssssssss", time);
                Log.i("ssssssssssss", get_msg_uid);

                db.SaveDBSqliteToCalendar(msg,current_uid,time ,get_msg_uid);

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }


}