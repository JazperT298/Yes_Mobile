package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.theyestech.yes_mobile.adapters.ContactDropdownAdapter;
import com.theyestech.yes_mobile.adapters.MessageAdapter;
import com.theyestech.yes_mobile.interfaces.APIService;
import com.theyestech.yes_mobile.models.Chat;
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.notifications.Client;
import com.theyestech.yes_mobile.notifications.Data;
import com.theyestech.yes_mobile.notifications.MyResponse;
import com.theyestech.yes_mobile.notifications.Sender;
import com.theyestech.yes_mobile.notifications.Token;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.KeyboardHandler;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatNewConversationActivity extends AppCompatActivity {
    private Context context;

    private EditText etMessage;
    private ImageView ivBack, ivSend,imageView56;
    private AutoCompleteTextView etSearch;

    private String role;
    private boolean doneSelecting;
    private String message;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference threadRef;
    private DatabaseReference conversationRef;

    private String receiverId;
    private String senderId;
    private String threadId;
    private Date currentDate;
    private String conversationId;

    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private ContactDropdownAdapter contactDropdownAdapter;
    private Contact selectedContact = new Contact();
    boolean notify = false;

    private APIService apiService;

    private ArrayList<Chat> chatArrayList;
    private DatabaseReference reference;
    private MessageAdapter messageAdapter;
    private RecyclerView recycler_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new_conversation);

        context = this;

        contactArrayList = getIntent().getParcelableArrayListExtra("CONTACTARRAYLIST");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        role = UserRole.getRole(context);

        doneSelecting = false;

        senderId = firebaseUser.getUid();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        initializeUI();
    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_NewConversationBack);
        ivSend = findViewById(R.id.iv_NewConversationSend);
        etSearch = findViewById(R.id.et_NewConversationSearch);
        etMessage = findViewById(R.id.et_NewConversationMessage);
        recycler_view = findViewById(R.id.recycler_view);
        imageView56 = findViewById(R.id.imageView56);
        etSearch.requestFocus();

        contactDropdownAdapter = new ContactDropdownAdapter(context, R.layout.listrow_chat_contacts_dropdown, contactArrayList);
        etSearch.setAdapter(contactDropdownAdapter);

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
                    sendUserMessage(firebaseUser.getUid(), receiverId, etMessage.getText().toString(), currentDate);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                contactDropdownAdapter.getFilter().filter(charSequence);
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
                    receiverId = selectedContact.getId();
                    doneSelecting = true;

                    readUserMessages(firebaseUser.getUid(), receiverId, selectedContact.getPhotoName());
                }

                KeyboardHandler.closeKeyboard(etSearch, context);
            }
        });
    }

    private void sendUserMessage(String senderId, final String receiverId, String message, Date currentDate){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderId", senderId);
        hashMap.put("receiverId", receiverId);
        hashMap.put("message", message);
        hashMap.put("messageDateCreated", currentDate);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);


        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(receiverId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(receiverId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiverId)
                .child(firebaseUser.getUid());
        chatRefReceiver.child("id").setValue(firebaseUser.getUid());

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                if (notify) {
                    sendUserNotification(receiverId, contact.getFullName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        finish();
    }

    private void sendUserNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message", currentDate,
                            receiverId);

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

    private void readUserMessages(final String myid, final String userid, final String imageurl){
        chatArrayList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiverId().equals(myid) && chat.getSenderId().equals(userid) ||
                            chat.getReceiverId().equals(userid) && chat.getSenderId().equals(myid)){
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("userid", userid);
                        startActivity(intent);
                        finish();
                    }
                }
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

        threadRef = FirebaseDatabase.getInstance().getReference("Threads").child(threadId);
        HashMap<String, Object> threadHashMap = new HashMap<>();
        threadHashMap.put("id", threadId);
        threadHashMap.put("senderId", senderId);
        threadHashMap.put("receiverId", receiverId);
        threadHashMap.put("lastMessage", message);
        threadHashMap.put("lastMessageDateCreated", currentDate);
        threadHashMap.put("isSeen", false);
        threadRef.setValue(threadHashMap);


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

        finish();

    }

    private void checkUserThread() {
        ProgressPopup.showProgress(context);

        threadRef = FirebaseDatabase.getInstance().getReference("Threads");
        threadRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatThread chatThread = snapshot.getValue(ChatThread.class);
                    assert chatThread != null;
                    if (chatThread.getSenderId().equals(senderId) && chatThread.getReceiverId().equals(selectedContact.getId())
                            || chatThread.getReceiverId().equals(senderId) && chatThread.getSenderId().equals(selectedContact.getId())) {
                        Intent intent = new Intent(context, ChatConversationActivity.class);
                        intent.putExtra("CONTACT", selectedContact);
                        intent.putExtra("THREAD", chatThread);
                        startActivity(intent);
                        finish();
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
