package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.theyestech.yes_mobile.R;

public class HomeFragment extends Fragment {

    private View view;
    private Context context;

    private ImageView ivProfile, ivChat, ivNewPost;
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

        initializeUI();
    }

    private void initializeUI() {
        ivProfile = view.findViewById(R.id.iv_HomeProfile);
        ivChat = view.findViewById(R.id.iv_HomeChat);
        ivNewPost = view.findViewById(R.id.iv_HomeNewPost);
        swipeRefreshLayout = view.findViewById(R.id.swipe_Home);
        recyclerView = view.findViewById(R.id.rv_Home);

        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, QuizListActivity.class);
//                startActivity(intent);
            }
        });
    }
}