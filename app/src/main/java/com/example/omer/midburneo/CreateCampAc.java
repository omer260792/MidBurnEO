package com.example.omer.midburneo;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.Tabs.MainPageAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.omer.midburneo.RegisterAc.SHPRF;

public class CreateCampAc extends AppCompatActivity {

    public EditText nameCampField, themeCampField;
    public Button confirmCreateCamp;
    public String camp_name, camptheme, name, current_uid, current_name, current_camp_user, randomNumString;

    DatabaseReference mUserDatabase;
    SharedPreferences prefs;

    public String num = "1";


    public SQLiteDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_camp);

        nameCampField = findViewById(R.id.nameCampField);
        themeCampField = findViewById(R.id.themeCampField);
        confirmCreateCamp = findViewById(R.id.registerButton);

        random();


        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        confirmCreateCamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camptheme = themeCampField.getText().toString();
                camp_name = nameCampField.getText().toString();

                if (camp_name != null && camptheme != null) {

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference mUserDatabase1 = database.getReference().child("Camps");

                    mUserDatabase1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            try {
                                name = dataSnapshot.child(camp_name).getValue().toString();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (camp_name == name) {
                                Toast.makeText(CreateCampAc.this, "The name allready exist", Toast.LENGTH_LONG).show();

                            } else {

                                SaveDBFireBase();
                             //   SaveDBSqlite();

                                prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                                prefs.edit().putString("camps", camp_name).apply();

                                Intent intent = new Intent(CreateCampAc.this, MainPageAc.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }


    public void SaveDBSqlite() {


        DBHelper mDbHelper = new DBHelper(getApplicationContext());

        db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.ADMIN, "admin");
        values.put(FeedReaderContract.FeedEntry.CAMPS, camp_name);

        String selection = FeedReaderContract.FeedEntry.NAME + " = ?";
        String[] selectionArgs = {current_name};

        db.update(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);



//        ContentValues valuess = new ContentValues();
//
//        valuess.put(FeedReaderContract.FeedEntry.ADMIN, "default");
//        valuess.put(FeedReaderContract.FeedEntry.CAMPS, camp_name);
//        valuess.put(FeedReaderContract.FeedEntry.CHAT, "default");
//        valuess.put(FeedReaderContract.FeedEntry.EMAIL, "default");
//        valuess.put(FeedReaderContract.FeedEntry.IMAGE, "default");
//        valuess.put(FeedReaderContract.FeedEntry.NAME, camp_name);
//        valuess.put(FeedReaderContract.FeedEntry.NUMBER, "default");
//        valuess.put(FeedReaderContract.FeedEntry.PASSWORD, "default");
//        valuess.put(FeedReaderContract.FeedEntry.STATUS, "default");
//        valuess.put(FeedReaderContract.FeedEntry.TIME, "default");
//        valuess.put(FeedReaderContract.FeedEntry.UID_id, "default");
//        valuess.put(FeedReaderContract.FeedEntry.UID, "default");
//        valuess.put(FeedReaderContract.FeedEntry.LASTMSG, "default");
//
//        long result = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, valuess);
//        Log.i("iaiaiaiaiaiaia", String.valueOf(result));



        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        prefs.edit().putString("camp", camp_name).apply();

        num = "2";


    }

    public void SaveDBFireBase() {


        current_camp_user = randomNumString;


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(camp_name);
        Map<String, Object> mapCampsUpdates1 = new HashMap<>();
        mapCampsUpdates1.put("camptheme", camptheme);
        mapCampsUpdates1.put("name", camp_name);
        mapCampsUpdates1.put("chat", current_camp_user);
        mUserDatabase.updateChildren(mapCampsUpdates1);


        new Thread(new Runnable() {
            public void run() {

                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
                Map<String, Object> mapUserUpdates4 = new HashMap<>();
                mapUserUpdates4.put("camps", camp_name);
                mapUserUpdates4.put("admin", "admin");
                mapUserUpdates4.put("chat", current_camp_user);
                mUserDatabase.updateChildren(mapUserUpdates4);


                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_camp_user);
                Map<String, Object> userMapCamp = new HashMap<>();


                userMapCamp.put(FeedReaderContract.FeedEntry.NAME, camp_name);
                userMapCamp.put(FeedReaderContract.FeedEntry.EMAIL, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.PASSWORD, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.NUMBER, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.IMAGE, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.ADMIN, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.CHAT, current_camp_user);
                userMapCamp.put(FeedReaderContract.FeedEntry.CAMPS, camp_name);
                userMapCamp.put(FeedReaderContract.FeedEntry.STATUS, "status");
                userMapCamp.put(FeedReaderContract.FeedEntry.TIME, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.LASTMSG, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.UID, current_uid);
                userMapCamp.put(FeedReaderContract.FeedEntry.ROLE, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.ONLINE, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.PHONE, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, "default");


                mUserDatabase.updateChildren(userMapCamp);




            }
        }).start();


    }


    public void random() {

        Random randomGenerator = new Random();

        long randomInt = randomGenerator.nextInt(1000000000);
        randomNumString = String.valueOf(randomInt);


    }

}
