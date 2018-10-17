package com.example.omer.midburneo.Tabs;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class NoteEditAc extends AppCompatActivity {

    private static final String TAG = "NoteEditAc";


    public EditText etTitleNote, etContentNote;
    public Button saveButton, TimeButtonNote, addBtnfirendDateNote;

    public SharedPreferences prefs;

    public String current_uid, current_camp, current_name, UidRandom, date, title, content, dateEnd, timeMilliString;
    public long currentDateTime, timeMilli;
    private String imgLocalPath = "default";


    private DatabaseReference mUserDatabase;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public DBHelper dbHelper;
    public SQLiteDatabase db;

    private ProgressDialog mprogress;

    private String[] listItems;
    String[] listItemsKey;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    private Map<String, Object> stringObjectHashMapNote = new HashMap<>();
    private int num = 1;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_note);

        etTitleNote = findViewById(R.id.etTitleNote);
        etContentNote = findViewById(R.id.etContentNote);
        saveButton = findViewById(R.id.saveButtonNote);
        TimeButtonNote = findViewById(R.id.TimeButtonNote);
        addBtnfirendDateNote = findViewById(R.id.addBtnfirendDateNote);


        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbHelper = new DBHelper(this);
        mprogress = new ProgressDialog(this);

        getNamesUsersFromFireBase();


        addBtnfirendDateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringObjectHashMapNote.put(current_uid, firebaseUserModel.getName());

                num = 2;
                android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(NoteEditAc.this);
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
                        String name = firebaseUserModel.getName();

                        for (int i = 0; i < mUserItems.size(); i++) {

                            String listItemString = listItems[mUserItems.get(i)];
                            String listItemKeyString = listItemsKey[mUserItems.get(i)];

                            stringObjectHashMapNote.put(listItemKeyString, listItemString);
                            stringObjectHashMapNote.put(current_uid, firebaseUserModel.getName());


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

                            // Todo not dismiss
//                            mUserItems.size();

                            //   mItemSelected.setText("");
                        }
                    }
                });

                android.support.v7.app.AlertDialog mDialog = mBuilder.create();
                mDialog.show();


            }
        });


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

                    if (num == 1) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = true;

                            mUserItems.add(i);
                        }
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {

                            String listItemString = listItems[mUserItems.get(i)];
                            String listItemKeyString = listItemsKey[mUserItems.get(i)];

                            stringObjectHashMapNote.put(listItemKeyString, listItemString);

                            item = item + listItems[mUserItems.get(i)];

                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                    }
                    mprogress.setMessage("שולח בקשה");
                    mprogress.show();

                    currentDateTime = System.currentTimeMillis();
                    date = String.valueOf(currentDateTime);
                    UidRandom = UUID.randomUUID().toString();

                    SaveFireBaseDB();
                    dbHelper.SaveDBSqliteToNote(title, content, date, timeMilliString, "false", firebaseUserModel.getName(), current_uid, UidRandom);

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

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Note").child(UidRandom);
        Map<String, Object> mapAdminUpdate = new HashMap<>();
        mapAdminUpdate.put("title", title);
        mapAdminUpdate.put("content", content);
        mapAdminUpdate.put("date", date);
        mapAdminUpdate.put("dateEnd", timeMilliString);
        mapAdminUpdate.put("sender", firebaseUserModel.getName());
        mapAdminUpdate.put("dateBool", "false");
        mapAdminUpdate.put("uid", current_uid);

        if (num == 2) {
            mapAdminUpdate.put(FeedReaderContract.FeedEntry.TAG_USER, stringObjectHashMapNote);

        }
        mUserDatabase.updateChildren(mapAdminUpdate);
    }


    public void getNamesUsersFromFireBase() {


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Users");

        String chatUid = firebaseUserModel.getChat();

        Query query = discussionRoomsRef.orderByChild("chat").equalTo(chatUid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> contactsArray = new ArrayList<>();
                ArrayList<String> keyArray = new ArrayList<>();

                long FBCount = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    FBCount = dataSnapshot.getChildrenCount();
                    String key = ds.getKey();


                    if (!key.equals(firebaseUserModel.getChat())) {

                        if (!key.equals(current_uid)) {
                            String name = ds.child("name").getValue().toString();


                            contactsArray.add(name);
                            keyArray.add(key);
                        } else {

                        }


                    }


                }


                listItems = new String[contactsArray.size()];
                listItems = contactsArray.toArray(listItems);

                listItemsKey = new String[keyArray.size()];
                listItemsKey = keyArray.toArray(listItemsKey);

                // listItems = getResources().getStringArray((int) FBCount);
                checkedItems = new boolean[listItems.length];


                int count = (int) FBCount;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);

    }


}
