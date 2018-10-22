package com.example.omer.midburneo.Tabs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.LoginAc;
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Utils.UtilHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.Tabs.MainPageAc.SHPRF;
import static com.example.omer.midburneo.Tabs.MainPageAc.prefs;;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;

import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;

public class ProfileAc extends AppCompatActivity {

    private static final String TAG = "ProfileAc";


    private DatabaseReference mUserDatabase;

    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;
    private StorageReference filrpath;

    private String image = "imageDefault";
    private String name, role, status, stringUrl, current_uid;
    private TextView txtViewName;
    private EditText editTxtRole, editTxtStatus;
    private Button signOutBtn, saveBtn;
    private CircleImageView imageView;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile);


        txtViewName = (TextView) findViewById(R.id.txtViewName);
        editTxtStatus = (EditText) findViewById(R.id.statusFieldProfile);
        editTxtRole = (EditText) findViewById(R.id.roleFieldProfile);
        signOutBtn = (Button) findViewById(R.id.signOutBtn);
        saveBtn = (Button) findViewById(R.id.saveBtnProfile);
        imageView = (CircleImageView) findViewById(R.id.profile_image);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mImageStorage = FirebaseStorage.getInstance().getReference();


        txtViewName.setText(firebaseUserModel.getName());
        editTxtRole.setText(firebaseUserModel.getRole());
        editTxtStatus.setText(firebaseUserModel.getStatus());



        Picasso.get().load(firebaseUserModel.getImage()).error(R.drawable.midburn_logo).into(imageView);


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


                Picasso.get().load(resultUri).error(R.drawable.midburn_logo).into(imageView);

                String imageS = String.valueOf(resultUri);
                firebaseUserModel.setImage(imageS);

                //Progress Dialog
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                File file = UtilHelper.getTempFile(ProfileAc.this, String.valueOf(resultUri));
                File file1 = UtilHelper.getPrivateAlbumStorageDir(ProfileAc.this,"name");

                firebaseUserModel.setImage(String.valueOf(file));
               // firebaseUserModel.setImage(String.valueOf(file1));
                prefs.edit().putString(FeedReaderContract.FeedEntry.IMAGE, String.valueOf(resultUri)).apply();

                filrpath = mImageStorage.child("profile_images").child(resultUri.getLastPathSegment());//(random() + ".jpg")


                filrpath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {


                            filrpath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    stringUrl = String.valueOf(uri);




                                    mProgressDialog.dismiss();
                                    Toast.makeText(ProfileAc.this, "התמונה העלתה בהצלחה", Toast.LENGTH_LONG).show();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });


                        } else {

                            Toast.makeText(ProfileAc.this, "לא הצליח לעלות תמונה", Toast.LENGTH_LONG).show();


                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(ProfileAc.this, "error", Toast.LENGTH_LONG).show();

            }
        }

    }


    public void SaveProfile() {

        name = txtViewName.getText().toString();
        role = editTxtRole.getText().toString();
        status = editTxtStatus.getText().toString();


        if (!name.equals("") && !role.equals("") && !status.equals("")) {

            if (!stringUrl.equals(null)) {
                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                Map<String, Object> mapUserUpdates = new HashMap<>();
                mapUserUpdates.put("name", name);
                mapUserUpdates.put("status", status);
                mapUserUpdates.put("role", role);
                mapUserUpdates.put("image", stringUrl);


                mUserDatabase.updateChildren(mapUserUpdates);


                Toast.makeText(ProfileAc.this, "נשמר בהצלחה", Toast.LENGTH_LONG).show();

                firebaseUserModel.setRole(role);
                firebaseUserModel.setStatus(status);
                firebaseUserModel.setName(name);

            } else {
                Toast.makeText(ProfileAc.this, "תעלה תמונה שוב", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ProfileAc.this, "מלא פרטים חסרים", Toast.LENGTH_LONG).show();
        }
    }

    public void SignOutProfile() {

        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        prefs.edit().putString("email", "default").apply();

        long currentDateTime = System.currentTimeMillis();
        String timeString = String.valueOf(currentDateTime);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        Map<String, Object> mapCampsUpdates = new HashMap<>();
        mapCampsUpdates.put("time", timeString);
        mapCampsUpdates.put("online", "false");
        mUserDatabase.updateChildren(mapCampsUpdates);

        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(ProfileAc.this, LoginAc.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public void onClickNameUser(View view) {


        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        final EditText input = new EditText(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);


        alertDialog.setTitle("עריכת שם משתמש");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "שמור שם ", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                String nameUser = input.getText().toString();
                txtViewName.setText(nameUser);


            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "חזור", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                return;

            }
        });

        alertDialog.show();
    }


}


