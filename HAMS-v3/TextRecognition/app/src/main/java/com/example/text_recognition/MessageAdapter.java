package com.example.text_recognition;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<Messages> MessageList;

    FirebaseAuth auth;

    CircleImageView circleImageView;
    CircleImageView circleImageView2;

    DatabaseReference userRef;

    public MessageAdapter(List<Messages> MessageList)
    {
        this.MessageList = MessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);

        circleImageView = view.findViewById(R.id.message_single_image);
        circleImageView2 = view.findViewById(R.id.message_single_image);

        return new MessageViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        auth = FirebaseAuth.getInstance();

        String current_user_id = auth.getCurrentUser().getUid();

        Messages c = MessageList.get(position);

        String from_user = c.getFrom();

        Log.i("FROM USER",from_user);



        if(from_user.equals(current_user_id)){
//            userRef = FirebaseDatabase.getInstance().getReference().child("Users");
//            Log.i("USER_REF-----------------",userRef.toString());
//            Log.i("USER_REF_CHILD-#################",userRef.child(current_user_id).getKey().toString());
//            userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.i("IMAGE-%%%%%%%%%%%%%%%%%%",dataSnapshot.child("image").getValue().toString());
//                    String image = dataSnapshot.child("image").getValue().toString();
//                    Picasso.get().load(image).placeholder(R.drawable.avatar).into(circleImageView);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);
        }
        else
        {
//            userRef = FirebaseDatabase.getInstance().getReference().child("Users");
//            Log.i("USER_REF!!!!!!!!!!!!!!!!!!!!!!!!",userRef.toString());
//            Log.i("USER_REF_CHILD_@@@@@@@@@@@@@@@@@@",userRef.child(from_user).getKey().toString());
//            userRef.child(from_user).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.i("IMAGE-$$$$$$$$$$$$$$$$$$$$",dataSnapshot.child("image").getValue().toString());
//                    String image = dataSnapshot.child("image").getValue().toString();
//                    Picasso.get().load(image).placeholder(R.drawable.avatar).into(circleImageView2);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
        }

        holder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder(@NonNull View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_single_text);
            profileImage = (CircleImageView) view.findViewById(R.id.message_single_image);
        }
    }
}
