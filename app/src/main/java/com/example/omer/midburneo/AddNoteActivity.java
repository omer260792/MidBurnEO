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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.NotePreviewActivity.getFormattedDate;
import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;
import static com.example.omer.midburneo.Tabs.MainPageAc.SHPRF;
import static com.example.omer.midburneo.Tabs.MainPageAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class AddNoteActivity extends AppCompatActivity {

    public DBHelper db;
    public String current_uid, get_msg_uid, timeMilliString, addressString;
    private String imgPre = "default";
    private String imgLocalPath = "default";
    private long timeMili = 123456789;
    public MyEventDay myEventDay;
    private DatabaseReference mUserDatabase;
    private Button BtnTimeNoteEdit, addBtnDateCalEdit, addBtnLoctionCalEdit, addBtnfirendCalEdit;
    private TimePickerDialog mTimeSetListener;
    private DatePickerDialog mDatePickerDialog;
    private Calendar c;
    private StorageReference mImageStorage, filePath;
    private ProgressDialog mprogress;
    public EditText noteEditText;


    private Uri resultUri;

    String[] listItems;
    String[] listItemsKey;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    Map<String, Object> stringObjectHashMap = new HashMap<>();
    private int num = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_activity);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        get_msg_uid = UUID.randomUUID().toString();

        db = new DBHelper(getApplicationContext());
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mprogress = new ProgressDialog(this);


        Button button = (Button) findViewById(R.id.addNoteButton);
        BtnTimeNoteEdit = findViewById(R.id.BtnTimeNoteEdit);
        addBtnDateCalEdit = findViewById(R.id.addBtnDateCalEdit);
        addBtnLoctionCalEdit = findViewById(R.id.addBtnLoctionCalEdit);
        addBtnfirendCalEdit = findViewById(R.id.addBtnfirendCalEdit);

       noteEditText = (EditText) findViewById(R.id.noteEditText);

        String currentTimeIntent = getIntent().getStringExtra("currentDate");

        getNameUserFromFireBase();


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

                mDatePickerDialog = new DatePickerDialog(AddNoteActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            c.set(Calendar.YEAR, view.getYear());
                            c.set(Calendar.MONTH, view.getMonth());
                            c.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());


                        }
                    }
                }, year, month, day);
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


        addBtnfirendCalEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stringObjectHashMap.put(current_uid, firebaseUserModel.getName());


                num = 2;
                android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(AddNoteActivity.this);
                mBuilder.setTitle("סמן חברים");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                        if (isChecked) {
                            mUserItems.add(position);


                        } else {
                            mUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";

                        for (int i = 0; i < mUserItems.size(); i++) {

                            String listItemString = listItems[mUserItems.get(i)];
                            String listItemKeyString = listItemsKey[mUserItems.get(i)];

                            stringObjectHashMap.put(listItemKeyString, listItemString);

                            item = item + listItems[mUserItems.get(i)];

                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }


                    }
                });

                mBuilder.setNegativeButton("חזור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("בחר הכל", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = true;


                        }
                    }
                });

                android.support.v7.app.AlertDialog mDialog = mBuilder.create();
                mDialog.show();


            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (timeMili == 123456789 || noteEditText.equals("")) {

                    Toast.makeText(AddNoteActivity.this, "בבקשה תמלא את כל השורות", Toast.LENGTH_LONG).show();

                } else {
                    imgUpload();

                    if (num == 1){
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = true;

                            mUserItems.add(i);
                        }
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {

                            String listItemString = listItems[mUserItems.get(i)];
                            String listItemKeyString = listItemsKey[mUserItems.get(i)];

                            stringObjectHashMap.put(listItemKeyString, listItemString);

                            stringObjectHashMap.put(current_uid, firebaseUserModel.getName());

                            item = item + listItems[mUserItems.get(i)];

                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }

                    }

                    Intent returnIntent = new Intent();
                    MyEventDay myEventDay = new MyEventDay(c,
                            R.drawable.ic_send, noteEditText.getText().toString());


                    returnIntent.putExtra(ScheduleAc.RESULT, myEventDay);

                    long currentDateTime = System.currentTimeMillis();
                    String time = String.valueOf(currentDateTime);
                    String msg = myEventDay.getNote();

                    String calendar = getFormattedDate(myEventDay.getCalendar().getTime());


                    SaveInFireBase(msg, current_uid, calendar, get_msg_uid, timeMilliString, firebaseUserModel.getName(), imgPre);
                    db.SaveDBSqliteToCalendar(msg, current_uid, calendar, get_msg_uid, timeMilliString, firebaseUserModel.getName(), imgLocalPath);


                    prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                    prefs.edit().putString("time_calendar", calendar).apply();

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();


                }
            }
        });
    }

    public void SaveInFireBase(String msg, String sender, String time, String msguid, String timeMsg, String name, String image) {

        try {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Calendar").child(msguid);

            Map<String, Object> mapCampsUpdates = new HashMap<>();
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, sender);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.TIME, time);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.TIME__SET, timeMsg);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.NAME, name);
            mapCampsUpdates.put(FeedReaderContract.FeedEntry.IMAGE, image);

            if (num == 2){
                mapCampsUpdates.put(FeedReaderContract.FeedEntry.TAG_USER, stringObjectHashMap);

            }

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
                imgLocalPath = String.valueOf(resultUri);

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

                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Calendar").child(get_msg_uid);

                        Map<String, Object> mapCampsUpdates = new HashMap<>();
                        mapCampsUpdates.put(FeedReaderContract.FeedEntry.IMAGE, imgPre);

                        mUserDatabase.updateChildren(mapCampsUpdates);

                        mprogress.dismiss();


                    }
                });
            }
        });


    }

    public void getNameUserFromFireBase() {


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Users");

        String chatUid = firebaseUserModel.getChat();

        Query query = discussionRoomsRef.orderByChild("chat").equalTo(chatUid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> contactsArray = new ArrayList<>();
                ArrayList<String> keyArray = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String key = ds.getKey();

                    if (!key.equals(firebaseUserModel.getChat())) {

                        if (!key.equals(current_uid)){
                            String name = ds.child("name").getValue().toString();


                            contactsArray.add(name);
                            keyArray.add(key);
                        }else {

                        }
                    }
                }

                listItems = new String[contactsArray.size()];
                listItems = contactsArray.toArray(listItems);

                listItemsKey = new String[keyArray.size()];
                listItemsKey = keyArray.toArray(listItemsKey);

                checkedItems = new boolean[listItems.length];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);

    }


}