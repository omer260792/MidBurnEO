package com.example.omer.midburneo.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.ChatAc;
import com.example.omer.midburneo.Tabs.ChatListAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
import static com.example.omer.midburneo.Tabs.ChatAc.callPhoneChatAc;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {


    private Context context;
    private List<Friend> personUtils;
    public String current_uid, urlString, chatRoomsString, deviceID;
    public int countString, countLastMsg;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    private DBHelper dbHelper;


    public FriendsAdapter(Context context, List<Friend> personUtils) {
        this.context = context;
        this.personUtils = personUtils;

    }


    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(personUtils.get(position));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();

        Friend friend = personUtils.get(position);

        chatRoomsString = friend.getChatRoom();
        countString = Integer.parseInt(friend.getUidCount());
        deviceID = friend.getDevice();

        if (friend.getLastMsg().equals("default")){
            countLastMsg = 0;
        }else{
            countLastMsg = Integer.parseInt(friend.getLastMsg());

        }

        holder.pName.setText(friend.getName());
        holder.pRoll.setText(friend.getRole());

      ///  holder.pLastmsg.setText(friend.getLastMsg());

        if (countLastMsg == 0){
            holder.pLastmsg.setText("");

        }else {
            holder.pLastmsg.setText(friend.getLastMsg());

        }



        urlString = friend.getImage();


        if (urlString.equals("default")) {
            Glide.with(context).load(R.drawable.midburn_logo).into(holder.pImage);

        } else {
            Glide.with(context).load(urlString).into(holder.pImage);


        }
    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pName, pRoll, pLastmsg;
        public ImageView pImage;

        public TextView txtclose, tvCampPopUp, tvNamePopUp, tvRolePopUp;
        public Button btnCallPopUp, tvAdminPopUp, tvDeleteMsgPopUp;
        public CircleImageView imgPopUp;

        private Dialog myDialog;


        public ViewHolder(View itemView) {
            super(itemView);

            pName = itemView.findViewById(R.id.nameFriendClass);
            pRoll = itemView.findViewById(R.id.rollFriendClass);
            pImage = itemView.findViewById(R.id.imageFriendClass);
            pLastmsg = itemView.findViewById(R.id.lastMsgFriendClass);


            myDialog = new Dialog(context);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Friend friend = (Friend) view.getTag();

                    // user.SPUser();
                    Intent intent = new Intent(context, ChatListAc.class);
                    intent.putExtra("nameUidFriend", friend.getName());
                    intent.putExtra("imageUidFriend", friend.getImage());
                    intent.putExtra("receiverUidFriend", friend.getUidReceiver());
                    intent.putExtra("campUidFriend", friend.getCamp());
                    intent.putExtra("statusUidFriend", friend.getStatus());
                    intent.putExtra("countUidFriend", friend.getUidCount());
                    intent.putExtra("timeUidFriend", friend.getTime());
                    intent.putExtra("onilneUidFriend", friend.getOnline());
                    intent.putExtra("deviceUidFriend", friend.getDevice());
                    intent.putExtra("tokenUidFriend", friend.getToken());
                    intent.putExtra("chatRoomsUidFriend", friend.getChatRoom());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);


                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Friend friend = (Friend) view.getTag();


                    dbHelper = new DBHelper(context);

                    String nameGroup = friend.getName();

                    if (!friend.getDevice().equals("default")) {
                        Log.e("FF", deviceID);
                        ShowPopup(view);

                    } else {
                        //Todo **************


                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("עריכת קבוצה")
                                .setMessage(nameGroup)
                                .setNegativeButton("חזור", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("מחק קבוצה", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        int count = Integer.parseInt(friend.getUidCount());
                                        String ChatRooms = friend.getChatRoom();
                                        String nameColums = FeedReaderContract.FeedEntry.CHAT_ROOMS;

                                        deleteRawFormFireBase(ChatRooms);

                                        dbHelper.deleteRawFromTable(count, ChatRooms, TABLE_NAME, nameColums);
                                        Log.e("ff", "64cfe10c-4999-437e-a495-32511d89f94b");
                                        Intent i = new Intent(context, ChatAc.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(i);

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();


                    }
                    return true;
                }
            });

        }

        public void ShowPopup(View v) {

            Friend friend = (Friend) v.getTag();

            myDialog.setContentView(R.layout.item_user_popup);
            txtclose = myDialog.findViewById(R.id.txtclose);
            imgPopUp = myDialog.findViewById(R.id.imgPopUp);
            btnCallPopUp = myDialog.findViewById(R.id.btnCallPopUp);
            tvCampPopUp = myDialog.findViewById(R.id.tvCampPopUp);
            tvNamePopUp = myDialog.findViewById(R.id.tvNamePopUp);
            tvAdminPopUp = myDialog.findViewById(R.id.tvAdminPopUp);
            tvDeleteMsgPopUp = myDialog.findViewById(R.id.tvDeleteMsgPopUp);
            tvRolePopUp = myDialog.findViewById(R.id.tvRolePopUp);


            try {
                Picasso.get().load(friend.getImage()).resize(300, 300).error(R.drawable.midburn_logo).into(imgPopUp);

            } catch (NullPointerException e) {
                Picasso.get().load(R.drawable.midburn_logo).resize(100, 200).error(R.drawable.midburn_logo).into(imgPopUp);
            }
            tvCampPopUp.setText(friend.getCamp());
            tvNamePopUp.setText(friend.getName());
            tvRolePopUp.setText(friend.getRole());

            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

            tvAdminPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (firebaseUserModel.getAdmin().equals("admin")) {
                        UpdateUserAdmin(friend.getUidReceiver());

                    } else {
                        Toast.makeText(context, "אתה לא מנהל",
                                Toast.LENGTH_SHORT).show();
                    }


                }
            });

            tvDeleteMsgPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper = new DBHelper(context);


                    if (friend.getUidReceiver().equals(current_uid)) {

                        dbHelper.deleteRawFromTable(countString, chatRoomsString, TABLE_NAME, FeedReaderContract.FeedEntry.CHAT_ROOMS);
                        deleteRawFormFireBase(chatRoomsString);

                    } else {
                        dbHelper.deleteRawFromTable(countString, chatRoomsString, TABLE_NAME, FeedReaderContract.FeedEntry.CHAT_ROOMS);

                    }

                    myDialog.dismiss();
//                    if (friend.getName().equals(firebaseUserModel.getCamp())) {
//                        dbHelper.DeleteTableSqliteDB(current_uid);
//
//                    } else {
//                        dbHelper.DeleteTableSqliteDB(friend.getUidReceiver());
//
//                    }
                }
            });

            btnCallPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callPhoneChatAc(friend.getPhone(), context);
                }
            });


            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();

                }
            });

        }

        public void UpdateUserAdmin(String uid) {

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            Map<String, Object> mapCampsUpdates = new HashMap<>();
            mapCampsUpdates.put("admin", "admin");

            mUserDatabase.updateChildren(mapCampsUpdates);


        }
    }


    private void deleteRawFormFireBase(String uidMsg) {


        mUserDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(current_uid).child(FeedReaderContract.FeedEntry.GROUP).child(uidMsg);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                dataSnapshot.getRef().removeValue();


                // ToDo delete from Firebase

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void filterList(List<Friend> filteredList) {
        personUtils = filteredList;
        notifyDataSetChanged();
    }
}