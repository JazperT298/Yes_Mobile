package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.ProfileActivity;
import com.theyestech.yes_mobile.activities.StartActivity;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private View view;
    private Context context;

    private String role;

    private ImageView ivProfile, ivChat, ivNewPost;
    private TextView tvFirstname;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        context = getContext();
        role = UserRole.getRole(context);

        checkSession();
        initializeUI();
    }

    private void initializeUI() {
        ivProfile = view.findViewById(R.id.iv_HomeProfile);
        ivChat = view.findViewById(R.id.iv_HomeChat);
        ivNewPost = view.findViewById(R.id.iv_HomeNewPost);
        tvFirstname = view.findViewById(R.id.tv_HomeFirstname);
        swipeRefreshLayout = view.findViewById(R.id.swipe_Home);
        recyclerView = view.findViewById(R.id.rv_Home);

        swipeRefreshLayout.setRefreshing(false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    public void checkSession() {
        if (role.equals(UserRole.Educator())) {
            if (UserEducator.getToken(context) == null) {
                Intent intent = new Intent(context, StartActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();
            }
        } else {
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        }
    }
}