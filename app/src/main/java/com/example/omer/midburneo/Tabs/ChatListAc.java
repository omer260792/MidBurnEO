package com.example.omer.midburneo.Tabs;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.Adapters.MessagesListAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.FirebaseMessageModel;
import com.example.omer.midburneo.Class.MessageCell;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.omer.midburneo.Adapters.MessagesListAdapter.VIEW_TYPE_PICTURE;
import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.REQUEST_PHONE_RECORD;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;


public class ChatListAc extends AppCompatActivity {
    private static final String TAG = "ChattingActivity";


    ListView listView;
    EditText textComment;
    private ImageView btnSend, imagebtnChat;
    private CircleImageView imageUser;
    TextView tvNameUser, tvTimeUser;
    private Button mRecordButton;

    List<FirebaseMessageModel> messages = new ArrayList<FirebaseMessageModel>();

    FirebaseDatabase database;
    DatabaseReference messagesRef;
    DatabaseReference usersRef;
    private DatabaseReference mUserDatabase;
    public SharedPreferences prefs;
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private Uri resultUri;
    private StorageReference mImageStorage, filePath;

    private MediaRecorder mRecorder;
    private ProgressDialog progressDialog;
    private boolean permissionToRecordAccepted = false;
    private String mFileName = null;


    public static ChatListAc chatListAc;
    public String nameUserIntent, campUserIntent, uidUserIntent, chatRoomsUserIntent, imageUserIntent, statusUserIntent, deviceUserIntent, tokenUserIntent, countUserIntent, timeUserIntent, onilneUserIntent, current_image, current_uid, current_name, current_device, table;
    public String stringUrl = "stringUrl";
    public int num = 1;

    JSONArray registration_ids = new JSONArray();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chtting_layout);


        listView = (ListView) findViewById(R.id.chattingList);
        textComment = (EditText) findViewById(R.id.comment_text);
        btnSend = (ImageView) findViewById(R.id.send_button);
        imagebtnChat = (ImageView) findViewById(R.id.imagebtnChat);
        tvNameUser = findViewById(R.id.tvNameChat);
        tvTimeUser = findViewById(R.id.tvTimeChat);
        imageUser = (CircleImageView) findViewById(R.id.imgUserChat);
        mRecordButton = findViewById(R.id.recored_button);

        nameUserIntent = getIntent().getStringExtra("nameUidFriend");
        campUserIntent = getIntent().getStringExtra("campUidFriend");
        uidUserIntent = getIntent().getStringExtra("receiverUidFriend");
        imageUserIntent = getIntent().getStringExtra("imageUidFriend");
        statusUserIntent = getIntent().getStringExtra("statusUidFriend");
        countUserIntent = getIntent().getStringExtra("countUidFriend");
        timeUserIntent = getIntent().getStringExtra("timeUidFriend");
        onilneUserIntent = getIntent().getStringExtra("onilneUidFriend");
        deviceUserIntent = getIntent().getStringExtra("deviceUidFriend");
        tokenUserIntent = getIntent().getStringExtra("tokenUidFriend");
        chatRoomsUserIntent = getIntent().getStringExtra("chatRoomsUidFriend");

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mImageStorage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        usersRef = database.getReference("Users");
        messagesRef = database.getReference("ChatRooms").child(chatRoomsUserIntent);

        progressDialog = new ProgressDialog(this);
        dbHelper = new DBHelper(getApplicationContext());
        chatListAc = this;


        tvNameUser.setText(nameUserIntent);


        btnSend.setEnabled(false);
        btnSend.setColorFilter(getResources().getColor(android.R.color.darker_gray));


        if (imageUserIntent == null || imageUserIntent == "default") {
            Picasso.get().load(R.drawable.midburn_logo).error(R.drawable.midburn_logo).into(imageUser);
        } else {
            Picasso.get().load(imageUserIntent).error(R.drawable.midburn_logo).into(imageUser);
        }

        CheckUserIfOnline();


        //*******************888888888888888888************
        //## need to add permision file
        /// ActivityCompat.requestPermissions(ChatListAc.this, permissions, REQUEST_PHONE_RECORD);
        PermissionManager.check(ChatListAc.this, Manifest.permission.RECORD_AUDIO, REQUEST_PHONE_RECORD);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        mRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    stopRecording();

                }


                return false;
            }
        });

        textComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    btnSend.setEnabled(false);
                    btnSend.setColorFilter(getResources().getColor(android.R.color.darker_gray));
                } else {
                    btnSend.setEnabled(true);
                    btnSend.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        });

        final ProgressDialog Dialog = new ProgressDialog(this);
        Dialog.setMessage("Please wait..");
        Dialog.setCancelable(false);
        Dialog.show();

        final com.google.firebase.database.ValueEventListener commentValueEventListener = new com.google.firebase.database.ValueEventListener() {

            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                messages.clear();


                for (com.google.firebase.database.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("Child: " + postSnapshot);
                    //Getting the data from snapshot
                    FirebaseMessageModel firebaseMessageModel = postSnapshot.getValue(FirebaseMessageModel.class);
                    firebaseMessageModel.setId(postSnapshot.getKey());

                    String sender = postSnapshot.child("senderId").getValue().toString();
                    String receiver = postSnapshot.child("receiverId").getValue().toString();
                    String image = postSnapshot.child("image").getValue().toString();


                    if (image.equals("default")) {
                        VIEW_TYPE_PICTURE = 2;

                    }

                    firebaseMessageModel.setId(postSnapshot.getKey());

                    messages.add(firebaseMessageModel);
                }

                updateListView();

                if (Dialog.isShowing()) {
                    Dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                messages.clear();
                updateListView();

                if (Dialog.isShowing()) {
                    Dialog.dismiss();
                }
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        };


        final com.google.firebase.database.ValueEventListener userValueEventListener = new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                registration_ids = new JSONArray();

                for (com.google.firebase.database.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("Child: " + postSnapshot);
                    //Getting the data from snapshot
                    try {

                        String name = postSnapshot.child(FeedReaderContract.FeedEntry.NAME).getValue().toString();
                        String device = postSnapshot.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID).getValue().toString();
                        String getUid = postSnapshot.getKey();
                        String token = postSnapshot.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN).getValue().toString();


                        if (firebaseUserModel.getCamp().equals(nameUserIntent)) {


                            if (!device.equals("default") && !token.equals("default")) {

                                registration_ids.put(token);
                                System.out.println("Good: ");

                            }


                        } else if (getUid.equals(uidUserIntent)) {


                            if (!device.equals(current_device) && !token.isEmpty()) {
                                registration_ids.put(token);
                                System.out.println("Good: ");

                            }
                        } else {

                        }

                    } catch (Exception e) {

                    }


                }

                if (Dialog.isShowing()) {
                    Dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                registration_ids = new JSONArray();

                if (Dialog.isShowing()) {
                    Dialog.dismiss();
                }
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        };

        usersRef.addValueEventListener(userValueEventListener);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                sendMsgWithNotification();

                messagesRef.addValueEventListener(commentValueEventListener);

            }
        });

        //Value event listener for realtime data update
        messagesRef.addValueEventListener(commentValueEventListener);
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) chatListAc.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(chatListAc.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.i(TAG, "Exception while hiding keyboard");
        }
    }

    public void updateListView() {
        Log.i(TAG, "Inside prepareWishList()");

        int totalWishes = messages.size();

        Log.i(TAG, "Total Wishes : " + totalWishes);

        MessageCell[] messageCells;
        messageCells = new MessageCell[totalWishes];

        for (int counter = 0; counter < totalWishes; counter++) {
            final FirebaseMessageModel firebaseMessageModel = messages.get(counter);

            MessageCell messageCell = new MessageCell(firebaseMessageModel.getSenderName(), firebaseMessageModel.getText(),
                    getDate(firebaseMessageModel.getCreatedDateLong()), firebaseMessageModel.getSenderId().equals(current_uid), firebaseMessageModel.getImage());

            messageCells[counter] = messageCell;
        }

        MessagesListAdapter adapter = new MessagesListAdapter(this, messageCells);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setSelection(listView.getCount() - 1);

        listView.requestFocus();
    }


    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy, hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void CheckUserIfOnline() {

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uidUserIntent);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String time = dataSnapshot.child("time").getValue().toString();
                String onilne = dataSnapshot.child("online").getValue().toString();

                if (!onilne.equals("default")) {
                    DateFormat getTimeDmY = new SimpleDateFormat("dd:MM:yyyy:HH:mm");
                    long timeMilLong = Long.parseLong(time);

                    String realTime = getTimeDmY.format(timeMilLong);

                    if (onilne.equals("true")) {
                        tvTimeUser.setText("מחובר");
                    } else {
                        tvTimeUser.setText("last seen:" + realTime);

                    }
                } else {
                    tvTimeUser.setText("");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void loadImagebtn(View view) {

        PermissionManager.check(ChatListAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
        PermissionManager.check(ChatListAc.this, android.Manifest.permission.CAMERA, CAMERA);
        PermissionManager.check(ChatListAc.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

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


                filePath = mImageStorage.child("msg_images").child(resultUri.getLastPathSegment());

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                stringUrl = String.valueOf(uri);

                                if (stringUrl != null) {

                                    hideKeyboard();
                                    sendMsgWithNotification();


                                }

                            }
                        });
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(ChatListAc.this, "error", Toast.LENGTH_LONG).show();

            }
        }


    }

    public void sendMsgWithNotification() {

        final String wishMessage = textComment.getText().toString().trim();
        if (stringUrl.equals("stringUrl") && wishMessage.isEmpty()) {
            return;
        } else {
            // send text as wish

            if (stringUrl.equals("stringUrl")) {

                current_image = "default";
            } else {
                current_image = stringUrl;
            }

            final FirebaseMessageModel firebaseMessageModel = new FirebaseMessageModel();
            firebaseMessageModel.setText(wishMessage);
            firebaseMessageModel.setSenderId(current_uid);
            firebaseMessageModel.setSenderName(current_name);
            firebaseMessageModel.setReceiverId(uidUserIntent);
            firebaseMessageModel.setImage(current_image);
            firebaseMessageModel.setStatus("false");

            updateListView();


            final ProgressDialog Dialog = new ProgressDialog(chatListAc);
            Dialog.setMessage("Please wait..");
            Dialog.setCancelable(false);
            Dialog.show();

            final DatabaseReference newRef = messagesRef.push();
            newRef.setValue(firebaseMessageModel, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.i(TAG, databaseError.toString());
                    } else {
                        textComment.setText("");

                        long currentDateTime = System.currentTimeMillis();

                        String time = String.valueOf(currentDateTime);
                        if (nameUserIntent.equals(firebaseUserModel.getCamp())) {

                            table = current_uid;

                        } else {
                            table = uidUserIntent;

                        }
                        // need to find uid msg
                        dbHelper.SaveDBSqliteMsgUser(wishMessage, uidUserIntent, current_uid, current_name, time, time, "false", table);

                        if (registration_ids.length() > 0) {


                            String url = "https://fcm.googleapis.com/fcm/send";
                            AsyncHttpClient client = new AsyncHttpClient();

                            client.addHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyDV08bsa0Cdtnzt4EJkm29qsvs-3giVFbc");
                            client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

                            try {


                                JSONObject params = new JSONObject();

                                params.put("registration_ids", registration_ids);

                                JSONObject notificationObject = new JSONObject();
                                notificationObject.put("body", wishMessage);
                                notificationObject.put("title", "MidBurnEO");

                                params.put("notification", notificationObject);

                                StringEntity entity = new StringEntity(params.toString());

                                Log.i(TAG, String.valueOf(entity));
                                Log.i(TAG, params.toString());
                                Log.i(TAG, String.valueOf(notificationObject));

                                client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                                        Dialog.dismiss();
                                        Log.i(TAG, responseString);


                                    }

                                    @Override
                                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                                        Dialog.dismiss();
                                        Log.i(TAG + "succccc", responseString);

                                        Log.i(TAG, String.valueOf(statusCode));
                                        Log.i(TAG, String.valueOf(headers));
                                    }
                                });

                            } catch (Exception e) {

                            }
                        }

                    }
                    Dialog.dismiss();

                }
            });
            num = 2;

        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileName);


        try {
            mRecorder.prepare();
            mRecorder.start();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }

    private void uploadAudio() {
        progressDialog.setMessage("מעלה הקלטה");
        progressDialog.show();

        StorageReference filePath = mImageStorage.child("Audio").child("new_audio.3gp");

        Uri uri = Uri.fromFile(new File(mFileName));
        filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressDialog.dismiss();
                progressDialog.setMessage("סיים לעלות הקלטה");

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PHONE_RECORD:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }


}


