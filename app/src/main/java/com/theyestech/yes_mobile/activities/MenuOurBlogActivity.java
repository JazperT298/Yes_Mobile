package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.UserRole;

public class MenuOurBlogActivity extends AppCompatActivity {
    private Context context;

    private String role;

    private ImageView iv_BlogBack;
    private TextView textView1s, textView2s,textView3s,textView4s,textView5s,textView6s,textView7s,textView8s,textView9s;
    private TextView mainText1,mainText2, mainText3,mainText4,mainText5, mainText6,mainText7,mainText8, mainText9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_our_blog);
        context = this;

        role = UserRole.getRole(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();
    }

    private void initializeUI(){
        iv_BlogBack = findViewById(R.id.iv_BlogBack);
        textView1s = findViewById(R.id.textView1s);
        textView2s = findViewById(R.id.textView2s);
        textView3s = findViewById(R.id.textView3s);
        textView4s = findViewById(R.id.textView4s);
        textView5s = findViewById(R.id.textView5s);
        textView6s = findViewById(R.id.textView6s);
        textView7s = findViewById(R.id.textView7s);
        textView8s = findViewById(R.id.textView8s);
        textView9s = findViewById(R.id.textView9s);

        mainText1 = findViewById(R.id.mainText1);
        mainText2 = findViewById(R.id.mainText2);
        mainText3 = findViewById(R.id.mainText3);
        mainText4 = findViewById(R.id.mainText4);
        mainText5 = findViewById(R.id.mainText5);
        mainText6 = findViewById(R.id.mainText6);
        mainText7 = findViewById(R.id.mainText7);
        mainText8 = findViewById(R.id.mainText8);
        mainText9 = findViewById(R.id.mainText9);

        iv_BlogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textView1s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText1.getVisibility() == View.VISIBLE){
                    mainText1.setVisibility(View.GONE);
                    textView1s.setText("READ MORE");
                }
                else{
                    mainText1.setVisibility(View.VISIBLE);
                    textView1s.setText("READ LESS");
                }
            }
        });
        textView2s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText2.getVisibility() == View.VISIBLE){
                    mainText2.setVisibility(View.GONE);
                    textView2s.setText("READ MORE");
                }
                else{
                    mainText2.setVisibility(View.VISIBLE);
                    textView2s.setText("READ LESS");
                }
            }
        });
        textView3s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText3.getVisibility() == View.VISIBLE){
                    mainText3.setVisibility(View.GONE);
                    textView3s.setText("READ MORE");
                }
                else{
                    mainText3.setVisibility(View.VISIBLE);
                    textView3s.setText("READ LESS");
                }
            }
        });
        textView4s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText4.getVisibility() == View.VISIBLE){
                    mainText4.setVisibility(View.GONE);
                    textView4s.setText("READ MORE");
                }
                else{
                    mainText4.setVisibility(View.VISIBLE);
                    textView4s.setText("READ LESS");
                }
            }
        });
        textView5s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText5.getVisibility() == View.VISIBLE){
                    mainText5.setVisibility(View.GONE);
                    textView5s.setText("READ MORE");
                }
                else{
                    mainText5.setVisibility(View.VISIBLE);
                    textView5s.setText("READ LESS");
                }
            }
        });
        textView6s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText6.getVisibility() == View.VISIBLE){
                    mainText6.setVisibility(View.GONE);
                    textView6s.setText("READ MORE");
                }
                else{
                    mainText6.setVisibility(View.VISIBLE);
                    textView6s.setText("READ LESS");
                }
            }
        });
        textView7s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText7.getVisibility() == View.VISIBLE){
                    mainText7.setVisibility(View.GONE);
                    textView7s.setText("READ MORE");
                }
                else{
                    mainText7.setVisibility(View.VISIBLE);
                    textView7s.setText("READ LESS");
                }
            }
        });
        textView8s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText8.getVisibility() == View.VISIBLE){
                    mainText8.setVisibility(View.GONE);
                    textView8s.setText("READ MORE");
                }
                else{
                    mainText8.setVisibility(View.VISIBLE);
                    textView8s.setText("READ LESS");
                }
            }
        });
        textView9s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (mainText9.getVisibility() == View.VISIBLE){
                    mainText9.setVisibility(View.GONE);
                    textView9s.setText("READ MORE");
                }
                else{
                    mainText9.setVisibility(View.VISIBLE);
                    textView9s.setText("READ LESS");
                }
            }
        });
    }
}
