package com.theyestech.yes_mobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.FirebaseUserActivity;
import com.theyestech.yes_mobile.activities.MessageActivity;
import com.theyestech.yes_mobile.interfaces.APIService;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.notifications.Client;
import com.theyestech.yes_mobile.notifications.Data;
import com.theyestech.yes_mobile.notifications.MyResponse;
import com.theyestech.yes_mobile.notifications.Sender;
import com.theyestech.yes_mobile.notifications.Token;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseUserAdapter
        extends RecyclerView.Adapter<FirebaseUserAdapter.ViewHolder> {

    private Context mContext;
    private List<Contact> contacts;
    private boolean ischat;
    private String files;
    private String fileType;
    private Activity activity;

    FirebaseUser fuser;
    DatabaseReference reference;

    boolean notify = false;

    String theLastMessage;

    APIService apiService;
    private Date currentDate;
    private String role;

    public FirebaseUserAdapter(Context mContext, List<Contact> contacts, boolean ischat, String files, String fileType, Activity activity){
        this.contacts = contacts;
        this.mContext = mContext;
        this.ischat = ischat;
        this.files = files;
        this.fileType = fileType;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.firebase_user_list, parent, false);
        return new FirebaseUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        role = UserRole.getRole(mContext);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        currentDate = Calendar.getInstance().getTime();
        final Contact user = contacts.get(position);
        holder.username.setText(user.getFullName());
        if (user.getPhotoName().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
//            Glide.with(mContext).load(user.getPhotoName()).into(holder.profile_image);
            Glide.with(mContext)
                    .load(HttpProvider.getProfileDir() + user.getPhotoName())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                    .apply(GlideOptions.getOptions())
                    .into(holder.profile_image);
        }

//        if (ischat){
//            lastMessage(user.getId(), holder.last_msg);
//        } else {
//            holder.last_msg.setVisibility(View.GONE);
//        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Debugger.logD(files);
//                Debugger.logD(fileType);
//                Debugger.logD(user.getId());//receiver
//                Debugger.logD(fuser.getUid());//sender
                if (role.equals(UserRole.Educator())){
                    sendFile(fuser.getUid(), UserEducator.getFirstname(mContext) + " " + UserEducator.getLastname(mContext) , UserEducator.getImage(mContext), user.getId(), user.getFullName(), user.getPhotoName(), files, fileType);
                }else{
                    sendFile(fuser.getUid(), UserStudent.getFirstname(mContext) + " " + UserStudent.getLastname(mContext) , UserStudent.getImage(mContext), user.getId(), user.getFullName(), user.getPhotoName(), files, fileType);
                }

                Toast.makeText(mContext, "Shared Successfully!", Toast.LENGTH_SHORT).show();
                activity.finish();

            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    private void sendFile(String sender, String sendername, String senderphoto, String receiver, String receivername, String receiverphoto, String fileName, String fileType){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("sendername", sendername);
        hashMap.put("senderphoto", senderphoto);
        hashMap.put("receiver", receiver);
        hashMap.put("receivername", receivername);
        hashMap.put("receiverphoto", receiverphoto);
        hashMap.put("fileName", fileName);
        hashMap.put("fileType", fileType);
        hashMap.put("dateposted", currentDate);

        reference.child("Files").push().setValue(hashMap);


        // add user to chat fragment
        final DatabaseReference fileRef = FirebaseDatabase.getInstance().getReference("Fileslist")
                .child(fuser.getUid())
                .child(receiver);

        fileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    fileRef.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Fileslist")
                .child(receiver)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());

        final String msg = files;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                if (notify) {
                    if (contact.getFullName() == null){
                        sendNotification(receiver, contact.getEmail(), msg);

                    }else{
                        sendNotification(receiver, contact.getFullName(), msg);

                    }
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message", currentDate,
                            receiver);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

