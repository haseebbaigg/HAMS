package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button button;

    FirebaseAuth firebaseAuth;

    String doc;

    private ProgressDialog mLoginProgress;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        button = findViewById(R.id.login_btn);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (email.getText().toString().trim().matches(emailPattern) && s.length() > 0)
                {
                    Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
                    email.setBackgroundColor(Color.BLACK);
                    email.setTextColor(Color.WHITE);
                    button.setEnabled(true);
                }
                else
                {
                    email.setBackgroundColor(Color.RED);
                    email.setTextColor(Color.WHITE);
                    button.setEnabled(false);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >=7 )
                {
                    Toast.makeText(getApplicationContext(),"valid password address",Toast.LENGTH_SHORT).show();
                    password.setBackgroundColor(Color.BLACK);
                    password.setTextColor(Color.WHITE);
                    button.setEnabled(true);
                }
                else
                {
                    password.setBackgroundColor(Color.RED);
                    password.setTextColor(Color.WHITE);
                    button.setEnabled(false);
                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(email.getText().toString()) && !(TextUtils.isEmpty(password.getText().toString()))) {
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.i("TAG", "signInWithEmail:success");
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        Intent intent = new Intent(LoginActivity.this, ChatScreen.class);
                                        startActivity(intent);

                                    } else {
                                        mLoginProgress.hide();
                                        // If sign in fails, display a message to the user.
                                        Log.i("TAG", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else {
                    Toast.makeText(LoginActivity.this,"AUTHENTICATION INVALID:  ENTER ALL FIELDS",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}