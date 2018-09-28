package com.example.omer.midburneo.Tabs;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.SHPRF;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;

public class AdminAc extends AppCompatActivity {

    private static final String TAG = "AdminAc";


    public EditText statusFieldAdmin;
    private TextView tvNameAdmin;
    public Button saveButton;
    private CircleImageView circleImageView;

    public SharedPreferences prefs;

    public String current_uid, current_camp, current_camp_uid, camp, status, date, title, content, image, stringUrl;
    public long currentDateTime;

    private DatabaseReference mUserDatabase;

    public DBHelper dbHelper;
    public SQLiteDatabase db;


    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;
    private StorageReference filrpath;


    private ProgressDialog mprogress;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_admin);

        circleImageView = findViewById(R.id.imageProfileAdmin);
        tvNameAdmin = findViewById(R.id.tvNameAdmin);
        statusFieldAdmin = findViewById(R.id.statusFieldAdmin);
        saveButton = findViewById(R.id.saveBtnAdmin);

        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        current_camp = prefs.getString("camps", null);
        current_camp_uid = prefs.getString("chat", null);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbHelper = new DBHelper(this);
        mprogress = new ProgressDialog(this);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        getProfile();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = statusFieldAdmin.getText().toString();
                camp = tvNameAdmin.getText().toString();


                if (!status.equals("") && !camp.equals("")) {


                    if (!stringUrl.equals(null)) {
                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_camp_uid);

                        Map<String, Object> mapUserUpdates = new HashMap<>();
                        mapUserUpdates.put("name", camp);
                        mapUserUpdates.put("status", status);
                        mapUserUpdates.put("camps", camp);
                        mapUserUpdates.put("image", stringUrl);

                        mUserDatabase.updateChildren(mapUserUpdates);

                       // prefs.edit().putString("image", image).apply();

                        if (!current_camp.equals(camp)) {
                            prefs.edit().putString("camps", camp).apply();

                            // firebase

                        }
                        Toast.makeText(AdminAc.this, "נשמר בהצלחה", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(AdminAc.this, "עדכן תמונה שוב", Toast.LENGTH_LONG).show();

                    }


                } else {
                    Toast.makeText(AdminAc.this, "מלא פרטים חסרים", Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    public void onClickNameUserAdmin(View view) {

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
                tvNameAdmin.setText(nameUser);


            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "חזור", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                return;

            }
        });

        alertDialog.show();
    }

    public void changePicAdmin(View view) {

        PermissionManager.check(AdminAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(AdminAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(AdminAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

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

                                    mProgressDialog.dismiss();
                                    stringUrl = String.valueOf(uri);


                                    Picasso.get().load(uri).error(R.drawable.admin_btn_logo).into(circleImageView);
                                    Toast.makeText(AdminAc.this, "Success", Toast.LENGTH_LONG).show();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });


                        } else {

                            Toast.makeText(AdminAc.this, "not", Toast.LENGTH_LONG).show();


                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(AdminAc.this, "error", Toast.LENGTH_LONG).show();

            }
        }


    }

    public void getProfile() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_camp_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                camp = dataSnapshot.child("camps").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();

                tvNameAdmin.setText(camp);
                statusFieldAdmin.setText(status);

                if (image.equals("default") || image == null) {
                    Picasso.get().load(image).error(R.drawable.admin_btn_logo).into(circleImageView);

                } else {

                    Picasso.get().load(image).error(R.drawable.admin_btn_logo).into(circleImageView);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
