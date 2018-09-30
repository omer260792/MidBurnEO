package com.example.omer.midburneo.Tabs;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.RegisterAc.SHPRF;

public class NoteEditAc extends AppCompatActivity {

    private static final String TAG = "NoteEditAc";


    public EditText etTitleNote, etContentNote;
    public Button saveButton, TimeButtonNote;

    public SharedPreferences prefs;

    public String current_uid, current_camp, current_name, UidRandom, date, title, content, dateEnd, timeMilliString;
    public long currentDateTime, timeMilli;

    private DatabaseReference mUserDatabase;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public DBHelper dbHelper;
    public SQLiteDatabase db;

    private ProgressDialog mprogress;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_note);

        etTitleNote =  findViewById(R.id.etTitleNote);
        etContentNote =  findViewById(R.id.etContentNote);
        saveButton = findViewById(R.id.saveButtonNote);
        TimeButtonNote = findViewById(R.id.TimeButtonNote);

        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        current_camp = prefs.getString("camps", null);
        current_name = prefs.getString("name", null);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbHelper = new DBHelper(this);
        mprogress = new ProgressDialog(this);


        TimeButtonNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        NoteEditAc.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                cal.set(Calendar.MONTH, datePicker.getMonth());
                cal.set(Calendar.YEAR, datePicker.getYear());

                dateEnd = month + "/" + day + "/" + year;
                timeMilli = cal.getTimeInMillis();
                timeMilliString = String.valueOf(timeMilli);


            }
        };



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = etTitleNote.getText().toString();
                content = etTitleNote.getText().toString();

                if (title.equals("") && content.equals("")) {
                    Toast.makeText(getBaseContext(), "נא למלא את כל שדות המטלה", Toast.LENGTH_SHORT).show();

                } else {

                    mprogress.setMessage("שולח בקשה");
                    mprogress.show();

                    currentDateTime = System.currentTimeMillis();
                    date = String.valueOf(currentDateTime);
                    UidRandom = UUID.randomUUID().toString();

                    SaveFireBaseDB();
                    dbHelper.SaveDBSqliteToNote(title, content, date, timeMilliString, "false", current_name, current_camp, UidRandom);

                    etTitleNote.setText("");
                    etContentNote.setText("");

                    Intent intent = new Intent(NoteEditAc.this, NotesAc.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    mprogress.dismiss();

                }

            }
        });

    }

    public void SaveFireBaseDB() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(current_camp).child("Note").child(UidRandom);
        Map<String, Object> mapAdminUpdate = new HashMap<>();
        mapAdminUpdate.put("title", title);
        mapAdminUpdate.put("content", content);
        mapAdminUpdate.put("date", date);
        mapAdminUpdate.put("dateEnd", timeMilliString);
        mapAdminUpdate.put("sender", current_name);
        mapAdminUpdate.put("dateBool", "false");
        mUserDatabase.updateChildren(mapAdminUpdate);
    }


}
