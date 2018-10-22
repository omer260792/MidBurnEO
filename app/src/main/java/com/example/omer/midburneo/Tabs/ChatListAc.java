package com.example.omer.midburneo.Tabs;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.omer.midburneo.Class.FirebaseUserModel;
import com.example.omer.midburneo.Class.MessageCell;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.PermissionManager;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Service.MyFirebaseMessagingService;
import com.example.omer.midburneo.Utils.UtilHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME;
import static com.example.omer.midburneo.Class.FeedReaderContract.FeedEntry.TABLE_NAME_MESSAGE;
import static com.example.omer.midburneo.DataBase.DBHelper.DATABASE_NAME;
import static com.example.omer.midburneo.RegisterAc.CAMERA;
import static com.example.omer.midburneo.RegisterAc.GALLERY;
import static com.example.omer.midburneo.RegisterAc.WRITE_STORAGE;
import static com.example.omer.midburneo.Tabs.MainPageAc.SHPRF;
import static com.example.omer.midburneo.Tabs.MainPageAc.firebaseUserModel;


public class ChatListAc extends AppCompatActivity {
    private static final String TAG = "ChatListAc";


    ListView listView;
    EditText textComment;
    private ImageView btnSend;
    private CircleImageView imageUser;
    TextView tvNameUser, tvTimeUser;
    private Button mRecordButton, imagebtnChat;

    List<FirebaseMessageModel> messages = new ArrayList<FirebaseMessageModel>();

    FirebaseDatabase database;
    private DatabaseReference messagesRef;
    DatabaseReference usersRef;
    private DatabaseReference mUserDatabase;
    public SharedPreferences prefs;
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private Uri resultUri, recordUri;
    private StorageReference mImageStorage, filePath, filePathRecord;

    private ProgressDialog progressDialog;

    public String nameUserIntent, campUserIntent, uidUserIntent, chatRoomsUserIntent, imageUserIntent, statusUserIntent, deviceUserIntent,
            tokenUserIntent, countUserIntent, timeUserIntent, onilneUserIntent, current_uid, current_name, current_device, table, wishMessage;
    public String stringUrl = "stringUrl";
    public String urlPathString = null;
    public String uidMsg;
    public int num = 1;
    public int numStop = 1;
    public int numTotal = 1;
    private int number = 1;
    private int numberFromPush = 1;
    private Boolean isConnected;
    public static String roomPush, uidPush, namePush, devicePush;


    JSONArray registration_ids = new JSONArray();

    ///record

    private Button buttonRecordPop, buttonStopPop, buttonPlayPop, sendRecordBtn;

    private String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    public CardView cardView;
    private int numCheck = 1;
    private int numPermisionChat = 1;

    public static String nameGroupSql;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_chatting);


        isConnected = UtilHelper.checkInternetConnection(this);
        numPermisionChat = 2;

        listView = (ListView) findViewById(R.id.chattingList);
        textComment = (EditText) findViewById(R.id.comment_text);
        btnSend = (ImageView) findViewById(R.id.send_button);
        imagebtnChat = findViewById(R.id.imagebtnChat);
        tvNameUser = findViewById(R.id.tvNameChat);
        tvTimeUser = findViewById(R.id.tvTimeChat);
        imageUser = (CircleImageView) findViewById(R.id.imgUserChat);
        mRecordButton = findViewById(R.id.recored_button);
        cardView = findViewById(R.id.CardViewChatlist);

        //record
        buttonRecordPop = findViewById(R.id.buttonRecordPop);//1
        buttonStopPop = findViewById(R.id.buttonStopPop);//2
        buttonPlayPop = findViewById(R.id.buttonPlayPop);//3
        sendRecordBtn = findViewById(R.id.sendRecordBtn);//4
        //  sendRecordBtn = myDialog.findViewById(R.id.sendRecordBtn);

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
        filePath = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();


        progressDialog = new ProgressDialog(this);
        dbHelper = new DBHelper(getApplicationContext());


        usersRef = database.getReference("Users");
        usersRef.orderByChild("chat").equalTo(firebaseUserModel.getChat());

        if (chatRoomsUserIntent == null) {

            chatRoomsUserIntent = roomPush;
            nameUserIntent = namePush;
            uidUserIntent = uidPush;
            deviceUserIntent = devicePush;
            Log.e(TAG + 1, nameUserIntent);
            Log.e(TAG + 2, chatRoomsUserIntent);
            FirebaseUserModel.getSPToFirebaseUserModel(firebaseUserModel, getApplicationContext());
            DATABASE_NAME = firebaseUserModel.getChat();
            TABLE_NAME_MESSAGE = firebaseUserModel.getUidReceiver();
            dbHelper = new DBHelper(getApplicationContext());


        } else {

        }

        if (imageUserIntent == null || imageUserIntent.equals("default")) {
            Picasso.get().load(R.drawable.midcamp_logo).error(R.drawable.midburn_logo).into(imageUser);
        } else {
            Picasso.get().load(imageUserIntent).error(R.drawable.midcamp_logo).into(imageUser);
        }


        messagesRef = database.getReference("ChatRooms").child(chatRoomsUserIntent);
        tvNameUser.setText(nameUserIntent);


        btnSend.setEnabled(false);
        btnSend.setColorFilter(getResources().getColor(android.R.color.darker_gray));

        CheckUserIfOnline();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//
//
//                CheckUserIfOnline();
//
//
//            }
//        }, 2000);   //


        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (numCheck == 1) {


                    cardView.setVisibility(View.VISIBLE);
                    buttonStopPop.setVisibility(View.GONE);
                    buttonPlayPop.setVisibility(View.GONE);
                    sendRecordBtn.setVisibility(View.GONE);
                    popUp();
                    numCheck = 2;
                } else {
                    cardView.setVisibility(View.GONE);
                    popUp();
                    numCheck = 1;
                }

            }
        });

        imagebtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermissionManager.check(ChatListAc.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY);
                PermissionManager.check(ChatListAc.this, android.Manifest.permission.CAMERA, CAMERA);
                PermissionManager.check(ChatListAc.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE);

//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {


                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder = new AlertDialog.Builder(ChatListAc.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ChatListAc.this);
                }
                builder.setTitle("עריכת מצלמה")
                        .setMessage("!בחר תמונה")
                        .setNegativeButton("חזור", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("גלרייה", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                gallery();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

//                    }
//                }, 500);   //


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


                // count sqlite messages == ount firebase on server
                for (com.google.firebase.database.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    FirebaseMessageModel firebaseMessageModel = postSnapshot.getValue(FirebaseMessageModel.class);
                    firebaseMessageModel.setId(postSnapshot.getKey());


                    String image = firebaseMessageModel.setImage((postSnapshot.child("image").getValue().toString()));
                    String sender = firebaseMessageModel.setSenderId(postSnapshot.child("senderId").getValue().toString());
                    String createDate = firebaseMessageModel.setCreatedDate(Long.valueOf(postSnapshot.child("createdDate").getValue().toString()));

                    String status = firebaseMessageModel.setStatus(postSnapshot.child("status").getValue().toString());
                    String record = firebaseMessageModel.setRecord(postSnapshot.child("record").getValue().toString());


                    firebaseMessageModel.setId(postSnapshot.getKey());

                    messages.add(firebaseMessageModel);
                    numTotal = number++;


                }
                String count = String.valueOf(numTotal);

                //save count of massage SharedPreferences
                if (numPermisionChat == 2) {
                    prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
                    prefs.edit().putString(chatRoomsUserIntent, count).apply();
                }


                numTotal = 0;
                number = 1;

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
                    //Getting the data from snapshot
                    try {

                        String name = postSnapshot.child(FeedReaderContract.FeedEntry.NAME).getValue().toString();
                        String device = postSnapshot.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_ID).getValue().toString();
                        String getUid = postSnapshot.getKey();
                        String token = postSnapshot.child(FeedReaderContract.FeedEntry.CURRENT_DEVICE_TOKEN).getValue().toString();


                        if (firebaseUserModel.getChat().equals(uidUserIntent)) {


                            if (!current_uid.equals(getUid)) {

                                registration_ids.put(token);
                                Log.e(TAG, "push camp" + registration_ids.toString());
                            }


                        } else if (getUid.equals(uidUserIntent)) {


                            if (!device.equals(current_device) && !token.isEmpty()) {

                                registration_ids.put(token);
                                Log.e(TAG, registration_ids.toString());
                                Log.e(TAG, "omer" + registration_ids.toString());

                            }


                        } else {
                            Log.e(TAG, "all camps" + registration_ids.toString());

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


//        Handler handlers = new Handler();
//        handlers.postDelayed(new Runnable() {
//            public void run() {
//
//                usersRef.addListenerForSingleValueEvent(userValueEventListener);
//
//
//            }
//        }, 2000);   //
        usersRef.addListenerForSingleValueEvent(userValueEventListener);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                sendMsgWithNotification();

                messagesRef.addValueEventListener(commentValueEventListener);


            }
        });


        messagesRef.addValueEventListener(commentValueEventListener);


        buttonRecordPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkPermission()) {

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonRecordPop.setEnabled(false);
                    buttonStopPop.setEnabled(true);
                    buttonStopPop.setVisibility(View.VISIBLE);

                    numStop = 1;

                    Toast.makeText(ChatListAc.this, "Recording started",
                            Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }


            }
        });

        buttonStopPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (numStop == 1) {
                    mediaRecorder.stop();
                    buttonStopPop.setEnabled(false);
                    buttonPlayPop.setEnabled(true);
                    buttonRecordPop.setEnabled(true);
                    sendRecordBtn.setEnabled(true);

                    Toast.makeText(ChatListAc.this, "Recording Completed",
                            Toast.LENGTH_SHORT).show();

                    buttonStopPop.setVisibility(View.VISIBLE);
                    buttonPlayPop.setVisibility(View.VISIBLE);
                    sendRecordBtn.setVisibility(View.VISIBLE);


                } else {

                    buttonStopPop.setEnabled(false);
                    buttonRecordPop.setEnabled(true);
                    sendRecordBtn.setEnabled(true);
                    buttonPlayPop.setEnabled(true);

                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        MediaRecorderReady();
                    }

                    buttonStopPop.setVisibility(View.VISIBLE);
                    buttonPlayPop.setVisibility(View.VISIBLE);
                    sendRecordBtn.setVisibility(View.VISIBLE);
                }


            }
        });

        buttonPlayPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonStopPop.setEnabled(false);
                buttonRecordPop.setEnabled(false);
                sendRecordBtn.setEnabled(true);


                numStop = 2;

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                mediaPlayer.start();
                Toast.makeText(ChatListAc.this, "Recording Playing",
                        Toast.LENGTH_SHORT).show();

            }
        });

        sendRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();


                    if (AudioSavePathInDevice != null) {
                        long currentDateTime = System.currentTimeMillis();
                        String time = String.valueOf(currentDateTime);
                        uploadAudio(AudioSavePathInDevice, time);
                        cardView.setVisibility(v.GONE);

                        mediaPlayer.stop();
                        mediaPlayer.release();
                        MediaRecorderReady();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        cardView.setVisibility(View.GONE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (countUserIntent != null){
            db = dbHelper.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put(FeedReaderContract.FeedEntry.LASTMSG, 0);
            Log.e("ssssss", countUserIntent);
            db.update(TABLE_NAME, data, "_id=" + countUserIntent, null);
        }



    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(ChatListAc.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.i(TAG, "Exception while hiding keyboard");
        }
    }

    public void updateListView() {

        int totalWishes = messages.size();


        MessageCell[] messageCells;
        messageCells = new MessageCell[totalWishes];


        for (int counter = 0; counter < totalWishes; counter++) {
            FirebaseMessageModel firebaseMessageModel = messages.get(counter);

            MessageCell messageCell = new MessageCell(firebaseMessageModel.getSenderName(), firebaseMessageModel.getText(),
                    getDate(firebaseMessageModel.getCreatedDateLong()), firebaseMessageModel.getSenderId().equals(current_uid), firebaseMessageModel.getImage(), firebaseMessageModel.getSenderId(), firebaseMessageModel.getRecord());

            messageCells[counter] = messageCell;

        }
        MessagesListAdapter adapter = new MessagesListAdapter(this, messageCells);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);
        listView.requestFocus();

    }


    public void sendMsgWithNotification() {

        wishMessage = textComment.getText().toString().trim();

        if (resultUri == null) {
            resultUri = Uri.parse("default");

        }

        if (urlPathString == null) {
            AudioSavePathInDevice = "default";
        } else {
            AudioSavePathInDevice = String.valueOf(urlPathString);

        }

        current_name = firebaseUserModel.getName();

        final FirebaseMessageModel firebaseMessageModel = new FirebaseMessageModel();
        firebaseMessageModel.setText(wishMessage);
        firebaseMessageModel.setSenderId(current_uid);
        firebaseMessageModel.setSenderName(current_name);
        firebaseMessageModel.setReceiverId(uidUserIntent);
        firebaseMessageModel.setImage(String.valueOf(resultUri));
        firebaseMessageModel.setStatus("false");
        firebaseMessageModel.setRecord(AudioSavePathInDevice);


        updateListView();


        final ProgressDialog Dialog = new ProgressDialog(this);
        Dialog.setMessage("Please wait..");
        Dialog.setCancelable(false);
        Dialog.show();

        if (isConnected) {

        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    textComment.setText("");
                    Dialog.dismiss();
                    Toast.makeText(ChatListAc.this, "אין תקשורת", Toast.LENGTH_LONG).show();

                }
            }, 5000);   //
        }


        final DatabaseReference newRef = messagesRef.push();
        newRef.setValue(firebaseMessageModel, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                Dialog.dismiss();

                uidMsg = databaseReference.getKey();

                if (!stringUrl.equals("stringUrl")) {
                    loadimageFirebase(uidMsg, chatRoomsUserIntent);

                }

                if (AudioSavePathInDevice.equals("default")) {

                    if (resultUri.equals("default")) {
                        try {
                            wishMessage = URLEncoder.encode(wishMessage, "UTF-8");
                            Log.e("eeeefffffe", wishMessage);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.e("noTry", wishMessage);


                    } else {
                        if (wishMessage.equals("")) {
                            wishMessage = "picture";

                        }else {
                            try {
                                wishMessage = URLEncoder.encode(wishMessage, "UTF-8");
                                Log.e("dddddd", wishMessage);

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.e("noTry3", wishMessage);

                    }
                } else {
                    Log.e("noTry4", wishMessage);

                    if (wishMessage.equals("")) {
                        Log.e("noTry5", wishMessage);
                        wishMessage = "record";



                    } else {
                        Log.e("noTry6", wishMessage);


                            try {
                                wishMessage = URLEncoder.encode(wishMessage, "UTF-8");

                                Log.e("eeeffffffffffee", wishMessage);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }



                    }

                }


                if (databaseError != null) {
                    Log.i(TAG, databaseError.toString());
                } else {
                    textComment.setText("");




                    if (registration_ids.length() > 0) {


                        String url = "https://fcm.googleapis.com/fcm/send";
                        AsyncHttpClient client = new AsyncHttpClient();

                        client.addHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyDYqZFXvM8I1cEDncsWQjffMLm6Uq55UoI");
                        client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

                        try {
                            String nameString;

                            if (uidUserIntent.equals(firebaseUserModel.getChat())) {
                                nameString =  firebaseUserModel.getCamp();

                                Log.e("dddd",nameString);

                            } else {
                                nameString = firebaseUserModel.getName();
                                Log.e("dddd",nameString);

                            }

                            String name =URLEncoder.encode(firebaseUserModel.getName(), "UTF-8");
                            String namegroup =URLEncoder.encode(nameString, "UTF-8");


                            JSONObject params = new JSONObject();

                            params.put("registration_ids", registration_ids);

                            JSONObject notificationObject = new JSONObject();
                            notificationObject.put("body", wishMessage);
                            notificationObject.put("title", namegroup);

                            JSONObject notificationObjectData = new JSONObject();
                            notificationObjectData.put("Room", chatRoomsUserIntent);
                            notificationObjectData.put("uidSender", current_uid);
                            notificationObjectData.put("deviceId", deviceUserIntent);
                            notificationObjectData.put("name", name);
                            notificationObjectData.put("msg", wishMessage);
                            notificationObjectData.put("chat", firebaseUserModel.getChat());

                            params.put("notification", notificationObject);
                            params.put("data", notificationObjectData);


                            StringEntity entity = new StringEntity(params.toString());

                            client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                                    Dialog.dismiss();

                                    Log.i(TAG, String.valueOf(statusCode));
                                    Log.i(TAG, responseString);
                                    Log.i(TAG, String.valueOf(throwable));
                                    Log.i(TAG, String.valueOf(headers));

                                }

                                @Override
                                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                                    Dialog.dismiss();
                                    AudioSavePathInDevice = null;
                                    resultUri = null;
                                    mediaPlayer = new MediaPlayer();


                                    Log.i(TAG + 1, responseString);
                                    Log.i(TAG + 2, String.valueOf(statusCode));
                                    Log.i(TAG + 3, String.valueOf(headers.length));
                                    Log.i(TAG + 3, String.valueOf(headers.clone()));

                                }
                            });

                        } catch (Exception e) {

                        }
                    }

                }

            }


        });
        num = 2;


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

        try {
            if (!deviceUserIntent.equals("default")) {

                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uidUserIntent);
                mUserDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        boolean check = dataSnapshot.child("time").exists();
                        if (check) {
                            String time = dataSnapshot.child("time").getValue().toString();
                            String onilne = dataSnapshot.child("online").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();

                            //check if image exsists after push
                            if (numberFromPush == 2 && image.equals("default")) {
                                Picasso.get().load(image).error(R.drawable.midburn_logo).into(imageUser);

                            }

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

                            Log.e("fffffffffff", String.valueOf(check));


                        } else {
                            db = dbHelper.getWritableDatabase();

                            db.delete(TABLE_NAME, "_id=" + countUserIntent, null);
                            db.close();
                            Intent i = new Intent(ChatListAc.this, ChatAc.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                tvTimeUser.setText("");

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }


    private void uploadAudio(String mFileName, String time) {
        progressDialog.setMessage("מעלה הקלטה");
        progressDialog.show();
        String getMyKeyMsg = UUID.randomUUID().toString();

        filePathRecord = mImageStorage.child("Audio").child(time + ".3gp");
        urlPathString = "Audio/" + time + ".3gp";

        recordUri = Uri.fromFile(new File(mFileName));
        filePathRecord.putFile(recordUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                progressDialog.dismiss();
                progressDialog.setMessage("סיים לעלות הקלטה");

                sendMsgWithNotification();

            }
        });

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
                stringUrl = String.valueOf(resultUri);

                filePath = mImageStorage.child("msg_images").child(resultUri.getLastPathSegment());

                hideKeyboard();
                sendMsgWithNotification();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(ChatListAc.this, "נכשל בעלת התמונה לשרת", Toast.LENGTH_LONG).show();

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

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(getchat).child(uidMsg);


                            Map<String, Object> mapCampsUpdates = new HashMap<>();
                            mapCampsUpdates.put("image", stringUrl);


                            mUserDatabase.updateChildren(mapCampsUpdates);

                            stringUrl = "stringUrl";

                        }

                    }
                });
            }
        });
    }
    ////////////////Record


    public void popUp() {

        buttonStopPop.setEnabled(true);
        buttonPlayPop.setEnabled(false);
        sendRecordBtn.setEnabled(false);

        random = new Random();

    }


    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ChatListAc.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean getDataRemoteFromPush(RemoteMessage remoteMessage) {


        roomPush = remoteMessage.getData().get("Room");
        uidPush = remoteMessage.getData().get("uidSender");
        devicePush = remoteMessage.getData().get("deviceId");
        try {
            String name = remoteMessage.getNotification().getTitle();

            namePush=  URLDecoder.decode(name,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return true;
    }


}


