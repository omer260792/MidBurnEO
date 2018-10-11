package com.example.omer.midburneo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;


import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.Tabs.EquipmentEditAc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.NotePreviewActivity.getFormattedDate;
import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class AddNoteActivity extends AppCompatActivity {

    public DBHelper db;
    public String current_uid, get_msg_uid, timeMilliString, addressString ;
    private String imgPre = "default";
    public String msg = "hello";
    private long timeMili = 123456789;
    public MyEventDay myEventDay;
    private DatabaseReference mUserDatabase;
    private Button BtnTimeNoteEdit, addBtnDateCalEdit, addBtnLoctionCalEdit;
    private TimePickerDialog mTimeSetListener;
    private DatePickerDialog mDatePickerDialog;
    private Calendar c;
    private StorageReference mImageStorage, filePath;
    private ProgressDialog mprogress;


    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_activity);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        get_msg_uid = UUID.randomUUID().toString();

        db = new DBHelper(getApplicationContext());
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mprogress = new ProgressDialog(this);



        final CalendarView datePicker = (CalendarView) findViewById(R.id.datePicker);
        Button button = (Button) findViewById(R.id.addNoteButton);
        BtnTimeNoteEdit = findViewById(R.id.BtnTimeNoteEdit);
        addBtnDateCalEdit = findViewById(R.id.addBtnDateCalEdit);
        addBtnLoctionCalEdit = findViewById(R.id.addBtnLoctionCalEdit);

        final EditText noteEditText = (EditText) findViewById(R.id.noteEditText);

        String currentTimeIntent = getIntent().getStringExtra("currentDate");


        addBtnLoctionCalEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNoteActivity.this);
                alertDialog.setTitle("כתובת האירוע");
                alertDialog.setMessage("הכנס כתובת");

                final EditText input = new EditText(AddNoteActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.midburn_logo);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                addressString = input.getText().toString();
                                if (addressString.compareTo("") != 0) {

                                        Toast.makeText(getApplicationContext(),
                                                "הוכנס כתובת", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }

        });


        addBtnDateCalEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Get Current Date
                 c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                mDatePickerDialog= new DatePickerDialog(AddNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            c.set(Calendar.YEAR, view.getYear());
                            c.set(Calendar.MONTH, view.getMonth());
                            c.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());

//                            Calendar cal = Calendar.getInstance();
//
//                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
//
//                            timeMili = c.getTimeInMillis();
//
//                            realDate = sdf.format(timeMili);
//
//                            try {
//                                cal.setTime(sdf.parse(realDate));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//
//                            Log.i("ssssssssffffffssss", realDate);


                        }
                    }
                },year,month,day);
                mDatePickerDialog.show();



            }
        });
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

//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
//        try {
//            cal.setTime(sdf.parse(currentTimeIntent));// all done
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        myEventDay = new MyEventDay(cal, R.drawable.ic_send, msg);
//
//        try {
//            datePicker.setDate(cal);
//
//        } catch (OutOfDateRangeException e) {
//            e.printStackTrace();
//        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (timeMili == 123456789 || noteEditText.equals("")) {

                    Toast.makeText(AddNoteActivity.this, "please typ time", Toast.LENGTH_LONG).show();

                } else {
                    imgUpload();

                    Intent returnIntent = new Intent();
                    MyEventDay myEventDay = new MyEventDay(c,
                            R.drawable.ic_send, noteEditText.getText().toString());


                    returnIntent.putExtra(ScheduleAc.RESULT, myEventDay);

                    long currentDateTime = System.currentTimeMillis();
                    String time = String.valueOf(currentDateTime);
                    String msg = myEventDay.getNote();

                    String calendar = getFormattedDate(myEventDay.getCalendar().getTime());


                    SaveInFireBase(msg, current_uid, calendar, get_msg_uid, timeMilliString, firebaseUserModel.getName(),imgPre);
                    db.SaveDBSqliteToCalendar(msg, current_uid, calendar, get_msg_uid, timeMilliString, firebaseUserModel.getName(),imgPre);


                    prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                    prefs.edit().putString("time_calendar", calendar).apply();

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();


                }
            }
        });
    }

    public void SaveInFireBase(String msg, String sender, String time, String msguid, String timeMsg, String name,String image) {

        try {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getCamp()).child("Calendar").child(msguid);

            Map<String, Object> mapCampsUpdates = new HashMap<>();
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, sender);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.TIME, time);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.TIME__SET, timeMsg);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.NAME, name);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.IMAGE, image);

            mUserDatabase.updateChildren(mapCampsUpdates);
        } catch (Exception e) {

        }

    }

    public void onImg(View view) {

        PermissionManager.check(AddNoteActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(AddNoteActivity.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(AddNoteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("תמונת פרופיל");
        alertDialog.setMessage("בחר תמונה");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "גלריה", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                gallery();

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "חזור", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                return;

            }
        });

        alertDialog.show();
    }


    private void gallery() {

        //open media activity for image --
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)//crop image activity after shot
                    .setAspectRatio(1, 1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imgPre =String.valueOf(resultUri) ;

                filePath = mImageStorage.child("profile_images").child(resultUri.getLastPathSegment());


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(AddNoteActivity.this, "error", Toast.LENGTH_LONG).show();

            }
        }

    }

    private void imgUpload() {

        if (resultUri == null) {
            return;
        }

        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        imgPre = String.valueOf(uri);

                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getCamp()).child("Equipment").child(get_msg_uid);

                        Map<String, Object> mapCampsUpdates = new HashMap<>();
                        mapCampsUpdates.put(FeedReaderContract.FeedEntry.IMAGE, imgPre);

                        mUserDatabase.updateChildren(mapCampsUpdates);


                        mprogress.dismiss();


                    }
                });
            }
        });


    }


}