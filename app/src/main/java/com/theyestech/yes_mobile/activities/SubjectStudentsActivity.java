package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.StudentsAdapter;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectStudentsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Student> studentArrayList = new ArrayList<>();
    private StudentsAdapter studentAdapter;
    private Student student = new Student();

    private Subject subject;

    private String studentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_students);

        subject = getIntent().getParcelableExtra("SUBJECT");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_SubjectStudentsBack);
        swipeRefreshLayout = findViewById(R.id.swipe_SubjectStudents);
        recyclerView = findViewById(R.id.rv_SubjectStudents);
        floatingActionButton = findViewById(R.id.fab_SubjectStudentsAdd);
        emptyIndicator = findViewById(R.id.view_Empty);

        swipeRefreshLayout.setRefreshing(true);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStudentDetails();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddStudentDialog();
            }
        });

        getStudentDetails();
    }

    private void getStudentDetails() {
        studentArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("subj_id", subject.getId());

        HttpProvider.post(context, "controller_educator/get_students_from_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {

                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("STUDENTS: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String user_id = jsonObject.getString("user_id");
                        String user_token = jsonObject.getString("user_token");
                        String user_email_address = jsonObject.getString("user_email_address");
                        String user_password = jsonObject.getString("user_password");
                        String user_firstname = jsonObject.getString("user_firstname");
                        String user_lastname = jsonObject.getString("user_lastname");
                        String user_middlename = jsonObject.getString("user_middlename");
                        String user_suffixes = jsonObject.getString("user_suffixes");
                        String user_gender = jsonObject.getString("user_gender");
                        String user_contact_number = jsonObject.getString("user_contact_number");
                        String user_image = jsonObject.getString("user_image");

                        Student student = new Student();
                        student.setUser_id(user_id);
                        student.setUser_token(user_token);
                        student.setUser_email_address(user_email_address);
                        student.setUser_password(user_password);
                        student.setUser_firstname(user_firstname);
                        student.setUser_lastname(user_lastname);
                        student.setUser_middlename(user_middlename);
                        student.setUser_suffixes(user_suffixes);
                        student.setUser_gender(user_gender);
                        student.setUser_contact_number(user_contact_number);
                        student.setUser_image(user_image);

                        studentArrayList.add(student);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    studentAdapter = new StudentsAdapter(context, studentArrayList);
                    studentAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position) {
//                            student = studentArrayList.get(position);
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("STUDENT", student);
//                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(studentAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void addStudentToSubject() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("stud_code", studentCode);
        params.put("subj_id", subject.getId());

        HttpProvider.post(context, "controller_educator/add_student_to_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String result = jsonObject.getString("result");
                    Debugger.logD(str);
                    if (result.contains("success")) {
                        Toasty.success(context, "Saved.").show();
                    } else
                        Toasty.warning(context, result).show();
                    getStudentDetails();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Debugger.logD("STUDENT: " + e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void openAddStudentDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_student_to_subject, null);
        final EditText etCode;
        final Button btnAdd;
        final ImageView ivClose;

        etCode = dialogView.findViewById(R.id.et_AddStudentToSubjectCode);
        btnAdd = dialogView.findViewById(R.id.btn_AddStudentToSubjectAdd);
        ivClose = dialogView.findViewById(R.id.iv_AddStudentToSubjectClose);

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentCode = etCode.getText().toString();
                addStudentToSubject();
                b.hide();
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
