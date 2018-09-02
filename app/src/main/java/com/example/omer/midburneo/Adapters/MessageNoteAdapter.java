package com.example.omer.midburneo.Adapters;


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
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.bumptech.glide.Glide;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.Class.MessageNote;
import com.example.omer.midburneo.R;
        import com.example.omer.midburneo.Tabs.ChatListAc;
import com.example.omer.midburneo.Tabs.NotesAc;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MessageNoteAdapter extends RecyclerView.Adapter<MessageNoteAdapter.ViewHolder> {


    private Context context;
    private final List<MessageNote> MessageNoteList;
    public String senderString, uidMsgString, campString;
    private DatabaseReference mUserDatabase;



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


        holder.pMsg.setText(messageNote.getMsg());
        holder.pTime.setText(messageNote.getTime());

        senderString = messageNote.getSender();
        uidMsgString = messageNote.getUidMsg();
        campString = messageNote.getCamp();



    }

    @Override
    public int getItemCount() {
        return MessageNoteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pMsg, pTime;


        public ViewHolder(View itemView) {
            super(itemView);

            pMsg = (TextView) itemView.findViewById(R.id.txtViewMsgNote);
            pTime = (TextView) itemView.findViewById(R.id.txtViewTimeNote);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//
//                    Friend friend = (Friend) view.getTag();
//
//                    Uri myUrl = friend.getImage();
//                    urlString = String.valueOf(myUrl);
//
//                    Log.e("55555555555555555", urlString);
//
//                    Toast.makeText(view.getContext(), friend.getName(), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, ChatListAc.class);
//                    intent.putExtra("nameUidFriend", friend.getName());
//                    intent.putExtra("imageUidFriend", urlString);
//                    intent.putExtra("receiverUidFriend", friend.getUidReceiver());
//                    intent.putExtra("campUidFriend", friend.getCamp());
//                    context.startActivity(intent);

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
//
//
//
//        mUserDatabase.removeEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                mUserDatabase.child(uidMsgString).removeValue();
//
//
//                return;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUserDatabase.child(uidMsgString).removeValue();

                String test = dataSnapshot.getKey();


                return;


                //  NotesAc.refreshList = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}