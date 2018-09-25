package com.example.omer.midburneo.Tabs;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.omer.midburneo.Adapters.EquipmentAdapter;
import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.Message;
import com.example.omer.midburneo.DataBase.DBEquipment;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static com.example.omer.midburneo.RegisterAc.prefs;

public class EquipmentAc extends AppCompatActivity {

    private Button addEquipmentBtn;
    public RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;
    private final List<Equipment> equipmentUtilsList = new ArrayList<>();

    public String current_admin,current_uid;
    public DBEquipment dbEquipment;
    public SQLiteDatabase db;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_equipment);

        addEquipmentBtn = findViewById(R.id.addEquipmentButton);
        recyclerView = findViewById(R.id.recycler_Equipment);

        dbEquipment = new DBEquipment(getApplicationContext());
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(EquipmentAc.this);
        recyclerView.setLayoutManager(layoutManager);

        getEquipment();

        addEquipmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                current_admin = prefs.getString("admin", null);

                if (current_admin.equals("admin")) {
                    Intent i =  new Intent(EquipmentAc.this, EquipmentEditAc.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("UidEquipment", current_uid);
                    startActivity(i);
                } else {
                    Toast.makeText(EquipmentAc.this, "אתה לא מנהל",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void getEquipment() {

        Log.e("*******************", "getEquipment");

        try {
            equipmentUtilsList.addAll(dbEquipment.getAllEquipment());
            mAdapter = new EquipmentAdapter(EquipmentAc.this, equipmentUtilsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(EquipmentAc.this));
            recyclerView.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            e.getStackTrace();
            e.getMessage();

        }
    }


    }
