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
import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.EquipmentPreviewAc;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Equipment> EquipmentNoteList;
    public String imageString, uidMsgString, campString;
    private DatabaseReference mUserDatabase;

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


        holder.pContent.setText(equipment.getContent());
        holder.pMount.setText(equipment.getMount());
        holder.pMountCurrnet.setText(equipment.getMountCurrent());
        holder.pNameProd.setText(equipment.getNameProd());
        holder.pTime.setText(equipment.getTime());

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

                    context.startActivity(intent);

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

    public void filterList(ArrayList<Equipment> filteredList) {
        EquipmentNoteList = filteredList;
        notifyDataSetChanged();
    }
}
