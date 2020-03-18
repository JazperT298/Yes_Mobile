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
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.ChatConversationActivity;
import com.theyestech.yes_mobile.activities.ChatNewConversationActivity;
import com.theyestech.yes_mobile.adapters.ChatThreadsAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ChatFragment extends Fragment {
    private View view;
    private Context context;

    private TextView tvHeader;
    private ImageView ivProfile;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeThreads, swipeContacts;
    private RecyclerView rvThreads, rvContacts;
    private ConstraintLayout emptyIndicator;
    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBar;

    private String role;

    //Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private boolean isDoneFetching = false;

    private ArrayList<ChatThread> threadArrayList = new ArrayList<>();
    private ChatThreadsAdapter chatThreadsAdapter;
    private ChatThread selectedThread = new ChatThread();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        role = UserRole.getRole(context);

        firebaseAuth = FirebaseAuth.getInstance();

        checkFirebaseLogin();
    }

    public void initializeUi() {
        tabLayout = view.findViewById(R.id.tabLayout_Chat);
        tvHeader = view.findViewById(R.id.tv_ChatHeader);
        ivProfile = view.findViewById(R.id.iv_ChatProfile);
        swipeThreads = view.findViewById(R.id.swipe_ChatThreads);
        swipeContacts = view.findViewById(R.id.swipe_ChatContacts);
        rvThreads = view.findViewById(R.id.rv_ChatThreads);
        rvContacts = view.findViewById(R.id.rv_ChatContacts);
        emptyIndicator = view.findViewById(R.id.view_EmptyChat);
        floatingActionButton = view.findViewById(R.id.fab_ChatThreadNew);
        progressBar = view.findViewById(R.id.progress_ChatThreads);

        if (role.equals(UserRole.Educator()))
            setEducatorHeader();

        firebaseUser = firebaseAuth.getCurrentUser();

        displayConversationView();

        getAllContacts();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    displayConversationView();
                } else {
                    swipeThreads.setVisibility(View.GONE);
                    swipeContacts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                UserEducator user = dataSnapshot.getValue(UserEducator.class);
//                assert user != null;
//                if (!UserEducator.getFirstname(context).equals("")){
//                    tvHeader.setText(UserEducator.getFirstname(context));
//                } else{
//                    tvHeader.setText(UserEducator.getEmail(context));
//                }
//                assert user != null;
//                tv_SignIn.setText(user.getEmail_address());
//                if (user.getImage().equals("default")){
//                    profile_image.setImageResource(R.drawable.ic_educator_profile);
//                } else {


        //}
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                viewPagerAdapter = new ChatViewPagerAdapter(getChildFragmentManager());
//                int unread = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat = snapshot.getValue(Chat.class);
//                    assert chat != null;
//                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
//                        unread++;
//                    }
//                }
//
//                if (unread == 0){
//                    viewPagerAdapter.addFragment(new ChatConversationFragment(), "Conversation");
//                } else {
//                    viewPagerAdapter.addFragment(new ChatConversationFragment(), "("+unread+") Conversation");
//                }
//
//                viewPagerAdapter.addFragment(new ChatContactFragment(), "Contacts");
//                viewPager.setAdapter(viewPagerAdapter);
//                tabLayout.setupWithViewPager(viewPager);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    @SuppressLint("RestrictedApi")
    private void accessingServer(boolean isAccessing) {
        floatingActionButton.setVisibility(isAccessing ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isAccessing ? View.VISIBLE : View.GONE);
    }

    private void setEducatorHeader() {
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
    }

    private void displayConversationView() {
        swipeThreads.setVisibility(View.VISIBLE);
        swipeContacts.setVisibility(View.GONE);
        emptyIndicator.setVisibility(View.GONE);

        swipeThreads.setRefreshing(true);

        swipeThreads.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllThreads();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatNewConversationActivity.class);
                intent.putParcelableArrayListExtra("CONTACTARRAYLIST", contactArrayList);
                startActivity(intent);

            }
        });
    }

    private void checkFirebaseLogin() {
        if (firebaseAuth.getCurrentUser() == null) {
            openWelcomeChatDialog();
        } else {
            initializeUi();
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
                if (role.equals(UserRole.Educator()))
                    registerEducatorToFirebase(b);
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void registerEducatorToFirebase(final AlertDialog b) {
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

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("email", email);
                            hashMap.put("fullName", fullName);
                            hashMap.put("status", "online");
                            hashMap.put("search", search);
                            hashMap.put("role", role);
                            hashMap.put("photoName", photoName);

                            databaseReference.setValue(hashMap);

                            if (b != null) {
                                b.dismiss();
                                initializeUi();
                            }

                        } else {
                            Toasty.warning(context, "Email address already exists.").show();
                        }
                    }
                });
    }

    private void getAllContacts() {
        accessingServer(true);
        contactArrayList.clear();

        DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference("Users");
        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    assert contact != null;
                    assert firebaseUser != null;
                    if (!contact.getId().equals(firebaseUser.getUid())) {
                        contactArrayList.add(contact);
                    }

                    Debugger.logD("CONTACT CHANGED");
                }

                accessingServer(false);
                getAllThreads();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                accessingServer(false);
            }
        });
    }

    private void getAllThreads() {
        DatabaseReference threadsRef = FirebaseDatabase.getInstance().getReference("Threads");
        threadsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                swipeThreads.setRefreshing(false);
                threadArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatThread chatThread = snapshot.getValue(ChatThread.class);
                    assert chatThread != null;
                    if (chatThread.getSenderId().equals(firebaseUser.getUid())) {
                        for (Contact contact : contactArrayList) {
                            if (chatThread.getReceiverId().equals(contact.getId())) {
                                chatThread.setContact(contact);
                                break;
                            }
                        }
                        threadArrayList.add(chatThread);

                    } else if (chatThread.getReceiverId().equals(firebaseUser.getUid())) {
                        for (Contact contact : contactArrayList) {
                            if (chatThread.getSenderId().equals(contact.getId())) {
                                chatThread.setContact(contact);
                                break;
                            }
                        }
                        threadArrayList.add(chatThread);
                    }
                }

                rvThreads.setLayoutManager(new LinearLayoutManager(context));
                rvThreads.setHasFixedSize(true);
                chatThreadsAdapter = new ChatThreadsAdapter(context, threadArrayList);
                chatThreadsAdapter.setClickListener(new OnClickRecyclerView() {
                    @Override
                    public void onItemClick(View view, int position, int fromButton) {
                        selectedThread = threadArrayList.get(position);
                        Contact selectedContact = selectedThread.getContact();

                        if (fromButton == 1) {
                            Intent intent = new Intent(context, ChatConversationActivity.class);
                            intent.putExtra("CONTACT", selectedContact);
                            intent.putExtra("THREADID", selectedThread.getId());
                            startActivity(intent);
                        } else if (fromButton == 2) {

                        }
                    }
                });
                rvThreads.setAdapter(chatThreadsAdapter);

                if (threadArrayList.isEmpty())
                    emptyIndicator.setVisibility(View.VISIBLE);

                swipeThreads.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeThreads.setRefreshing(false);
            }
        });
    }
}