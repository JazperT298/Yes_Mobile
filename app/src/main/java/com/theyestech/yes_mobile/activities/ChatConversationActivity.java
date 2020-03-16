package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
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
import com.theyestech.yes_mobile.adapters.ChatThreadsAdapter;
import com.theyestech.yes_mobile.adapters.ContactDropdownAdapter;
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
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private Contact contact;

    private ArrayList<Conversation> conversationArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        context = this;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        contact = getIntent().getParcelableExtra("CONTACT");

        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI(){
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
    }

    private void setHeader(){
        tvName.setText(contact.getFullName());
        tvEmail.setText(contact.getEmail());
    }

    private void getAllConversation(){
//        DatabaseReference conversationRef = FirebaseDatabase.getInstance().getReference("Conversations");
//        conversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                conversationArrayList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Conversation conversation = snapshot.getValue(Conversation.class);
//                    assert conversation != null;
//                    if (conversation.getParticipant1().equals(firebaseUser.getUid())) {
//                        for (Contact contact : contactArrayList) {
//                            if (chatThread.getParticipant2().equals(contact.getId())) {
//                                chatThread.setContact(contact);
//                                break;
//                            }
//                        }
//                        threadArrayList.add(chatThread);
//                    } else if (chatThread.getParticipant2().equals(firebaseUser.getUid())) {
//                        for (Contact contact : contactArrayList) {
//                            if (chatThread.getParticipant1().equals(contact.getId())) {
//                                chatThread.setContact(contact);
//                                break;
//                            }
//                        }
//                        threadArrayList.add(chatThread);
//                    }
//                }
//
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//                recyclerView.setHasFixedSize(true);
//                chatThreadsAdapter = new ChatThreadsAdapter(context, threadArrayList);
//                chatThreadsAdapter.setClickListener(new OnClickRecyclerView() {
//                    @Override
//                    public void onItemClick(View view, int position, int fromButton) {
//
//                    }
//                });
//                recyclerView.setAdapter(chatThreadsAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                swipeThreads.setRefreshing(false);
//            }
//        });
    }
}
