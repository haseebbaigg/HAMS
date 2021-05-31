package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {


    TextView profilename,profilestatus;
    ImageView profileimageView;
    Button sendRequest,declineRequest;

    DatabaseReference databaseReference;

    String cur_state;

    DatabaseReference friendReqDatabaseReference;
    FirebaseUser cur_user;

    DatabaseReference friendDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilename = findViewById(R.id.profile_name);
        profileimageView = findViewById(R.id.profile_image);
        sendRequest = findViewById(R.id.send_req);
        declineRequest = findViewById(R.id.decline_req);
        profilestatus = findViewById(R.id.profile_status);
        profilestatus.setMovementMethod(new ScrollingMovementMethod());

        cur_state = "not_friends";
        friendReqDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        cur_user = FirebaseAuth.getInstance().getCurrentUser();


        friendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");

        Intent intent = getIntent();
        final String userId = intent.getStringExtra("user_id");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String image = snapshot.child("image").getValue().toString();

                profilename.setText(name);
                profilestatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.avatar).into(profileimageView);

                friendReqDatabaseReference.child(cur_user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(userId))
                                {
                                    String req_type = snapshot.child(userId).child("request_type").getValue().toString();
                                    if(req_type.equals("received"))
                                    {
                                        cur_state = "req_received";
                                        sendRequest.setText("Acccept Friend Request");
                                        declineRequest.setVisibility(View.VISIBLE);
                                        declineRequest.setEnabled(true);
                                    }
                                    else if(req_type.equals("sent"))
                                    {
                                        cur_state = "req_sent";
                                        sendRequest.setText("Cancel Friend Request");

                                        declineRequest.setVisibility(View.INVISIBLE);
                                        declineRequest.setEnabled(false);
                                    }
                                    //dismiss dialog
                                }
                                else
                                {
                                    friendDatabaseReference.child(cur_user.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.hasChild(userId))
                                                    {
                                                        sendRequest.setEnabled(true);
                                                        cur_state = "friends";
                                                        sendRequest.setText("UnFriend "+profilename.getText().toString());
                                                    }
                                                    //dismiss dialog
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    //dismiss dialog
                                                }
                                            });
                                }

                                //dismiss_dialog
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest.setEnabled(false);

                if (cur_state.equals("not_friends")) {
                    friendReqDatabaseReference.child(cur_user.getUid()).child(userId).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendReqDatabaseReference.child(userId).child(cur_user.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        cur_state = "req_sent";
                                        sendRequest.setText("Cancel Friend Request");

                                        declineRequest.setVisibility(View.INVISIBLE);
                                        declineRequest.setEnabled(false);
                                        Toast.makeText(getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {

                                Toast.makeText(getApplicationContext(), "Failed Sending Request", Toast.LENGTH_SHORT).show();
                            }
                            sendRequest.setEnabled(true);
                        }
                    });
                }


                if (cur_state.equals("req_sent")) {
                    friendReqDatabaseReference.child(cur_user.getUid()).child(userId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendReqDatabaseReference.child(userId).child(cur_user.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sendRequest.setEnabled(true);
                                                    cur_state = "not_friends";
                                                    sendRequest.setText("Send Friend Request");

                                                    declineRequest.setVisibility(View.INVISIBLE);
                                                    declineRequest.setEnabled(false);
                                                }
                                            });
                                }
                            });
                }


                if (cur_state.equals("req_received"))
                {
                    final String cur_date = DateFormat.getDateInstance().format(new Date());
                    friendDatabaseReference.child(cur_user.getUid()).child(userId).child("date").setValue(cur_date)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendDatabaseReference.child(userId).child(cur_user.getUid()).child("date").setValue(cur_date)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    friendReqDatabaseReference.child(cur_user.getUid()).child(userId).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    friendReqDatabaseReference.child(userId).child(cur_user.getUid()).removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    sendRequest.setEnabled(true);
                                                                                    cur_state = "friends";
                                                                                    sendRequest.setText("UnFriend "+profilename.getText().toString());
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }

                                            });
                                }
                            });
                }

                if(cur_state.equals("friends"))
                {
                    friendDatabaseReference.child(cur_user.getUid()).child(userId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendDatabaseReference.child(userId).child(cur_user.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sendRequest.setEnabled(true);
                                                    cur_state = "not_friends";
                                                    sendRequest.setText("Send Friend Request");
                                                }
                                            });
                                }
                            });
                }
            }
        });

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
}