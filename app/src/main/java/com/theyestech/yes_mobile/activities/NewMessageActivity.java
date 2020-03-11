package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserEducator;

public class NewMessageActivity extends AppCompatActivity {
    private View view;
    private Context context;

    private TextView tvSignUp;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    private String role;
    //Firebase
    FirebaseAuth auth;

    UserEducator userEducator = new UserEducator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

//        Intent extras = getIntent();
//        Bundle bundle = extras.getExtras();
//        assert bundle != null;
//        role = bundle.getString("ROLE");

        context = this;

        initializeUI();
    }

    private void initializeUI(){

    }
}
