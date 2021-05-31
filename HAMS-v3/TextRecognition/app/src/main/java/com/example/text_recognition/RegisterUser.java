package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterUser extends AppCompatActivity {

    EditText name,email , password , status;
    Button button;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String doctor;

    Switch aSwitch;

    ProgressDialog mLoginProgress;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        firebaseAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        name = findViewById(R.id.name);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        status = findViewById(R.id.login_status);

        aSwitch = findViewById(R.id.doc_switch);

        button = findViewById(R.id.login_btn);

        aSwitch.setVisibility(View.GONE);

        Intent intent= getIntent();
        doctor = intent.getStringExtra("DoctorRegister");
        if(!doctor.equals("null"))
        {
            aSwitch.setVisibility(View.VISIBLE);
        }

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 5)
                {
                    name.setBackgroundColor(Color.BLACK);
                    name.setTextColor(Color.WHITE);
                    button.setEnabled(true);
                }else
                {
                    name.setBackgroundColor(Color.RED);
                    name.setTextColor(Color.WHITE);
                    button.setEnabled(false);
                }
            }
        });

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
                if(!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())
                && !TextUtils.isEmpty(status.getText().toString()) && !TextUtils.isEmpty(name.getText().toString())) {
                    mLoginProgress.setTitle("Registration");
                    mLoginProgress.setMessage("Please wait while we add your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.i("TAG", "createUserWithEmail:success");
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = user.getUid();

                                        Log.i("DB_REFERENCE", String.valueOf(FirebaseDatabase.getInstance().getReference()));
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                        //databaseReference.setValue("Hello");

                                        HashMap<String, String> userMap = new HashMap<>();
                                        Log.i("DISPLAY NAME", name.getText().toString());
                                        userMap.put("name", name.getText().toString());
                                        if (!doctor.equals("null")) {
                                            status.setHint("Doctor Speciality");
                                            userMap.put("status", status.getText().toString());
                                            userMap.put("isDoctor", "true");
                                        } else {
                                            if (status.getText().toString().isEmpty()) {
                                                userMap.put("status", "Hi! I am using HAMS Application");
                                            } else {
                                                userMap.put("status", status.getText().toString());
                                            }
                                            userMap.put("isDoctor", "false");
                                        }

                                        userMap.put("image", "default");

                                        databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mLoginProgress.hide();
                                                    Log.i("TASK SUCCESSFULL", "SUCCESSFULL");
                                                    Intent intent = new Intent(RegisterUser.this, ChatScreen.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    aSwitch.setVisibility(View.GONE);
                                                    finish();
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.i("TAG", "DATABASE CREATION:failure", task.getException());
                                                    Toast.makeText(RegisterUser.this, "DB CREATION failed.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.i("TAG", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegisterUser.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(RegisterUser.this,"AUTHENTICATION INVALID:  ENTER ALL FIELDS",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}