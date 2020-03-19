package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ChatConversationAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.Conversation;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;

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

    private ChatConversationAdapter chatConversationAdapter;
    private Contact contact;
    private ArrayList<Conversation> conversationArrayList = new ArrayList<>();
    private ChatThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        context = this;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        contact = getIntent().getParcelableExtra("CONTACT");
        thread = getIntent().getParcelableExtra("THREAD");

        if (thread.getSenderId().equals(firebaseUser.getUid())){
            threadRef = FirebaseDatabase.getInstance().getReference("Threads");
            threadRef.child(thread.getId()).child("isSeen").setValue(true);
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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
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
                    if (conversation.getThreadId().equals(thread.getId())) {
                        conversationArrayList.add(conversation);
                    }
                }

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

    private void sendMessage(){
    }
}
