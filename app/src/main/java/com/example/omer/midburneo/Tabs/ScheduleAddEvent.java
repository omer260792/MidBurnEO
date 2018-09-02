package com.example.omer.midburneo.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.R;

public class ScheduleAddEvent extends AppCompatActivity {


    public Button saveBtn;
    public EditText textEditSchrdule;

    public String date,month,year,day, msg;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_single_event);


        day = getIntent().getStringExtra("dayOfMonth");
        month = getIntent().getStringExtra("month");
        year = getIntent().getStringExtra("year");

        saveBtn = findViewById(R.id.saveButtonSchedule);
        textEditSchrdule = findViewById(R.id.editTextSchedule);


        Toast.makeText(ScheduleAddEvent.this, month+year+day, Toast.LENGTH_LONG).show();






    }

}
