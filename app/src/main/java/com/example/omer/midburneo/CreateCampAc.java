package com.example.omer.midburneo;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.Tabs.MainPageAc;
import com.example.omer.midburneo.Tabs.ProfileAc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;

public class CreateCampAc extends AppCompatActivity {

    public EditText nameCampField, themeCampField;
    public Button confirmCreateCamp;
    private ImageView btnCoverCamp;
    public String camp_name, camptheme, name, current_uid, current_name, current_camp_user, randomNumString;
    public Uri imageUri;
    public String stringUrl = "default";
    private DatabaseReference mUserDatabase;
    private SharedPreferences prefs;
    private FirebaseDatabase database;
    private StorageReference mImageStorage, filePath;

    public String num = "1";
    public String numcount = "1";


    public SQLiteDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_camp);

        nameCampField = findViewById(R.id.nameCampField);
        themeCampField = findViewById(R.id.themeCampField);
        confirmCreateCamp = findViewById(R.id.registerButton);
        btnCoverCamp = findViewById(R.id.btnCoverCamp);

        random();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        confirmCreateCamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camptheme = themeCampField.getText().toString();
                camp_name = nameCampField.getText().toString();

                if (camp_name != null && camptheme != null) {

                    // trim name camp ????
//                    camp_name = camp_name.replace(" ", "");
//                    camp_name = camp_name.trim();

                    database = FirebaseDatabase.getInstance();

                    DatabaseReference mUserDatabase1 = database.getReference().child("Camps").child("AllCamps");

                    mUserDatabase1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                                Boolean check = dataSnapshot1.child(camp_name).exists();
                                if (check){


                                }else {

                                        numcount = "2";
                                }



                            }
                            if (numcount.equals("1")){
                                Toast.makeText(CreateCampAc.this, "The name allready exist", Toast.LENGTH_LONG).show();

                            }else {
                                SaveDBFireBase();

                                prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                                prefs.edit().putString("camps", camp_name).apply();

                                Intent intent = new Intent(CreateCampAc.this, MainPageAc.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();

                            }












                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }
        });
    }

    public void SaveDBFireBase() {


        current_camp_user = randomNumString;


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child(current_camp_user);
        Map<String, Object> mapCampsUpdates1 = new HashMap<>();
        mapCampsUpdates1.put("camptheme", camptheme);
        mapCampsUpdates1.put("name", camp_name);
        mapCampsUpdates1.put("chat", current_camp_user);
        mUserDatabase.updateChildren(mapCampsUpdates1);


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Camps").child("AllCamps");
        Map<String, Object> mapCampsUpdates = new HashMap<>();
        mapCampsUpdates.put(camp_name, current_camp_user);
        mUserDatabase.updateChildren(mapCampsUpdates);


        new Thread(new Runnable() {
            public void run() {

                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
                Map<String, Object> mapUserUpdates4 = new HashMap<>();
                mapUserUpdates4.put("camps", camp_name);
                mapUserUpdates4.put("admin", "admin");
                mapUserUpdates4.put("number", "1");
                mapUserUpdates4.put("chat", current_camp_user);
                mUserDatabase.updateChildren(mapUserUpdates4);


                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_camp_user);
                Map<String, Object> userMapCamp = new HashMap<>();


                userMapCamp.put(FeedReaderContract.FeedEntry.NAME, camp_name);
                userMapCamp.put(FeedReaderContract.FeedEntry.EMAIL, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.PASSWORD, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.NUMBER, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.IMAGE, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.ADMIN, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.CHAT, current_camp_user);
                userMapCamp.put(FeedReaderContract.FeedEntry.CAMPS, camp_name);
                userMapCamp.put(FeedReaderContract.FeedEntry.STATUS, "status");
                userMapCamp.put(FeedReaderContract.FeedEntry.TIME, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.LASTMSG, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.UID, current_uid);
                userMapCamp.put(FeedReaderContract.FeedEntry.ROLE, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.ONLINE, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.PHONE, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, "default");
                userMapCamp.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, "default");


                mUserDatabase.updateChildren(userMapCamp);


            }
        }).start();


    }


    public void random() {

        Random randomGenerator = new Random();

        long randomInt = randomGenerator.nextInt(1000000000);
        randomNumString = String.valueOf(randomInt);


    }

    public void onImg(View view) {
        PermissionManager.check(CreateCampAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(CreateCampAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(CreateCampAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

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

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { ////clear final in intent
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri resultUri = data.getData();

            CropImage.activity(resultUri)
                    .setAspectRatio(1, 1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Picasso.get().load(imageUri).error(R.drawable.admin_btn_logo).into(btnCoverCamp);

            }
        }
    }


    private void imgUpload() {

        if (imageUri == null) {
            return;
        }

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        stringUrl = String.valueOf(uri);

                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                        Map<String, Object> mapCampsUpdates = new HashMap<>();
                        mapCampsUpdates.put(FeedReaderContract.FeedEntry.IMAGE, stringUrl);

                        mUserDatabase.updateChildren(mapCampsUpdates);


                    }
                });
            }
        });


    }

}
