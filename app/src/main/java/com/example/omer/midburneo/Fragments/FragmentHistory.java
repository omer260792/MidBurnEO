package com.example.omer.midburneo.Fragments;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.omer.midburneo.Adapters.MessageNoteAdapter;
import com.example.omer.midburneo.Class.MessageNote;
import com.example.omer.midburneo.DataBase.DBHelper;
import com.example.omer.midburneo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class FragmentHistory extends Fragment {

    private static final String TAG = "FragmentHistory";


    private CheckBox checkBox;
    public RecyclerView recyclerViewNote;
    public MessageNoteAdapter mAdapterNote;
    private List<MessageNote> messageNoteList = new ArrayList<>();

    public SharedPreferences prefs;
    public DBHelper dbHelper;
    public SQLiteDatabase db;

    public String getUid, date;

    View view;

    public FragmentHistory() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_history_fragment, container, false);

        recyclerViewNote = view.findViewById(R.id.recyclerFragmentHistory);
        checkBox = view.findViewById(R.id.checkBoxNote);

        dbHelper = new DBHelper(getContext());

        getNoteMsg();



        return view;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void getNoteMsg() {

        dbHelper = new DBHelper(getContext());

        try {
            messageNoteList.addAll(dbHelper.getAllNote("true"));
            mAdapterNote = new MessageNoteAdapter(getContext(), messageNoteList);
            recyclerViewNote.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewNote.setAdapter(mAdapterNote);
            mAdapterNote.notifyDataSetChanged();


        } catch (Exception e) {
            e.printStackTrace();
            e.getStackTrace();
            e.getMessage();

        }

    }

}
