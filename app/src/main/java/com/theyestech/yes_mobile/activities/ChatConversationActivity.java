package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.Conversation;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

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
    private ValueEventListener conversationListener;

    private Query query;

    private ChatConversationAdapter chatConversationAdapter;
    private Contact contact;
    private ArrayList<Conversation> conversationArrayList = new ArrayList<>();
    private ChatThread thread;

    private String senderId;
    private String receiverId;
    private Date currentDate;
    private String conversationId;
    private String message;
    private String threadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        context = this;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        contact = getIntent().getParcelableExtra("CONTACT");
        thread = getIntent().getParcelableExtra("THREAD");

        senderId = firebaseUser.getUid();
        receiverId = contact.getId();
        threadId = thread.getId();
        Debugger.logD("contact " + receiverId);
        Debugger.logD("thread " + threadId);

        if (!thread.getSenderId().equals(firebaseUser.getUid())) {
            threadRef = FirebaseDatabase.getInstance().getReference("Threads");
            threadRef.child(threadId).child("isSeen").setValue(true);
        }

        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI() {
        tvName = findViewById(R.id.tv_ChatConversationName);
        tvEmail = findViewById(R.id.tv_ChatConversationEmail);
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
                    sendMessage();
                    ivSend.setEnabled(false);
                    ivSend.setImageResource(R.drawable.ic_send_colored_disabled);
                }
            }
        });
    }

    private void setHeader() {
        tvName.setText(contact.getFullName());
        tvEmail.setText(contact.getEmail());
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

                if (!thread.getSenderId().equals(senderId)) {
                    threadRef = FirebaseDatabase.getInstance().getReference("Threads");
                    threadRef.child(threadId).child("isSeen").setValue(true);
                }

                Collections.sort(conversationArrayList, Conversation.ConversationComparator);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                chatConversationAdapter = new ChatConversationAdapter(context, conversationArrayList, contact);
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

        etMessage.setText("");
        etMessage.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conversationListener != null)
            conversationRef.removeEventListener(conversationListener);
    }
}
