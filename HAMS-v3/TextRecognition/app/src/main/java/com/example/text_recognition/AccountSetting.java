package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetting extends AppCompatActivity {


    Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    TextView name;
    EditText status;
    CircleImageView circleImageView;
    Button image;

    StorageReference storageReference;

    int Gallery_CODE =1;

    RelativeLayout relativeLayout;

    ProgressDialog mLoginProgress;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__account_setting);


        toolbar = findViewById(R.id.user_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Setting");

        mLoginProgress = new ProgressDialog(this);

        relativeLayout = findViewById(R.id.account_setting_layout);

        name = findViewById(R.id.UserName);
        status = findViewById(R.id.UserStatus);
        circleImageView = (CircleImageView) findViewById(R.id.UserImage);
        image = findViewById(R.id.UserChangeImage);

        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        String cur_user = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(cur_user);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.child("name").getValue().toString();
                String user_status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                Log.i("IMAGE",image);
                name.setText(user_name);
                status.setText(user_status);
                Picasso.get().load(image).placeholder(R.drawable.avatar).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent gallery_intent = new Intent();
//                gallery_intent.setType("images/*");
//                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(gallery_intent,"SELECT IMAGE"),Gallery_CODE);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_CODE);
            }
        });

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if(status.isFocused()) {
                        status.clearFocus();
                    }
                }
                return false;
            }
        });

        status.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    Log.i("FOCUS LOST","EDIT TEXT STATUS FOCUS LOST");
                    String cur_user = firebaseAuth.getCurrentUser().getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(cur_user);
                    String getStatus = status.getText().toString();
                    databaseReference.child("status").setValue(getStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.i("TASK SUCCESSFUL", "EDIT STATUS IS UPDATED");
                            }
                            else {
                                Log.i("TASK GOT ERROR", "EDIT STATUS GOT ERROR");
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == Gallery_CODE && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

            Log.i("RESULT CODe", String.valueOf(requestCode));

        }
        Log.i("RESULT CODE---222", String.valueOf(requestCode));
        Log.i("CROP IMAGE REQ CODe", String.valueOf(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE));
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            Log.i("IN CONDITION","IF CONDITION");
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            Log.i("result CropImage",result.toString());
            if(resultCode == RESULT_OK)
            {
                mLoginProgress = new ProgressDialog(AccountSetting.this);
                mLoginProgress.setTitle("Uploading Image...");
                mLoginProgress.setMessage("Please wait while we upload and process the image.");
                mLoginProgress.setCanceledOnTouchOutside(false);
                mLoginProgress.show();
                Uri resultUri = result.getUri();
                Log.i("RESULT URI",resultUri.toString());
                StorageReference filePath = storageReference.child("profile_images").child(generateRandomNumbers() + ".jpg");

//                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if(task.isSuccessful())
//                        {
//                            String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
//                            Log.i("DOWNLOAD_URL",downloadUrl);
//                            //databaseReference.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                @Override
////                                public void onComplete(@NonNull Task<Void> task) {
////                                    Toast.makeText(getApplicationContext(),"WORKING",Toast.LENGTH_LONG).show();
////                                }
////                            });
//                        }
//                        else
//                        {
//                            Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mLoginProgress.dismiss();
                                        String downloadUrl = uri.toString();
                                        databaseReference.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(),"WORKING",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });

                            }
                        });

            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception exception = result.getError();
                Log.i("EXCEPTION",exception.toString());
            }
        }
    }
    public static String md5(String s)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    private static int generateRandomNumbers() {
        Random RANdom = new Random();
        int random = RANdom.nextInt(10000);
        return random;
    }
}