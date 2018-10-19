package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.FirebaseMessageModel;
import com.example.omer.midburneo.Class.FirebaseUserModel;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class AddGroupAc extends AppCompatActivity {

    private static final String TAG = "AddGroupAc";


    private Button addBtnfirendAddGroup, addGroupButtonAddGroup;
    private EditText etNameAddGroup;
    private ImageView imageAddGroup;

    private Uri resultUri;
    private int num = 1;
    private String get_msg_uid, current_uid, current_name, uidMsg, stringUrl, chatUid, listItemString, listItemKeyString, nameGroup;
    private String imgLocalPath = "default";
    private StorageReference mImageStorage, filePath;
    private ProgressDialog mprogress;
    private DatabaseReference mMsgDatabase, mUserDatabase;
    private FirebaseDatabase database;
    public DBHelper dbHelper;


    String[] listItems;
    String[] listItemsKey;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    Map<String, Object> stringObjectHashMapAddGroup = new HashMap<>();





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_add_group);

        addBtnfirendAddGroup = findViewById(R.id.addBtnfirendAddGroup);
        addGroupButtonAddGroup = findViewById(R.id.addGroupButtonAddGroup);
        etNameAddGroup = findViewById(R.id.etNameAddGroup);
        imageAddGroup = findViewById(R.id.imageAddGroup);

        dbHelper = new DBHelper(getApplicationContext());

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mprogress = new ProgressDialog(this);

        get_msg_uid = UUID.randomUUID().toString();
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        current_name = firebaseUserModel.getName();
        chatUid = firebaseUserModel.getChat();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        filePath = FirebaseStorage.getInstance().getReference();


        database = FirebaseDatabase.getInstance();
        mMsgDatabase = database.getReference("ChatRooms").child(get_msg_uid);

        getNamesUsersFromFireBase();


        addGroupButtonAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameGroup = etNameAddGroup.getText().toString();
                hideKeyboard();

                if (!nameGroup.equals("")) {

                    if (imgLocalPath == null) {
                        //resultUri = Uri.parse("default");
                        stringUrl = "default";

                    }else {
                        stringUrl = String.valueOf(resultUri);

                    }


                    if (num == 2) {

                        for (int i = 0; i < mUserItems.size(); i++) {

                            listItemKeyString = listItemsKey[mUserItems.get(i)];

//                            Boolean key = stringObjectHashMapAddGroup.get(listItemKeyString).equals(listItemKeyString);
//
//                            Log.e("ddddd", String.valueOf(key));

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(listItemKeyString).child(FeedReaderContract.FeedEntry.GROUP);

                            Map<String, Object> mapCampsUpdates = new HashMap<>();
                            mapCampsUpdates.put(get_msg_uid, nameGroup);
                            mUserDatabase.updateChildren(mapCampsUpdates);

                        }


                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child(FeedReaderContract.FeedEntry.GROUP);

                        Map<String, Object> mapCampsUpdates = new HashMap<>();
                        mapCampsUpdates.put(get_msg_uid, nameGroup);
                        mUserDatabase.updateChildren(mapCampsUpdates);

                    }else {

                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = true;

                            mUserItems.add(i);
                        }
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {

                            String listItem = listItems[mUserItems.get(i)];
                            String listItemKey = listItemsKey[mUserItems.get(i)];

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(listItemKey).child(FeedReaderContract.FeedEntry.GROUP);

                            Map<String, Object> mapCampsUpdates = new HashMap<>();
                            mapCampsUpdates.put(get_msg_uid, nameGroup);
                            mUserDatabase.updateChildren(mapCampsUpdates);


                        }

                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child(FeedReaderContract.FeedEntry.GROUP);

                        Map<String, Object> mapCampsUpdates = new HashMap<>();
                        mapCampsUpdates.put(get_msg_uid, nameGroup);
                        mUserDatabase.updateChildren(mapCampsUpdates);


                    }
                    long currentDateTime = System.currentTimeMillis();

                    final FirebaseMessageModel firebaseMessageModel = new FirebaseMessageModel();
                    firebaseMessageModel.setText(":נפתחה קבוצה חדשה על ידי"+""+current_name);
                    firebaseMessageModel.setSenderId(current_uid);
                    firebaseMessageModel.setSenderName(current_name);
                    firebaseMessageModel.setReceiverId(get_msg_uid);
                    firebaseMessageModel.setImage("default");
                    firebaseMessageModel.setStatus("false");
                    firebaseMessageModel.setCreatedDate(currentDateTime);
                    firebaseMessageModel.setRecord("default");

                    ProgressDialog Dialog = new ProgressDialog(AddGroupAc.this);
                    Dialog.setMessage("Please wait..");
                    Dialog.setCancelable(false);
                    Dialog.show();

                    final DatabaseReference newRef = mMsgDatabase.push();
                    newRef.setValue(firebaseMessageModel, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {



                            uidMsg = databaseReference.getKey();

                            if (!imgLocalPath.equals("default")){
                                loadimageFirebase(uidMsg, get_msg_uid);

                            }

                            dbHelper.SaveDBSqliteUser(nameGroup, firebaseUserModel.getCamp(), current_uid, imgLocalPath, "default", "default", "default", "default", get_msg_uid);



                            mprogress.dismiss();

                            Intent intent = new Intent(AddGroupAc.this,ChatAc.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                    });

                }


            }
        });



        addBtnfirendAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stringObjectHashMapAddGroup.put(current_uid, firebaseUserModel.getName());

                android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(AddGroupAc.this);
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

                        for (int i = 0; i < mUserItems.size(); i++) {
                            num = 2;

                            listItemString = listItems[mUserItems.get(i)];
                            listItemKeyString = listItemsKey[mUserItems.get(i)];

                            stringObjectHashMapAddGroup.put(listItemKeyString, listItemString);
                            stringObjectHashMapAddGroup.put(current_uid, current_name);


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
        });


    }




    public void changePicAddGroup(View view) {

        PermissionManager.check(AddGroupAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(AddGroupAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(AddGroupAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Profile Photo");
        alertDialog.setMessage("Please Pick A Method");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Gallery", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                gallery();

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {

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
                Picasso.get().load(resultUri).error(R.drawable.admin_btn_logo).into(imageAddGroup);
                imgLocalPath = String.valueOf(resultUri);

                filePath = mImageStorage.child("profile_images").child(resultUri.getLastPathSegment());


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(AddGroupAc.this, "error", Toast.LENGTH_LONG).show();

            }
        }

    }

    public void loadimageFirebase(String uidMsg, String getchat) {
        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        stringUrl = String.valueOf(uri);

                        if (stringUrl != null) {


                            mMsgDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(getchat).child(uidMsg);

                            Map<String, Object> mapCampsUpdates = new HashMap<>();
                            mapCampsUpdates.put("image", stringUrl);


                            mMsgDatabase.updateChildren(mapCampsUpdates);

                        }

                    }
                });
            }
        });
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.i(TAG, "Exception while hiding keyboard");
        }
    }


    public void getNamesUsersFromFireBase() {


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference discussionRoomsRef = rootRef.child("Users");



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

                        if (!key.equals(current_uid)){
                            String name = ds.child("name").getValue().toString();


                            contactsArray.add(name);
                            keyArray.add(key);
                        }else {

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

}
