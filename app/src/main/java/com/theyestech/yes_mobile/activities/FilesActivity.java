package com.theyestech.yes_mobile.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.FilesAdapter;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.Files;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;
import java.util.Collections;

public class FilesActivity extends AppCompatActivity {
    private Context context;
    private View view;
    private String role;
    private String userToken;
    private String userId;


    private ImageView iv_FilesBack;
    private SwipeRefreshLayout swipe_Files;
    private RecyclerView rv_Files;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Files> filesArrayList;
    private FilesAdapter filesAdapter;

    FirebaseUser fuser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        context = this;
        role = UserRole.getRole(context);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        initializeUI();
    }

    private void initializeUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorAmethyst));
//        }

        iv_FilesBack = findViewById(R.id.iv_FilesBack);
        swipe_Files = findViewById(R.id.swipe_Files);
        rv_Files = findViewById(R.id.rv_Files);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);

        if (role.equals(UserRole.Educator())) {
            userId = UserEducator.getID(context);
            userToken = UserEducator.getToken(context);
        } else {
            userId = UserStudent.getID(context);
            userToken = UserStudent.getToken(context);
        }

        swipe_Files.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllFiles();
            }
        });



        iv_FilesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAllFiles( );

    }

    private void getAllFiles(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        filesArrayList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Files");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                filesArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Files files = snapshot.getValue(Files.class);
                    Debugger.logD("PISTE");
                    Debugger.logDint(filesArrayList.size());

                    if (files.getReceiver().equals(fuser.getUid()) && !files.getSender().equals(firebaseUser.getUid()) ||
                            !files.getReceiver().equals(firebaseUser.getUid()) && files.getSender().equals(fuser.getUid())){
                        filesArrayList.add(files);
                    }


                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    rv_Files.setLayoutManager(layoutManager);
                    rv_Files.setHasFixedSize(true);

                    filesAdapter = new FilesAdapter(context, filesArrayList);
                    filesAdapter.notifyDataSetChanged();
                    rv_Files.setAdapter(filesAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Debugger.logD("CANCELLED" + databaseError.getMessage());
            }
        });
    }

}