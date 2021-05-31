package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class ChatScreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    Button button;

    Toolbar toolbar;

    TabLayout tabLayout;

    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat App");

        if(firebaseAuth.getCurrentUser() != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
            Log.i("USER REF_USER_ID",userRef.toString());
//            userRef.child("status").setValue("MUGHEES HAMS");
            userRef.child("online").setValue("true");
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null)
        {
            Intent intent = new Intent(ChatScreen.this,StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
        {
            //
            Intent intent = new Intent(ChatScreen.this,MessagesActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("USER_CHAT+SCREEN","STOP");
//        if(firebaseAuth.getCurrentUser() != null) {
//            userRef.child("online").setValue(ServerValue.TIMESTAMP);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.chat_menu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() ==  R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            userRef.child("online").setValue(ServerValue.TIMESTAMP);

            Intent intent = new Intent(ChatScreen.this,StartActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.account_setting)
        {
            Intent intent = new Intent(ChatScreen.this,AccountSetting.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.users)
        {
            Intent intent = new Intent(ChatScreen.this,UsersActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() ==  R.id.doctors)
        {
            Intent intent = new Intent(ChatScreen.this,AllDoctorsActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.friends)
        {
            Intent intent = new Intent(ChatScreen.this,FriendsActivity.class);
            startActivity(intent);
        }
        return true;
    }
}