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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ViewPagerAdapter;
import com.theyestech.yes_mobile.models.Chat;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.GlideOptions;

public class ChatFragment extends Fragment {

    private View view;

    private Context context;
    private ImageView profile_image;
    private TextView tv_SignIn;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        context = getContext();

        initializeUI();
    }


    public void initializeUI(){
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.view_pager);
        profile_image = view.findViewById(R.id.iv_ProfileEducatorImage);
        tv_SignIn = view.findViewById(R.id.tv_SignIn);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Educator").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserEducator user = dataSnapshot.getValue(UserEducator.class);
                assert user != null;
                if (!UserEducator.getFirstname(context).equals("")){
                    tv_SignIn.setText(UserEducator.getFirstname(context));
                } else{
                    tv_SignIn.setText(UserEducator.getEmail(context));
                }
//                if (user.getImage().equals("default")){
//                    profile_image.setImageResource(R.drawable.ic_educator_profile);
//                } else {

                    Glide.with(context)
                            .load(R.drawable.ic_educator_profile)
                            .apply(GlideOptions.getOptions())
                            .into(profile_image);
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }

                if (unread == 0){
                    viewPagerAdapter.addFragment(new CurrentChatFragment(), "Chats");
                } else {
                    viewPagerAdapter.addFragment(new CurrentChatFragment(), "("+unread+") Chats");
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