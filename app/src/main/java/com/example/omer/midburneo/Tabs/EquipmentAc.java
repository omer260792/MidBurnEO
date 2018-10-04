package com.example.omer.midburneo.Tabs;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.example.omer.midburneo.Adapters.EquipmentAdapter;
import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBEquipment;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.DataBase.DBEquipment.DATABASE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.EquipmentEditAc.directory_path;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_camp_static;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_name_static;

public class EquipmentAc extends AppCompatActivity {

    private static final String TAG = "EquipmentAc";

    private EditText etSearchEquipment;
    private EquipmentAdapter mAdapter;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    private ArrayList<Equipment> equipmentUtilsList = new ArrayList<>();

    public String current_admin, current_uid;
    public Boolean boolRecycler = false;
    public DBEquipment dbEquipment;
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private int countSqlLite;
    private int num = 1;



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_equipment);

        Log.e(TAG, "onCreate");



        recyclerView = findViewById(R.id.recycler_Equipment);
        etSearchEquipment = findViewById(R.id.etSearchEquipment);

        dbEquipment = new DBEquipment(getApplicationContext());
        dbHelper = new DBHelper(getApplicationContext());
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            current_camp_static = prefs.getString("camps", null);
            current_name_static = prefs.getString("name", null);

            Log.e(TAG, current_name_static);
            Log.e(TAG, current_camp_static);

        } catch (NullPointerException e) {

        }


        layoutManager = new LinearLayoutManager(EquipmentAc.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        getRawCountSql();
        getEquipment(boolRecycler);

        etSearchEquipment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(TAG, "beforeTextChanged");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "onTextChanged");


            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
                Log.e(TAG, "afterTextChanged");
                String test = etSearchEquipment.getText().toString() + "";

                if (!test.equals("")) {

                    boolRecycler = false;
                    Log.e(TAG, "boolRecycler = false;" + test);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(EquipmentAc.this);
                    layoutManager.setStackFromEnd(boolRecycler);
                    recyclerView.setLayoutManager(layoutManager);

                } else {
                    boolRecycler = true;
                    Log.e(TAG, "boolRecycler = true;" + test);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(EquipmentAc.this);
                    layoutManager.setStackFromEnd(boolRecycler);
                    recyclerView.setLayoutManager(layoutManager);


                }


            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onstart");

        if (countSqlLite == 0) {
            Log.e(TAG, "oncreat if " + String.valueOf(countSqlLite));

            UpdateDateFromFireBaseToSQLiteEquipment();

        } else {
            String test = etSearchEquipment.getText().toString() + "";

            if (test.equals("")) {

                boolRecycler = true;
                Log.e(TAG, "boolRecycler = true onCreat;" + test);
                LinearLayoutManager layoutManager = new LinearLayoutManager(EquipmentAc.this);
                layoutManager.setStackFromEnd(boolRecycler);

            } else {
                boolRecycler = false;
                Log.e(TAG, "boolRecycler = false onCreat;" + test);
                LinearLayoutManager layoutManager = new LinearLayoutManager(EquipmentAc.this);
                layoutManager.setStackFromEnd(boolRecycler);

            }
        }
    }

    public void getEquipment(Boolean bool) {

        Log.e(TAG, "getEquipment");

        try {
            if (num==1){
                equipmentUtilsList.addAll(dbHelper.getAllEquipment());
                mAdapter = new EquipmentAdapter(EquipmentAc.this, equipmentUtilsList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(EquipmentAc.this);
                layoutManager.setStackFromEnd(bool);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(mAdapter);
                Log.e(TAG, "getEquipment() + try");

                UpdateDateFromFireBaseToSQLiteEquipment();
                num= 2;
            }




        } catch (Exception e) {
            e.printStackTrace();
            e.getStackTrace();
            e.getMessage();

        }
    }

    private void filter(String text) {
        ArrayList<Equipment> filteredList = new ArrayList<>();

        for (Equipment item : equipmentUtilsList) {
            if (item.nameProd.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);

            }

//            if (item.content.toLowerCase().contains(text.toLowerCase())) {
//                filteredList.add(item);
//
//            }
        }

        mAdapter.filterList(filteredList);
    }

    public void UpdateDateFromFireBaseToSQLiteEquipment() {
        if (current_camp_static.equals(null)) {

            Log.e(TAG, "UpdateDateFromFireBaseToSQLiteEquipment After + if");

            return;
        } else {
            getRawCountSql();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference discussionRoomsRef = rootRef.child("Camps").child(current_camp_static).child("Equipment");
            Log.e(TAG, "UpdateDateFromFireBaseToSQLiteEquipment After + else");

            discussionRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    long FBCount = dataSnapshot.getChildrenCount();
                    if (countSqlLite == 0) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {


                            String name = ds.child("nameProd").getValue(String.class);
                            String content = ds.child(FeedReaderContract.FeedEntry.CONTENT).getValue(String.class);
                            String amount = ds.child(FeedReaderContract.FeedEntry.MOUNT).getValue(String.class);
                            String amountCurrent = ds.child("mountCurrent").getValue(String.class);
                            String time = ds.child(FeedReaderContract.FeedEntry.TIME).getValue(String.class);
                            String image = ds.child(FeedReaderContract.FeedEntry.IMAGE).getValue(String.class);
                            String sender = ds.child("sender").getValue(String.class);
                            String msgUid = ds.child(FeedReaderContract.FeedEntry.MESSAGE_UID).getKey();


                            dbHelper.SaveDBSqliteToEquipment(name, content, amount, amountCurrent, time, image, sender, msgUid);
                            Log.e(TAG, "UpdateDateFromFireBaseToSQLiteEquipment After + countSqlLite == 0");


                        }

                        if (!etSearchEquipment.equals("")) {

                            boolRecycler = false;

                            getEquipment(boolRecycler);

                        } else {
                            boolRecycler = true;

                            getEquipment(boolRecycler);

                        }
                    } else if (countSqlLite < FBCount) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (countSqlLite < FBCount) {

                                String name = ds.child("nameProd").getValue(String.class);
                                String content = ds.child(FeedReaderContract.FeedEntry.CONTENT).getValue(String.class);
                                String amount = ds.child(FeedReaderContract.FeedEntry.MOUNT).getValue(String.class);
                                String amountCurrent = ds.child("mountCurrent").getValue(String.class);
                                String time = ds.child(FeedReaderContract.FeedEntry.TIME).getValue(String.class);
                                String image = ds.child(FeedReaderContract.FeedEntry.IMAGE).getValue(String.class);
                                String sender = ds.child("sender").getValue(String.class);
                                String msgUid = ds.child(FeedReaderContract.FeedEntry.MESSAGE_UID).getKey();


                                dbHelper.SaveDBSqliteToEquipment(name, content, amount, amountCurrent, time, image, sender, msgUid);
                                Log.e(TAG, "UpdateDateFromFireBaseToSQLiteEquipment After + countSqlLite == 0");


                            }

                        }

                    } else {
                        return;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }

    public long getRawCountSql() {
        db = dbHelper.getWritableDatabase();

        String countQuery = "SELECT  * FROM " + TABLE_NAME_EQUIPMENT;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        long count = cursor.getCount();

        Log.e(TAG, "getrawCount:" + String.valueOf(count));
        countSqlLite = (int) count;
        cursor.close();

        return countSqlLite;
    }

    public void ExcelToSQLite() {

        File file = new File(directory_path, "/" + current_camp_static + ".xls"); //    "/users.xls"
        if (!file.exists()) {
            Toast.makeText(EquipmentAc.this,
                    "אין תיקייה ", //ADD THIS
                    Toast.LENGTH_SHORT).show();
            return;
        }
        dbEquipment.open();

        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DATABASE_NAME_EQUIPMENT, true);
        // Import EXCEL FILE to SQLite
        excelToSQLite.importFromFile(directory_path + "/users.xls", new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String dbName) {
                Log.e(TAG, dbName);
            }

            @Override
            public void onError(Exception e) {
                //Log.i("exr",e.st);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_equipment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.item_equipment) {
            current_admin = prefs.getString("admin", null);

            if (current_admin.equals("admin")) {
                Intent i = new Intent(EquipmentAc.this, EquipmentEditAc.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("UidEquipment", current_uid);
                i.putExtra("nameSenderEquipment", current_name_static);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(EquipmentAc.this, "אתה לא מנהל",
                        Toast.LENGTH_SHORT).show();
            }


            return true;
        }
        if (id == R.id.item_excel) {
            ExcelToSQLite();

        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickCheckBox(View view) {


    }



}
