package com.example.omer.midburneo.Adapters;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.Class.MessageNote;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.RegisterAc;
import com.example.omer.midburneo.Tabs.NotesAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_NOTE;
import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class MessageNoteAdapter extends RecyclerView.Adapter<MessageNoteAdapter.ViewHolder> {

    private static final String TAG = "MessageNoteAdapter";

    private Context context;
    private List<MessageNote> MessageNoteList;
    public String senderString, uidMsgString, time, current_uid, checkBool, countString;
    private int countMsg;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    private DBHelper dbHelper;
    public SQLiteDatabase db;
    private ViewGroup viewGroup;

    public MessageNoteAdapter(Context context, List MessageNoteList) {
        this.context = context;
        this.MessageNoteList = MessageNoteList;
    }


    @NonNull
    @Override
    public MessageNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_note, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewGroup = parent;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.itemView.setTag(MessageNoteList.get(position));


        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        MessageNote messageNote = MessageNoteList.get(position);

        long timeLong = Long.parseLong(messageNote.getDate());
        long timeEndLong = Long.parseLong(messageNote.getDateEnd());
        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm MMMM dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String date = getTimeHourMintus.format(timeLong);
        String dateEnd = simpleDateFormat.format(timeEndLong);


        holder.pTitle.setText(messageNote.getMsg());
        holder.pContent.setText(messageNote.getContent());
        holder.pDate.setText(date);
        holder.pDateEnd.setText(dateEnd);
        holder.checkBox.setBackgroundColor(android.R.drawable.checkbox_off_background);

        checkBool = messageNote.getDateBool();
        uidMsgString = messageNote.getUidMsg();
        senderString = messageNote.getUid();
        countMsg = Integer.parseInt(messageNote.getCount());
        time = messageNote.getTime();
        countString = messageNote.getCount();

        if (checkBool.equals("true")) {
            if (current_uid.equals(senderString)) {
                int btnDrawable = android.R.drawable.checkbox_on_background;
                holder.checkBox.setButtonDrawable(btnDrawable);
            }
        } else {
            if (current_uid.equals(senderString)) {
            }
        }

        holder.checkBox.isChecked();

    }


    @Override
    public int getItemCount() {
        return MessageNoteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pTitle, pContent, pDate, pDateEnd;
        public CheckBox checkBox;
        public CardView relativeLayout;
        public ConstraintLayout parentRelative;


        public ViewHolder(View itemView) {
            super(itemView);

            pTitle = itemView.findViewById(R.id.txtViewMsgNote);
            pContent = itemView.findViewById(R.id.tvContentNote);
            pDate = itemView.findViewById(R.id.txtViewTimeNote);
            pDateEnd = itemView.findViewById(R.id.tvDateEndNote);
            checkBox = itemView.findViewById(R.id.checkBoxNote);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutTop3);
            parentRelative = itemView.findViewById(R.id.parentRelative);


            this.setIsRecyclable(false);


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    MessageNote messageNote = (MessageNote) itemView.getTag();

                    senderString = messageNote.getUid();

                    if (current_uid.equals(senderString)) {

                        checkBool = messageNote.getDateBool();
                        uidMsgString = messageNote.getUidMsg();
                        countMsg = Integer.parseInt(messageNote.getCount());
                        time = messageNote.getTime();
                        countString = messageNote.getCount();

                        if (checkBool.equals("false")) {

                            int btnDrawable = android.R.drawable.checkbox_on_background;
                            checkBox.setButtonDrawable(btnDrawable);

                            UpdateFirebase(uidMsgString);
                            Toast.makeText(context, "המטלה הושלמה", Toast.LENGTH_SHORT).show();

                            try {
                                ContentValues data = new ContentValues();
                                data.put(FeedReaderContract.FeedEntry.DATE_BOOL, "false");
                                db.update(TABLE_NAME_NOTE, data, "_id=" + countMsg, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(context, NotesAc.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);

                        }
                        checkBox.isChecked();

                    } else {
                        Toast.makeText(context, "רק מפרסם המטלה יכול לשנות סטטוס", Toast.LENGTH_LONG).show();

                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    MessageNote messageNote = (MessageNote) v.getTag();

                    String user = messageNote.getUid();
                    uidMsgString = messageNote.getUidMsg();

                    if (user.equals(current_uid)) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("עריכת הודעה");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "מחק הודעה ", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {


                                dbHelper = new DBHelper(context);

                                dbHelper.deleteRawFromTable(countMsg, time, TABLE_NAME_NOTE, FeedReaderContract.FeedEntry.TIME);
                                Intent intent = new Intent(context, NotesAc.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);

                            }
                        });


                        if (current_uid.equals(user)) {

                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "מחק לכולם", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                                    deleteRawFormFireBase();

                                    dbHelper = new DBHelper(context);

                                    dbHelper.deleteRawFromTable(countMsg, time, TABLE_NAME_NOTE, FeedReaderContract.FeedEntry.TIME);
                                    Intent intent = new Intent(context, NotesAc.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);

                                }
                            });
                        }
                        if (current_uid.equals(user)) {

                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "החזר משימה", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Note").child(uidMsgString);

                                    Map<String, Object> mapCampsUpdates = new HashMap<>();
                                    mapCampsUpdates.put("dateBool", "false");

                                    mUserDatabase.updateChildren(mapCampsUpdates);


                                    Intent i = new Intent(context, NotesAc.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(i);


                                }
                            });
                        }

                        alertDialog.show();

                    } else {
                        Toast.makeText(context, "רק מפרסם המטלה יכול לשנות סטטוס", Toast.LENGTH_SHORT).show();

                    }


                    return true;
                }
            });

        }
    }

    private void UpdateFirebase(String uidMsgString) {


        long currentDateTime = System.currentTimeMillis();
        String timeString = String.valueOf(currentDateTime);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Note").child(uidMsgString);

        Map<String, Object> mapCampsUpdates = new HashMap<>();
        mapCampsUpdates.put("dateEnd", timeString);
        mapCampsUpdates.put("dateBool", "true");

        mUserDatabase.updateChildren(mapCampsUpdates);
    }


    private void deleteRawFormFireBase() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Camps").child(firebaseUserModel.getChat()).child("Note").child(uidMsgString);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


//    public void getLastMsg(String countRaw) {
//
//        try {
//
//            dbHelper = new DBHelper(context);
//            db = dbHelper.getWritableDatabase();
//
//            String countQuery = "SELECT  * FROM " + TABLE_NAME_NOTE;
//            Cursor cursor = db.rawQuery(countQuery, null);
//            cursor.moveToLast();
//            String dateBool = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.DATE_BOOL));
////            Log.e(TAG, "onDestroy_lastMsg: " + last_msg);
////            Log.e(TAG, "onDestroy_lastMsg: " + time);
//            cursor.close();
//
//            int count = Integer.parseInt(countRaw);
//
//            ContentValues cv = new ContentValues();
//            cv.put(FeedReaderContract.FeedEntry.DATE_BOOL, "true");
//
//
//            db.update(TABLE_NAME_NOTE, cv, "_id=" + count, null);
//
//
//        } catch (Exception e) {
//            Log.e(TAG, "Exception - Error change Bool date");
//
//        }
//
//
//    }


}