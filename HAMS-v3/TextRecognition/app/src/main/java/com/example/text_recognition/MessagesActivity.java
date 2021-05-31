package com.example.text_recognition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    Toolbar toolbar;

    DatabaseReference userRef;

    FirebaseAuth firebaseAuth;

    ProgressDialog mLoginProgress;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        drawerLayout = findViewById(R.id.drawer_layout);

        mLoginProgress = new ProgressDialog(this);

//        toolbar = findViewById(R.id.user_app_bar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Chat App");



        firebaseAuth = FirebaseAuth.getInstance();
        mConvList = (RecyclerView) findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);

        if(firebaseAuth.getCurrentUser() != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mConvList.setLayoutManager(linearLayoutManager);
    }


    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConvDatabase.orderByChild("timestamp");
        FirebaseRecyclerOptions<Conv> conv_options =
                new FirebaseRecyclerOptions.Builder<Conv>()
                        .setQuery(conversationQuery, Conv.class)
                        .build();




        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(conv_options) {
            @Override
            protected void onBindViewHolder(@NonNull final ConvViewHolder holder, int position, @NonNull final Conv model) {


                final String list_user_id = getRef(position).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.setMessage(data, model.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);

                        }

                        holder.setName(userName);
                        Log.i("USER_IMAGE++++++++++++++++",image);
                        holder.setUserImage(image);

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                chatIntent.putExtra("user_name", userName);
                                startActivity(chatIntent);

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MessagesActivity.ConvViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout,parent,false));
            }
        };

        firebaseConvAdapter.startListening();
        mConvList.setAdapter(firebaseConvAdapter);

    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setMessage(String message, boolean isSeen){

            TextView userStatusView = (TextView) mView.findViewById(R.id.UserSingleStatus);
            userStatusView.setText(message);

            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.UserSingleName);
            userNameView.setText(name);

        }

        @SuppressLint("LongLogTag")
        public void setUserImage(String thumb_image){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.UserSingleImage);
            Log.i("THUMB:IMAGE==================",thumb_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.avatar).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.online_image_view);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


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

            Intent intent = new Intent(MessagesActivity.this,MessagesActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.account_setting)
        {
            Intent intent = new Intent(MessagesActivity.this,AccountSetting.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.users)
        {
            Intent intent = new Intent(MessagesActivity.this,UsersActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() ==  R.id.doctors)
        {
            Intent intent = new Intent(MessagesActivity.this,AllDoctorsActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.friends)
        {
            Intent intent = new Intent(MessagesActivity.this,FriendsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void ClickMenu(View view)
    {
        openDrawer(drawerLayout);
    }
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer(DrawerLayout drawerLayout)
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public void ClickAlarmList(View view)
    {
        redirectActivity(this,AlarmList.class);
    }
    public void ClickAddAlarm(View view)
    {
        redirectActivity(this,Alarm.class);
    }
    public void ClickAddMedicine(View view)
    {
        redirectActivity(this,MainActivity.class);
    }

    public void ClickScanMedicine(View view)
    {
        redirectActivity(this,ScanMedicine.class);
    }
    public void ClickQRScan(View view)
    {
        redirectActivity(this,ScanQR.class);
    }

    public void ClickMap(View view)
    {
    }

    public void ClickChat(View view)
    {
        recreate();
    }

    public void ClickOption(View view){
            //Log.i("OPEN","OPEN OPTIONS MENU");
            PopupMenu popupMenu = new PopupMenu(MessagesActivity.this, view);//View will be an anchor for PopupMenu
            popupMenu.inflate(R.menu.chat_menu);
            Menu menu = popupMenu.getMenu();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(item.getItemId() ==  R.id.logout)
                    {
                        FirebaseAuth.getInstance().signOut();
                        userRef.child("online").setValue(ServerValue.TIMESTAMP);

                        Intent intent = new Intent(MessagesActivity.this,StartActivity.class);
                        startActivity(intent);
                    }
                    if(item.getItemId() == R.id.account_setting)
                    {
                        Intent intent = new Intent(MessagesActivity.this,AccountSetting.class);
                        startActivity(intent);
                    }
                    if(item.getItemId() == R.id.users)
                    {
                        Intent intent = new Intent(MessagesActivity.this,UsersActivity.class);
                        startActivity(intent);
                    }
                    if(item.getItemId() ==  R.id.doctors)
                    {
                        Intent intent = new Intent(MessagesActivity.this,AllDoctorsActivity.class);
                        startActivity(intent);
                    }
                    if(item.getItemId() == R.id.friends)
                    {
                        Intent intent = new Intent(MessagesActivity.this,FriendsActivity.class);
                        startActivity(intent);
                    }
                    return true;
                }
            });
            popupMenu.show();
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
        Log.d("lifecycle","onPause invoked");
    }
}