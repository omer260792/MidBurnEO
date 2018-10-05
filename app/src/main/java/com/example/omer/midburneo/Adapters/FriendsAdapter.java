package com.example.omer.midburneo.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.Class.UserTest;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.ChatListAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.ChatAc.callPhoneChatAc;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_admin_static;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_camp_static;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {


    private Context context;
    private final List<Friend> personUtils;
    public String uidString, current_uid, urlString;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    private DBHelper dbHelper;


    public FriendsAdapter(Context context, List personUtils) {
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

        uidString = friend.uidReceiver;
        if (uidString.equals(current_uid)) {

        } else {
            holder.pName.setText(friend.getName());
            holder.pLastMsg.setText(friend.getRole());

            urlString = friend.getImage();


            if (urlString.equals("default")) {
                Glide.with(context).load(R.drawable.midburn_logo).into(holder.pImage);

            } else {
                Glide.with(context).load(urlString).into(holder.pImage);

            }
        }
    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pName, pLastMsg;
        public ImageView pImage;

        public TextView txtclose, tvCampPopUp, tvNamePopUp, tvRolePopUp;
        public Button btnCallPopUp, tvAdminPopUp, tvDeleteMsgPopUp;
        public CircleImageView imgPopUp;

        public Dialog myDialog;

        UserTest user = UserTest.getInstance();



        public ViewHolder(View itemView) {
            super(itemView);

            pName = itemView.findViewById(R.id.nameFriendClass);
            pLastMsg = itemView.findViewById(R.id.lastMsgFriendClass);
            pImage = itemView.findViewById(R.id.imageFriendClass);


            myDialog = new Dialog(context);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Friend friend = (Friend) view.getTag();

                   // user.SPUser();
                    Toast.makeText(view.getContext(), friend.getName(), Toast.LENGTH_SHORT).show();
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
                    context.startActivity(intent);

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    ShowPopup(view);

//                    Friend friend = (Friend) view.getTag();
//
//                    Intent intent = new Intent(context, PopUpUser.class);
//                    intent.putExtra("nameUidFriend", friend.getName());
//                    intent.putExtra("imageUidFriend", friend.getImage());
//                    intent.putExtra("receiverUidFriend", friend.getUidReceiver());
//                    intent.putExtra("campUidFriend", friend.getCamp());
//                    intent.putExtra("statusUidFriend", friend.getStatus());
//                    intent.putExtra("countUidFriend", friend.getUidCount());
//                    intent.putExtra("timeUidFriend", friend.getTime());
//                    context.startActivity(intent);

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
                Picasso.get().load(R.drawable.midburn_logo).resize(200, 200).error(R.drawable.midburn_logo).into(imgPopUp);
            }
            tvCampPopUp.setText(friend.getCamp());
            tvNamePopUp.setText(friend.getName());
            tvRolePopUp.setText(friend.getRole());

            tvAdminPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    current_admin_static = prefs.getString("admin", null);

                    if (current_admin_static.equals("admin")) {
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
                    current_camp_static = prefs.getString("camps", null);

                    if (friend.getName().equals(current_camp_static)) {
                        dbHelper.DeleteTableSqliteDB(current_uid);

                    } else {
                        dbHelper.DeleteTableSqliteDB(friend.getUidReceiver());

                    }
                }
            });

            btnCallPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callPhoneChatAc(friend.getPhone(),context);
                }
            });


            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
//                    Intent intent = new Intent(context, ChatAc.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    context.startActivity(intent);

                }
            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
        }

        public void UpdateUserAdmin(String uid) {

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            Map<String, Object> mapCampsUpdates = new HashMap<>();
            mapCampsUpdates.put("admin", "admin");

            mUserDatabase.updateChildren(mapCampsUpdates);


        }
    }


}