package com.example.omer.midburneo.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omer.midburneo.Class.Message;
import com.example.omer.midburneo.R;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;


import java.util.List;

import static com.example.omer.midburneo.Tabs.MainPageAc.current_uid_camp_static;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MsgViewHolder> {

    private Context context;
    private final List<Message> msgList;
    public String current_uid, current_camp_static_get;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_GROUP = 3;
    private static final int VIEW_TYPE_MESSAGE_IMAGE_SENT = 4;
    private static final int VIEW_TYPE_MESSAGE_IMAGE_RECEIVED = 5;
    private static final int VIEW_TYPE_MESSAGE_IMAGE_GROUP = 6;


    public MessageAdapter(Context context, List msgList) {
        this.context = context;
        this.msgList = msgList;

    }


    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;


        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_left, parent, false);
            return new MsgViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_right, parent, false);
            return new MsgViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_GROUP) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_right, parent, false);
            return new MsgViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_IMAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_image_left, parent, false);
            return new MsgViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_IMAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_image_right, parent, false);
            return new MsgViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_IMAGE_GROUP) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_image_right, parent, false);
            return new MsgViewHolder(view);
        }


        return null;


    }

    @Override
    public int getItemViewType(int position) {

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Message message = (Message) msgList.get(position);
        current_camp_static_get = current_uid_camp_static;

        if (message.getMsg().equals("image")) {

            if (message.getSender().equals(current_uid)) {
                return VIEW_TYPE_MESSAGE_IMAGE_SENT;

            } else if (message.getReceiver().equals(current_camp_static_get)) {
                return VIEW_TYPE_MESSAGE_IMAGE_GROUP;

            } else {
                return VIEW_TYPE_MESSAGE_IMAGE_RECEIVED;

            }

        } else if (message.getReceiver().equals(current_camp_static_get)) {

            if (message.getSender().equals(current_uid)) {
                return VIEW_TYPE_MESSAGE_SENT;

            }
            return VIEW_TYPE_MESSAGE_GROUP;

        } else {
            if (message.getSender().equals(current_uid)) {
                return VIEW_TYPE_MESSAGE_SENT;

            } else {
                return VIEW_TYPE_MESSAGE_RECEIVED;

            }
        }


    }


    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {

        Message message = msgList.get(position);


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:

                holder.tvMsg.setText(message.getMsg());
                holder.tvTime.setText(message.getTime());
                Glide.with(context).load(message.getImage()).into(holder.tvImage);

                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:

                holder.tvMsg.setText(message.getMsg());
                holder.tvTime.setText(message.getTime());
                Glide.with(context).load(message.getImage()).into(holder.tvImage);

            case VIEW_TYPE_MESSAGE_GROUP:

                holder.tvMsg.setText(message.getMsg());
                holder.tvName.setText(message.getName_sender());
                holder.tvTime.setText(message.getTime());
                Glide.with(context).load(message.getImage()).into(holder.tvImage);

            case VIEW_TYPE_MESSAGE_IMAGE_SENT:
                holder.tvTime.setText(message.getTime());
                Glide.with(context).load(message.getImage()).into(holder.tvImage);

            case VIEW_TYPE_MESSAGE_IMAGE_RECEIVED:
                holder.tvTime.setText(message.getTime());
                Glide.with(context).load(message.getImage()).into(holder.tvImage);

            case VIEW_TYPE_MESSAGE_IMAGE_GROUP:
               // holder.tvName.setText(message.getName_sender());
                holder.tvTime.setText(message.getTime());
                Glide.with(context).load(message.getImage()).into(holder.tvImage);
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }


    public class MsgViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMsg, tvTime, tvName;
        public ImageView tvImage;

        public MsgViewHolder(View itemView) {
            super(itemView);

            tvImage = itemView.findViewById(R.id.image_message_profile);
            tvMsg = itemView.findViewById(R.id.text_message_body);
            tvName = itemView.findViewById(R.id.text_message_name);
            tvTime = itemView.findViewById(R.id.text_message_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Message msg = (Message) view.getTag();

                }
            });


        }
    }
}

