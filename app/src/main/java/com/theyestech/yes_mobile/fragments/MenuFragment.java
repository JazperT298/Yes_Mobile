package com.theyestech.yes_mobile.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.MenuAboutUsActivity;
import com.theyestech.yes_mobile.activities.MenuContactUsActivity;
import com.theyestech.yes_mobile.activities.MenuOurBlogActivity;
import com.theyestech.yes_mobile.activities.MenuPrivacyPolicyActivity;
import com.theyestech.yes_mobile.utils.UserRole;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    private View view;
    private Context context;

    private ConstraintLayout constraintLayout2,constraintLayout3,constraintLayout4,constraintLayout5;

    private String role;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        role = UserRole.getRole(context);
        initializeUI();
    }

    private void initializeUI() {
        constraintLayout2 = view.findViewById(R.id.constraintLayout2);
        constraintLayout3 = view.findViewById(R.id.constraintLayout3);
        constraintLayout4 = view.findViewById(R.id.constraintLayout4);
        constraintLayout5 = view.findViewById(R.id.constraintLayout5);

        constraintLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuAboutUsActivity.class);
                startActivity(intent);
            }
        });
        constraintLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuOurBlogActivity.class);
                startActivity(intent);
            }
        });
        constraintLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuContactUsActivity.class);
                startActivity(intent);
            }
        });
        constraintLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuPrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
    }

}
