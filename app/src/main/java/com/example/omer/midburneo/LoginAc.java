//done ido
package com.example.omer.midburneo;

import android.content.Intent;
import android.content.SharedPreferences;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.omer.midburneo.Tabs.MainPageAc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;


import static com.example.omer.midburneo.RegisterAc.SHPRF;


public class LoginAc extends AppCompatActivity {

    private final String TAG = "LoginAc";


    EditText loginUserName, loginUserPassword;
    public ImageView imageView;
    String email, password;
    SharedPreferences prefs;
    private FirebaseAuth mAuth;
    public String uidKeySP, emailSP;


    private FirebaseAuth.AuthStateListener mAuthLis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginUserName = findViewById(R.id.LoginUserName);
        loginUserPassword = findViewById(R.id.LoginPassword);


        mAuth = FirebaseAuth.getInstance();

        //listener check if user is logged in
        mAuthLis = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);


                try {
                    emailSP = prefs.getString("email", null);

                } catch (NullPointerException e) {
                    e.printStackTrace();

                }

                if (firebaseAuth.getCurrentUser() == null) {

                    return;

                } else {

                    try {

                        if (emailSP == "register") {


                            Intent intent = new Intent(LoginAc.this, CampsAc.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();


                        } else {

                            uidKeySP = prefs.getString("email", null);

                            if (uidKeySP == null) {
                                //this is the first time the user is logging in


                                Intent intent = new Intent(LoginAc.this, CampsAc.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else {
                                prefs.edit().putString(firebaseAuth.getCurrentUser().getUid(), firebaseAuth.getCurrentUser().getUid()).apply();

                                Intent intent = new Intent(LoginAc.this, MainPageAc.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginAc.this,
                                "יש משתמש כזה במערת החלף מייל", //ADD THIS
                                Toast.LENGTH_SHORT).show();


                    }


                }
            }
        };

        //assign the listener to the auth object
        mAuth.addAuthStateListener(mAuthLis);

    }


    public void RegisterBtn(View view) {

        Intent intent = new Intent(LoginAc.this, RegisterAc.class);
        startActivity(intent);
    }

    public void LoginBtnFunc(View view) {

        checkLoginAccount();
    }

    public void checkLoginAccount() {


        email = loginUserName.getText().toString();
        password = loginUserPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(LoginAc.this,
                    "Please fill up the text", //ADD THIS
                    Toast.LENGTH_SHORT).show();
        } else {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        //if the user logged in successfully
                        Intent intent = new Intent(LoginAc.this, MainPageAc.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginAc.this,
                                "לא קיים שם משתמש במערכת", //ADD THIS
                                Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }
    }

}
