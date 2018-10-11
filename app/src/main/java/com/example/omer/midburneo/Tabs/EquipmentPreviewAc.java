package com.example.omer.midburneo.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class EquipmentPreviewAc extends AppCompatActivity {

    private Button addEquipmentBtnPre;
    private EditText etNameProdPre, etContentPre, etMountPre, etMountCurrentPre, etTimePre;
    private ImageView imagePre;
    public String nameProdPre, contentPre, mountPre, mountCurrentPre, timePre, imgPre, current_uid, get_msg_uid;
    private DatabaseReference mUserDatabase;
    public DBHelper dbHelper;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipment_preview_activity);


        addEquipmentBtnPre = findViewById(R.id.addEquipmentButtonPre);
        etNameProdPre = findViewById(R.id.etNameProdPre);
        etContentPre = findViewById(R.id.etContetPre);
        etMountPre = findViewById(R.id.etMountPre);
        imagePre = findViewById(R.id.imagePre);
        etMountCurrentPre = findViewById(R.id.etMountCurrentPre);
        etTimePre = findViewById(R.id.etTimedPre);

        dbHelper = new DBHelper(getApplicationContext());


        nameProdPre = getIntent().getStringExtra("nameProdEquipment");
        contentPre = getIntent().getStringExtra("contentEquipment");
        mountPre = getIntent().getStringExtra("mountEquipment");
        mountCurrentPre = getIntent().getStringExtra("mountCurrentEquipment");
        timePre = getIntent().getStringExtra("timeEquipment");
        imgPre = getIntent().getStringExtra("imageEquipment");
        current_uid = getIntent().getStringExtra("senderUidEquipment");
        get_msg_uid = getIntent().getStringExtra("msgUidEquipment");

        if (imgPre == null || imgPre == "default") {

            Picasso.get().load(R.drawable.midburn_logo).error(R.drawable.midburn_logo).into(imagePre);

        } else {
            Picasso.get().load(imgPre).error(R.drawable.midburn_logo).into(imagePre);

        }
        etNameProdPre.setText(nameProdPre);
        etContentPre.setText(contentPre);
        etMountPre.setText(mountPre);
        etMountCurrentPre.setText(mountCurrentPre);
        etTimePre.setText(timePre);


        addEquipmentBtnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mountCurrentPre = etMountCurrentPre.getText().toString();

                if (!mountCurrentPre.isEmpty()) {

                    long currentDateTime = System.currentTimeMillis();
                    timePre = String.valueOf(currentDateTime);
                    UpdateDataToFireBaseEquipment();




                } else {
                    Toast.makeText(EquipmentPreviewAc.this,
                            "בבקשה תמלא את הפרטים ",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void UpdateDataToFireBaseEquipment() {

        Log.e("*******************", "UpdateDataToFireBaseEquipment");

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getCamp()).child("Equipment").child(get_msg_uid);

        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("mountCurrent", mountCurrentPre);
        mUserDatabase.updateChildren(stringObjectHashMap);


    }


}
