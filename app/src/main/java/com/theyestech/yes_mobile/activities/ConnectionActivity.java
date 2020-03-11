package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI(){

    }
}
