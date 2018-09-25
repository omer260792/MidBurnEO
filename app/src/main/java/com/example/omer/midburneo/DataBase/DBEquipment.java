package com.example.omer.midburneo.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.RegisterAc.prefs;

public class DBEquipment extends SQLiteOpenHelper {

    private static final String TAG = "DBEquipment";
    private Context context;
    private SQLiteDatabase database;
    private DBEquipment dbEquipment;


    public String SQL_CREATE_ENTRIES_USERS_EQUIPMENT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_EQUIPMENT + " (" + FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.NAME_EQUIPMENT + " TEXT," +
                    FeedReaderContract.FeedEntry.CONTENT + " TEXT," +
                    FeedReaderContract.FeedEntry.MOUNT + " TEXT," +
                    FeedReaderContract.FeedEntry.MOUNT_CURRENT + " TEXT," +
                    FeedReaderContract.FeedEntry.TIME + " TEXT," +
                    FeedReaderContract.FeedEntry.IMAGE + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_SENDER + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_UID + " TEXT)";


    private String SQL_DELETE_ENTRIES_EQUIPMENT =
            "DROP TABLE IF EXISTS " + TABLE_NAME_EQUIPMENT;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME_EQUIPMENT = "Equipment.db";

    public String current_uid;
    private FirebaseUser mCurrentUser;

    public DBEquipment(Context context) {
        super(context, DATABASE_NAME_EQUIPMENT, null, DATABASE_VERSION);
    }

    public DBEquipment open() throws SQLException {
        database = this.getWritableDatabase();
        return this;
    }

    public void close() {

        dbEquipment = new DBEquipment(context);

        dbEquipment.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_USERS_EQUIPMENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_EQUIPMENT);

    }

    public void SaveDBSqliteToEquipment(String nameProd, String content, String mount, String mountCurrent, String time, String image, String sender, String uid_msg) {

        SQLiteDatabase db;

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.NAME_EQUIPMENT, nameProd);
        values.put(FeedReaderContract.FeedEntry.CONTENT, content);
        values.put(FeedReaderContract.FeedEntry.MOUNT, mount);
        values.put(FeedReaderContract.FeedEntry.MOUNT_CURRENT, mountCurrent);
        values.put(FeedReaderContract.FeedEntry.TIME, time);
        values.put(FeedReaderContract.FeedEntry.IMAGE, image);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, sender);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME_EQUIPMENT, null, values);

        prefs.edit().putString("time_equipment", time);


        Log.e("*******************", String.valueOf(time) + " time_equipment");

        db.close();
    }
    public List<Equipment> getAllEquipment() {
        List<Equipment> equipment = new ArrayList<>();

        // Select All Query

        Log.e("*******************", "getAllEquipment");

        String selectQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                current_uid = mCurrentUser.getUid();

                Equipment equipments = new Equipment();


                String getTimeCursor = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));
                long getTimeLong = Long.parseLong(getTimeCursor);

                DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
                String timeFormat = getTimeHourMintus.format(getTimeLong);

                String name = equipments.setNameProd(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.NAME_EQUIPMENT)));
                String content =equipments.setContent(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.CONTENT)));
                String mount = equipments.setMount(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MOUNT)));
                String mountCurrent = equipments.setMountCurrent(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MOUNT_CURRENT)));
                String time = equipments.setTime(timeFormat);
                String image = equipments.setImage(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.IMAGE)));
                String sender = equipments.setSender(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                String msg_uid = equipments.setUid(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));

                Log.e("nanananannana",content);
                Log.e("nanananannana",mount);
                Log.e("nanananannana",mountCurrent);
                Log.e("nanananannana",image);
                Log.e("nanananannana",sender);
                Log.e("nanananannana",time);
                Log.e("nanananannana",msg_uid);
                Log.e("nanananannana",name);



                equipment.add(equipments);



            } while (cursor.moveToNext());

        }
        // close db connection
        db.close();
        // return notes list
        return equipment;
    }
}
