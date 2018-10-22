package com.example.omer.midburneo.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.FirebaseMessageModel;
import com.example.omer.midburneo.Class.Friend;

import com.example.omer.midburneo.Class.MessageNote;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_NOTE;
import static com.example.omer.midburneo.Tabs.MainPageAc.SHPRF;
import static com.example.omer.midburneo.Tabs.MainPageAc.prefs;;
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
                    FeedReaderContract.FeedEntry.IMAGE + " TEXT," +
                    FeedReaderContract.FeedEntry.RECORD + " TEXT)";


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
                    FeedReaderContract.FeedEntry.UID + " TEXT," +
                    FeedReaderContract.FeedEntry.MESSAGE_UID + " TEXT)";


    public DBHelper open() throws SQLException {
        db = this.getWritableDatabase();
        return this;
    }

    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "Usres.db";

    public String current_uid;
    private FirebaseUser mCurrentUser;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_USERS_MESSAGE);
        db.execSQL(SQL_CREATE_ENTRIES_USERS);
        db.execSQL(SQL_CREATE_ENTRIES_USERS_CALENDAR);
        db.execSQL(SQL_CREATE_ENTRIES_USERS_EQUIPMENT);
        db.execSQL(SQL_CREATE_ENTRIES_USERS_NOTE);

        Log.e(TAG, "DB_OnCreate");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
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

        String selectQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

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

                } else if (cToken.equals("default")) {

                    notes.add(friend);


                }

            } while (cursor.moveToNext());


        }
        db.close();

        return notes;
    }

    public List<FirebaseMessageModel> getAllMsg() {
        List<FirebaseMessageModel> msgList = new ArrayList<>();

        db = this.getReadableDatabase();


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


                    msgList.add(message);


                } while (cursor.moveToNext());

            }
            db.close();
        } catch (Exception e) {
        }

        return msgList;
    }


    public List<Equipment> getAllEquipment() {
        List<Equipment> equipment = new ArrayList<>();


        String selectQuery = "SELECT  * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                current_uid = mCurrentUser.getUid();

                Equipment equipments = new Equipment();

                String time = equipments.setTime(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME)));
                String name = equipments.setNameProd(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.NAME_EQUIPMENT)));
                String content = equipments.setContent(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.CONTENT)));
                String mount = equipments.setMount(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MOUNT)));
                String mountCurrent = equipments.setMountCurrent(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MOUNT_CURRENT)));
                String image = equipments.setImage(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.IMAGE)));
                String sender = equipments.setSender(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                String msg_uid = equipments.setUid(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));
                String count = equipments.setCount(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID)));


                equipment.add(equipments);


            } while (cursor.moveToNext());

        }
        db.close();
        return equipment;
    }


    public List<MessageNote> getAllNote(String bool) {
        List<MessageNote> msgNoteList = new ArrayList<>();

        db = this.getReadableDatabase();

        Log.e(TAG,"33ffff33");


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
                        String uid = messageNote.setUid(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.UID)));
                        String msgUid = messageNote.setUidMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));
                        String uidcount = messageNote.setCount(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID)));

                        Log.e(TAG,"getallnote");

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

    public List<Calendar> getAllCalnderNotePreview(String timeCalnder) {

        List<Calendar> calendarList = new ArrayList<>();

        db = this.getReadableDatabase();

        try {
            String selectQuery = "SELECT  * FROM " + TABLE_NAME_CALENDAR;


            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Calendar calendar = new Calendar();

                    String time = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME));

                    if (timeCalnder.equals(time)) {

                        String msg = calendar.setMsg(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE)));
                        String senderUid = calendar.setSender(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_SENDER)));
                        String messageUid = calendar.setMsguid(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.MESSAGE_UID)));
                        String timeSetString = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.TIME__SET));
                        String nameSender = calendar.setName(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.NAME)));
                        String image = calendar.setImage(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.IMAGE)));
                        String countSetString = calendar.setCountRaw(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry._ID)));

                        long timeSetLong = (Long.parseLong(timeSetString));
                        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                        timeSetString = getTimeHourMintus.format(timeSetLong);

                        String timeSet = calendar.setTimeSet(timeSetString);
                        String timeSets = calendar.setTime(time);

                        calendarList.add(calendar);
                    }

                } while (cursor.moveToNext());
            }
            // close db connection
            db.close();
        } catch (Exception e) {
        }

        return calendarList;

    }


    public void SaveDBSqliteUser(String Name, String Camp, String Uid, String image, String lstmsg, String phone, String device, String token, String chat) {

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
        values.put(FeedReaderContract.FeedEntry.ROLE, "");
        values.put(FeedReaderContract.FeedEntry.PHONE, phone);
        values.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, device);
        values.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, token);
        values.put(FeedReaderContract.FeedEntry.CHAT_ROOMS, chat);


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);


        db.close();


    }

    public void SaveDBSqliteToMessage(String msg, String msg_receiver, String msg_sender, String name, String time, String msg_uid, String image, String record, String table) {
        TABLE_NAME_MESSAGE = table;

        db = this.getWritableDatabase();
        this.onCreate(SQL_CREATE_ENTRIES_USERS_MESSAGE);

//        String[] projection = {
//                BaseColumns._ID,
//                FeedReaderContract.FeedEntry.MESSAGE,
//                FeedReaderContract.FeedEntry.MESSAGE_RECEIVER,
//                FeedReaderContract.FeedEntry.MESSAGE_SENDER,
//                FeedReaderContract.FeedEntry.NAME,
//                FeedReaderContract.FeedEntry.TIME,
//                FeedReaderContract.FeedEntry.MESSAGE_UID,
//                FeedReaderContract.FeedEntry.IMAGE,
//                FeedReaderContract.FeedEntry.RECORD
//
//        };
//
//        String selection = FeedReaderContract.FeedEntry.TIME + " = ?";
//        String[] selectionArgs = {time};
//
//        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
//        builder.setTables(TABLE_NAME_MESSAGE);
//
//
//        try {
//
//            Cursor cursor = builder.query(this.getReadableDatabase(),
//                    projection, selection, selectionArgs, null, null, null);
//            if (cursor == null){
//                this.onCreate(SQL_CREATE_ENTRIES_USERS_MESSAGE);
//
//            }
//
//            if (!cursor.moveToFirst()) {


                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
                values.put(FeedReaderContract.FeedEntry.MESSAGE_RECEIVER, msg_receiver);
                values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, msg_sender);
                values.put(FeedReaderContract.FeedEntry.NAME, name);
                values.put(FeedReaderContract.FeedEntry.TIME, time);
                values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, msg_uid);
                values.put(FeedReaderContract.FeedEntry.IMAGE, image);
                values.put(FeedReaderContract.FeedEntry.RECORD, record);


                long newRowId = db.insert(TABLE_NAME_MESSAGE, null, values);
//            }
//
//        }catch (Exception e){
//        }
//        e.printStackTrace();
//

        db.close();
    }

    private void onCreate(String sql_create_entries_users_message) {
    }


    public void SaveDBSqliteToCalendar(String msg, String msg_sender, String time, String uid_msg, String setTime, String name, String image) {


        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.MESSAGE, msg);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, msg_sender);
        values.put(FeedReaderContract.FeedEntry.TIME, time);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);
        values.put(FeedReaderContract.FeedEntry.TIME__SET, setTime);
        values.put(FeedReaderContract.FeedEntry.NAME, name);
        values.put(FeedReaderContract.FeedEntry.IMAGE, image);


        long newRowId = db.insert(TABLE_NAME_CALENDAR, null, values);

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

        long newRowId = db.insert(TABLE_NAME_EQUIPMENT, null, values);

        db.close();
    }

    public void SaveDBSqliteToNote(String title, String content, String date, String dateEnd, String dateBool, String sender, String uid, String uid_msg) {

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.TITLE, title);
        values.put(FeedReaderContract.FeedEntry.CONTENT, content);
        values.put(FeedReaderContract.FeedEntry.DATE, date);
        values.put(FeedReaderContract.FeedEntry.DATE_END, dateEnd);
        values.put(FeedReaderContract.FeedEntry.DATE_BOOL, dateBool);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_SENDER, sender);
        values.put(FeedReaderContract.FeedEntry.UID, uid);
        values.put(FeedReaderContract.FeedEntry.MESSAGE_UID, uid_msg);

        long newRowId = db.insert(TABLE_NAME_NOTE, null, values);

        db.close();

    }

    public void deleteRawFromTable(int id, String time, String table, String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + table + " WHERE "
                + FeedReaderContract.FeedEntry._ID + " = '" + id + "'" +
                " AND " + key + " = '" + time + "'";
        db.execSQL(query);
    }


}

