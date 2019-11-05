package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ContactListAdapter;
import com.theyestech.yes_mobile.adapters.StudentsAdapter;
import com.theyestech.yes_mobile.adapters.UsersEducatorsAdapter;
import com.theyestech.yes_mobile.models.ChatContactList;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;

import java.util.ArrayList;

public class CurrentContactsFragment extends Fragment {

    private View view;
    private Context context;
    private RecyclerView recyclerView;
    private EditText etSearch;
    private ContactListAdapter contactListAdapter;
    private String role;


    private UsersEducatorsAdapter usersEducatorsAdapter;
    private ArrayList<ChatContactList> chatContactListArrayList;

    private StudentsAdapter studentAdapter;
    private ArrayList<Student> mStudents;

    public CurrentContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_current_contacts, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        initializeUI();
        initializeEducatorUI();
    }

    private void initializeUI() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    //-----------------------------------------Firebase----------------------------------------//
    //-----------------------------------------Educator----------------------------------------//

    private void initializeEducatorUI() {

        chatContactListArrayList = new ArrayList<>();

        readAllEducators();
        etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchAllEducators(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void searchAllEducators(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Educator").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatContactListArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatContactList chatContactList = snapshot.getValue(ChatContactList.class);

                    if (!chatContactList.getId().equals(fuser.getUid())) {
                        chatContactListArrayList.add(chatContactList);
                    }
                }

                usersEducatorsAdapter = new UsersEducatorsAdapter(getContext(), chatContactListArrayList, false);
                recyclerView.setAdapter(usersEducatorsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readAllEducators() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Educator");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (etSearch.getText().toString().equals("")) {
                    chatContactListArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatContactList chatContactList = snapshot.getValue(ChatContactList.class);

                        assert chatContactList != null;
                        assert firebaseUser != null;
                        if (!chatContactList.getId().equals(firebaseUser.getUid())) {
                            chatContactListArrayList.add(chatContactList);
                            Debugger.logD("educator " + chatContactList);
                        }

                    }

                    usersEducatorsAdapter = new UsersEducatorsAdapter(getContext(), chatContactListArrayList, false);
                    recyclerView.setAdapter(usersEducatorsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //-----------------------------------------Firebase----------------------------------------//
    //-----------------------------------------Student----------------------------------------//

    private void initializeStudentUI() {

        mStudents = new ArrayList<>();

        readAllStudents();
        etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchAllStudent(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void searchAllStudent(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Student").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mStudents.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Student student = snapshot.getValue(Student.class);

                    assert student != null;
                    assert fuser != null;
                    if (!student.getUser_id().equals(fuser.getUid())) {
                        mStudents.add(student);
                    }
                }

                studentAdapter = new StudentsAdapter(getContext(), mStudents);
                recyclerView.setAdapter(studentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readAllStudents() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Student");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (etSearch.getText().toString().equals("")) {
                    mStudents.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Student student = snapshot.getValue(Student.class);

                        assert student != null;
                        if (!student.getUser_id().equals(firebaseUser.getUid())) {
                            mStudents.add(student);
                        }

                    }

                    studentAdapter = new StudentsAdapter(getContext(), mStudents);
                    recyclerView.setAdapter(studentAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
