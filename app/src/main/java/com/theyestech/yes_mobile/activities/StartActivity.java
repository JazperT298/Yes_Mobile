package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.theyestech.yes_mobile.R;

public class StartActivity extends AppCompatActivity {

    private Context context;

    private CardView cvEducator, cvStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        context = this;

        initializeUI();
    }

    private void initializeUI(){
        cvEducator = findViewById(R.id.cv_StartEducator);
        cvStudent = findViewById(R.id.cv_StartStudent);

        cvEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("ROLE_ID", 1);
                startActivity(intent);
            }
        });

        cvStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("ROLE_ID", 2);
                startActivity(intent);
            }
        });
    }
}
