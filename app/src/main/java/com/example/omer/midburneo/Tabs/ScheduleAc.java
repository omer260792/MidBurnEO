package com.example.omer.midburneo.Tabs;


import android.content.Intent;
import android.graphics.Color;
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
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.omer.midburneo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleAc extends AppCompatActivity {
    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    private CalendarView mCalendarView;
    public TextView textViewSetdate;
    private List<EventDay> mEventDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        textViewSetdate = (TextView) findViewById(R.id.textViewSetDates);

        mCalendarView.setEvents(mEventDays);
        List<Calendar> calendars = new ArrayList<>();

        mCalendarView.setDisabledDays(calendars);
        //mCalendarView.setDisabledDays();
        mCalendarView.setBackgroundColor(Color.parseColor("#00BCD4"));


        Button getDateBTN = findViewById(R.id.getDateButton);
        getDateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });




        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                Log.e("ffgggggg", eventDay.toString());

                textViewSetdate.setText("hi");


                previewEvent(eventDay);
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

    private void addEvent() {

        Intent intent = new Intent(this, AddEventActivity.class);
        startActivityForResult(intent, ADD_NOTE);
    }

    private void previewEvent(EventDay eventDay) {
        Intent intent = new Intent(this, PreviewEventActivity.class);
        if (eventDay instanceof MyEventDay) {
            intent.putExtra(EVENT, (MyEventDay) eventDay);
        }
        startActivity(intent);
    }
}
