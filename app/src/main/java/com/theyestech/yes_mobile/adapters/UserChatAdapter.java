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
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.MessageActivity;
import com.theyestech.yes_mobile.models.Chat;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    private View view;
    private Context context;
    private ArrayList<Contact> contactArrayList;
    private boolean ischat;
    private LayoutInflater layoutInflater;

    String theLastMessage;

    public UserChatAdapter(Context context, ArrayList<Contact> contactArrayList, boolean ischat){
        this.contactArrayList = contactArrayList;
        this.context = context;
        this.ischat = ischat;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_chat_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Contact contact = contactArrayList.get(i);

        if (contact.getFullName().isEmpty() || contact.getFullName().equals("")){
            if (contact.getEmail().length() <= 6){
                viewHolder.name.setText(contact.getEmail());
            }else{
                viewHolder.name.setText(contact.getEmail().substring(0, 6) + "." );
            }
        }else{
            if (contact.getFullName().length() <= 6){
                viewHolder.name.setText(contact.getFullName());
            }else{
                viewHolder.name.setText(contact.getFullName().substring(0, 6) + "." );
            }
        }

        if (contact.getPhotoName().equals("default")){
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context)
                    .load(HttpProvider.getProfileDir()  + contact.getPhotoName())
                    .apply(GlideOptions.getOptions())
                    .into(viewHolder.profile_image);
        }
        if (ischat){
            if (contact.getStatus().equals("online")){
                viewHolder.img_on.setVisibility(View.VISIBLE);
                viewHolder.img_off.setVisibility(View.GONE);
            } else {
                viewHolder.img_on.setVisibility(View.GONE);
                viewHolder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.img_on.setVisibility(View.GONE);
            viewHolder.img_off.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", contact.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            //last_msg = itemView.findViewById(R.id.last_msg);
        }
    }
    //check for last message
//    private void lastMessage(final String userid, final TextView last_msg){
//        theLastMessage = "default";
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (firebaseUser != null && chat != null) {
//                        if (chat.getReceiverId().equals(firebaseUser.getUid()) && chat.getSenderId().equals(userid) ||
//                                chat.getReceiverId().equals(userid) && chat.getSenderId().equals(firebaseUser.getUid())) {
//                            theLastMessage = chat.getMessage();
//                        }
//                    }
//                }
//
//                switch (theLastMessage){
//                    case  "default":
//                        last_msg.setText("No Message");
//                        break;
//
//                    default:
//                        last_msg.setText(theLastMessage);
//                        break;
//                }
//
//                theLastMessage = "default";
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
