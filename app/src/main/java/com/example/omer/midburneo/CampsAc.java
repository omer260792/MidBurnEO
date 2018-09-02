package com.example.omer.midburneo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.omer.midburneo.Class.ListCampAc;
import com.example.omer.midburneo.Tabs.MainPageAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.prefs;


public class CampsAc extends AppCompatActivity {

    public Button confirmCampBtn, BtnCreate;
    private Spinner spinner2;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    public String current_uid, current_num, uid_camp, camp_name, get_name_camp;
    public int size;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camps);



        spinner2 = findViewById(R.id.spinner2);
        BtnCreate = findViewById(R.id.BtnCreate);
        confirmCampBtn = findViewById(R.id.confirmCampBtn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();


        confirmCampBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SaveDataFireBase();

                prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                prefs.edit().putString("camps", camp_name).apply();


                Intent intent = new Intent(CampsAc.this, MainPageAc.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        BtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CampsAc.this, CreateCampAc.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        spinner2.setOnItemSelectedListener(new ItemSelectedListener());

        getDataSpinner();
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner2);

            // Set popupWindow height to 500px
            popupWindow.setHeight(500);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
    }

    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

        //get strings of first item
        String firstItem = String.valueOf(camp_name);//String.valueOf(spinner1.getSelectedItem());

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (firstItem.equals(String.valueOf(spinner2.getSelectedItem()))) {
                // ToDo when first item is selected
                Toast.makeText(parent.getContext(),
                        "ToDo when first item is selected: ",
                        Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(parent.getContext(),
                        "You have selected : " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                // Todo when item is selected by the user

                get_name_camp = parent.getItemAtPosition(pos).toString();

                getUidCamp();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }


    private void getDataSpinner() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps");
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<ListCampAc> contacts = new ArrayList<>();

                for (int i = 0; i < 1; i++) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        camp_name = data.getKey();

                        contacts.add(new ListCampAc(camp_name));

                    }

                }

                ArrayAdapter<ListCampAc> adapter =
                        new ArrayAdapter<ListCampAc>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, contacts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner2.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    public void getUidCamp(){


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Users");

        Query query = discussionRoomsRef.orderByChild("camps").equalTo(get_name_camp);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        uid_camp = ds.child("chat").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);


    }





    public void SaveDataFireBase() {

                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                Map<String, Object> mapUserUpdates = new HashMap<>();
                mapUserUpdates.put("camps", get_name_camp);
                mapUserUpdates.put("num", current_num);
                mapUserUpdates.put("chat", uid_camp);
                mUserDatabase.updateChildren(mapUserUpdates);


    }
}
