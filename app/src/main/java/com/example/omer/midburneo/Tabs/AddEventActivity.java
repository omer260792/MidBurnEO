package com.example.omer.midburneo.Tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.MyEventDay;
import com.example.omer.midburneo.Tabs.ScheduleAc;

import java.util.ArrayList;
import java.util.List;

import static com.example.omer.midburneo.Tabs.ScheduleAc.RESULT;

public class AddEventActivity extends AppCompatActivity {
    public CalendarView datePicker;
    Button button;
    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    public TextView textViewSetdate;
    private List<EventDay> mEventDays = new ArrayList<>();

    EditText noteEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        datePicker = findViewById(R.id.datePicker);
        button = (Button) findViewById(R.id.addNoteButton);

        textViewSetdate = (TextView) findViewById(R.id.textViewSetDates);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        textViewSetdate = (TextView) findViewById(R.id.textViewSetDates);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddEventActivity.this,"oeoeoeoeooe",Toast.LENGTH_SHORT).show();

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                startActivityForResult(returnIntent, ADD_NOTE);



                MyEventDay myEventDay = new MyEventDay(datePicker.getFirstSelectedDate(),
                        R.drawable.common_google_signin_btn_icon_dark, noteEditText.getText().toString());


                Log.e("1",noteEditText.getText().toString());
                Log.e("2",datePicker.getCurrentPageDate().toString());
                Log.e("3",datePicker.getFirstSelectedDate().toString());
                returnIntent.putExtra(RESULT, myEventDay);
                setResult(Activity.RESULT_OK, returnIntent);



                finish();
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
            MyEventDay myEventDay = data.getParcelableExtra(RESULT);
            try {
                datePicker.setDate(myEventDay.getCalendar());

            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }

            mEventDays.add(myEventDay);
            datePicker.setEvents(mEventDays);
        }
    }

}