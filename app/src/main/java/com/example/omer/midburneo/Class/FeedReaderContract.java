package com.example.omer.midburneo.Class;

import android.provider.BaseColumns;

public final class FeedReaderContract {

    private FeedReaderContract() {
    }

    public static class FeedEntry implements BaseColumns {

        public static final String TABLE_NAME = "users";
        public static final String TABLE_NAME_CALENDAR = "calendar";
        public static final String TABLE_NAME_EQUIPMENT = "equipment";
        public static final String TABLE_NAME_EQUIPMENT_FILE = "equipment";
        public static final String TABLE_NAME_NOTE = "note";
        public static String TABLE_NAME_MESSAGE = "message";


        public static final String ADMIN = "admin";
        public static final String CAMPS = "camps";
        public static final String CHAT = "chat";
        public static final String EMAIL = "email";
        public static final String IMAGE = "image";
        public static final String NAME = "name";
        public static final String NUMBER = "number";
        public static final String PASSWORD = "password";
        public static final String STATUS = "status";
        public static final String TITLE = "title";
        public static final String UID = "uid";
        public static final String TIME = "time";
        public static final String TAG_USER = "tag_user";
        public static final String ROLE = "role";
        public static final String LASTMSG = "lastmsg";
        public static final String MESSAGE = "message";
        public static final String MESSAGE_SENDER = "message_sender";
        public static final String MESSAGE_RECEIVER = "message_receiver";
        public static final String MESSAGE_UID = "message_uid";
        public static final String CONTENT = "content";
        public static final String NAME_EQUIPMENT = "name_equipment";
        public static final String MOUNT = "mount";
        public static final String MOUNT_CURRENT = "mount_current";
        public static final String DATE = "date";
        public static final String DATE_END = "date_end";
        public static final String DATE_BOOL = "date_bool";
        public static final String ONLINE = "online";
        public static final String PHONE = "phone";
        public static final String TIME__SET = "timeSet";
        public static final String GROUP = "group";
        public static final String RECORD = "record";


        public static final String CURRENT_DEVICE_ID = "device_id";
        public static final String CURRENT_DEVICE_TOKEN = "device_token";
        public static final String CHAT_ROOMS = "chat_rooms";




    }
}
