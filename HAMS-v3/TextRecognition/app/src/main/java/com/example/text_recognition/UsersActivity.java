package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;

    FirebaseAuth firebaseAuth;

    String cur_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        toolbar = findViewById(R.id.user_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");

        firebaseAuth = FirebaseAuth.getInstance();

        cur_user = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = findViewById(R.id.UsersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onStart() {
        super.onStart();

        Query query = databaseReference.orderByChild("isDoctor").equalTo("false");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

        Log.i("INSIDE START","START METHOD");
        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model) {
                Log.i("INSIDE BIND","ON BIND VIEW HOLDER");
                Log.i("MODEL GET NAME",model.getName());

                Log.i("CURRENT USER : ",cur_user);
                Log.i("GET CURRENT USER POSITION : ", String.valueOf(getRef(position).getKey()));
                if(cur_user.equals(getRef(position).getKey())) {
                    holder.userImageView.setVisibility(View.GONE);
                    holder.userNameView.setVisibility(View.GONE);
                    holder.userStatusView.setVisibility(View.GONE);
                    holder.userOnlineView.setVisibility(View.GONE);
                }
                else {
                    holder.userImageView.setVisibility(View.VISIBLE);
                    holder.userNameView.setVisibility(View.VISIBLE);
                    holder.userStatusView.setVisibility(View.VISIBLE);
                    holder.setDisplayName(model.getName());
                    holder.setUserImage(model.getImage());
                    holder.setUserStatus(model.getStatus());

                    holder.userOnlineView.setVisibility(View.GONE);
                }


                    final String user_id = getRef(position).getKey();
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                            intent.putExtra("user_id", user_id);
                            startActivity(intent);
                        }
                    });
            }
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.i("INSIDE CREATE","ON CREATE VIEW HOLDER");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout,parent,false);
                UsersViewHolder viewHolder =  new UsersViewHolder(view);
                return viewHolder;
            }
        };
        firebaseRecyclerAdapter.startListening();

        Log.i("FIREBASE RECYCLER ADAPTER", String.valueOf(firebaseRecyclerAdapter));
        recyclerView.setAdapter(firebaseRecyclerAdapter);

//
//        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
//
//                Users.class,
//                R.layout.single_user_layout,
//                UsersViewHolder.class,
//                databaseReference
//
//        ) {
//            @Override
//            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {
//
//                usersViewHolder.setDisplayName(users.getName());
//                usersViewHolder.setUserImage(users.getImage());
//
//                final String user_id = getRef(position).getKey();
//
//                usersViewHolder.view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
//                        profileIntent.putExtra("user_id", user_id);
//                        startActivity(profileIntent);
//
//                    }
//                });
//
//            }
//        };
//
//
//        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView userNameView;
        CircleImageView userImageView;
        ImageView userOnlineView;
        TextView userStatusView;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            userNameView = (TextView) view.findViewById(R.id.UserSingleName);
            userImageView = (CircleImageView) view.findViewById(R.id.UserSingleImage);
            userOnlineView = (ImageView) view.findViewById(R.id.online_image_view);
            userStatusView = view.findViewById(R.id.UserSingleStatus);

        }

            public void setDisplayName(String name){
                userNameView.setText(name);

            }

            public void setUserImage(String image){

                Picasso.get().load(image).placeholder(R.drawable.avatar).into(userImageView);
            }
        public void setUserStatus(String status){

            userStatusView.setText(status);
        }
        public void setUserOnline(String online_status) {

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }
    }

}