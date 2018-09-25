package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.LoginAc;
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;

public class ProfileAc extends AppCompatActivity {

    private DatabaseReference mUserDatabase;

    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;
    private StorageReference filrpath;

    private String image = "imageDefault";
    private String name, camp, status, stringUrl, current_uid;
    private TextView txtViewName;
    private EditText editTxtCamp, editTxtStatus;
    private Button signOutBtn, saveBtn;
    private CircleImageView imageView;


    SharedPreferences prefs;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile);


        txtViewName = (TextView) findViewById(R.id.txtViewName);
        editTxtStatus = (EditText) findViewById(R.id.statusFieldProfile);
        editTxtCamp = (EditText) findViewById(R.id.campFieldProfile);
        signOutBtn = (Button) findViewById(R.id.signOutBtn);
        saveBtn = (Button) findViewById(R.id.saveBtnProfile);
        imageView = (CircleImageView) findViewById(R.id.profile_image);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mImageStorage = FirebaseStorage.getInstance().getReference();


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignOutProfile();

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveProfile();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();
                camp = dataSnapshot.child("camps").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();

                txtViewName.setText(name);
                editTxtCamp.setText(camp);
                editTxtStatus.setText(status);

                if (image.equals("default") || image == null) {
                    Picasso.get().load(R.drawable.cmv_arrow).error(R.drawable.admin_btn_logo).into(imageView);

                } else {
                    Picasso.get().load(image).error(R.drawable.admin_btn_logo).into(imageView);

                    prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                    prefs.edit().putString("image", image).apply();


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void changePic(View view) {

        PermissionManager.check(ProfileAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(ProfileAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(ProfileAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

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


//        Intent galleryIntent = new Intent();
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY);

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { ////clear final in intent
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                //Progress Dialog
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                filrpath = mImageStorage.child("profile_images").child(resultUri.getLastPathSegment());//(random() + ".jpg")


                filrpath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {


                            filrpath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    stringUrl = String.valueOf(uri);


                                    Picasso.get().load(stringUrl).error(R.drawable.admin_btn_logo).into(imageView);


                                    mUserDatabase.child("url").setValue(stringUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                mProgressDialog.dismiss();


                                                mUserDatabase.child("image").setValue(stringUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            mProgressDialog.dismiss();

                                                            // mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNum);


                                                        } else {

                                                            Toast.makeText(ProfileAc.this, "Somesing Wrong Happend", Toast.LENGTH_LONG).show();

                                                        }

                                                    }
                                                });


                                            } else {

                                                Toast.makeText(ProfileAc.this, "Somesing Wrong Happend", Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });


                        } else {

                            Toast.makeText(ProfileAc.this, "not", Toast.LENGTH_LONG).show();


                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(ProfileAc.this, "error", Toast.LENGTH_LONG).show();

            }
        }

//        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(camp).child(current_uid);
//
//        Map<String, Object> mapUserUpdates1 = new HashMap<>();
//        mapUserUpdates1.put("image", stringUrl);
//
//
//
//        mUserDatabase.updateChildren(mapUserUpdates1);
    }


    public void SaveProfile() {

        name = txtViewName.getText().toString();
        camp = editTxtCamp.getText().toString();
        status = editTxtStatus.getText().toString();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        Map<String, Object> mapUserUpdates = new HashMap<>();
        mapUserUpdates.put("name", name);
        mapUserUpdates.put("status", status);
        mapUserUpdates.put("camps", camp);


        mUserDatabase.updateChildren(mapUserUpdates);


        Toast.makeText(ProfileAc.this, "נשמר בהצלחה", Toast.LENGTH_LONG).show();

    }

    public void SignOutProfile() {

        long currentDateTime = System.currentTimeMillis();
        String timeString = String.valueOf(currentDateTime);


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        Map<String, Object> mapCampsUpdates = new HashMap<>();
        mapCampsUpdates.put("time", timeString);
        mapCampsUpdates.put("status", "false");

        mUserDatabase.updateChildren(mapCampsUpdates);


        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ProfileAc.this, LoginAc.class));
        finish();
    }
}


