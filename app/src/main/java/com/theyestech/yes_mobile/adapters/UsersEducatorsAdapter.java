package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.MessageActivity;
import com.theyestech.yes_mobile.models.Chat;
import com.theyestech.yes_mobile.models.UserEducator;

import java.util.ArrayList;

public class UsersEducatorsAdapter extends RecyclerView.Adapter<UsersEducatorsAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<UserEducator> mEducators;
    private boolean ischat;

    String theLastMessage;
    public UsersEducatorsAdapter(Context context, ArrayList<UserEducator> educators, boolean ischats){
        mEducators = educators;
        mContext = context;
        ischat = ischats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.educator_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final UserEducator userEducator = mEducators.get(i);
        viewHolder.username.setText(userEducator.getEmail_address());
//        if (userEducator.getImage().equals("default")){
//            viewHolder.profile_image.setImageResource(R.drawable.yes_logo);
//        } else {
            Glide.with(mContext).load(userEducator.getImage()).into(viewHolder.profile_image);
        //}

        if (ischat){
            lastMessage(userEducator.getId(), viewHolder.last_msg);
        } else {
            viewHolder.last_msg.setVisibility(View.GONE);
        }

//        if (ischat){
//            if (userEducator.getStatus().equals("online")){
//                viewHolder.img_on.setVisibility(View.VISIBLE);
//                viewHolder.img_off.setVisibility(View.GONE);
//            } else {
//                viewHolder.img_on.setVisibility(View.GONE);
//                viewHolder.img_off.setVisibility(View.VISIBLE);
//            }
//        } else {
//            viewHolder.img_on.setVisibility(View.GONE);
//            viewHolder.img_off.setVisibility(View.GONE);
//        }
        final String role = "EDUCATOR";
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putString("ROLE", role);
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("ROLE", role);
                intent.putExtra("userid", userEducator.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEducators.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.iv_ProfileEducatorImage);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }
    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
