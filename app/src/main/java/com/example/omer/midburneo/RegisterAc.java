package com.example.omer.midburneo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.Tabs.ProfileAc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterAc extends AppCompatActivity {

    private EditText name, email, pass, num;
    private Button registerButton;
    private CircleImageView imageView;

    public String getName, getEmail, getPass, getNum, stringUrl, current_uid, image, timeString;

    private FirebaseAuth mAuth;
    private ProgressDialog mprogress;
    private DatabaseReference mDatabase;
    private StorageReference mImageStorage, filePath;

    private Uri resultUri;
    public DBHelper mDbHelper;

    public static SharedPreferences prefs;
    public static String SHPRF = "User";

    public static final int CAMERA = 1, GALLERY = 2, WRITE_STORAGE = 3, REQUEST_PHONE_CALL = 4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDbHelper = new DBHelper(this);

        name = findViewById(R.id.nameField);
        email = findViewById(R.id.emailFieldReg);
        pass = findViewById(R.id.textPasswordReg);
        num = findViewById(R.id.textNumReg);
        registerButton = findViewById(R.id.registerButton);
        imageView = findViewById(R.id.buttonImageRegister);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        mAuth = FirebaseAuth.getInstance();


        mprogress = new ProgressDialog(this);

        long currentDateTime = System.currentTimeMillis();
        timeString = String.valueOf(currentDateTime);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterBtnFunc();
            }
        });
    }


    public void RegisterBtnFunc() {
        getName = name.getText().toString();
        getEmail = email.getText().toString();
        getPass = pass.getText().toString();
        getNum = num.getText().toString();

        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        prefs.edit().putString("email", "register").apply();


        if (!TextUtils.isEmpty(getName) && !TextUtils.isEmpty(getEmail) && !TextUtils.isEmpty(getPass) ) {//&& getNum.trim().length()>123456789


            mprogress.setMessage("Signing Up");
            mprogress.show();

            mAuth.createUserWithEmailAndPassword(getEmail, getPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                current_uid = current_user.getUid();

                                DBHelper mDbHelper = new DBHelper(getApplicationContext());

                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                                if (stringUrl == null) {
                                    image = "default";
                                } else {
                                    image = stringUrl;

                                }

                                HashMap<String, String> userMap = new HashMap<>();

                                userMap.put(FeedReaderContract.FeedEntry.NAME, getName);
                                userMap.put(FeedReaderContract.FeedEntry.EMAIL, getEmail);
                                userMap.put(FeedReaderContract.FeedEntry.PASSWORD, getPass);
                                userMap.put(FeedReaderContract.FeedEntry.NUMBER, "default");
                                userMap.put(FeedReaderContract.FeedEntry.IMAGE, image);
                                userMap.put(FeedReaderContract.FeedEntry.ADMIN, "default");
                                userMap.put(FeedReaderContract.FeedEntry.CHAT, "default");
                                userMap.put(FeedReaderContract.FeedEntry.CAMPS, "default");
                                userMap.put(FeedReaderContract.FeedEntry.STATUS, "status");
                                userMap.put(FeedReaderContract.FeedEntry.TIME, timeString);
                                userMap.put(FeedReaderContract.FeedEntry.LASTMSG, "default");
                                userMap.put(FeedReaderContract.FeedEntry.UID, current_uid);
                                userMap.put(FeedReaderContract.FeedEntry.ROLE, "אין תפקיד");
                                userMap.put(FeedReaderContract.FeedEntry.ONLINE, "true");
                                userMap.put(FeedReaderContract.FeedEntry.PHONE, getNum);

                                mDatabase.setValue(userMap);

                                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                                ContentValues values = new ContentValues();
                                values.put(FeedReaderContract.FeedEntry.ADMIN, "default");
                                values.put(FeedReaderContract.FeedEntry.CAMPS, "default");
                                values.put(FeedReaderContract.FeedEntry.CHAT, "default");
                                values.put(FeedReaderContract.FeedEntry.EMAIL, getEmail);
                                values.put(FeedReaderContract.FeedEntry.IMAGE, image);
                                values.put(FeedReaderContract.FeedEntry.NAME, getName);
                                values.put(FeedReaderContract.FeedEntry.NUMBER, "default");
                                values.put(FeedReaderContract.FeedEntry.PASSWORD, getPass);
                                values.put(FeedReaderContract.FeedEntry.STATUS, "default");
                                values.put(FeedReaderContract.FeedEntry.TIME, timeString);
                                values.put(FeedReaderContract.FeedEntry.UID_id, "1");
                                values.put(FeedReaderContract.FeedEntry.UID, current_uid);
                                values.put(FeedReaderContract.FeedEntry.LASTMSG, "default");
                                values.put(FeedReaderContract.FeedEntry.ROLE, "אין תפקיד");
                                values.put(FeedReaderContract.FeedEntry.PHONE, getNum);



                                db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

                                prefs.edit().putString("name", getName).apply();
                                prefs.edit().putString("image", image).apply();




                            }

                        }
                    });


        }else {
            if (getNum.trim().length()>123456789){
                Toast.makeText(RegisterAc.this, "הכנס מספר פלאפון תקין", Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(RegisterAc.this, "מלא את השדות החסרים", Toast.LENGTH_LONG).show();

            }



        }
    }

    public  void onImg(View view) {

        PermissionManager.check(RegisterAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(RegisterAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(RegisterAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
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
                resultUri = result.getUri();

                filePath = mImageStorage.child("profile_images").child(resultUri.getLastPathSegment());

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                stringUrl = String.valueOf(uri);

                                if (stringUrl == null) {
                                    mprogress.dismiss();
                                    return;

                                } else {
                                    Picasso.get().load(stringUrl).error(R.drawable.admin_btn_logo).into(imageView);
                                    mprogress.dismiss();
                                    Toast.makeText(RegisterAc.this, "Success", Toast.LENGTH_LONG).show();


                                }

                            }
                        });
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(RegisterAc.this, "error", Toast.LENGTH_LONG).show();

            }
        }


    }


}
