package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.UserRole;

public class MenuPrivacyPolicyActivity extends AppCompatActivity {
    private Context context;

    private String role;

    private ImageView iv_PrivacyBack;
    private ConstraintLayout constraintLayout2,constraintLayout3,constraintLayout4,constraintLayout5,constraintLayout6,constraintLayout7,constraintLayout8,constraintLayout9,constraintLayout10,constraintLayout11;
    private ImageView iv_account_setting_icon,iv_personal_info_icon,iv_language_icon,iv_payments_icon,iv_security_icon,iv_security_login_icon,iv_app_websites_icon,iv_limitation_icon,iv_nudity_icon,iv_report_icon;
    private TextView account_settings,personal_info,language,payments,security,security_login,app_websites,limitation,nudity,report,tv_copy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_privacy_policy);

        context = this;

        role = UserRole.getRole(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();
    }

    private void initializeUI(){
        iv_PrivacyBack = findViewById(R.id.iv_PrivacyBack);
        constraintLayout2 = findViewById(R.id.constraintLayout2);
        constraintLayout3 = findViewById(R.id.constraintLayout3);
        constraintLayout4 = findViewById(R.id.constraintLayout4);
        constraintLayout5 = findViewById(R.id.constraintLayout5);
        constraintLayout6 = findViewById(R.id.constraintLayout6);
        constraintLayout7 = findViewById(R.id.constraintLayout7);
        constraintLayout8 = findViewById(R.id.constraintLayout8);
        constraintLayout9 = findViewById(R.id.constraintLayout9);
        constraintLayout10 = findViewById(R.id.constraintLayout10);
        constraintLayout11 = findViewById(R.id.constraintLayout11);

        iv_account_setting_icon = findViewById(R.id.iv_account_setting_icon);
        iv_personal_info_icon = findViewById(R.id.iv_personal_info_icon);
        iv_language_icon = findViewById(R.id.iv_language_icon);
        iv_payments_icon = findViewById(R.id.iv_payments_icon);
        iv_security_icon = findViewById(R.id.iv_security_icon);
        iv_security_login_icon = findViewById(R.id.iv_security_login_icon);
        iv_app_websites_icon = findViewById(R.id.iv_app_websites_icon);
        iv_limitation_icon = findViewById(R.id.iv_limitation_icon);
        iv_nudity_icon = findViewById(R.id.iv_nudity_icon);
        iv_report_icon = findViewById(R.id.iv_report_icon);

        account_settings = findViewById(R.id.account_settings);
        personal_info = findViewById(R.id.personal_info);
        language = findViewById(R.id.language);
        payments = findViewById(R.id.payments);
        security = findViewById(R.id.security);
        security_login = findViewById(R.id.security_login);
        app_websites = findViewById(R.id.app_websites);
        limitation = findViewById(R.id.limitation);
        nudity = findViewById(R.id.nudity);
        report = findViewById(R.id.report);
        tv_copy = findViewById(R.id.tv_copy);
        String text = "<font color=#C0C0C0>© Copyright 2019 All Rights Reserved by </font> <font color=#19c880>theyestech.com™</font>";
        tv_copy.setText(Html.fromHtml(text));

        constraintLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (account_settings.getVisibility() == View.VISIBLE){
                    account_settings.setVisibility(View.GONE);
                    iv_account_setting_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    account_settings.setVisibility(View.VISIBLE);
                    iv_account_setting_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (personal_info.getVisibility() == View.VISIBLE){
                    personal_info.setVisibility(View.GONE);
                    iv_personal_info_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    personal_info.setVisibility(View.VISIBLE);
                    iv_personal_info_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (language.getVisibility() == View.VISIBLE){
                    language.setVisibility(View.GONE);
                    iv_language_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    language.setVisibility(View.VISIBLE);
                    iv_language_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (payments.getVisibility() == View.VISIBLE){
                    payments.setVisibility(View.GONE);
                    iv_payments_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    payments.setVisibility(View.VISIBLE);
                    iv_payments_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (security.getVisibility() == View.VISIBLE){
                    security.setVisibility(View.GONE);
                    iv_security_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    security.setVisibility(View.VISIBLE);
                    iv_security_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (security_login.getVisibility() == View.VISIBLE){
                    security_login.setVisibility(View.GONE);
                    iv_security_login_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    security_login.setVisibility(View.VISIBLE);
                    iv_security_login_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (app_websites.getVisibility() == View.VISIBLE){
                    app_websites.setVisibility(View.GONE);
                    iv_app_websites_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    app_websites.setVisibility(View.VISIBLE);
                    iv_app_websites_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (limitation.getVisibility() == View.VISIBLE){
                    limitation.setVisibility(View.GONE);
                    iv_limitation_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    limitation.setVisibility(View.VISIBLE);
                    iv_limitation_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (nudity.getVisibility() == View.VISIBLE){
                    nudity.setVisibility(View.GONE);
                    iv_nudity_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    nudity.setVisibility(View.VISIBLE);
                    iv_nudity_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });
        constraintLayout11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (report.getVisibility() == View.VISIBLE){
                    report.setVisibility(View.GONE);
                    iv_report_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_down));
                }
                else{
                    report.setVisibility(View.VISIBLE);
                    iv_report_icon.setImageDrawable(getResources().getDrawable( R.drawable.ic_keyboard_arrow_up));
                }
            }
        });

        iv_PrivacyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
