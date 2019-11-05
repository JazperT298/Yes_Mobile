package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.StudentsAdapter;
import com.theyestech.yes_mobile.adapters.UsersEducatorsAdapter;
import com.theyestech.yes_mobile.models.ChatContactList;
import com.theyestech.yes_mobile.models.Chatlist;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.notifications.Token;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;


public class ChatConversationFragment extends Fragment {
    private View view;
    private Context context;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String role;

    //Firebase
    FirebaseUser fuser;
    DatabaseReference reference;

    private ArrayList<ChatContactList> chatContactListArrayList;
    private ArrayList<Student> mStudent;

    private UsersEducatorsAdapter usersEducatorsAdapter ;
    private StudentsAdapter studentAdapter;

    private ArrayList<Chatlist> educatorsCbatList;
    private ArrayList<Chatlist> studentCbatList;
    public ChatConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_chat_conversation, container, false);
        initializeUI ();

        context = getContext();

        role = UserRole.getRole(context);

        getEducatorChatList();

        return view;
    }

    private void initializeUI () {
        etSearch = view.findViewById(R.id.etSearch);
        swipeRefreshLayout = view.findViewById(R.id.swipe_Chats);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (role.equals(UserRole.Educator()))
                    getEducatorChatList();
            }
        });
    }

    private void getEducatorChatList() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        educatorsCbatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                educatorsCbatList.clear();
                swipeRefreshLayout.setRefreshing(false);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    educatorsCbatList.add(chatlist);
                }

                educatorChatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateEducatorToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateEducatorToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void educatorChatList() {
        chatContactListArrayList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Educator");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatContactListArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatContactList chatContactList = snapshot.getValue(ChatContactList.class);
                    for (Chatlist chatlist : educatorsCbatList){
                        if (chatContactList.getId().equals(chatlist.getId())){
                            chatContactListArrayList.add(chatContactList);
                        }
                    }
                }
                usersEducatorsAdapter = new UsersEducatorsAdapter(getContext(), chatContactListArrayList, true);
                recyclerView.setAdapter(usersEducatorsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //-----------------------------------------Firebase----------------------------------------//
    //-----------------------------------------Student----------------------------------------//
    private void getStudentChatList() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        studentCbatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentCbatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    studentCbatList.add(chatlist);
                }

                studentChatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateStudentToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateStudentToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void studentChatList() {
        mStudent = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Student");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mStudent.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Student student = snapshot.getValue(Student.class);
                    for (Chatlist chatlist : studentCbatList){
                        if (student.getUser_id().equals(chatlist.getId())){
                            mStudent.add(student);
                        }
                    }
                }
                studentAdapter = new StudentsAdapter(getContext(), mStudent);
                recyclerView.setAdapter(studentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
