package com.example.omer.midburneo.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.EquipmentPreviewAc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_CALENDAR;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Equipment> EquipmentNoteList;
    public String imageString, uidMsgString, uidUserString, timeSting;
    private int countInt;
    private DatabaseReference mUserDatabase;
    public DBHelper dbHelper;


    public EquipmentAdapter(Context context, ArrayList<Equipment> EquipmentNoteList) {
        this.context = context;
        this.EquipmentNoteList = EquipmentNoteList;
    }

    @NonNull
    @Override
    public EquipmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipment, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(EquipmentNoteList.get(position));




        Equipment equipment = EquipmentNoteList.get(position);

        imageString = equipment.getImage();
        uidMsgString = equipment.getUid();
        uidUserString = equipment.getSender();
        timeSting = equipment.getTime();
        countInt = Integer.parseInt(equipment.getCount());


        long getTimeLong = Long.parseLong(timeSting);
        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
        String timeFormat = getTimeHourMintus.format(getTimeLong);



        holder.pContent.setText(equipment.getContent());
        holder.pMount.setText(equipment.getMount());
        holder.pMountCurrnet.setText(equipment.getMountCurrent());
        holder.pNameProd.setText(equipment.getNameProd());
        holder.pTime.setText(timeFormat);

        if (imageString.equals("default")) {
            holder.pImage.setVisibility(View.GONE);

        } else {
            Glide.with(context).load(imageString).into(holder.pImage);

        }
    }

    @Override
    public int getItemCount() {
        return EquipmentNoteList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pNameProd, pContent, pTime, pMountCurrnet, pMount;
        public ImageView pImage;

        public ViewHolder(View itemView) {
            super(itemView);

            pNameProd = itemView.findViewById(R.id.nameEquip);
            pContent = itemView.findViewById(R.id.contentEquip);
            pTime = itemView.findViewById(R.id.timeEquip);
            pImage = itemView.findViewById(R.id.imageEquip);
            pMountCurrnet = itemView.findViewById(R.id.tvMountCurrnetEquip);
            pMount = itemView.findViewById(R.id.tvMountEquip);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Equipment equipment = (Equipment) view.getTag();

                    Toast.makeText(view.getContext(), equipment.getNameProd(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, EquipmentPreviewAc.class);
                    intent.putExtra("nameProdEquipment", equipment.getNameProd());
                    intent.putExtra("contentEquipment", equipment.getContent());
                    intent.putExtra("mountEquipment", equipment.getMount());
                    intent.putExtra("mountCurrentEquipment", equipment.getMountCurrent());
                    intent.putExtra("timeEquipment", equipment.getTime());
                    intent.putExtra("imageEquipment", equipment.getImage());
                    intent.putExtra("senderUidEquipment", equipment.getSender());
                    intent.putExtra("msgUidEquipment", equipment.getUid());
                    intent.putExtra("countEquipment", equipment.getCount());

                    context.startActivity(intent);

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    String current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                    alertDialog.setTitle("עריכת הודעה");


                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "מחק הודעה ", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {


                            if (current_uid.equals(uidUserString)) {

                                dbHelper = new DBHelper(context);

                                dbHelper.deleteRawFromTable(countInt, timeSting, TABLE_NAME_EQUIPMENT, "time");


                            }


                        }
                    });


                    if (current_uid.equals(uidUserString)) {

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "מחק לכולם", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {


                                deleteRawFormFireBase(uidMsgString);
                                dbHelper = new DBHelper(context);

                                dbHelper.deleteRawFromTable(countInt, timeSting, TABLE_NAME_EQUIPMENT, "time");


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

    public void filterList(ArrayList<Equipment> filteredList) {
        EquipmentNoteList = filteredList;
        notifyDataSetChanged();
    }


    private void deleteRawFormFireBase(String uidMsg) {


        mUserDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Camps").child(firebaseUserModel.getChat()).child("Equipment").child(uidMsg);
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
