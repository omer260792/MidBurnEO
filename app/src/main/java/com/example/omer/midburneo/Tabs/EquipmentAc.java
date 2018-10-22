package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
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
import android.widget.EditText;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.omer.midburneo.Adapters.EquipmentAdapter;
import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBEquipment;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.DataBase.DBEquipment.DATABASE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;
import static com.example.omer.midburneo.Tabs.EquipmentEditAc.directory_path;

import static com.example.omer.midburneo.Tabs.EquipmentEditAc.directory_path_sdk_kikat_down;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class EquipmentAc extends AppCompatActivity {

    private static final String TAG = "EquipmentAc";

    private EditText etSearchEquipment;
    private EquipmentAdapter mAdapter;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    private ArrayList<Equipment> equipmentUtilsList = new ArrayList<>();

    public String current_admin, current_uid, countSqLiteUpdate;
    public String filePathString = "לא יצר קובץ";
    public Boolean boolRecycler = false;
    public DBEquipment dbEquipment;
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private int countSqlLite;
    private int num = 1;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_equipment);


        recyclerView = findViewById(R.id.recycler_Equipment);
        etSearchEquipment = findViewById(R.id.etSearchEquipment);

        dbEquipment = new DBEquipment(getApplicationContext());
        dbHelper = new DBHelper(getApplicationContext());
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        layoutManager = new LinearLayoutManager(EquipmentAc.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        getRawCountSql();

        PermissionManager.check(EquipmentAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);


            String test = etSearchEquipment.getText().toString() + "";
            //   getEquipment(boolRecycler);
            if (test.equals("")) {

                if (countSqlLite > 7) {
                    boolRecycler = true;//start up

                } else {
                    boolRecycler = false;//start down
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(EquipmentAc.this);
                layoutManager.setStackFromEnd(boolRecycler);
                getEquipment(boolRecycler);


            } else {
                boolRecycler = false;

                LinearLayoutManager layoutManager = new LinearLayoutManager(EquipmentAc.this);
                layoutManager.setStackFromEnd(boolRecycler);
                getEquipment(boolRecycler);


            }


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
                    boolRecycler = false;
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
        UpdateDateFromFireBaseToSQLiteEquipment();

    }

    public void getEquipment(Boolean bool) {

        Log.e(TAG, "getEquipment");

        try {

            equipmentUtilsList.addAll(dbHelper.getAllEquipment());
            mAdapter = new EquipmentAdapter(EquipmentAc.this, equipmentUtilsList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(EquipmentAc.this);
            layoutManager.setStackFromEnd(bool);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(mAdapter);
            Log.e(TAG, "getEquipment() + try");

            //  UpdateDateFromFireBaseToSQLiteEquipment();


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
        if (firebaseUserModel.getCamp().equals(null)) {

            Log.e(TAG, "UpdateDateFromFireBaseToSQLiteEquipment == null");

            return;
        } else {
            getRawCountSql();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference discussionRoomsRef = rootRef.child("Camps").child(firebaseUserModel.getChat()).child("Equipment");

            discussionRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    long FBCount = dataSnapshot.getChildrenCount();
                    if (countSqlLite == 0) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String name = ds.child("nameProd").getValue(String.class);
                            String content = ds.child(FeedReaderContract.FeedEntry.CONTENT).getValue(String.class);
                            String amount = ds.child(FeedReaderContract.FeedEntry.MOUNT).getValue(String.class);
                            String amountCurrent = ds.child(FeedReaderContract.FeedEntry.MOUNT_CURRENT).getValue(String.class);
                            String time = ds.child(FeedReaderContract.FeedEntry.TIME).getValue(String.class);
                            String image = ds.child(FeedReaderContract.FeedEntry.IMAGE).getValue(String.class);
                            String sender = ds.child("sender").getValue(String.class);
                            String msgUid = ds.getKey();


                            Boolean tagUserGroup = ds.child(FeedReaderContract.FeedEntry.TAG_USER).exists();
                            if (tagUserGroup.equals(false)) {
                                dbHelper.SaveDBSqliteToEquipment(name, content, amount, amountCurrent, time, image, sender, msgUid);

                                dbEquipment.SaveDBSqliteToEquipmentExcel(name, content, amount, amountCurrent, time, image, firebaseUserModel.getName());
                            }


                            Boolean tagUser = ds.child(FeedReaderContract.FeedEntry.TAG_USER).child(current_uid).exists();
                            if (tagUser.equals(true)) {
                                dbHelper.SaveDBSqliteToEquipment(name, content, amount, amountCurrent, time, image, sender, msgUid);
                                Log.e(TAG, "UpdateDateFromFireBaseToSQLiteEquipment After + countSqlLite == 0");

                                dbEquipment.SaveDBSqliteToEquipmentExcel(name, content, amount, amountCurrent, time, image, firebaseUserModel.getName());

                            }


                        }

                        if (!etSearchEquipment.equals("")) {

                            boolRecycler = false;

                            getEquipment(boolRecycler);

                        } else {
                            boolRecycler = true;

                            getEquipment(boolRecycler);

                        }
                    } else {


                        for (DataSnapshot ds : dataSnapshot.getChildren()) {


                            String name = ds.child("nameProd").getValue(String.class);
                            String content = ds.child(FeedReaderContract.FeedEntry.CONTENT).getValue(String.class);
                            String amount = ds.child(FeedReaderContract.FeedEntry.MOUNT).getValue(String.class);
                            String amountCurrent = ds.child(FeedReaderContract.FeedEntry.MOUNT_CURRENT).getValue(String.class);
                            String time = ds.child(FeedReaderContract.FeedEntry.TIME).getValue(String.class);
                            String image = ds.child(FeedReaderContract.FeedEntry.IMAGE).getValue(String.class);
                            String sender = ds.child("sender").getValue(String.class);
                            String msgUid = ds.getKey();

                            Boolean check = queryEquipment(msgUid, FeedReaderContract.FeedEntry.MESSAGE_UID);

                            if (check.equals(false)) {


                                Boolean tagUserGroup = ds.child(FeedReaderContract.FeedEntry.TAG_USER).exists();
                                if (tagUserGroup.equals(false)) {
                                    dbHelper.SaveDBSqliteToEquipment(name, content, amount, amountCurrent, time, image, sender, msgUid);

                                    dbEquipment.SaveDBSqliteToEquipmentExcel(name, content, amount, amountCurrent, time, image, firebaseUserModel.getName());
                                }


                                Boolean tagUser = ds.child(FeedReaderContract.FeedEntry.TAG_USER).child(current_uid).exists();
                                if (tagUser.equals(true)) {
                                    dbHelper.SaveDBSqliteToEquipment(name, content, amount, amountCurrent, time, image, sender, msgUid);

                                    dbHelper.SaveDBSqliteToEquipment(name, content, amount, amountCurrent, time, image, sender, msgUid);

                                    dbEquipment.SaveDBSqliteToEquipmentExcel(name, content, amount, amountCurrent, time, image, firebaseUserModel.getName());


                                }
                            }else {
                                ContentValues data=new ContentValues();
                                data.put(FeedReaderContract.FeedEntry.NAME_EQUIPMENT,name);
                                data.put(FeedReaderContract.FeedEntry.CONTENT,content);
                                data.put(FeedReaderContract.FeedEntry.MOUNT,amount);
                                data.put(FeedReaderContract.FeedEntry.MOUNT_CURRENT,amountCurrent);
                                data.put(FeedReaderContract.FeedEntry.IMAGE,image);
                                db.update(TABLE_NAME_EQUIPMENT, data, "_id=" + countSqLiteUpdate, null);
                            }


                        }


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

        countSqlLite = (int) count;
        cursor.close();

        return countSqlLite;
    }

    public void getExcelSettings() {


        File file = null; //    "/users.xls"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "/" + firebaseUserModel.getCamp() + ".xls");
        }else {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "/" + firebaseUserModel.getCamp() + ".xls");
        }
        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(EquipmentAc.this,
                    filePathString, //ADD THIS
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(EquipmentAc.this,
                    "הקובץ נוצר", //ADD THIS
                    Toast.LENGTH_SHORT).show();
            SQLiteToExcel();


        }

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
            current_admin = firebaseUserModel.getAdmin();

                Intent i = new Intent(EquipmentAc.this, EquipmentEditAc.class);
                i.putExtra("UidEquipment", current_uid);
                i.putExtra("nameSenderEquipment", firebaseUserModel.getName());
                startActivity(i);



            return true;
        }
        if (id == R.id.item_excel) {
            getExcelSettings();

        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickCheckBox(View view) {


    }

    public void SQLiteToExcel() {

        SQLiteToExcel sqLiteToExcel;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            sqLiteToExcel = new SQLiteToExcel(getApplicationContext(), DATABASE_NAME_EQUIPMENT, directory_path_sdk_kikat_down);

        }else {
            sqLiteToExcel = new SQLiteToExcel(getApplicationContext(), DATABASE_NAME_EQUIPMENT, directory_path);

        }
        dbEquipment.open();

        sqLiteToExcel.exportAllTables(firebaseUserModel.getCamp() + ".xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {
                filePathString = "הצליח ליצור קובץ אקסל";

                ExcelToSQLite();
                Log.e("Successfully Exported", filePath);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    public void ExcelToSQLite() {

        dbEquipment.open();

        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DATABASE_NAME_EQUIPMENT, true);
        // Import EXCEL FILE to SQLite
        excelToSQLite.importFromFile(directory_path + "/users.xls", new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String dbName) {
            }

            @Override
            public void onError(Exception e) {
                Log.i("exr", String.valueOf(e));
            }
        });


    }


    private Boolean queryEquipment(String selectionArgss, String selectio) {


        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.NAME_EQUIPMENT,
                FeedReaderContract.FeedEntry.CONTENT,
                FeedReaderContract.FeedEntry.MOUNT,
                FeedReaderContract.FeedEntry.MOUNT,
                FeedReaderContract.FeedEntry.TIME,
                FeedReaderContract.FeedEntry.IMAGE,
                FeedReaderContract.FeedEntry.MESSAGE_SENDER,
                FeedReaderContract.FeedEntry.MESSAGE_UID

        };

        String selection = selectio + " = ?";
        String[] selectionArgs = {selectionArgss};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME_EQUIPMENT);

        Cursor cursor = builder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {


            cursor.close();
            return false;
        }
        countSqLiteUpdate = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID));

        return true;
    }


}
