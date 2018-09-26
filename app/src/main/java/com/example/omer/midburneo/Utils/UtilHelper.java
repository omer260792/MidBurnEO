package com.example.omer.midburneo.Utils;

import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class UtilHelper extends AppCompatActivity {


    public static String getStaticTimeInString(String time) {

        long currentDateTime = System.currentTimeMillis();
        String StringCurrentMil = String.valueOf(time);

        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");

        time = getTimeHourMintus.format(currentDateTime);
        return time;
    }


}
