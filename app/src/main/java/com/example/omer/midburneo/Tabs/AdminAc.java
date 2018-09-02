package com.example.omer.midburneo.Tabs;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.RegisterAc.SHPRF;

public class AdminAc extends AppCompatActivity {

    public EditText notesText;
    public Button saveButton;

    public SharedPreferences prefs;

    public String current_uid, current_camp, UidRandom;
    public long currentDateTime;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_admin);

        notesText = (EditText) findViewById(R.id.editTextNote);
        saveButton = (Button) findViewById(R.id.saveButtonNote);

        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        current_camp = prefs.getString("camps", null);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = notesText.getText().toString();


                if (msg.equals("")) {
                    Toast.makeText(getBaseContext(), "נא למלא מטלה", Toast.LENGTH_SHORT).show();

                } else {
                    currentDateTime = System.currentTimeMillis();
                    String time = String.valueOf(currentDateTime);
                    UidRandom = UUID.randomUUID().toString();


                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Admin").child(current_camp).child("message").child(UidRandom);
                    Map<String, Object> mapAdminUpdate = new HashMap<>();
                    mapAdminUpdate.put("msg", msg);
                    mapAdminUpdate.put("time", time);
                    mapAdminUpdate.put("sender", current_uid);
                    mUserDatabase.updateChildren(mapAdminUpdate);

                    notesText.setText("");
                }


            }
        });

    }

}
