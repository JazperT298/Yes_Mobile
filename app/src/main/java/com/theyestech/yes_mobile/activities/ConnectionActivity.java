package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;

public class ConnectionActivity extends AppCompatActivity {
    private Context context;
    private String role;

    private ImageView imageView35;

    private ConstraintLayout emptyIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI(){
        imageView35 = findViewById(R.id.imageView35);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);
        swipeRefreshLayout = findViewById(R.id.swipe_Connections);
        imageView35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emptyIndicator.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
