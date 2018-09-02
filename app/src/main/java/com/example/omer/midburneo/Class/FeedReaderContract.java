package com.example.omer.midburneo.Class;

import android.provider.BaseColumns;

public final class FeedReaderContract {

    private FeedReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {

        public static final String TABLE_NAME = "users";

        public static final String ADMIN = "admin";
        public static final String CAMPS = "camps";
        public static final String CHAT = "chat";
        public static final String EMAIL = "email";
        public static final String IMAGE = "image";
        public static final String NAME = "name";
        public static final String NUMBER = "number";
        public static final String PASSWORD = "password";
        public static final String STATUS = "status";
        public static final String URL = "url";
        public static final String UID = "uid";
        public static final String TIME = "time";
        public static final String UID_id = "id";
        public static final String NUM = "num";
        public static final String LASTMSG = "lastmsg";
        public static final String MESSAGE = "message";
        public static final String MESSAGE_SENDER = "message_sender";
        public static final String MESSAGE_RECEIVER = "message_receiver";
        public static final String MESSAGE_UID = "message_uid";


    }
}
