package com.example.omer.midburneo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
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
import com.example.omer.midburneo.CampsAc;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.ChatListAc;
import com.example.omer.midburneo.Tabs.MainPageAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.himanshusoni.chatmessageview.ChatMessageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {


    private Context context;
    private final List<Friend> personUtils;
    public String uidString, current_uid, urlString;
    private FirebaseUser mCurrentUser;


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
            holder.pLastMsg.setText(friend.getLastMsg());

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


        public ViewHolder(View itemView) {
            super(itemView);

            pName = itemView.findViewById(R.id.nameFriendClass);
            pLastMsg = itemView.findViewById(R.id.lastMsgFriendClass);
            pImage = itemView.findViewById(R.id.imageFriendClass);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Friend friend = (Friend) view.getTag();

                    Toast.makeText(view.getContext(), friend.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ChatListAc.class);
                    intent.putExtra("nameUidFriend", friend.getName());
                    intent.putExtra("imageUidFriend", friend.getImage());
                    intent.putExtra("receiverUidFriend", friend.getUidReceiver());
                    intent.putExtra("campUidFriend", friend.getCamp());
                    intent.putExtra("statusUidFriend", friend.getStatus());
                    intent.putExtra("countUidFriend", friend.getUidCount());
                    intent.putExtra("timeUidFriend", friend.getTime());
                    context.startActivity(intent);

                }
            });

        }
    }
}