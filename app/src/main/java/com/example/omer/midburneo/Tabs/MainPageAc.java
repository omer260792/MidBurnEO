package com.example.omer.midburneo.Tabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.omer.midburneo.Adapters.ImageAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.LoginAc;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.RegisterAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.ChatListAc.setImgUrlDefault;


public class MainPageAc extends AppCompatActivity {


    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    public String current_uid, current_image, getNameSP, current_admin, current_name, image;
    public static String current_camp_static;
    public static String current_uid_camp_static;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        prefs.edit().putString("email", "main").apply();
        prefs.edit().putString("status", "true").apply();
        current_camp_static = prefs.getString("camp", null);
        getNameSP = prefs.getString("name", null);


        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this));


        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        new Thread(new Runnable() {
            public void run() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                current_image = dataSnapshot.child("image").getValue().toString();
                current_admin = dataSnapshot.child("admin").getValue().toString();
                current_name = dataSnapshot.child("name").getValue().toString();
                current_camp_static = dataSnapshot.child("camps").getValue().toString();
                current_uid_camp_static = dataSnapshot.child("chat").getValue().toString();


                if(current_image.equals("default")){
                    image = setImgUrlDefault;

                }else {
                    image = current_image;

                }

                prefs.edit().putString("image",image).apply();
                prefs.edit().putString("admin", current_admin).apply();
                prefs.edit().putString("name", current_name).apply();
                prefs.edit().putString("camps", current_camp_static).apply();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

                    }
        }).start();




        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        current_admin = prefs.getString("admin", null);

                        if (current_admin.equals("admin")) {
                            startActivity(new Intent(MainPageAc.this, AdminAc.class));

                        } else {
                            Toast.makeText(MainPageAc.this, "אתה לא מנהל",
                                    Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 1:
                        Intent intent = new Intent(MainPageAc.this, ChatAc.class);
                        startActivity(intent);
                        break;
                    case 2:
                        startActivity(new Intent(MainPageAc.this, EquipmentAc.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainPageAc.this, ScheduleAc.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainPageAc.this, NotesAc.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainPageAc.this, ProfileAc.class));
                        break;


                }
            }
        });

        new Thread(new Runnable() {
            public void run() {

                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                Map<String, Object> mapCampsUpdates = new HashMap<>();
                mapCampsUpdates.put("status", "true");

                mUserDatabase.updateChildren(mapCampsUpdates);


            }
        }).start();

    }



}


