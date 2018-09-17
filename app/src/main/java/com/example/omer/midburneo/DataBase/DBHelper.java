package com.example.omer.midburneo.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.applandeo.materialcalendarview.EventDay;
import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.Class.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
//import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_MESSAGE;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.TABLE_NAME_MESSAGE;


public class DBHelper extends SQLiteOpenHelper {

    public Context context;
    private static final String TAG = "DBHelper";
    public static Boolean BoolRefresh = true;
    public SQLiteDatabase db;
    public String checkid;


    private static final String SQL_CREATE_ENTRIES_USERS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.ADMIN + " TEXT," +
                    FeedReaderContract.FeedEntry.CAMPS + " TEXT," +
                    FeedReaderContract.FeedEntry.CHAT + " TEXT," +
                    FeedReaderContract.FeedEntry.EMAIL + " TEXT," +
                    FeedReaderContract.FeedEntry.IMAGE + " TEXT," +
                    FeedReaderContract.FeedEntry.NAME + " TEXT," +
                    FeedReaderContract.FeedEntry.NUMBER + " TEXT," +
                    FeedReaderContract.FeedEntry.PASSWORD + " TEXT," +
                    FeedReaderContract.FeedEntry.STATUS + " TEXT," +
                    FeedReaderContract.FeedEntry.TIME + " TEXT," +
                    FeedReaderContract.FeedEntry.UID + " TEXT," +
                    FeedReaderContract.FeedEntry.LASTMSG + " TEXT)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public String SQL_CREATE_ENTRIES_USERS_MESSAGE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MESSAGE + " (" + FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.MESSAGE + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_RECEIVER + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_SENDER + " TEXT," +
                    FeedReaderContract.FeedEntry.NAME + " TEXT," +
                    FeedReaderContract.FeedEntry.TIME + " TEXT," +
                    FeedReaderContract.FeedEntry.IMAGE + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_UID + " TEXT," +
                    FeedReaderContract.FeedEntry.STATUS + " TEXT)";


    public String SQL_CREATE_ENTRIES_USERS_CALENDAR =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CALENDAR + " (" + FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.MESSAGE + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_SENDER + " TEXT," +
                    FeedReaderContract.FeedEntry.TIME + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_UID + " TEXT)";



    private String SQL_DELETE_ENTRIES_CALENDAR =
            "DROP TABLE IF EXISTS " + TABLE_NAME_MESSAGE;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Usres.db";

    public String current_uid;
    private FirebaseUser mCurrentUser;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_USERS);
        db.execSQL(SQL_CREATE_ENTRIES_USERS_MESSAGE);
        db.execSQL(SQL_CREATE_ENTRIES_USERS_CALENDAR);

        Log.e("*******************", "DB_OnCreate");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES_CALENDAR);
        //db.execSQL(SQL_DELETE_ENTRIESs);
        // onCreate(db);
        Log.e("*******************", "DB_onUpgrade");

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public List<Friend> getAllFriend() {
        List<Friend> notes = new ArrayList<>();

        // Select All Query

        Log.e("*******************", "getAllFriend");

        String selectQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                current_uid = mCurrentUser.getUid();

                Friend friend = new Friend();

                String name = friend.setName(cursor.getString(cursor.getColumnIndex("name")));
                String camps = friend.setCamp(cursor.getString(cursor.getColumnIndex("camps")));
                String image = friend.setImage(cursor.getString(cursor.getColumnIndex("image")));
                String status = friend.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                String uid = friend.setUidReceiver(cursor.getString(cursor.getColumnIndex("uid")));
                String lastmsg = friend.setLastMsg(cursor.getString(cursor.getColumnIndex("lastmsg")));
                String uidcount = friend.setUidCount(cursor.getString(cursor.getColumnIndex("_id")));
                String time = friend.setTime(cursor.getString(cursor.getColumnIndex("time")));


                if (!uid.equals(current_uid)) {
                    notes.add(friend);

                }

            } while (cursor.moveToNext());

        }
        // close db connection
        db.close();
        // return notes list
        return notes;
    }

    public List<Message> getAllMsg() {
        List<Message> msgList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        db = this.getReadableDatabase();
        Log.e("*******************", "getAllMsg");


        try {
            String selectQuery = "SELECT  * FROM " + TABLE_NAME_MESSAGE;


            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    Message message = new Message();

                    String getTimeCursor = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));
                    long getTimeLong = Long.parseLong(getTimeCursor);

                    DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
                    String timeFormat = getTimeHourMintus.format(getTimeLong);

                    String name_sender = message.setName_sender(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.NAME)));
                    String msg = message.setMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE)));
                    String receiver = message.setReceiver(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_RECEIVER)));
                    String sender = message.setSender(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                    String time = message.setTime(timeFormat);
                    String image = message.setImage(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.IMAGE)));
                    String uidMsg = message.setUidMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));
                    String status = message.setStatus(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.STATUS)));


                    msgList.add(message);


                } while (cursor.moveToNext());

            }
            // close db connection
            db.close();
        } catch (Exception e) {
        }


        // return notes list

        return msgList;
    }

    public List<Calendar> getAllCalendar() {
        List<Calendar> calendarList = new ArrayList<>();

        // Select All Query

        Log.e("*******************", "getAllCalendar");

        String selectQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

//                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
//                current_uid = mCurrentUser.getUid();

                Calendar calendar = new Calendar();

                String msg = calendar.setMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE)));
                String sender = calendar.setMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                String time = calendar.setMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME)));
                String msgUid = calendar.setMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));


                calendarList.add(calendar);



            } while (cursor.moveToNext());

        }
        // close db connection
        db.close();
        // return notes list
        return calendarList;
    }

    public void SaveDBSqlite(String Name, String Camp, String Uid, String image, String lstmsg) {


        SQLiteDatabase db = this.getReadableDatabase();
        db = this.getWritableDatabase();


        try {


            String[] projection = {
                    BaseColumns._ID,
                    FeedReaderContract.FeedEntry.NAME,
                    FeedReaderContract.FeedEntry.CAMPS,
                    FeedReaderContract.FeedEntry.ADMIN,
                    FeedReaderContract.FeedEntry.IMAGE,
                    FeedReaderContract.FeedEntry.UID,
                    FeedReaderContract.FeedEntry.UID_id
            };

            String selection = FeedReaderContract.FeedEntry.NAME + " = ?";
            String[] selectionArgs = {Name};

            String sortOrder =
                    FeedReaderContract.FeedEntry.UID + " DESC";

            Cursor cursor = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );


            cursor.moveToNext();

            String nameCheck = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.NAME));


            if (!nameCheck.equals(Name)) {

                Log.e("*******************", "SaveDBSqlite_succ");

                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.ADMIN, "default");
                values.put(FeedReaderContract.FeedEntry.CAMPS, Camp);
                values.put(FeedReaderContract.FeedEntry.CHAT, "default");
                values.put(FeedReaderContract.FeedEntry.EMAIL, "default");
                values.put(FeedReaderContract.FeedEntry.IMAGE, image);
                values.put(FeedReaderContract.FeedEntry.NAME, Name);
                values.put(FeedReaderContract.FeedEntry.NUMBER, "default");
                values.put(FeedReaderContract.FeedEntry.PASSWORD, "default");
                values.put(FeedReaderContract.FeedEntry.STATUS, "default");
                values.put(FeedReaderContract.FeedEntry.TIME, "default");
                values.put(FeedReaderContract.FeedEntry.UID, Uid);
                values.put(FeedReaderContract.FeedEntry.LASTMSG, lstmsg);


                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
            }

        } catch (Exception e) {
            Log.e("*******************", "SaveDBSqlite_error");

            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.ADMIN, "default");
            values.put(FeedReaderContract.FeedEntry.CAMPS, Camp);
            values.put(FeedReaderContract.FeedEntry.CHAT, "default");
            values.put(FeedReaderContract.FeedEntry.EMAIL, "default");
            values.put(FeedReaderContract.FeedEntry.IMAGE, image);
            values.put(FeedReaderContract.FeedEntry.NAME, Name);
            values.put(FeedReaderContract.FeedEntry.NUMBER, "default");
            values.put(FeedReaderContract.FeedEntry.PASSWORD, "default");
            values.put(FeedReaderContract.FeedEntry.STATUS, "default");
            values.put(FeedReaderContract.FeedEntry.TIME, "default");
            values.put(FeedReaderContract.FeedEntry.UID, Uid);
            values.put(FeedReaderContract.FeedEntry.LASTMSG, lstmsg);


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        }

        db.close();


    }

    public void SaveDBSqliteUser(String msg, String msg_receiver, String msg_sender, String name, String time, String image, String uid_msg, String status, String table) {

        TABLE_NAME_MESSAGE = table;

        SQLiteDatabase db;

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_RECEIVER, msg_receiver);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, msg_sender);
        values.put(FeedReaderContract.FeedEntry.NAME, name);
        values.put(FeedReaderContract.FeedEntry.TIME, time);
        values.put(FeedReaderContract.FeedEntry.IMAGE, image);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);
        values.put(FeedReaderContract.FeedEntry.STATUS, status);

        prefs.edit().putString("time_msg", time);


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME_MESSAGE, null, values);

        Log.e("*******************", String.valueOf(newRowId) + " SaveDBSqliteUserrrrrrrr");


        db.close();

    }


    public void SaveDBSqliteGroup(String msg, String msg_receiver, String msg_sender, String name, String time, String image, String uid_msg, String status, String table) {


        TABLE_NAME_MESSAGE = table;

        SQLiteDatabase db = this.getReadableDatabase();
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_RECEIVER, msg_receiver);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, msg_sender);
        values.put(FeedReaderContract.FeedEntry.NAME, name);
        values.put(FeedReaderContract.FeedEntry.TIME, time);
        values.put(FeedReaderContract.FeedEntry.IMAGE, image);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);
        values.put(FeedReaderContract.FeedEntry.STATUS, status);

        prefs.edit().putString("time_msg", time);


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME_MESSAGE, null, values);

        db.close();


    }

    public void SaveDBSqliteToCalendar(String msg, String msg_sender, String time, String uid_msg){

        SQLiteDatabase db;

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, msg_sender);
        values.put(FeedReaderContract.FeedEntry.TIME, time);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME_CALENDAR, null, values);

        Log.e("*******************", String.valueOf(newRowId) + " SaveDBSqliteToCalendar");

        db.close();
    }
}