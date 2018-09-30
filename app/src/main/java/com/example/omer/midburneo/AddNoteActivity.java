package com.example.omer.midburneo;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.omer.midburneo.Class.FeedReaderContract;
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
    public String current_uid, get_msg_uid, timeMilliString;
    public String msg = "hello";
    private long timeMili = 123456789;
    public MyEventDay myEventDay;
    private DatabaseReference mUserDatabase;
    private Button BtnTimeNoteEdit;
    private TimePickerDialog mTimeSetListener;


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
        BtnTimeNoteEdit = findViewById(R.id.BtnTimeNoteEdit);
        final EditText noteEditText = (EditText) findViewById(R.id.noteEditText);

        String currentTimeIntent = getIntent().getStringExtra("currentDate");


        BtnTimeNoteEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);


                mTimeSetListener = new TimePickerDialog(AddNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // eReminderTime.setText( selectedHour + ":" + selectedMinute);

                        Log.i("ssssssssffffffssss", String.valueOf(timePicker.getDrawingTime()));
                        Log.i("ssssssssffffffssss", String.valueOf(selectedHour));
                        Log.i("ssssssssffffffssss", String.valueOf(selectedMinute));


                        Calendar cal = Calendar.getInstance();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cal.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            cal.set(Calendar.MINUTE, timePicker.getMinute());

                            timeMili = cal.getTimeInMillis();
                            timeMilliString = String.valueOf(timeMili);

                        }

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimeSetListener.setTitle("Select Time");
                mTimeSetListener.show();

            }
        });

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


                if (timeMili == 123456789 || noteEditText.equals("")) {

                    Toast.makeText(AddNoteActivity.this, "please typ time", Toast.LENGTH_LONG).show();

                } else {

                    Intent returnIntent = new Intent();
                    MyEventDay myEventDay = new MyEventDay(datePicker.getSelectedDate(),
                            R.drawable.ic_send, noteEditText.getText().toString());


                    returnIntent.putExtra(ScheduleAc.RESULT, myEventDay);

                    long currentDateTime = System.currentTimeMillis();
                    String time = String.valueOf(currentDateTime);
                    String msg = myEventDay.getNote();

                    String calendar = getFormattedDate(myEventDay.getCalendar().getTime());


                    db.SaveDBSqliteToCalendar(msg, current_uid, calendar, get_msg_uid, timeMilliString);
                    SaveInFireBase(msg, current_uid, calendar, get_msg_uid, timeMilliString);

                    prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                    prefs.edit().putString("time_calendar", calendar).apply();

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();


                }
            }
        });
    }

    public void SaveInFireBase(String msg, String sender, String time, String msguid, String timeMsg) {

        try {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(current_camp_static).child("Calendar").child(msguid);

            Map<String, Object> mapCampsUpdates = new HashMap<>();
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, sender);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.TIME, time);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.TIME__SET, timeMsg);

            mUserDatabase.updateChildren(mapCampsUpdates);
        } catch (Exception e) {

        }

    }

}