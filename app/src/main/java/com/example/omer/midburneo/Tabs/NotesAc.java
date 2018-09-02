package com.example.omer.midburneo.Tabs;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omer.midburneo.Adapters.FriendsAdapter;
import com.example.omer.midburneo.Adapters.MessageNoteAdapter;
import com.example.omer.midburneo.Class.FeedReaderContract;
import com.example.omer.midburneo.Class.Friend;
import com.example.omer.midburneo.Class.MessageNote;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.omer.midburneo.RegisterAc.SHPRF;

public class NotesAc extends AppCompatActivity {

    private DatabaseReference mUserDatabase;

    public RecyclerView.Adapter mAdapterNote;
    public RecyclerView.LayoutManager layoutManagerNote;
    public RecyclerView recyclerViewNote;
    private List<MessageNote> messageNoteList;

    public static Boolean refreshList = true;

    public SharedPreferences prefs;

    public String current_camp, current_uid, getUid;

    private String getMsg = "getMsg";
    private String getSender = "getSender";
    private String getTime = "getTime";
    private long timeLong;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_note);
        prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);
        current_camp = prefs.getString("camps", null);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerViewNote = (RecyclerView) findViewById(R.id.recyclerViewNote);
        recyclerViewNote.setHasFixedSize(true);
        layoutManagerNote = new LinearLayoutManager(NotesAc.this);

        recyclerViewNote.setLayoutManager(layoutManagerNote);

        messageNoteList = new ArrayList<>();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Admin").child(current_camp).child("message");
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    getUid = snapshot.getKey().toString();

                    String test = dataSnapshot.getChildren().toString();

                    Log.i("f;;;;;f;f;f;f;f;f",test);



                    if (refreshList.equals(false)){


                    }else {
                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Admin").child(current_camp).child("message").child(getUid);
                        mUserDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                try{

                                    getMsg = dataSnapshot.child("msg").getValue().toString();
                                    getSender = dataSnapshot.child("sender").getValue().toString();
                                    getTime = dataSnapshot.child("time").getValue().toString();


                                    if (getMsg.equals(null)){

                                    }else {
                                        timeLong = Long.parseLong(getTime);
                                        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");
                                        String rTime = getTimeHourMintus.format(timeLong);



                                        messageNoteList.add(new MessageNote(getMsg, rTime, getSender,getUid,current_camp));

                                        mAdapterNote = new MessageNoteAdapter(NotesAc.this, messageNoteList);
                                        mAdapterNote.notifyItemRemoved(messageNoteList.size());


                                        recyclerViewNote.setAdapter(mAdapterNote);
                                        //mAdapterNote.notifyDataSetChanged();


                                    }

                                }catch(NullPointerException e ){

                                }





                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();




    }
}
