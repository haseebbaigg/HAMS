package com.example.text_recognition;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView FriendsrecyclerView;

    DatabaseReference databaseReference;

    DatabaseReference UsersDatabaseReference;

    String userID;
    View mainView;
    static String name;
    String image;
    String onlineStatus;

    String list_user_id;

    ViewGroup parent1;
    int viewType1;

    Boolean got_data = false;

    ArrayList<String> arrayList = new ArrayList<>();

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        // Inflate the layout for this fragment



        toolbar = findViewById(R.id.user_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Friends");

        firebaseAuth = FirebaseAuth.getInstance();

        String currentUser = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUser);

        UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendsrecyclerView = (RecyclerView) findViewById(R.id.FriendsList);

        FriendsrecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onStart() {
        super.onStart();
//
//        //getData();
//        FirebaseRecyclerOptions<Friends> options2 =
//                new FirebaseRecyclerOptions.Builder<Friends>()
//                        .setQuery(databaseReference, Friends.class)
//                        .build();
//
//        Log.i("INSIDE START", "START METHOD");
//        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options2) {
//            @Override
//            public void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, final Friends model) {
//                Log.i("INSIDE BIND", "ON BIND VIEW HOLDER");
//                String userIDS = getRef(position).getKey();
//                UsersDatabaseReference.child(userIDS).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot){
//                        String namee = snapshot.child("name").getValue().toString();
//                        holder.setName(namee);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                });
//
//            }
//            @NonNull
//            @Override
//            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                Log.i("INSIDE CREATE", "ON CREATE VIEW HOLDER");
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout, parent, false);
//                FriendsViewHolder viewHolder2 = new FriendsViewHolder(view);
//                return viewHolder2;
//            }
//        };
//
//
//        Log.i("FIREBASE RECYCLER ADAPTER 2", String.valueOf(firebaseRecyclerAdapter2));
//        FriendsrecyclerView.setAdapter(firebaseRecyclerAdapter2);
//
//        firebaseRecyclerAdapter2.startListening();

        FirebaseRecyclerOptions<Friends> friend_options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(databaseReference, Friends.class)
                        .build();


        Log.i("INSIDE START","START METHOD");

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(friend_options) {

            @Override
            protected void onBindViewHolder(final FriendsViewHolder holder, int position, Friends model) {
                final String list_user_id = getRef(position).getKey();
                holder.setDate(model.getDate());
                Log.i("LIST USER ID",list_user_id);
                UsersDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        name = snapshot.child("name").getValue().toString();
                        image = snapshot.child("image").getValue().toString();
                        Log.i("USER NAME",name);

                        if(snapshot.hasChild("online")) {
                            onlineStatus = snapshot.child("online").getValue().toString();
                            holder.setUserOnline(onlineStatus);
                        }
                        Log.i("USER IMAGE",image);

                        holder.setName(name);
                        holder.setUserImage(image);
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent intent = new Intent(FriendsActivity.this, ProfileActivity.class);
                                            intent.putExtra("user_id", list_user_id);
                                            startActivity(intent);
                                        } else if (which == 1) {
                                            Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
                                            intent.putExtra("user_id", list_user_id);
                                            intent.putExtra("user_name", name);
                                            startActivity(intent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

            }
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.i("CREAT$E VIEW","ON CREATE VIEW HOLDER FRIENDS");
                return new FriendsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout,parent,false));
            }
        };
        firebaseRecyclerAdapter.startListening();
        FriendsrecyclerView.setAdapter(firebaseRecyclerAdapter);

    }
//        if(got_data == true) {
//            FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
//
//                    Friends.class,
//                    R.layout.single_user_layout,
//                    FriendsViewHolder.class,
//                    databaseReference
//            ) {
//                @Override
//                protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int i) {
//
//
//                    friendsViewHolder.setDate(friends.getDate());
//
//                    list_user_id = getRef(i).getKey();
//                    Log.i("GET POSITION", String.valueOf(i));
//                    Log.i("GET REF KEY", list_user_id);
//                    Log.i("FRIEND DB KEY", databaseReference.getKey());
//                    Log.i("FRIEND USER NAME", friends.getDate());
////                friendsViewHolder.setName(arrayList.get(i));
////                friendsViewHolder.setUserImage(arrayList.get(i+1));
////                if(UsersDatabaseReference.child(list_user_id) != null) {
////                    UsersDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////
////                            name = dataSnapshot.child("name").getValue().toString();
////                            image = dataSnapshot.child("image").getValue().toString();
////
////                            if (dataSnapshot.hasChild("online")) {
////
////                                String userOnline = dataSnapshot.child("online").getValue().toString();
////                                friendsViewHolder.setUserOnline(userOnline);
////
////                            }
////
////                            friendsViewHolder.setName(name);
////                            friendsViewHolder.setUserImage(image);
////
////                            friendsViewHolder.view.setOnClickListener(new View.OnClickListener() {
////                                @Override
////                                public void onClick(View view) {
////
////                                    CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};
////
////                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
////
////                                    builder.setTitle("Select Options");
////                                    builder.setItems(options, new DialogInterface.OnClickListener() {
////                                        @Override
////                                        public void onClick(DialogInterface dialogInterface, int i) {
////
////                                            //Click Event for each item.
////                                            if (i == 0) {
////
////                                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
////                                                profileIntent.putExtra("user_id", list_user_id);
////                                                startActivity(profileIntent);
////
////                                            }
////
////                                            if (i == 1) {
////
////                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
////                                                chatIntent.putExtra("user_id", list_user_id);
////                                                chatIntent.putExtra("user_name", name);
////                                                startActivity(chatIntent);
////
////                                            }
////
////                                        }
////                                    });
////
////                                    builder.show();
////
////                                }
////                            });
////
////
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
////                }
//                }
//            };
//
//            FriendsrecyclerView.setAdapter(friendsRecyclerViewAdapter);
//        }
//        else
//        {
//            getData();
//        }

//    public void recyclerView()
//    {
//        FirebaseRecyclerOptions<Friends> options3 =
//                new FirebaseRecyclerOptions.Builder<Friends>()
//                        .setQuery(databaseReference, Friends.class)
//                        .build();
//        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options3){
//            @NonNull
//            @Override
//            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                return null;

//                friendsViewHolder.setDate(friends.getDate());
//
//                list_user_id = getRef(i).getKey();
//                Log.i("GET POSITION", String.valueOf(i));
//                Log.i("GET REF KEY", list_user_id);
//                Log.i("FRIEND DB KEY", databaseReference.getKey());
//                Log.i("FRIEND USER NAME", friends.getDate());
//                Log.i("USER NAME 2222222222222222", arrayList.get(i));
//                Log.i("USER IMAGE 222222222222222", arrayList.get(i+1));
//                friendsViewHolder.setName(arrayList.get(i));
//                friendsViewHolder.setUserImage(arrayList.get(i+1));
//                if(UsersDatabaseReference.child(list_user_id) != null) {
//                    UsersDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            name = dataSnapshot.child("name").getValue().toString();
//                            image = dataSnapshot.child("image").getValue().toString();
//
//                            if (dataSnapshot.hasChild("online")) {
//
//                                String userOnline = dataSnapshot.child("online").getValue().toString();
//                                friendsViewHolder.setUserOnline(userOnline);
//
//                            }
//
//                            friendsViewHolder.setName(name);
//                            friendsViewHolder.setUserImage(image);
//
//                            friendsViewHolder.view.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//
//                                    CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};
//
//                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//                                    builder.setTitle("Select Options");
//                                    builder.setItems(options, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                                            //Click Event for each item.
//                                            if (i == 0) {
//
//                                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
//                                                profileIntent.putExtra("user_id", list_user_id);
//                                                startActivity(profileIntent);
//
//                                            }
//
//                                            if (i == 1) {
//
//                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
//                                                chatIntent.putExtra("user_id", list_user_id);
//                                                chatIntent.putExtra("user_name", name);
//                                                startActivity(chatIntent);
//
//                                            }
//
//                                        }
//                                    });
//
//                                    builder.show();
//
//                                }
//                            });
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {
//
//            }
//        };
//        FriendsrecyclerView.setAdapter(friendsRecyclerViewAdapter);
//    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View view;

        TextView username;
        CircleImageView imageView;
        ImageView onlineImageView;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            view = itemView;

        }
        public void setDate(String date){

            TextView userStatusView = (TextView) view.findViewById(R.id.UserSingleStatus);
            userStatusView.setText(date);

        }
        public void setName(String name){

            TextView userNameView = (TextView) view.findViewById(R.id.UserSingleName);
            userNameView.setText(name);

        }

        public void setUserImage(String image){

            CircleImageView userImageView = (CircleImageView) view.findViewById(R.id.UserSingleImage);
            Picasso.get().load(image).placeholder(R.drawable.avatar).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) view.findViewById(R.id.online_image_view);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }
    }
//    public void getData(){
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.i("Count " ,""+dataSnapshot.getChildrenCount());
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    Log.i("Get Data _!!!!!!!!!!", dataSnapshot.getKey());
//                    UsersDatabaseReference.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
//                        @SuppressLint("LongLogTag")
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Log.i("USER NAME _!!!!!!!!!!!!!",dataSnapshot.child("name").getValue().toString());
//                            Log.i("USER IMAGE _ !!!!!!!!!!!!!!!",dataSnapshot.child("image").getValue().toString());
//                            String name1 = dataSnapshot.child("name").getValue().toString();
//                            String image1 = dataSnapshot.child("image").getValue().toString();
//                            arrayList.add(name1);
//                            arrayList.add(image1);
//                            if (dataSnapshot.hasChild("online")) {
//
//                                String userOnline1 = dataSnapshot.child("online").getValue().toString();
//                                arrayList.add(userOnline1);
//                            }
//                            got_data = true;
//                        }
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

}