package com.theyestech.yes_mobile.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.FirebaseUserAdapter;
import com.theyestech.yes_mobile.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUserActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private FirebaseUserAdapter firebaseUserAdapter;
    private List<Contact> contacts;

    ImageView iv_NewsFeedBack;

    EditText search_users;

    Intent intent;
    String files, fileType;

    FirebaseUser fuser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_user);
        iv_NewsFeedBack = findViewById(R.id.iv_NewsFeedBack);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        contacts = new ArrayList<>();

        intent = getIntent();
        files = intent.getStringExtra("NEWSFEED_FILES");
        fileType = intent.getStringExtra("NEWSFEED_TYPE");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        readUsers();

        search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        iv_NewsFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contacts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Contact user = snapshot.getValue(Contact.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getId().equals(fuser.getUid())){
                        contacts.add(user);
                    }
                }

                firebaseUserAdapter = new FirebaseUserAdapter(getApplicationContext(), contacts, false, files, fileType, FirebaseUserActivity.this);
                recyclerView.setAdapter(firebaseUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_users.getText().toString().equals("")) {
                    contacts.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Contact user = snapshot.getValue(Contact.class);

                        if (!user.getId().equals(firebaseUser.getUid())) {
                            contacts.add(user);
                        }

                    }

                    firebaseUserAdapter = new FirebaseUserAdapter(getApplicationContext(), contacts, false,files,fileType,FirebaseUserActivity.this);
                    recyclerView.setAdapter(firebaseUserAdapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}