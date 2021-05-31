package com.example.text_recognition;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class HAMSApp extends Application {

    DatabaseReference userDatabaseReference;
    FirebaseAuth userFirebaseAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("CHAT APP","CHAT APP STARTS!!!!!!!!!!!!!!!!!!!!!!1");
        userFirebaseAuth = FirebaseAuth.getInstance();
        if(userFirebaseAuth.getCurrentUser() != null) {
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userFirebaseAuth.getCurrentUser().getUid());

            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null) {
                        userDatabaseReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
