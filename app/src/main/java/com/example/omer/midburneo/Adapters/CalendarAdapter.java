package com.example.omer.midburneo.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.R;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Calendar> CalendarNoteList;
    private String msgString, senderString, timeString;
    private DatabaseReference mUserDatabase;

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



        holder.pMsg.setText(calendar.getMsg());
        holder.pSender.setText(calendar.getSender());
        holder.pTime.setText(calendar.getTimeSet());

    }



    @Override
    public int getItemCount() {
        return CalendarNoteList.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pMsg, pSender, pTime;

        public ViewHolder(View itemView) {
            super(itemView);

            pMsg = itemView.findViewById(R.id.tvContentNotePreview);
            pSender = itemView.findViewById(R.id.tvNameNotePreview);
            pTime = itemView.findViewById(R.id.tvTimeNotePreview);



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
}


