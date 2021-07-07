package com.theyestech.yes_mobile.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.UserRole;

public class MenuAboutUsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView iv_AboutBack;
    private TextView tv_copy;

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
        tv_copy = findViewById(R.id.tv_copy);
        String text = "<font color=#C0C0C0>© Copyright 2019 All Rights Reserved by </font> <font color=#19c880>theyestech.com™</font>";
        tv_copy.setText(Html.fromHtml(text));

        iv_AboutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
