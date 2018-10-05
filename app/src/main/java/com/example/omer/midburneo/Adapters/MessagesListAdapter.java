package com.example.omer.midburneo.Adapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Class.MessageCell;

import static com.example.omer.midburneo.RegisterAc.prefs;
import static com.example.omer.midburneo.Tabs.MainPageAc.current_camp_static;


public class MessagesListAdapter extends ArrayAdapter<MessageCell> {
    MessageCell[] cellItem = null;
    Context context;
//    public TextView sender, wish, dateTime;


    public static int VIEW_TYPE = 0;
    public static int VIEW_TYPE_PICTURE = 0;

    public MessagesListAdapter(Context context, MessageCell[] resource) {
        super(context, R.layout.test_message_cell, resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.cellItem = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if(VIEW_TYPE_PICTURE==2 && !cellItem[position].getImage().equals("default")){

            if (cellItem[position].getSender()) {
                VIEW_TYPE=0;

                convertView = inflater.inflate(R.layout.item_message_image_left, parent, false);

            } else {
                VIEW_TYPE=1;

                convertView = inflater.inflate(R.layout.item_message_image_right, parent, false);

            }

        }else {

            if (cellItem[position].getSender()) {
                VIEW_TYPE=0;

                convertView = inflater.inflate(R.layout.test_message_cell, parent, false);

            } else {
                VIEW_TYPE=1;

                convertView = inflater.inflate(R.layout.test_sender_message_cell, parent, false);

            }
        }

        TextView sender = (TextView) convertView.findViewById(R.id.photoName);
        TextView wish = (TextView) convertView.findViewById(R.id.wishMessage);
        TextView dateTime = (TextView) convertView.findViewById(R.id.dateTime);
        TextView text_message_time = (TextView) convertView.findViewById(R.id.text_message_time);
        ImageView image_message_profile = (ImageView) convertView.findViewById(R.id.image_message_profile);



        if (VIEW_TYPE_PICTURE==2 && !cellItem[position].getImage().equals("default")){


           

            Glide.with(context).load(cellItem[position].getImage()).into(image_message_profile);
            text_message_time.setText(cellItem[position].getMessageDateTime());

          //  VIEW_TYPE_PICTURE==2;

        }else {
            wish.setText(cellItem[position].getMessageText());
            dateTime.setText(cellItem[position].getMessageDateTime());

            if (VIEW_TYPE==1){

                sender.setText(cellItem[position].getMessageSender());

                VIEW_TYPE=0;
            }

        }






        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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

                return false;
            }
        });




        return convertView;
    }
}