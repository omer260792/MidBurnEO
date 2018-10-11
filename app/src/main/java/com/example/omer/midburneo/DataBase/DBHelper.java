package com.example.omer.midburneo.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.FirebaseMessageModel;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.Class.Message;
import com.example.omer.midburneo.Class.MessageNote;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_NOTE;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_MESSAGE;


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
                    FeedReaderContract.FeedEntry.LASTMSG + " TEXT," +
                    FeedReaderContract.FeedEntry.ROLE + " TEXT," +
                    FeedReaderContract.FeedEntry.PHONE + " TEXT," +
                    FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID + " TEXT," +
                    FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN + " TEXT," +
                    FeedReaderContract.FeedEntry.CHAT_ROOMS + " TEXT)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public String SQL_CREATE_ENTRIES_USERS_MESSAGE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MESSAGE + " (" + FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.MESSAGE + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_RECEIVER + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_SENDER + " TEXT," +
                    FeedReaderContract.FeedEntry.NAME + " TEXT," +
                    FeedReaderContract.FeedEntry.TIME + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_UID + " TEXT," +
                    FeedReaderContract.FeedEntry.STATUS + " TEXT)";


    public String SQL_CREATE_ENTRIES_USERS_CALENDAR =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CALENDAR + " (" + FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.MESSAGE + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_SENDER + " TEXT," +
                    FeedReaderContract.FeedEntry.TIME + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_UID + " TEXT," +
                    FeedReaderContract.FeedEntry.TIME__SET + " TEXT," +
                    FeedReaderContract.FeedEntry.NAME + " TEXT," +
                    FeedReaderContract.FeedEntry.IMAGE + " TEXT)";

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


    public String SQL_CREATE_ENTRIES_USERS_NOTE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NOTE + " (" + FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.TITLE + " TEXT," +
                    FeedReaderContract.FeedEntry.CONTENT + " TEXT," +
                    FeedReaderContract.FeedEntry.DATE + " TEXT," +
                    FeedReaderContract.FeedEntry.DATE_END + " TEXT," +
                    FeedReaderContract.FeedEntry.DATE_BOOL + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_SENDER + " TEXT," +
                    FeedReaderContract.FeedEntry.CAMPS + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_UID + " TEXT)";


    private String SQL_DELETE_ENTRIES_CALENDAR =
            "DROP TABLE IF EXISTS " + TABLE_NAME_MESSAGE;

    public DBHelper open() throws SQLException {
        db = this.getWritableDatabase();
        return this;
    }

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
        db.execSQL(SQL_CREATE_ENTRIES_USERS_EQUIPMENT);
        db.execSQL(SQL_CREATE_ENTRIES_USERS_NOTE);

        Log.e(TAG, "DB_OnCreate");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES_CALENDAR);
        //db.execSQL(SQL_DELETE_ENTRIESs);
        // onCreate(db);
        Log.e(TAG, "DB_onUpgrade");

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public List<Friend> getAllFriend() {
        List<Friend> notes = new ArrayList<>();

        // Select All Query

        Log.e(TAG, "getAllFriend");

        String selectQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
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
                String role = friend.setRole(cursor.getString(cursor.getColumnIndex("role")));
                String phone = friend.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                String cDevice = friend.setDevice(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID)));
                String cToken = friend.setToken(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN)));
                String chatRooms = friend.setChatRoom(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.CHAT_ROOMS)));


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

    public List<FirebaseMessageModel> getAllMsg() {
        List<FirebaseMessageModel> msgList = new ArrayList<>();

        db = this.getReadableDatabase();
        Log.e(TAG, "getAllMsg");


        try {
            String selectQuery = "SELECT  * FROM " + TABLE_NAME_MESSAGE;


            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseMessageModel message = new FirebaseMessageModel();

                    String getTimeCursor = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));
                    long getTimeLong = Long.parseLong(getTimeCursor);

                    DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
                    long timeFormat = Long.parseLong(getTimeHourMintus.format(getTimeLong));

                    String name_sender = message.setSenderName(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.NAME)));
                    String msg = message.setText(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE)));
                    String receiver = message.setReceiverId(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_RECEIVER)));
                    String sender = message.setSenderId(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                    String time = message.setCreatedDate(timeFormat);
                    String uidMsg = message.setId(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));
                    String status = message.setStatus(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.STATUS)));
                    //String uidcount = message.setc(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID)));

                    Log.e((TAG), "getAllMsg" + msg);
                    Log.e((TAG), "getAllMsg" + status);
                    Log.e((TAG), "getAllMsg" + sender);
                    Log.e((TAG), "getAllMsg" + receiver);
                    Log.e((TAG), "getAllMsg" + name_sender);
                    Log.e((TAG), "getAllMsg" + time);


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
                String timeSet = calendar.setTimeSet(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME__SET)));
                String image = calendar.setImage(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.IMAGE)));


                calendarList.add(calendar);


            } while (cursor.moveToNext());

        }
        // close db connection
        db.close();
        // return notes list
        return calendarList;
    }

    public List<Equipment> getAllEquipment() {
        List<Equipment> equipment = new ArrayList<>();

        // Select All Query

        Log.e(TAG, "getAllEquipment");

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
                String content = equipments.setContent(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.CONTENT)));
                String mount = equipments.setMount(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MOUNT)));
                String mountCurrent = equipments.setMountCurrent(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MOUNT_CURRENT)));
                String time = equipments.setTime(timeFormat);
                String image = equipments.setImage(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.IMAGE)));
                String sender = equipments.setSender(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                String msg_uid = equipments.setUid(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));

                Log.e("nanananannana", content);
                Log.e("nanananannana", mount);
                Log.e("nanananannana", mountCurrent);
                Log.e("nanananannana", image);
                Log.e("nanananannana", sender);
                Log.e("nanananannana", time);
                Log.e("nanananannana", msg_uid);
                Log.e("nanananannana", name);


                equipment.add(equipments);


            } while (cursor.moveToNext());

        }
        // close db connection
        db.close();
        // return notes list
        return equipment;
    }


    public List<MessageNote> getAllNote(String bool) {
        List<MessageNote> msgNoteList = new ArrayList<>();

        db = this.getReadableDatabase();
        Log.e(TAG, "getAllMsg");


        try {
            String selectQuery = "SELECT  * FROM " + TABLE_NAME_NOTE;


            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    MessageNote messageNote = new MessageNote();

                    String dateBool = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.DATE_BOOL));

                    if (dateBool.equals(bool)) {

                        String getTimeCursor = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.DATE));
                        String getTimeCursorEnd = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.DATE_END));
                        long getTimeLong = Long.parseLong(getTimeCursor);

                        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
                        String timeFormat = getTimeHourMintus.format(getTimeLong);

                        String title = messageNote.setMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TITLE)));
                        String content = messageNote.setContent(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.CONTENT)));
                        String date = messageNote.setDate(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.DATE)));
                        String dateEnd = messageNote.setDateEnd(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.DATE_END)));
                        messageNote.setDateBool(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.DATE_BOOL)));

                        String sender = messageNote.setSender(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                        String camp = messageNote.setCamp(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.CAMPS)));
                        String msgUid = messageNote.setUidMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));
                        String uidcount = messageNote.setCount(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID)));

                        Log.e((TAG), "msgNoteList" + title);
                        Log.e((TAG), "msgNoteList" + content);


                        msgNoteList.add(messageNote);
                    }


                } while (cursor.moveToNext());

            }
            // close db connection
            db.close();
        } catch (Exception e) {
        }


        return msgNoteList;
    }

    public ArrayList<Calendar> getAllCalnderNotePreview(String time) {

        ArrayList<Calendar> calendarArrayList = new ArrayList<>();


        db = this.getReadableDatabase();

        Calendar calendar = new Calendar();


        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.MESSAGE,
                FeedReaderContract.FeedEntry.MESSAGE_SENDER,
                FeedReaderContract.FeedEntry.TIME,
                FeedReaderContract.FeedEntry.MESSAGE_UID,
                FeedReaderContract.FeedEntry.TIME__SET,
                FeedReaderContract.FeedEntry.NAME,
                FeedReaderContract.FeedEntry.IMAGE

        };

        String selection = FeedReaderContract.FeedEntry.TIME + " = ?";
        String[] selectionArgs = {time};

        String sortOrder =
                FeedReaderContract.FeedEntry.TIME__SET + " DESC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        Log.e(TAG + " Read", String.valueOf(cursor.getColumnCount()));
        Log.e(TAG + " Read", String.valueOf(cursor.getColumnNames()));
        Log.e(TAG + " Read", String.valueOf(cursor.getCount()));


        if (cursor.moveToFirst()) {
            do {



                String msg = calendar.setMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE)));
                String senderUid = calendar.setSender(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                time = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));
                String messageUid = calendar.setMsguid(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));
                String timeSetString = calendar.setTimeSet(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME__SET)));
                String nameSender = calendar.setName(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.NAME)));
                String image = calendar.setImage(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.IMAGE)));
                String countSetString = calendar.setCountRaw(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID)));



                long timeSetLong = (Long.parseLong(timeSetString));
                DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
                timeSetString = getTimeHourMintus.format(timeSetLong);

                String timeSet = calendar.setTimeSet(timeSetString);
                String timeSets = calendar.setTime(time);

                Log.e(TAG + "Read", msg);
                Log.e(TAG + "Reaeeeeeeed", countSetString);
                Log.e(TAG + "Read", time);
                Log.e(TAG + "Read", timeSet);
                Log.e(TAG + "Read", timeSets);


                calendarArrayList.add(calendar);


            } while (cursor.moveToNext());

        }
        // close db connection
        db.close();

        return calendarArrayList;

    }


    public void SaveDBSqliteUser(String Name, String Camp, String Uid, String image, String lstmsg, String phone, String device, String token,String chat) {

        SQLiteDatabase db;

        db = this.getWritableDatabase();

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
        values.put(FeedReaderContract.FeedEntry.ROLE, "אין תפקיד");
        values.put(FeedReaderContract.FeedEntry.PHONE, phone);
        values.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, device);
        values.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, token);
        values.put(FeedReaderContract.FeedEntry.CHAT_ROOMS, chat);


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

        Log.e(TAG, " SaveDBSqliteUser" + String.valueOf(newRowId));


        db.close();


    }

    public void SaveDBSqliteMsgUser(String msg, String msg_receiver, String msg_sender, String name, String time, String uid_msg, String status, String table) {

        TABLE_NAME_MESSAGE = table;

        SQLiteDatabase db;

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_RECEIVER, msg_receiver);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, msg_sender);
        values.put(FeedReaderContract.FeedEntry.NAME, name);
        values.put(FeedReaderContract.FeedEntry.TIME, time);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);
        values.put(FeedReaderContract.FeedEntry.STATUS, status);

        //prefs.edit().putString("time_msg", time);


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME_MESSAGE, null, values);

        Log.e(TAG, " SaveDBSqliteMsgUser" + String.valueOf(newRowId));


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

    public void SaveDBSqliteToCalendar(String msg, String msg_sender, String time, String uid_msg, String setTime, String name, String image) {

        SQLiteDatabase db;

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, msg_sender);
        values.put(FeedReaderContract.FeedEntry.TIME, time);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);
        values.put(FeedReaderContract.FeedEntry.TIME__SET, setTime);
        values.put(FeedReaderContract.FeedEntry.NAME, name);
        values.put(FeedReaderContract.FeedEntry.IMAGE, image);



        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME_CALENDAR, null, values);

        prefs.edit().putString("time_calendar", time);


        Log.e(TAG, String.valueOf(time) + " time_calendar");

        db.close();
    }

    public void SaveDBSqliteToEquipment(String nameProd, String content, String mount, String mountCurrent, String time, String image, String sender, String uid_msg) {


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

      //  prefs.edit().putString("time_equipment", time);


        Log.e(TAG, String.valueOf(time) + " time_equipment");

        db.close();
    }

    public void SaveDBSqliteToNote(String title, String content, String date, String dateEnd, String dateBool, String sender, String camp, String uid_msg) {

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.TITLE, title);
        values.put(FeedReaderContract.FeedEntry.CONTENT, content);
        values.put(FeedReaderContract.FeedEntry.DATE, date);
        values.put(FeedReaderContract.FeedEntry.DATE_END, dateEnd);
        values.put(FeedReaderContract.FeedEntry.DATE_BOOL, dateBool);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, sender);
        values.put(FeedReaderContract.FeedEntry.CAMPS, camp);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME_NOTE, null, values);

        db.close();

    }


    public void DeleteTableSqliteDB(String tableName) {

        db = this.getWritableDatabase();
        db.execSQL("delete from " + tableName);
        db.close();


    }

    public void DeleteMsgSqliteDB(String tableName, String id) {

        db = this.getWritableDatabase();

        db.delete(tableName, FeedReaderContract.FeedEntry._ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();


    }

    public void deleteRawFromTable(int id, String time, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + table + " WHERE "
                + FeedReaderContract.FeedEntry._ID + " = '" + id + "'" +
                " AND " + FeedReaderContract.FeedEntry.TIME + " = '" + time + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + time + " from database.");
        db.execSQL(query);
    }





}
