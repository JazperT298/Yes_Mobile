package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theyestech.yes_mobile.R;

public class NotificationFragment extends Fragment {

    private View view;

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        context = getContext();

        initializeUI();
    }

    private void initializeUI() {

    }
}