package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ChatConversationAdapter;
import com.theyestech.yes_mobile.interfaces.APIService;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.Conversation;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.notifications.Client;
import com.theyestech.yes_mobile.notifications.Data;
import com.theyestech.yes_mobile.notifications.MyResponse;
import com.theyestech.yes_mobile.notifications.Sender;
import com.theyestech.yes_mobile.notifications.Token;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatConversationActivity extends AppCompatActivity {
    private Context context;

    private EditText etMessage;
    private TextView tvName, tvEmail;
    private ImageView ivBack, ivSend;
    private RecyclerView recyclerView;

    private String role;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference threadRef;
    private DatabaseReference conversationRef;
    private DatabaseReference reference;
    private ValueEventListener conversationListener;

    private Query query;

    private ChatConversationAdapter chatConversationAdapter;
    private Contact contact;
    private ArrayList<Conversation> conversationArrayList = new ArrayList<>();
//    private ChatThread thread;

    private String senderId;
    private String receiverId;
    private String receivername;
    private String photo_name;
    private String senderid;
    private Date currentDate;
    private String conversationId;
    private String message;
    private String threadId;
    private String fullname, email;

    APIService apiService;

    boolean notify = false;
    boolean isEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        context = this;

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

//        contact = getIntent().getParcelableExtra("CONTACT");
//        thread = getIntent().getParcelableExtra("THREAD");
        Intent extras = getIntent();
        Bundle bundle = extras.getExtras();
        assert bundle != null;
        receiverId = bundle.getString("RECEIVER_ID");
        receivername = bundle.getString("RECEIVER_NAME");
        threadId = bundle.getString("THREAD_ID");
        photo_name = bundle.getString("RECEIVER_PHOTO");
        senderid = bundle.getString("THREAD_SENDER_ID");

        senderId = firebaseUser.getUid();
//        receiverId = contact.getId();
//        threadId = thread.getId();
        fullname = firebaseUser.getDisplayName();
        email = firebaseUser.getEmail();
//        Debugger.logD("contact " + receiverId);
//        Debugger.logD("thread " + threadId);

//        if (!thread.getSenderId().equals(firebaseUser.getUid())) {
//            threadRef = FirebaseDatabase.getInstance().getReference("Threads");
//            threadRef.child(threadId).child("isSeen").setValue(true);
//        }
        if (!senderid.equals(firebaseUser.getUid())) {
            threadRef = FirebaseDatabase.getInstance().getReference("Threads");
            threadRef.child(threadId).child("isSeen").setValue(true);
        }

        role = UserRole.getRole(context);


        initializeUI();
    }

    private void initializeUI() {
        tvName = findViewById(R.id.tv_ChatConversationName);
        ivBack = findViewById(R.id.iv_ChatConversationBack);
        ivSend = findViewById(R.id.iv_ChatConversationSend);
        etMessage = findViewById(R.id.et_ChatConversationMessage);
        recyclerView = findViewById(R.id.rv_ChatConversation);

        setHeader();

        getAllConversation();

        ivSend.setEnabled(false);
        ivSend.setImageResource(R.drawable.ic_send_colored_disabled);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivSend.setEnabled(true);
                ivSend.setImageResource(R.drawable.ic_send_colored);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString();
                if (!message.isEmpty()){
                    notify = true;
                    sendMessage();
                    ivSend.setEnabled(false);
                    ivSend.setImageResource(R.drawable.ic_send_colored_disabled);
                }
            }
        });
    }

    private void setHeader() {
        tvName.setText(receivername);
//        tvEmail.setText(contact.getEmail());
    }

    private void getAllConversation() {
        conversationRef = FirebaseDatabase.getInstance().getReference("Conversations");
        conversationListener = conversationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversationArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Conversation conversation = snapshot.getValue(Conversation.class);
                    assert conversation != null;
                    if (conversation.getThreadId().equals(threadId)) {
                        conversationArrayList.add(conversation);
                    }
                }

//                if (!thread.getSenderId().equals(senderId)) {
//                    threadRef = FirebaseDatabase.getInstance().getReference("Threads");
//                    threadRef.child(threadId).child("isSeen").setValue(true);
//                }
                if (!threadRef.equals(senderId)) {
                    threadRef = FirebaseDatabase.getInstance().getReference("Threads");
                    threadRef.child(threadId).child("isSeen").setValue(true);
                }

                Collections.sort(conversationArrayList, Conversation.ConversationComparator);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                chatConversationAdapter = new ChatConversationAdapter(context, conversationArrayList, photo_name);
                chatConversationAdapter.setClickListener(new OnClickRecyclerView() {
                    @Override
                    public void onItemClick(View view, int position, int fromButton) {

                    }
                });
                recyclerView.setAdapter(chatConversationAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage() {
        long timeStamp = System.currentTimeMillis() / 1000;
        currentDate = Calendar.getInstance().getTime();

        conversationId = UUID.randomUUID().toString() + timeStamp;
        conversationRef = FirebaseDatabase.getInstance().getReference("Conversations").child(conversationId);
        HashMap<String, Object> conversationHashMap = new HashMap<>();
        conversationHashMap.put("id", conversationId);
        conversationHashMap.put("threadId", threadId);
        conversationHashMap.put("senderId", senderId);
        conversationHashMap.put("receiverId", receiverId);
        conversationHashMap.put("message", message);
        conversationHashMap.put("messageDateCreated", currentDate);
        conversationRef.setValue(conversationHashMap);

        threadRef = FirebaseDatabase.getInstance().getReference("Threads");
        threadRef.child(threadId).child("senderId").setValue(senderId);
        threadRef.child(threadId).child("receiverId").setValue(receiverId);
        threadRef.child(threadId).child("isSeen").setValue(false);
        threadRef.child(threadId).child("lastMessage").setValue(message);
        threadRef.child(threadId).child("lastMessageDateCreated").setValue(currentDate);

//        if (notify) {
//            Debugger.logD("Message fuck");
//            sendNotification(receiverId, fullname, message);
//            Debugger.logD("Message Sent");
//        }
//        notify = false;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserEducator userEducator = dataSnapshot.getValue(UserEducator.class);
                if (notify) {
//                    if(userEducator.getFirsname() == null){
//                        sendNotification(receiverId, userEducator.getFirsname(), message);
//                    }else{
                        sendNotification(receiverId, email, message);
                    //}
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        etMessage.setText("");
        etMessage.requestFocus();
    }
    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(senderId, R.mipmap.ic_launcher, username+": "+message, "New Message", currentDate, "text",
                            receiverId);
                    Debugger.logD("threadId " + threadId);
                    Debugger.logD("contact " + contact);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void currentUser(String receiverId){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", receiverId);
        editor.apply();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(receiverId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(conversationListener);
        status("offline");
        currentUser("none");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conversationListener != null)
            conversationRef.removeEventListener(conversationListener);
    }
}
