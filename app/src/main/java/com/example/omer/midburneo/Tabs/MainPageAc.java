package com.example.omer.midburneo.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.omer.midburneo.Adapters.ImageAdapter;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.ScheduleAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.ChatListAc.setImgUrlDefault;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_MESSAGE;


public class MainPageAc extends AppCompatActivity {


    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    public String current_uid, current_image, current_name, image;
    public static String current_camp_static;
    public static String current_admin_static;
    public static String current_uid_camp_static;
    public static String current_time_static;
    public static String current_time_calendar_static;
    public static String current_name_static;
    public static String current_onilne_static;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        prefs.edit().putString("email", "main").apply();
        prefs.edit().putString("status", "אין סטטוס").apply();
        current_camp_static = prefs.getString("camps", null);
        current_name_static = prefs.getString("name", null);
        current_name_static = prefs.getString("name", null);


        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TABLE_NAME_MESSAGE = current_uid;


        getUserDBFromFireBase();
        UpdateUserOnline();


        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        current_admin_static = prefs.getString("admin", null);

                        if (current_admin_static.equals("admin")) {
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


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void getUserDBFromFireBase() {
//        new Thread(new Runnable() {
//            public void run() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                current_image = dataSnapshot.child("image").getValue().toString();
                current_admin_static = dataSnapshot.child("admin").getValue().toString();
                current_name = dataSnapshot.child("name").getValue().toString();
                current_camp_static = dataSnapshot.child("camps").getValue().toString();
                current_uid_camp_static = dataSnapshot.child("chat").getValue().toString();
                //current_time_static = dataSnapshot.child("time").getValue().toString();

                if (current_image.equals("default")) {
                    image = setImgUrlDefault;

                } else {
                    image = current_image;

                }

                prefs.edit().putString("image", image).apply();
                prefs.edit().putString("admin", current_admin_static).apply();
                prefs.edit().putString("name", current_name).apply();
                prefs.edit().putString("camps", current_camp_static).apply();
                prefs.edit().putString("chat", current_uid_camp_static).apply();
                //prefs.edit().putString("time", current_time_static).apply();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//            }
//        }).start();

    }

    public void UpdateUserOnline() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        Map<String, Object> mapCampsUpdates = new HashMap<>();
        mapCampsUpdates.put("online", "true");

        mUserDatabase.updateChildren(mapCampsUpdates);


    }


}


