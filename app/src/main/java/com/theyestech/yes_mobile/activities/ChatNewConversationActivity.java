package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ContactDropdownAdapter;
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.utils.KeyboardHandler;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class ChatNewConversationActivity extends AppCompatActivity {
    private Context context;

    private EditText etMessage;
    private ImageView ivBack, ivSend;
    private AutoCompleteTextView etSearch;

    private String role;
    private boolean doneSelecting;
    private String message;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private String receiverId;
    private String senderId;
    private String threadId;
    private Date currentDate;
    private String conversationId;

    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private ContactDropdownAdapter contactDropdownAdapter;
    private Contact selectedContact = new Contact();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new_conversation);

        context = this;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        role = UserRole.getRole(context);

        doneSelecting = false;

        initializeUI();
    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_NewConversationBack);
        ivSend = findViewById(R.id.iv_NewConversationSend);
        etSearch = findViewById(R.id.et_NewConversationSearch);
        etMessage = findViewById(R.id.et_NewConversationMessage);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!doneSelecting) {
                    Toasty.warning(context, "Please select one of your contacts.").show();
                } else {
                    createNewThread();
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchContacts(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof Contact) {
                    selectedContact = (Contact) item;
                    etSearch.setText(selectedContact.getFullName());
                    doneSelecting = true;

                    senderId = firebaseUser.getUid();
                    checkUserThread();
                }

                KeyboardHandler.closeKeyboard(etSearch, context);
            }
        });
    }

    private void searchContacts(String text) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(text)
                .endAt(text + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    assert contact != null;
                    assert firebaseUser != null;
                    if (!contact.getId().equals(firebaseUser.getUid())) {
                        contactArrayList.add(contact);
                    }
                }

                contactDropdownAdapter = new ContactDropdownAdapter(context, R.layout.listrow_contacts_dropdown, contactArrayList);
                etSearch.setAdapter(contactDropdownAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createNewThread() {
        long timeStamp = System.currentTimeMillis() / 1000;
        threadId = UUID.randomUUID().toString() + timeStamp;
        receiverId = selectedContact.getId();
        message = etMessage.getText().toString();
        currentDate = Calendar.getInstance().getTime();

        DatabaseReference threadsRef = FirebaseDatabase.getInstance().getReference("Threads").child(threadId);

        HashMap<String, Object> threadHashMap = new HashMap<>();
        threadHashMap.put("id", threadId);
        threadHashMap.put("participant1", senderId);
        threadHashMap.put("participant2", receiverId);
        threadHashMap.put("lastMessage", message);
        threadHashMap.put("lastMessageDateCreated", currentDate);
        threadHashMap.put("isSeen", false);

        threadsRef.setValue(threadHashMap);

        conversationId = UUID.randomUUID().toString() + timeStamp;

        DatabaseReference conversation = FirebaseDatabase.getInstance().getReference("Conversations").child(conversationId);

        HashMap<String, Object> conversationHashMap = new HashMap<>();
        conversationHashMap.put("id", conversationId);
        conversationHashMap.put("threadId", threadId);
        conversationHashMap.put("senderId", senderId);
        conversationHashMap.put("receiverId", receiverId);
        conversationHashMap.put("message", message);
        conversationHashMap.put("messageDateCreated", currentDate);

        conversation.setValue(conversationHashMap);

        // add user to chat fragment
//        final DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("Conversations")
//                .child(senderId)
//                .child(receiverId);
//
//        senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()){
//                    senderRef.child("id").setValue(selectedContact.getId());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        final DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("Conversations")
//                .child(receiverId)
//                .child(senderId);
//        receiverRef.child("id").setValue(senderRef);
//
//        final String msg = message;
//
//        reference = FirebaseDatabase.getInstance().getReference("Educator").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Contact contact = dataSnapshot.getValue(Contact.class);
//                if (notify) {
//                    assert contact != null;
//                    sendEducatorNotifiaction(receiver, contact.getEmail(), msg);
//                }
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void checkUserThread() {
        ProgressPopup.showProgress(context);

        DatabaseReference threadsRef = FirebaseDatabase.getInstance().getReference("Threads");
        threadsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatThread chatThread = snapshot.getValue(ChatThread.class);
                    assert chatThread != null;
                    if (chatThread.getParticipant1().equals(senderId) || chatThread.getParticipant2().equals(senderId)){
                        Intent intent = new Intent(context, ChatConversationActivity.class);
                        startActivity(intent);
                    }
                }
                ProgressPopup.hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}