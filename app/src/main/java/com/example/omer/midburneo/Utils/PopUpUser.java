package com.example.omer.midburneo.Utils;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.ChatAc;
import com.example.omer.midburneo.Tabs.EquipmentPreviewAc;

public class PopUpUser extends AppCompatActivity {

    private Dialog myDialog;
    public TextView txtclose;
    public Button btnFollow;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black);
        myDialog = new Dialog(this);
        ShowPopup(v);
    }

    public void ShowPopup(View v) {

        myDialog.setContentView(R.layout.item_user_popup);
        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("M");
        btnFollow = (Button) myDialog.findViewById(R.id.btnCallPopUp);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();

                Intent intent = new Intent(PopUpUser.this, ChatAc.class);
                startActivity(intent);

            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
