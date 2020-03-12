package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ContactDropdownAdapter;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;

public class ChatConversationActivity extends AppCompatActivity {
    private Context context;

    private EditText etMessage;
    private ImageView ivBack, ivSend;
    private AutoCompleteTextView etSearch;

    private String role;

    //Firebase
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private ContactDropdownAdapter contactDropdownAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        context = this;

        firebaseAuth = FirebaseAuth.getInstance();

        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI(){

    }
}
