package com.example.omer.midburneo.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.FirebaseUserModel;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.NotePreviewActivity;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.NotesAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.omer.midburneo.Tabs.ChatAc.callPhoneChatAc;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;


import java.util.ArrayList;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.RegisterAc.prefs;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Calendar> CalendarNoteList;
    private String time, sender, image, uidMsg;
    private int count;
    private DatabaseReference mUserDatabase;
    public DBHelper dbHelper;
    public SQLiteDatabase db;

    public CalendarAdapter(Context context, ArrayList<Calendar> EquipmentNoteList) {
        this.context = context;
        this.CalendarNoteList = EquipmentNoteList;
    }

    @NonNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_preview, parent, false);
        CalendarAdapter.ViewHolder viewHolder = new CalendarAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(CalendarNoteList.get(position));

        Calendar calendar = CalendarNoteList.get(position);

        image = calendar.getImage();
        uidMsg = calendar.getMsguid();

        if (image.equals("default")) {

            holder.pMsg.setText(calendar.getMsg());
            holder.pSender.setText(calendar.getName());
            holder.pTime.setText(calendar.getTimeSet());
            holder.pImg.setVisibility(View.GONE);
        } else {


            holder.pMsg.setText(calendar.getMsg());
            holder.pSender.setText(calendar.getName());
            holder.pTime.setText(calendar.getTimeSet());
            Glide.with(context).load(calendar.getImage()).into(holder.pImg);

        }

        count = Integer.parseInt(calendar.getCountRaw());
        time = calendar.getTime();
        sender = calendar.getSender();


    }


    @Override
    public int getItemCount() {
        return CalendarNoteList.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pMsg, pSender, pTime, txtclose;
        public ImageView pImg, pImgPop;

        private Dialog myDialog;


        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View itemView) {
            super(itemView);

            pMsg = itemView.findViewById(R.id.tvContentNotePreview);
            pSender = itemView.findViewById(R.id.tvNameNotePreview);
            pTime = itemView.findViewById(R.id.tvTimeNotePreview);
            pImg = itemView.findViewById(R.id.tvImageNotePreview);


            myDialog = new Dialog(context);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (!image.equals("default")) {
                        Calendar calendar = (Calendar) view.getTag();

                        myDialog.setContentView(R.layout.item_image_popup);
                        txtclose = myDialog.findViewById(R.id.txtclose);
                        pImgPop = myDialog.findViewById(R.id.imgPopUp);


                        try {
                            Picasso.get().load(calendar.getImage()).resize(800, 1000).error(R.drawable.midburn_logo).into(pImgPop);

                        } catch (NullPointerException e) {
                        }


                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();

                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();


                            }
                        });
                    } else {
                        return;
                    }


                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    String current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    Calendar calendar = (Calendar) v.getTag();
                    uidMsg = calendar.getMsguid();

                    String name = calendar.getName();
                    String uid = calendar.getSender();


                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                    alertDialog.setTitle("עריכת הודעה");


                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "מחק הודעה ", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {


                            if (current_uid.equals(uid)) {

                                dbHelper = new DBHelper(context);

                                dbHelper.deleteRawFromTable(count, time, TABLE_NAME_CALENDAR, FeedReaderContract.FeedEntry.TIME);

                                Intent intent = new Intent(context, NotePreviewActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }


                        }
                    });


                    if (current_uid.equals(uid)) {

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "מחק לכולם", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {




                                    deleteRawFormFireBase(uidMsg);
                                    dbHelper = new DBHelper(context);

                                    dbHelper.deleteRawFromTable(count, time, TABLE_NAME_CALENDAR, FeedReaderContract.FeedEntry.TIME);


                                Intent intent = new Intent(context, NotePreviewActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);



                            }
                        });
                    }

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "חזור", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                            return;

                        }
                    });

                    alertDialog.show();


                    return true;
                }
            });

        }
    }

    private void deleteRawFormFireBase(String uidMsg) {


        mUserDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Camps").child(firebaseUserModel.getChat()).child("Calendar").child(uidMsg);
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
}


