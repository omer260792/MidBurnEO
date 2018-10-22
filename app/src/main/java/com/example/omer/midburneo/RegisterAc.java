package com.example.omer.midburneo;

// done ido

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.FirebaseUserModel;
import com.example.omer.midburneo.Class.User;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.Tabs.ProfileAc;
import com.example.omer.midburneo.Utils.UtilHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterAc extends AppCompatActivity {

    private EditText name, email, pass, num;
    private Button registerButton;
    private CircleImageView imageView;

    public String getName, getEmail, getPass, getNum, stringUrl, current_uid, image, timeString, currentDeviceId, currentToken;

    private FirebaseAuth mAuth;
    private FirebaseUser current_user;
    private ProgressDialog mprogress;
    private DatabaseReference mUserDatabase;
    private StorageReference mImageStorage, filePath;

    private Uri resultUri;
    public DBHelper mDbHelper;

    public static SharedPreferences prefs;
    public static String SHPRF = "User";
    private FirebaseUserModel firebaseUserModel;

    public static final int CAMERA = 1, GALLERY = 2, WRITE_STORAGE = 3, REQUEST_PHONE_CALL = 4, REQUEST_PHONE_RECORD = 5;
    User user = User.getInstance();


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
        // filePath = FirebaseStorage.getInstance().getReference();

        currentDeviceId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        mAuth = FirebaseAuth.getInstance();

        mprogress = new ProgressDialog(this);

        firebaseUserModel = new FirebaseUserModel();


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

        currentToken = FirebaseInstanceId.getInstance().getToken();
        firebaseUserModel.setDeviceToken(currentToken);
        firebaseUserModel.setDeviceId(currentDeviceId);


        long currentDateTime = System.currentTimeMillis();
        timeString = String.valueOf(currentDateTime);


        if (!TextUtils.isEmpty(getName) && !TextUtils.isEmpty(getEmail) && !TextUtils.isEmpty(getPass) && getNum.trim().length() == 10) {

            SharedPreferences sharedpreferences = getSharedPreferences(user.appPreferences, Context.MODE_PRIVATE);
            user.sharedpreferences = sharedpreferences;
            user.login(firebaseUserModel);


            prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);

            prefs.edit().putString("name", getName).apply();
            prefs.edit().putString("email", "register").apply();

            this.mprogress.setMessage("Signing Up");
            this.mprogress.show();

            mAuth.createUserWithEmailAndPassword(getEmail, getPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                current_user = FirebaseAuth.getInstance().getCurrentUser();
                                current_uid = current_user.getUid();

                                imgUpload();

                                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


                                HashMap<String, String> userMap = new HashMap<>();


                                userMap.put(FeedReaderContract.FeedEntry.NAME, getName);
                                userMap.put(FeedReaderContract.FeedEntry.EMAIL, getEmail);
                                userMap.put(FeedReaderContract.FeedEntry.PASSWORD, getPass);
                                userMap.put(FeedReaderContract.FeedEntry.NUMBER, "default");
                                userMap.put(FeedReaderContract.FeedEntry.IMAGE, "default");
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
                                userMap.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID, currentDeviceId);
                                userMap.put(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN, currentToken);

                                mUserDatabase.setValue(userMap);


                            }

                        }
                    });


        } else {
            if (getNum.trim().length() != 10) {
                Toast.makeText(RegisterAc.this, "הכנס מספר פלאפון תקין", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(RegisterAc.this, "מלא את השדות החסרים", Toast.LENGTH_LONG).show();

            }


        }


    }

    public void onImg(View view) {

        PermissionManager.check(RegisterAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(RegisterAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(RegisterAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

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

                Picasso.get().load(resultUri).error(R.drawable.admin_btn_logo).into(imageView);


                filePath = mImageStorage.child("profile_images").child(resultUri.getLastPathSegment());
                File file = UtilHelper.getTempFile(RegisterAc.this, String.valueOf(resultUri));


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(RegisterAc.this, "error", Toast.LENGTH_LONG).show();

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

                        stringUrl = String.valueOf(uri);

                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                        Map<String, Object> mapCampsUpdates = new HashMap<>();
                        mapCampsUpdates.put(FeedReaderContract.FeedEntry.IMAGE, stringUrl);

                        mUserDatabase.updateChildren(mapCampsUpdates);
                        mprogress.dismiss();


                    }
                });
            }
        });


    }


}


