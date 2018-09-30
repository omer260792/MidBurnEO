package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.omer.midburneo.DataBase.DBEquipment;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.DataBase.DBEquipment.DATABASE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_camp_static;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_name_static;

public class EquipmentEditAc extends AppCompatActivity {

    private static final String TAG = "EquipmentEditAc";


    private Button addEquipmentBtnPre;
    private EditText etNameProdPre, etContentPre, etMountPre;
    private ImageView imagePre;
    public String nameProdPre, contentPre, mountPre, timePre, current_uid, get_msg_uid;
    public String imgPre = "default";
    private ProgressDialog mprogress;
    private DatabaseReference mUserDatabase;
    public DBHelper dbHelper;
    public DBEquipment dbEquipment;

    public static String  directory_path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath();


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipment_add_event_activity);


        addEquipmentBtnPre = findViewById(R.id.addEquipmentButtonEditEquip);
        etNameProdPre = findViewById(R.id.etNameProdEditEquip);
        etContentPre = findViewById(R.id.etContetEditEquip);
        etMountPre = findViewById(R.id.etMountEditEquip);
        imagePre = findViewById(R.id.imageEditEquip);

        mprogress = new ProgressDialog(this);
        dbHelper = new DBHelper(getApplicationContext());
        dbEquipment = new DBEquipment(getApplicationContext());


        try {
            current_camp_static = prefs.getString("camps", null);
            current_name_static = prefs.getString("name", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        current_uid = getIntent().getStringExtra("UidEquipment");
        PermissionManager.check(EquipmentEditAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);



        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/equipmentsDoc.xls");
        if (!file.exists()) {


            try {
                Log.v("File Created", String.valueOf(file.createNewFile()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(EquipmentEditAc.this,
                    "אין תיקייה ", //ADD THIS
                    Toast.LENGTH_SHORT).show();
            return ;
        }
        getUserCamp();


        addEquipmentBtnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                nameProdPre = etNameProdPre.getText().toString();
                contentPre = etContentPre.getText().toString();
                mountPre = etMountPre.getText().toString();

                mprogress.setMessage("שולח בקשה");
                mprogress.show();

                if (!nameProdPre.isEmpty() && !contentPre.isEmpty() && !mountPre.isEmpty()) {

                    get_msg_uid = UUID.randomUUID().toString();

                    long currentDateTime = System.currentTimeMillis();
                    timePre = String.valueOf(currentDateTime);

                    SaveDataToFireBaseEquipment();
                    dbHelper.SaveDBSqliteToEquipment(nameProdPre, contentPre, mountPre, "0", timePre, imgPre, current_uid, get_msg_uid);
                    dbEquipment.SaveDBSqliteToEquipmentExcel(nameProdPre, contentPre, mountPre, "0", timePre, imgPre, current_name_static);

                    SQLiteToExcel();

                    Intent i = new Intent(EquipmentEditAc.this, EquipmentAc.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                } else {
                    Toast.makeText(EquipmentEditAc.this,
                            "בבקשה תמלא את הפרטים ", //ADD THIS
                            Toast.LENGTH_SHORT).show();
                }

                mprogress.dismiss();

            }
        });


    }

    public void SaveDataToFireBaseEquipment() {

        Log.e(TAG, "SaveDataToFireBaseEquipment");

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(current_camp_static).child("Equipment").child(get_msg_uid);

        Map<String, Object> stringObjectHashMap = new HashMap<>();

        stringObjectHashMap.put("image", "default");
        stringObjectHashMap.put("nameProd", nameProdPre);
        stringObjectHashMap.put("content", contentPre);
        stringObjectHashMap.put("mount", mountPre);
        stringObjectHashMap.put("mountCurrent", "0");
        stringObjectHashMap.put("time", timePre);
        stringObjectHashMap.put("sender", current_uid);

        mUserDatabase.updateChildren(stringObjectHashMap);


    }

    public void getUserCamp() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                current_camp_static = dataSnapshot.child("camps").getValue().toString();

                prefs.edit().putString("camps", current_camp_static).apply();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



  public void SQLiteToExcel(){

      dbEquipment.open();
      SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(getApplicationContext(), DATABASE_NAME_EQUIPMENT, directory_path);

      sqLiteToExcel.exportAllTables(current_camp_static+".xls", new SQLiteToExcel.ExportListener() {
          @Override
          public void onStart() {

          }

          @Override
          public void onCompleted(String filePath) {
              Log.e("Successfully Exported", filePath);
          }

          @Override
          public void onError(Exception e) {

          }
      });

      Log.v(TAG, "SQLiteToExcel");
  }





}

