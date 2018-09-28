package com.example.omer.midburneo.Adapters;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.Class.MessageNote;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.ChatListAc;
import com.example.omer.midburneo.Tabs.NotesAc;
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

import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_camp_static;

public class MessageNoteAdapter extends RecyclerView.Adapter<MessageNoteAdapter.ViewHolder> {


    private Context context;
    private final List<MessageNote> MessageNoteList;
    public String senderString, uidMsgString, campString;
    private int num = 1;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    private DBHelper dbHelper;



    public MessageNoteAdapter(Context context, List MessageNoteList) {
        this.context = context;
        this.MessageNoteList = MessageNoteList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_note, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // holder.itemView.setTag(MessageNoteList.get(position));

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



        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                // holder.checkBox.setChecked(false);
                holder.checkBox.isChecked();
                if (isChecked) {
                    int btnDrawable = android.R.drawable.checkbox_on_background;
                    holder.checkBox.setButtonDrawable(btnDrawable);
                    Toast.makeText(context, "ture", Toast.LENGTH_SHORT).show();
                    holder.relativeLayout.setBackgroundColor(R.color.red);

                    current_camp_static = prefs.getString("camps", null);

                    UpdateFirebase(current_camp_static,messageNote.getUidMsg());

                } else {
                    //checkBox clicked and unchecked
                    int btnDrawable = android.R.drawable.checkbox_off_background;
                    Toast.makeText(context, "fa", Toast.LENGTH_SHORT).show();
                    holder.checkBox.setButtonDrawable(btnDrawable);
                    holder.relativeLayout.setBackgroundColor(R.color.colorAccent);


                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return MessageNoteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pTitle, pContent, pDate, pDateEnd;
        public CheckBox checkBox;
        public RelativeLayout relativeLayout;
        // private CheckedTextView


        public ViewHolder(View itemView) {
            super(itemView);

            pTitle = itemView.findViewById(R.id.txtViewMsgNote);
            pContent = itemView.findViewById(R.id.tvContentNote);
            pDate = itemView.findViewById(R.id.txtViewTimeNote);
            pDateEnd = itemView.findViewById(R.id.tvDateEndNote);
            checkBox = itemView.findViewById(R.id.checkBoxNote);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutTop3);


            this.setIsRecyclable(false);


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
                            deleteMsg();

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

    private void deleteMsg() {
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Admin").child(campString).child("message");

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUserDatabase.child(uidMsgString).removeValue();

                String test = dataSnapshot.getKey();


                return;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void UpdateFirebase(String campString, String uidMsgString){


        long currentDateTime = System.currentTimeMillis();
        String timeString = String.valueOf(currentDateTime);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(campString).child("Note").child(uidMsgString);

        Map<String, Object> mapCampsUpdates = new HashMap<>();
        mapCampsUpdates.put("dateEnd", timeString);
        mapCampsUpdates.put("dateBool", "true");

        mUserDatabase.updateChildren(mapCampsUpdates);
    }
}