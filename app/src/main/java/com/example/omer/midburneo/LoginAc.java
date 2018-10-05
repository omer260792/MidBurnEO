package com.example.omer.midburneo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omer.midburneo.Tabs.MainPageAc;
import com.google.android.gms.flags.Flag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.omer.midburneo.RegisterAc.SHPRF;


public class LoginAc extends AppCompatActivity {

    private final String TAG = "LoginAc";


    EditText loginUserName, loginUserPassword;
    Button signinBtn;
    TextView signUpBtn;
    String email, password;
    SharedPreferences prefs;
    private FirebaseAuth mAuth;
    public String checkEmailReg;

    private FirebaseAuth.AuthStateListener mAuthLis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUserName = findViewById(R.id.LoginUserName);
        loginUserPassword = findViewById(R.id.LoginPassword);
        signUpBtn = findViewById(R.id.SignUpBtn);
        signinBtn = findViewById(R.id.SignInBtn);

        mAuth = FirebaseAuth.getInstance();

        mAuthLis = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                try {
                    prefs = getSharedPreferences(SHPRF, MODE_PRIVATE);

                    checkEmailReg = prefs.getString("email", null);
                    if (checkEmailReg != "register"){
                        checkEmailReg = "main";
                    }

                } catch (NullPointerException e) {

                }

                if (firebaseAuth.getCurrentUser() == null) {


                } else {

                    if (checkEmailReg.equals("register")) {
                        // startActivity(new Intent(LoginAc.this, CampsAc.class));
                        Intent intent = new Intent(LoginAc.this, CampsAc.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } else {
                        Intent intent = new Intent(LoginAc.this, MainPageAc.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthLis);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "succ");

        super.onDestroy();


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
                        // there was an erroromer
                        Toast.makeText(LoginAc.this,
                                "signin with email in LoginAc", //ADD THIS
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginAc.this, MainPageAc.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } else {

                        if (password.length() < 6) {

                            Toast.makeText(LoginAc.this,
                                    "Your Password must be set with 6 charcter", //ADD THIS
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(LoginAc.this,
                                    "Can't recognize the Users, Please Register", //ADD THIS
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

        }
    }
}
