package com.example.omer.midburneo.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;


import java.util.ArrayList;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.RegisterAc.prefs;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Calendar> CalendarNoteList;
    private String time, sender, image;
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



//        if (image.equals("default")){
//
//            holder.pMsg.setText(calendar.getMsg());
//            holder.pSender.setText(calendar.getName());
//            holder.pTime.setText(calendar.getTimeSet());
//        }else {
//
//
//            holder.pMsg.setText(calendar.getMsg());
//            holder.pSender.setText(calendar.getName());
//            holder.pTime.setText(calendar.getTimeSet());
//            Glide.with(context).load(calendar.getImage()).into(holder.pImg);
//
//        }
        holder.pMsg.setText(calendar.getMsg());
        holder.pSender.setText(calendar.getName());
        holder.pTime.setText(calendar.getTimeSet());
        Glide.with(context).load(calendar.getImage()).into(holder.pImg);



        count = Integer.parseInt(calendar.getCountRaw());
        time = calendar.getTime();
        sender = calendar.getSender();


    }


    @Override
    public int getItemCount() {
        return CalendarNoteList.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pMsg, pSender, pTime;
        public ImageView pImg;

        public ViewHolder(View itemView) {
            super(itemView);

            pMsg = itemView.findViewById(R.id.tvContentNotePreview);
            pSender = itemView.findViewById(R.id.tvNameNotePreview);
            pTime = itemView.findViewById(R.id.tvTimeNotePreview);
            pImg = itemView.findViewById(R.id.tvImageNotePreview);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    Toast.makeText(context, "please typ", Toast.LENGTH_LONG).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                    alertDialog.setTitle("עריכת הודעה");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "מחק הודעה ", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {



                            Calendar calendar = (Calendar) v.getTag();

                            dbHelper = new DBHelper(context);

                            dbHelper.deleteRawFromTable(count, time, TABLE_NAME_CALENDAR);

                            deleteRawFormFireBase();


                        }
                    });

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

    public void deleteRawFormFireBase() {


        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDatabase.child("Camps").child(firebaseUserModel.getCamp()).child("Calendar").orderByKey().equalTo(sender).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // String key = dataSnapshot.getKey();
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}


