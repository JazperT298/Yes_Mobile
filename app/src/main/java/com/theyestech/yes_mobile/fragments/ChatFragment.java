package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ViewPagerAdapter;
import com.theyestech.yes_mobile.models.Chat;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

public class ChatFragment extends Fragment {

    private View view;

    private Context context;
    private ImageView profile_image;
    private TextView tv_SignIn;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private String role;

    //Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

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

        initializesUI();

        ProgressPopup.showProgress(context);
    }

    public void initializesUI(){
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.view_pager);
        profile_image = view.findViewById(R.id.iv_ProfileEducatorImage);
        tv_SignIn = view.findViewById(R.id.tv_SignIn);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(profile_image);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Educator").child(firebaseUser.getUid());

//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                UserEducator user = dataSnapshot.getValue(UserEducator.class);
////                assert user != null;
////                if (!UserEducator.getFirstname(context).equals("")){
////                    tv_SignIn.setText(UserEducator.getFirstname(context));
////                } else{
////                    tv_SignIn.setText(UserEducator.getEmail(context));
////                }
//                assert user != null;
////                tv_SignIn.setText(user.getEmail_address());
//                Debugger.logD("asdd " + user.getEmail_address());
////                if (user.getImage().equals("default")){
////                    profile_image.setImageResource(R.drawable.ic_educator_profile);
////                } else {
//
//
//                //}
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProgressPopup.hideProgress();

                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }

                if (unread == 0){
                    viewPagerAdapter.addFragment(new CurrentChatFragment(), "Conversation");
                } else {
                    viewPagerAdapter.addFragment(new CurrentChatFragment(), "("+unread+") Conversation");
                }

                viewPagerAdapter.addFragment(new CurrentContactsFragment(), "Contacts");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




}