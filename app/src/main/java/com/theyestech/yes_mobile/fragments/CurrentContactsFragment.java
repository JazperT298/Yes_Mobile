package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theyestech.yes_mobile.R;

public class CurrentContactsFragment extends Fragment {

    private View view;
    private Context cOntext;

    public CurrentContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.fragment_current_contacts, container, false);
        return view;
    }


}
