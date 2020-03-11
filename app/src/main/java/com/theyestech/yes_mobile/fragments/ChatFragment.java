package com.theyestech.yes_mobile.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.NewMessageActivity;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ChatFragment extends Fragment {
    private View view;
    private Context context;

    private TextView tvHeader;
    private ImageView ivProfile;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeConversation, swipeContacts;
    private RecyclerView rvConversation, rvContacts;
    private ConstraintLayout emptyIndicator;
    private FloatingActionButton floatingActionButton;

    private String role;
    private String path;

    //Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

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

        if (role.equals(UserRole.Educator()))
            path = "Educator";
        else
            path = "Student";

        firebaseAuth = FirebaseAuth.getInstance();

        checkFirebaseLogin();
    }

    public void initializeUi() {
        tabLayout = view.findViewById(R.id.tabLayout_Chat);
        tvHeader = view.findViewById(R.id.tv_ChatHeader);
        ivProfile = view.findViewById(R.id.iv_ChatProfile);
        swipeConversation = view.findViewById(R.id.swipe_ChatConversation);
        swipeContacts = view.findViewById(R.id.swipe_ChatContacts);
        rvConversation = view.findViewById(R.id.rv_ChatConversation);
        rvContacts = view.findViewById(R.id.rv_ChatContacts);
        emptyIndicator = view.findViewById(R.id.view_EmptyChat);
        floatingActionButton = view.findViewById(R.id.fab_ChatConversationNew);

        if (role.equals(UserRole.Educator()))
            setEducatorHeader();

        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(firebaseUser.getUid());

        displayConversation();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    displayConversation();
                }
                else{
                    swipeConversation.setVisibility(View.GONE);
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewMessageActivity.class);
                startActivity(intent);
//                Toasty.success(context, "NEW").show();
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

    private void setEducatorHeader(){
//        tvHeader.setText(UserEducator.getFirstname(context));

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
    }

    private void displayConversation(){
        swipeConversation.setVisibility(View.VISIBLE);
        swipeContacts.setVisibility(View.GONE);

        swipeConversation.setRefreshing(true);
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
        final String search = UserEducator.getLastname(context).toLowerCase();
        final String role = UserRole.getRole(context);
        final String photoName = UserEducator.getImage(context);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ProgressPopup.hideProgress();
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("email", email);
                            hashMap.put("status", "offline");
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
}