package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBEquipment;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.DataBase.DBEquipment.DATABASE_NAME_EQUIPMENT;
import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;

import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class EquipmentEditAc extends AppCompatActivity {

    private static final String TAG = "EquipmentEditAc";


    private Button addEquipmentBtnPre, addBtnfirendEditEquip;
    private EditText etNameProdPre, etContentPre, etMountPre;
    private ImageView imagePreEquip;
    private String nameProdPre, contentPre, mountPre, timePre, current_uid, get_msg_uid;
    private String imgPre = "default";
    private String imgLocalPath = "default";
    private ProgressDialog mprogress;
    private DatabaseReference mUserDatabase;
    private DBHelper dbHelper;
    private DBEquipment dbEquipment;
    private StorageReference mImageStorage, filePath;
    private int num = 1;

    private Uri resultUri;


    String[] listItems;
    String[] listItemsKey;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    Map<String, Object> stringObjectHashMapEquipment = new HashMap<>();


    public static String directory_path_sdk_kikat_down = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    public static String directory_path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipment_add_event_activity);


        addEquipmentBtnPre = findViewById(R.id.addEquipmentButtonEditEquip);
        etNameProdPre = findViewById(R.id.etNameProdEditEquip);
        etContentPre = findViewById(R.id.etContetEditEquip);
        etMountPre = findViewById(R.id.etMountEditEquip);
        imagePreEquip = findViewById(R.id.imageEditEquip);
        addBtnfirendEditEquip = findViewById(R.id.addBtnfirendEditEquip);

        mprogress = new ProgressDialog(this);
        dbHelper = new DBHelper(getApplicationContext());
        dbEquipment = new DBEquipment(getApplicationContext());
        mImageStorage = FirebaseStorage.getInstance().getReference();


        current_uid = getIntent().getStringExtra("UidEquipment");
        PermissionManager.check(EquipmentEditAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

        getNamesUsersFromFireBase();


        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "/equipmentsDoc.xls");
        if (!file.exists()) {
            try {
                Log.v(TAG, "File Created" + String.valueOf(file.createNewFile()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(EquipmentEditAc.this,
                    "אין תיקייה ", //ADD THIS
                    Toast.LENGTH_SHORT).show();
            return;
        }


    }

    public void SaveDataToFireBaseEquipment() {


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Equipment").child(get_msg_uid);

        Map<String, Object> stringObjectHashMap = new HashMap<>();

        stringObjectHashMap.put("image", imgPre);
        stringObjectHashMap.put("nameProd", nameProdPre);
        stringObjectHashMap.put("content", contentPre);
        stringObjectHashMap.put("mount", mountPre);
        stringObjectHashMap.put("mountCurrent", "0");
        stringObjectHashMap.put("time", timePre);
        stringObjectHashMap.put("sender", current_uid);

        if (num == 2) {
            stringObjectHashMap.put(FeedReaderContract.FeedEntry.TAG_USER, stringObjectHashMapEquipment);

        }

        mUserDatabase.updateChildren(stringObjectHashMap);


    }


    public void SQLiteToExcel() {

        dbEquipment.open();
        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(getApplicationContext(), DATABASE_NAME_EQUIPMENT, directory_path);

        sqLiteToExcel.exportAllTables(firebaseUserModel.getCamp() + ".xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {
                Log.e("Successfully Exported", filePath);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }


    public void onImg(View view) {

        PermissionManager.check(EquipmentEditAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(EquipmentEditAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(EquipmentEditAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("תמונת פרופיל");
        alertDialog.setMessage("בחר תמונה");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "גלריה", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                gallery();

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "חזור", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                return;

            }
        });

        alertDialog.show();
    }


    private void gallery() {

        //open media activity for image --
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)//crop image activity after shot
                    .setAspectRatio(1, 1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.get().load(resultUri).error(R.drawable.midburn_logo).into(imagePreEquip);
                imgLocalPath = String.valueOf(resultUri);

                filePath = mImageStorage.child("profile_images").child(resultUri.getLastPathSegment());


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(EquipmentEditAc.this, "error", Toast.LENGTH_LONG).show();

            }
        }

    }

    private void imgUpload() {

        if (resultUri == null) {
            return;
        }

        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        imgPre = String.valueOf(uri);

                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(firebaseUserModel.getChat()).child("Equipment").child(get_msg_uid);

                        Map<String, Object> mapCampsUpdates = new HashMap<>();
                        mapCampsUpdates.put(FeedReaderContract.FeedEntry.IMAGE, imgPre);

                        mUserDatabase.updateChildren(mapCampsUpdates);


                        mprogress.dismiss();


                    }
                });
            }
        });


    }


    public void addEquipmentBtn(View view) {


        nameProdPre = etNameProdPre.getText().toString();
        contentPre = etContentPre.getText().toString();
        mountPre = etMountPre.getText().toString();

        mprogress.setMessage("שולח בקשה");
        mprogress.show();


        if (!nameProdPre.isEmpty() && !contentPre.isEmpty() && !mountPre.isEmpty()) {
            imgUpload();


            if (num == 1) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = true;

                    mUserItems.add(i);
                }
                String item = "";
                for (int i = 0; i < mUserItems.size(); i++) {

                    String listItemString = listItems[mUserItems.get(i)];
                    String listItemKeyString = listItemsKey[mUserItems.get(i)];

                    stringObjectHashMapEquipment.put(listItemKeyString, listItemString);
                    stringObjectHashMapEquipment.put(current_uid, firebaseUserModel.getName());


                    item = item + listItems[mUserItems.get(i)];

                    if (i != mUserItems.size() - 1) {
                        item = item + ", ";
                    }
                }


            }


            get_msg_uid = UUID.randomUUID().toString();

            long currentDateTime = System.currentTimeMillis();
            timePre = String.valueOf(currentDateTime);


            SaveDataToFireBaseEquipment();
            dbHelper.SaveDBSqliteToEquipment(nameProdPre, contentPre, mountPre, "0", timePre, imgLocalPath, current_uid, get_msg_uid);
            dbEquipment.SaveDBSqliteToEquipmentExcel(nameProdPre, contentPre, mountPre, "0", timePre, imgLocalPath, firebaseUserModel.getName());

            SQLiteToExcel();

            Intent i = new Intent(EquipmentEditAc.this, EquipmentAc.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        } else {
            Toast.makeText(EquipmentEditAc.this,
                    "בבקשה תמלא את הפרטים ", //ADD THIS
                    Toast.LENGTH_SHORT).show();
        }

        mprogress.dismiss();

    }


    public void getNamesUsersFromFireBase() {


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Users");

        String chatUid = firebaseUserModel.getChat();

        Query query = discussionRoomsRef.orderByChild("chat").equalTo(chatUid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> contactsArray = new ArrayList<>();
                ArrayList<String> keyArray = new ArrayList<>();

                long FBCount = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    FBCount = dataSnapshot.getChildrenCount();

                    String key = ds.getKey();


                    if (!key.equals(firebaseUserModel.getChat())) {

                        if (!key.equals(current_uid)) {
                            String name = ds.child("name").getValue().toString();


                            contactsArray.add(name);
                            keyArray.add(key);
                        } else {

                        }


                    }
                }


                listItems = new String[contactsArray.size()];
                listItems = contactsArray.toArray(listItems);

                listItemsKey = new String[keyArray.size()];
                listItemsKey = keyArray.toArray(listItemsKey);

                // listItems = getResources().getStringArray((int) FBCount);
                checkedItems = new boolean[listItems.length];


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);

    }

    public void getUserListEquip(View view) {

        Toast.makeText(EquipmentEditAc.this, "error", Toast.LENGTH_LONG).show();


        stringObjectHashMapEquipment.put(current_uid, firebaseUserModel.getName());

        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(EquipmentEditAc.this);
        mBuilder.setTitle("סמן חברים");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                if (isChecked) {
                    mUserItems.add(position);


                } else {
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                num = 2;

                for (int i = 0; i < mUserItems.size(); i++) {

                    String listItemString = listItems[mUserItems.get(i)];
                    String listItemKeyString = listItemsKey[mUserItems.get(i)];

                    stringObjectHashMapEquipment.put(listItemKeyString, listItemString);

                    item = item + listItems[mUserItems.get(i)];

                    if (i != mUserItems.size() - 1) {
                        item = item + ", ";
                    }
                }


                //  mItemSelected.setText(item);
            }
        });

        mBuilder.setNegativeButton("חזור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("בחר הכל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = true;

                    // Todo not dismiss
//                            mUserItems.size();

                    //   mItemSelected.setText("");
                }
            }
        });

        android.support.v7.app.AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
}

