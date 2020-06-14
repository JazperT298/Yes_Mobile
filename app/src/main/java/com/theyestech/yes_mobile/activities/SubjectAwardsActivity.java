package com.theyestech.yes_mobile.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.StickersAdapter;
import com.theyestech.yes_mobile.adapters.StudentListAdapter;
import com.theyestech.yes_mobile.adapters.StudentsAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Sticker;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectAwardsActivity extends AppCompatActivity {

    private Context context;
    private String role;

    private ImageView ivBack;
    private TextView tv_SendAward1,tv_SendAward2,tv_SendAward3;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Sticker> stickerArrayList = new ArrayList<>();
    private StickersAdapter stickersAdapter;

    private RecyclerView rv_StudentList;
    private SwipeRefreshLayout swipeRefreshLayout1;
    private ConstraintLayout emptyIndicator1;

    private ArrayList<Student> studentArrayList = new ArrayList<>();
    private StudentListAdapter studentListAdapter;
    private Subject subject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_awards);
        subject = getIntent().getParcelableExtra("SUBJECT");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();

    }

    private void initializeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorGreensea));
        }
        ivBack = findViewById(R.id.imageView34);
        tv_SendAward1 = findViewById(R.id.tv_SendAward1);
        tv_SendAward2 = findViewById(R.id.tv_SendAward2);
        tv_SendAward3 = findViewById(R.id.tv_SendAward3);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_SendAward1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStudentDialog();
            }
        });
        tv_SendAward2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStudentDialog();
            }
        });
        tv_SendAward3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStudentDialog();
            }
        });
    }

    private void openStudentDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.dialog_student_list);
        final ImageView iv_SearchBack,iv_SendToStudent;

        iv_SearchBack = dialog.findViewById(R.id.iv_SearchBack);
        rv_StudentList = dialog.findViewById(R.id.rv_StudentList);
        swipeRefreshLayout1 = dialog.findViewById(R.id.swipe_StudentList);
        emptyIndicator1 = dialog.findViewById(R.id.view_EmptyRecord);
        iv_SendToStudent = dialog.findViewById(R.id.iv_SendToStudent);
        iv_SendToStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(context, "`Successfully sent to Student").show();
                getAllStudentFromSubject();
                //dialog.dismiss();
            }
        });

        swipeRefreshLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllStudentFromSubject();
            }
        });

        iv_SearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        getAllStudentFromSubject();
    }

    private void getAllStudentFromSubject(){
        studentArrayList.clear();

        swipeRefreshLayout1.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("subj_id", subject.getId());

        HttpProvider.post(context, "controller_educator/get_students_from_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout1.setRefreshing(false);

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
                        String user_activation = jsonObject.getString("user_activation");
                        String validated = jsonObject.getString("validated");

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
                        student.setUser_activiation(user_activation);
                        student.setUser_validated(validated);

                        studentArrayList.add(student);
                    }

                    rv_StudentList.setLayoutManager(new LinearLayoutManager(context));
                    rv_StudentList.setHasFixedSize(true);
                    studentListAdapter = new StudentListAdapter(context, studentArrayList);
                    studentListAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
//                            student = studentArrayList.get(position);
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("STUDENT", student);
//                            startActivity(intent);
                        }
                    });

                    rv_StudentList.setAdapter(studentListAdapter);
                    emptyIndicator1.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void sendAwardToStudent(){

    }

}
