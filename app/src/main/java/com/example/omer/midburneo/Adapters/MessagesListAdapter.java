package com.example.omer.midburneo.Adapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.omer.midburneo.Class.FirebaseMessageModel;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Class.MessageCell;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;


public class MessagesListAdapter extends ArrayAdapter<MessageCell> {
    MessageCell[] cellItem = null;
    Context context;


    public static int VIEW_TYPE = 0;
    public static int VIEW_TYPE_PICTURE = 0;
    public String randomNumString;
    private CardView cardView;
    private StorageReference mImageStorage, filePath;
    private DatabaseReference ref;
    private Uri uriPath;
    private File fileFromFirebase;



    public MessagesListAdapter(Context context, MessageCell[] resource) {
        super(context, R.layout.message_cell, resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.cellItem = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        FirebaseMessageModel firebaseMessageModel = new FirebaseMessageModel();


        if (cellItem[position].getSender()) {
            VIEW_TYPE = 0;

            convertView = inflater.inflate(R.layout.message_cell_sender, parent, false);

        } else {
            VIEW_TYPE = 1;

            convertView = inflater.inflate(R.layout.message_cell, parent, false);

        }
        TextView nameSenderTv = (TextView) convertView.findViewById(R.id.tvNameItemMsg);
        TextView messageTv = (TextView) convertView.findViewById(R.id.tvContentItemMsg);
        TextView dateTimeTv = (TextView) convertView.findViewById(R.id.dateTimeItemMsg);
        Button btnRecod = (Button) convertView.findViewById(R.id.btnRecordItemMsg);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageItemMsg);
        cardView = (CardView) convertView.findViewById(R.id.cardViewMessageCell);


        String image = cellItem[position].getImage();
        String msg = cellItem[position].getMessageText();

        String sender = cellItem[position].getStringSender();
        String record = cellItem[position].getMessageRecord();

        filePath = FirebaseStorage.getInstance().getReference();


        if (VIEW_TYPE == 0) {

            if (image.equals("default")) {

                if (record.equals("default")) {
                    messageTv.setText(cellItem[position].getMessageText());
                    dateTimeTv.setText(cellItem[position].getMessageDateTime());
                    imageView.setVisibility(View.GONE);
                    btnRecod.setVisibility(View.GONE);
                    nameSenderTv.setVisibility(View.GONE);
                } else {

                    dateTimeTv.setText(cellItem[position].getMessageDateTime());
                    imageView.setVisibility(View.GONE);
                    messageTv.setVisibility(View.GONE);
                    btnRecod.setText(cellItem[position].getMessageText());
                    nameSenderTv.setVisibility(View.GONE);
                }


            } else {

                if (msg.equals("")) {
                    dateTimeTv.setText(cellItem[position].getMessageDateTime());
                    Glide.with(context).load(cellItem[position].getImage()).into(imageView);
                    messageTv.setVisibility(View.GONE);
                    btnRecod.setVisibility(View.GONE);
                    nameSenderTv.setVisibility(View.GONE);

                } else {
                    messageTv.setText(cellItem[position].getMessageText());
                    dateTimeTv.setText(cellItem[position].getMessageDateTime());
                    Glide.with(context).load(cellItem[position].getImage()).into(imageView);
                    btnRecod.setVisibility(View.GONE);
                    nameSenderTv.setVisibility(View.GONE);

                }


            }


        } else {

            if (image.equals("default")) {


                if (record.equals("default")) {
                    nameSenderTv.setText(cellItem[position].getMessageSender());
                    messageTv.setText(cellItem[position].getMessageText());
                    dateTimeTv.setText(cellItem[position].getMessageDateTime());
                    imageView.setVisibility(View.GONE);
                    btnRecod.setVisibility(View.GONE);
                } else {
                    nameSenderTv.setText(cellItem[position].getMessageSender());
                    dateTimeTv.setText(cellItem[position].getMessageDateTime());
                    imageView.setVisibility(View.GONE);
                    messageTv.setVisibility(View.GONE);
                    btnRecod.setText(cellItem[position].getMessageRecord());
                }


            } else {

                if (msg.equals("")) {
                    nameSenderTv.setText(cellItem[position].getMessageSender());
                    messageTv.setVisibility(View.GONE);
                    dateTimeTv.setText(cellItem[position].getMessageDateTime());
                    Glide.with(context).load(cellItem[position].getImage()).into(imageView);
                    btnRecod.setVisibility(View.GONE);

                } else {
                    nameSenderTv.setText(cellItem[position].getMessageSender());
                    messageTv.setText(cellItem[position].getMessageText());
                    dateTimeTv.setText(cellItem[position].getMessageDateTime());
                    Glide.with(context).load(cellItem[position].getImage()).into(imageView);
                    btnRecod.setVisibility(View.GONE);

                }


            }


        }


        btnRecod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!cellItem[position].getMessageRecord().equals("default")) {



                    filePath.child(record).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            uriPath = uri;
                            // fileFromFirebase = new File(uriPath.getPath());

                            MediaPlayer mediaPlayer = new MediaPlayer();

                            try {
                                mediaPlayer.setDataSource(context,uriPath);
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.start();




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("fffffffff3", String.valueOf(exception));
                        }
                    });



                } else if (!cellItem[position].getImage().equals("default")) {


                } else {

                }

            }
        });


        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                alertDialog.setTitle("עריכת הודעה");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "מחק הודעה ", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {


                    }
                });

                String img = cellItem[position].getImage();
                if (!img.equals("default")) {
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "הורד תמונה", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                            Glide.with(context)
                                    .asBitmap()
                                    .load(img)
                                    .into(new SimpleTarget<Bitmap>(100, 100) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            saveImage(resource);

                                        }


                                    });

                            return;

                        }
                    });
                }

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "חזור", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        return;

                    }
                });

                alertDialog.show();

                return false;
            }
        });


        return convertView;
    }

    private String saveImage(Bitmap image) {
        String savedImagePath = null;
        random();

        String imageFileName = "JPEG_" + randomNumString + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/YOUR_FOLDER_NAME");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Add the image to the system gallery
            galleryAddPic(savedImagePath);
            Toast.makeText(context, "תמונה נשמרה", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public void random() {

        Random randomGenerator = new Random();

        long randomInt = randomGenerator.nextInt(1000000000);
        randomNumString = String.valueOf(randomInt);


    }


}