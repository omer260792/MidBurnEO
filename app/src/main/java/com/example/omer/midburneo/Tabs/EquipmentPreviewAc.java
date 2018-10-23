package com.example.omer.midburneo.Tabs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.omer.midburneo.AddNoteActivity;
import com.example.omer.midburneo.Class.Calendar;
import com.example.omer.midburneo.Class.Equipment;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class EquipmentPreviewAc extends AppCompatActivity {

    private Button addEquipmentBtnPre, etTimePre;
    private EditText etNameProdPre, etContentPre, etMountPre, etMountCurrentPre;
    private TextView txtclose;
    private ImageView  pImgPop;
    private CircleImageView imagePre;
    public String nameProdPre, contentPre, mountPre, mountCurrentPre, timePre, imgPre, current_uid, get_msg_uid, timeMilliString, count, timeSetString, uid_msg;
    private DatabaseReference mUserDatabase;
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private TimePickerDialog mTimeSetListener;
    private long timeMili = 123456789;
    private int num = 1;


    public Dialog myDialog;


    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipment_preview_activity);


        addEquipmentBtnPre = findViewById(R.id.addEquipmentButtonPre);
        etNameProdPre = findViewById(R.id.etNameProdPre);
        etContentPre = findViewById(R.id.etContetPre);
        etMountPre = findViewById(R.id.etMountPre);
        imagePre = findViewById(R.id.imagePre);
        etMountCurrentPre = findViewById(R.id.etMountCurrentPre);
        etTimePre = findViewById(R.id.etTimedPre);

        dbHelper = new DBHelper(getApplicationContext());

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        nameProdPre = getIntent().getStringExtra("nameProdEquipment");
        contentPre = getIntent().getStringExtra("contentEquipment");
        mountPre = getIntent().getStringExtra("mountEquipment");
        mountCurrentPre = getIntent().getStringExtra("mountCurrentEquipment");
        timePre = getIntent().getStringExtra("timeEquipment");
        imgPre = getIntent().getStringExtra("imageEquipment");
        uid_msg = getIntent().getStringExtra("senderUidEquipment");
        get_msg_uid = getIntent().getStringExtra("msgUidEquipment");
        count = getIntent().getStringExtra("countEquipment");


        if (imgPre.equals("default")) {

            imagePre.setVisibility(View.GONE);

        } else {
            Picasso.get().load(imgPre).error(R.drawable.midburn_logo).into(imagePre);

        }

        myDialog = new Dialog(this);

        long timeSetLong = (Long.parseLong(timePre));
        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        timeSetString = getTimeHourMintus.format(timeSetLong);

        etContentPre.setText(contentPre);
        etMountPre.setText(mountPre);
        etMountCurrentPre.setText(mountCurrentPre);
        etTimePre.setText(timeSetString);
        etNameProdPre.setText(nameProdPre);


        imagePre.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                myDialog.setContentView(R.layout.item_image_popup);
                txtclose = myDialog.findViewById(R.id.txtclose);
                pImgPop = myDialog.findViewById(R.id.imgPopUp);


                try {
                    Picasso.get().load(imgPre).resize(800, 1000).error(R.drawable.midburn_logo).into(pImgPop);

                } catch (NullPointerException e) {
                }


                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();


                    }
                });

                return false;
            }
        });


        etTimePre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(java.util.Calendar.MINUTE);


                mTimeSetListener = new TimePickerDialog(EquipmentPreviewAc.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        num = 2;

                        java.util.Calendar cal = java.util.Calendar.getInstance();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cal.set(java.util.Calendar.HOUR_OF_DAY, timePicker.getHour());
                            cal.set(java.util.Calendar.MINUTE, timePicker.getMinute());

                            timeMili = cal.getTimeInMillis();
                            timeMilliString = String.valueOf(timeMili);

                        } else {

                            timeMili = cal.getTimeInMillis();
                            timeMilliString = String.valueOf(timeMili);

                        }

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimeSetListener.setTitle("Select Time");
                mTimeSetListener.show();
            }
        });


        addEquipmentBtnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uid_msg.equals(current_uid)) {

                    nameProdPre = etNameProdPre.getText().toString();
                    contentPre = etContentPre.getText().toString();
                    mountCurrentPre = etMountCurrentPre.getText().toString();
                    mountPre = etMountPre.getText().toString();
                    String time = etTimePre.getText().toString();

                    if (!nameProdPre.isEmpty() && !contentPre.isEmpty() && !mountCurrentPre.isEmpty() && !mountPre.isEmpty() && !time.isEmpty()) {

                        db = dbHelper.getWritableDatabase();


                        if (num == 1) {
                            timeMilliString = timePre;

                        }

                        UpdateDataToFireBaseEquipment();

                        ContentValues data = new ContentValues();
                        data.put(FeedReaderContract.FeedEntry.NAME_EQUIPMENT, nameProdPre);
                        data.put(FeedReaderContract.FeedEntry.CONTENT, contentPre);
                        data.put(FeedReaderContract.FeedEntry.MOUNT, mountPre);
                        data.put(FeedReaderContract.FeedEntry.MOUNT_CURRENT, mountCurrentPre);
                        data.put(FeedReaderContract.FeedEntry.TIME, timeMilliString);
                        db.update(TABLE_NAME_EQUIPMENT, data, "_id=" + count, null);

                        Intent i = new Intent(EquipmentPreviewAc.this, EquipmentAc.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(EquipmentPreviewAc.this,
                                "בבקשה תמלא את הפרטים ",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(EquipmentPreviewAc.this,
                            "רק המפרסם יכול לשנות את ההודעה",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void UpdateDataToFireBaseEquipment() {


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Equipment").child(get_msg_uid);

        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put(FeedReaderContract.FeedEntry.CONTENT, contentPre);
        stringObjectHashMap.put(FeedReaderContract.FeedEntry.MOUNT, mountPre);
        stringObjectHashMap.put(FeedReaderContract.FeedEntry.MOUNT_CURRENT, mountCurrentPre);
        stringObjectHashMap.put("nameProd", nameProdPre);
        stringObjectHashMap.put(FeedReaderContract.FeedEntry.TIME, timeMilliString);

        mUserDatabase.updateChildren(stringObjectHashMap);


    }


}
