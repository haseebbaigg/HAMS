package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String chatUser,username;

    Toolbar ChatToolbar;

    DatabaseReference UserDatabaseReference;

    TextView TitleView,LastSeenView;

    FirebaseAuth Auth;
    String current_user;

    ImageButton imageSendBtn;
    EditText editMessage;

    RecyclerView messagesRecyclerView;

    final List<Messages> messagesList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;

    MessageAdapter messageAdapter;

    CircleImageView circleImageView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageSendBtn = findViewById(R.id.sendBtn);
        editMessage = findViewById(R.id.messageText);


        messagesRecyclerView = findViewById(R.id.chatRecyclerView);

        messageAdapter = new MessageAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(this);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);

        messagesRecyclerView.setAdapter(messageAdapter);

        circleImageView = findViewById(R.id.message_single_image);



        Intent intent = getIntent();
        chatUser = intent.getStringExtra("user_id");
        username = intent.getStringExtra("user_name");


        Auth = FirebaseAuth.getInstance();
        current_user = Auth.getCurrentUser().getUid();

//        Log.i("CHAT USER ID",chatUser);
//        Log.i("CHAT USER NAME",username);

        ChatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(ChatToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_bar_layout,null);

        actionBar.setCustomView(action_bar_view);

        TitleView = (TextView) findViewById(R.id.CurrentUserName);
        LastSeenView = (TextView) findViewById(R.id.LastSeen);


        UserDatabaseReference = FirebaseDatabase.getInstance().getReference();
        UserDatabaseReference.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String onlineUser = snapshot.child("online").getValue().toString();

                Log.i("ONLINE USER" , onlineUser);

                if(onlineUser.equals("true"))
                {
                    if(username != null) {
                        TitleView.setText(username);
                        LastSeenView.setText("Online");
                    }
                }
                else
                {
                    Log.i("ONLINE USER NAME",username);
                    Log.i("ONLINE USER TEXT",onlineUser);
                    if(username != null && onlineUser != null) {
                        TitleView.setText(username);
                        GetTimeAgo time = new GetTimeAgo();
                        long lastTime = Long.parseLong(onlineUser);
                        String LastSeenTime = time.GetTimeAgo(lastTime,getApplicationContext());;
                        LastSeenView.setText(LastSeenTime);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UserDatabaseReference.child("Chat").child(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(chatUser))
                {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+current_user + "/" + chatUser,chatAddMap);
                    chatUserMap.put("Chat/"+chatUser + "/" + current_user , chatAddMap);

                    UserDatabaseReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if(error != null)
                            {
                                Log.i("ERROR",error.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        loadMessages();
    }

    private void loadMessages() {
        UserDatabaseReference.child("messages").child(current_user).child(chatUser)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Messages messages = snapshot.getValue(Messages.class);
                        Log.i("MESSAGES",messages.toString());
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();

                        messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage() {
        String message = editMessage.getText().toString();
        if(!TextUtils.isEmpty(message)){

            String current_user_ref  = "messages/" + current_user + "/" + chatUser;
            String chat_user_ref = "messages/" + chatUser + "/" + current_user;

            DatabaseReference user_message_push = UserDatabaseReference.child("messages").child(current_user).child(chatUser).push();

            String push_id = user_message_push.getKey();

            Log.i("PUSH_ID",push_id);
            Log.i("CURRENT USER ", current_user);

            Map messageMap = new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",current_user);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id,messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id,messageMap);

            editMessage.setText("");

            UserDatabaseReference.child("Chat").child(current_user).child(chatUser).child("seen").setValue(true);
            UserDatabaseReference.child("Chat").child(current_user).child(chatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            UserDatabaseReference.child("Chat").child(chatUser).child(current_user).child("seen").setValue(false);
            UserDatabaseReference.child("Chat").child(chatUser).child(current_user).child("timestamp").setValue(ServerValue.TIMESTAMP);


            UserDatabaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error != null)
                    {
                        Log.i("CHAT LOG",error.getMessage());
                    }
                }
            });
        }
    }
}