package com.theyestech.yes_mobile.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.ChatNewConversationActivity;
import com.theyestech.yes_mobile.activities.MessageActivity;
import com.theyestech.yes_mobile.adapters.ChatThreadsAdapter;
import com.theyestech.yes_mobile.adapters.UserChatAdapter;
import com.theyestech.yes_mobile.adapters.UserChatListAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Chatlist;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.notifications.Token;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserChatFragment extends Fragment {
    private View view;
    private Context context;

    private TextView tvHeader,tv_UserName;
    private ImageView ivProfile,iv_UserImage;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipe_ChatThreads;
    private RecyclerView rv_ChatThreads, rv_Contacts;
    private ConstraintLayout emptyIndicator;
    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBar;
    private UserChatAdapter userChatAdapter;
    private UserChatListAdapter userChatListAdapter;

    private String role;

    //Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference registerRef;
    private DatabaseReference userRef;
    private DatabaseReference threadRef;
    private FirebaseAuth firebaseAuth;
    private ValueEventListener userListener;
    private ValueEventListener threadListener;

    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private ArrayList<Contact> contactArrayList2 = new ArrayList<>();

    private ArrayList<ChatThread> threadArrayList = new ArrayList<>();
    private ChatThreadsAdapter chatThreadsAdapter;
    private ChatThread selectedThread = new ChatThread();

    private ArrayList<Chatlist> chatlistArrayList;
    private DatabaseReference reference;
    private Contact selectedContact;

    public UserChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_chat, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        role = UserRole.getRole(context);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        //threadRef = FirebaseDatabase.getInstance().getReference("Threads");

        checkFirebaseLogin();

    }

    private void initializeUI(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        iv_UserImage = view.findViewById(R.id.iv_UserImage);
        tv_UserName = view.findViewById(R.id.tv_UserName);
        rv_Contacts = view.findViewById(R.id.rv_Contacts);
        rv_ChatThreads = view.findViewById(R.id.rv_ChatThreads);
        swipe_ChatThreads = view.findViewById(R.id.swipe_ChatThreads);
        emptyIndicator = view.findViewById(R.id.view_EmptyChat);
        floatingActionButton = view.findViewById(R.id.fab_ChatThreadNew);
        progressBar = view.findViewById(R.id.progress_ChatThreads);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        tv_UserName.setText(UserEducator.getFirstname(context) + " " + UserEducator.getLastname(context) );
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(iv_UserImage);

        getAllContacts();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        getAllChats();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatNewConversationActivity.class);
                intent.putParcelableArrayListExtra("CONTACTARRAYLIST", contactArrayList);
                startActivity(intent);
            }
        });
    }

    private void getAllContacts(){
        accessingServer(true);
        //final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    assert contact != null;
                    if (contact.getId() == null){
                        Debugger.logD("s " + contact.getId() );
                    } else if (!contact.getId().equals(firebaseUser.getUid())){
                        contactArrayList.add(contact);
                    }
                    rv_Contacts.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
                    rv_Contacts.setHasFixedSize(true);
                    userChatAdapter = new UserChatAdapter(context, contactArrayList, false);
                    userChatAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedContact = contactArrayList.get(position);
                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("userid", selectedContact.getId());
                            context.startActivity(intent);
                        }
                    });
                    rv_Contacts.setAdapter(userChatAdapter);
                }
                accessingServer(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                accessingServer(false);
            }
        });
    }

    private void checkFirebaseLogin() {
        if (firebaseAuth.getCurrentUser() == null) {
            if (role.equals(UserRole.Educator())){
                if (UserEducator.getFirstname(context).equals("")){
                    Toasty.warning(context, "Please update your profile to use chat function").show();
                }else{
                    openWelcomeChatDialog();
                }
            }else{
                if (UserStudent.getFirstname(context).equals("")){
                    Toasty.warning(context, "Please update your profile to use chat function").show();
                }else{
                    openWelcomeChatDialog();
                }
            }
        } else {
            initializeUI();
            if (role.equals(UserRole.Educator())) {
                loginFirebase(UserEducator.getEmail(context).toLowerCase(), UserEducator.getPassword(context));
            }else {
                loginFirebase(UserStudent.getEmail(context).toLowerCase(), UserStudent.getPassword(context));
            }
        }
    }

    private void openWelcomeChatDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_welcome_chat, null);

        Button btnContinue;
        ImageView ivClose;

        btnContinue = dialogView.findViewById(R.id.btn_DialogWelcomeChatContinue);
        ivClose = dialogView.findViewById(R.id.iv_DialogWelcomeChatClose);

        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUserToFirebase(b);
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void registerUserToFirebase(final AlertDialog b) {
        ProgressPopup.showProgress(context);

        final String email = UserEducator.getEmail(context).toLowerCase();
        String password = UserEducator.getPassword(context);
        final String fullName = UserEducator.getFirstname(context) + " " + UserEducator.getLastname(context) + " " + UserEducator.getSuffix(context);
        final String search = UserEducator.getLastname(context).toLowerCase();
        final String role = UserRole.getRole(context);
        final String photoName = UserEducator.getImage(context);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ProgressPopup.hideProgress();
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            registerRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("email", email);
                            hashMap.put("fullName", fullName);
                            hashMap.put("status", "online");
                            hashMap.put("search", search);
                            hashMap.put("role", role);
                            hashMap.put("photoName", photoName);

                            registerRef.setValue(hashMap);

                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                assert firebaseUser != null;
                                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                                usersRef.child("status").setValue("online");
                                            }
                                        }
                                    });

                            if (b != null) {
                                b.dismiss();
                                initializeUI();
                            }


                        } else {
                            Toasty.warning(context, "Email address already exists.").show();
                        }
                    }
                });
    }

    private void loginFirebase(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            usersRef.child("status").setValue("online");
                        }
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private void accessingServer(boolean isAccessing) {
        floatingActionButton.setVisibility(isAccessing ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isAccessing ? View.VISIBLE : View.GONE);
    }

    private void getAllChats(){
        accessingServer(true);
        chatlistArrayList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlistArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    chatlistArrayList.add(chatlist);
                }
                Collections.reverse(chatlistArrayList);
                chatUserList();
                accessingServer(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                accessingServer(false);
            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }
    private void chatUserList() {
        contactArrayList2 = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactArrayList2.clear();
                emptyIndicator.setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Contact user = snapshot.getValue(Contact.class);
                    for (Chatlist chatlist : chatlistArrayList){
                        if (user.getId().equals(chatlist.getId())){
                            contactArrayList2.add(user);
                        }
                    }
                }
                Collections.reverse(contactArrayList2);

                rv_ChatThreads.setLayoutManager(new LinearLayoutManager(context));
                rv_ChatThreads.setHasFixedSize(true);

                userChatListAdapter = new UserChatListAdapter(context, contactArrayList2, true);
                rv_ChatThreads.setAdapter(userChatListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    @Override
    public void onResume() {
        super.onResume();
        //getAllChats();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
