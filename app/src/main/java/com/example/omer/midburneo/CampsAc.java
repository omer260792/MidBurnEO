// done
package com.example.omer.midburneo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;


public class CampsAc extends AppCompatActivity {

    private static final String TAG = "CampsAc";

    public Button confirmCampBtn, BtnCreate;
    private Spinner spinner;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mprogress;


    public String current_uid, current_uid_camp, uid_camp, camp_name, get_name_camp;
    public long countFB;
    public int size;
    public int num = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camps);

        spinner = findViewById(R.id.spinner2);
        BtnCreate = findViewById(R.id.BtnCreate);
        confirmCampBtn = findViewById(R.id.confirmCampBtn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();


        mprogress = new ProgressDialog(this);


        getDataSpinner();


        try {

            spinner.setOnItemSelectedListener(new ItemSelectedListener());

            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(500);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        confirmCampBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                updateDataFireBase();

                mprogress.setMessage("מוריד מידע");
                mprogress.show();

                prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                prefs.edit().putString("camps", get_name_camp).apply();
                prefs.edit().putString("chat", current_uid_camp).apply();


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        Intent intent = new Intent(CampsAc.this, MainPageAc.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();


                    }
                }, 2000);   //


            }
        });

        BtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CampsAc.this, CreateCampAc.class);
                startActivity(intent);

            }
        });

    }


    //get camp list and set in spinner
    private void getDataSpinner() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child("AllCamps");
        //get value of camps table
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<ListCampAc> contacts = new ArrayList<>();
                contacts.add(new ListCampAc("רשימת קמפים"));

                //running loop of all the children and getting key
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    camp_name = ds.getKey();


                    current_uid_camp = String.valueOf(ds.getValue());


                    contacts.add(new ListCampAc(camp_name));


                }


                //set adapter with drop down function
                ArrayAdapter<ListCampAc> adapter =
                        new ArrayAdapter<ListCampAc>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, contacts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {


        //get strings of first item
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (camp_name.equals(spinner.getSelectedItem().toString())) {

                get_name_camp = parent.getItemAtPosition(pos).toString();


                getUidCamp();

            } else {

                if (parent.getItemAtPosition(pos).toString().equals("רשימת קמפים")) {

                    if (num == 1) {
                        Toast.makeText(parent.getContext(),
                                "בחר קמפ",
                                Toast.LENGTH_LONG).show();
                        num = 2;
                    } else {
                        Toast.makeText(parent.getContext(),
                                "רשימת קמפים??? בחר קבוצה אחרת!",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    get_name_camp = parent.getItemAtPosition(pos).toString();


                    getUidCamp();
                }

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }


    public void getUidCamp() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Users");

        Query query = discussionRoomsRef.orderByChild("camps").equalTo(get_name_camp);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    countFB = dataSnapshot.getChildrenCount();
                    uid_camp = ds.child("chat").getValue(String.class);
                    camp_name = ds.child("camps").getValue(String.class);
                    return;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

    }


    public void updateDataFireBase() {

        long lastCount = countFB + 1;
        String lastCountString = String.valueOf(lastCount);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        Map<String, Object> mapUserUpdates = new HashMap<>();
        mapUserUpdates.put("camps", get_name_camp);
        mapUserUpdates.put("chat", uid_camp);
        mapUserUpdates.put("number", lastCountString);


        mUserDatabase.updateChildren(mapUserUpdates);

    }
}
