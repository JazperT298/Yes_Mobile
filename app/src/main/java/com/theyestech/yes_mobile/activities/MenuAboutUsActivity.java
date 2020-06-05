package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.UserRole;

public class MenuAboutUsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView iv_AboutBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_about_us);
        context = this;

        role = UserRole.getRole(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();
    }

    private void initializeUI(){
        iv_AboutBack = findViewById(R.id.iv_AboutBack);

        iv_AboutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}