package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.StudentRequestAdapter;
import com.theyestech.yes_mobile.adapters.StudentStickersAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Section;
import com.theyestech.yes_mobile.models.Sticker;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectDetailsActivity extends AppCompatActivity {

    private Context context;
    private String role;

    private ImageView ivBack, ivClose,iv_SubjectDetailsMenu;
    private TextView tvHeader;
    private CardView cvDetails, cvStudents, cvTopics, cvQuiz, cvStickers, cvAwards, cvAssessment, cvRequest;

    private EditText etName, etDescription, etSection;
    private MaterialSpinner spSection, spLevel, spSemester;
    private TextView tvHeaderDialog;
    private Button btnSave;

    private Subject subject;

    private ArrayList<String> sLevel = new ArrayList<>();
    private ArrayList<String> sSemester = new ArrayList<>();

    private String name = "", description = "", section = "", level = "", semester = "", schoolYear = "";

    private RecyclerView rv_StudentRequest;
    private SwipeRefreshLayout swipeRefreshLayout1;
    private ConstraintLayout emptyIndicator1;
    private ArrayList<Student> studentArrayList = new ArrayList<>();
    private StudentRequestAdapter studentRequestAdapter;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);

        subject = getIntent().getParcelableExtra("SUBJECT");

        context = this;
        role = UserRole.getRole(context);

        sLevel.add("Primary Level");
        sLevel.add("Secondary Level");
        sLevel.add("Tertiary Level");

        sSemester.add("1st Semester");
        sSemester.add("2nd Semester");
        sSemester.add("Summer");

        initializeUI();
    }

    private void initializeUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorAlizarin));
//        }

        tvHeader = findViewById(R.id.tv_SubjectDetailsSHeader);
        ivBack = findViewById(R.id.iv_SubjectDetailsBack);
        cvDetails = findViewById(R.id.cv_SubjectDetails_ViewDetails);
        cvStudents = findViewById(R.id.cv_SubjectDetails_ViewStudents);
        cvTopics = findViewById(R.id.cv_SubjectDetails_ViewTopics);
        cvQuiz = findViewById(R.id.cv_SubjectDetails_ViewQuizzes);
        cvStickers = findViewById(R.id.cv_SubjectDetails_ViewStickers);
        cvAwards = findViewById(R.id.cv_SubjectDetails_ViewAwards);
        cvAssessment = findViewById(R.id.cv_SubjectDetails_ViewAssessment);
        cvRequest = findViewById(R.id.cv_SubjectDetails_ViewStudentRequest);
        iv_SubjectDetailsMenu = findViewById(R.id.iv_SubjectDetailsMenu);

        tvHeader.setText(subject.getTitle());

        iv_SubjectDetailsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sideMenuDialog(v);
            }
        });

        if (!role.equals(UserRole.Educator())) {
            cvDetails.setVisibility(View.GONE);
            cvStudents.setVisibility(View.GONE);
            cvAssessment.setVisibility(View.GONE);
            cvRequest.setVisibility(View.GONE);
            cvStickers.setVisibility(View.GONE);
            cvAwards.setVisibility(View.GONE);
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cvDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getSectionDetails();
                openAddSubjectDialog();
            }
        });

        cvStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectStudentsActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        cvTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectTopicsActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        cvQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectQuizzesActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        cvStickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectStickersActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        cvAwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectAwardsActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        cvAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectAssessmentsQuizActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });
        cvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStudentRequestDialog();
            }
        });
    }

    private void getSectionDetails() {
//        sectionArrayList.clear();
//        sName.clear();
//        sId.clear();

        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("teach_token", UserEducator.getToken(context));
        params.put("teach_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_educator/get_sections.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("SECTION: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String section_id = jsonObject.getString("section_id");
                        String section_name = jsonObject.getString("section_name");
                        String section_year = jsonObject.getString("section_year");
                        String user_id = jsonObject.getString("user_id");

                        Section section = new Section();
                        section.setId(section_id);
                        section.setName(section_name);
                        section.setSchool_year(section_year);
                        section.setUser_id(user_id);

//                        sName.add(section_name);
//                        sId.add(section_id);
//
//                        sectionArrayList.add(section);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void openAddSubjectDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_edit_subject, null);

        etName = dialogView.findViewById(R.id.et_AddEditSubjectName);
        etDescription = dialogView.findViewById(R.id.et_AddEditSubjectDescription);
        etSection = dialogView.findViewById(R.id.et_AddEditSubjectSection);
//        spSection = dialogView.findViewById(R.id.sp_AddEditSubjectSection);
        spLevel = dialogView.findViewById(R.id.sp_AddEditSubjectLevel);
        spSemester = dialogView.findViewById(R.id.sp_AddEditSubjectSemester);
        tvHeaderDialog = dialogView.findViewById(R.id.tv_AddEditSubjectHeader);
        btnSave = dialogView.findViewById(R.id.btn_AddEditSubjectSave);
        ivClose = dialogView.findViewById(R.id.iv_AddEditSubjectClose);

        tvHeaderDialog.setText("Edit Subject");

        spSemester.setVisibility(View.GONE);

        setSubjectDetailsField();

////        spSection.setItems(sName);
//        spSection.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                section = sId.get(position);
//            }
//        });

        spLevel.setItems(sLevel);
        spLevel.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                level = sLevel.get(position);

                if (level.equals(sLevel.get(2))) {
                    spSemester.setVisibility(View.VISIBLE);
                    semester = sSemester.get(0);
                } else {
                    spSemester.setVisibility(View.GONE);
                    semester = "";
                }
            }
        });

        spSemester.setItems(sSemester);
        spSemester.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                semester = sSemester.get(position);
            }
        });

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                description = etDescription.getText().toString();
                schoolYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

                if (name.isEmpty())
                    Toasty.warning(context, "Please input subject name.").show();
                else {
                    if (level.equals(sLevel.get(2))) {
                        if (semester.isEmpty())
                            Toasty.warning(context, "Please select semester.").show();
                        else {
                            saveSubject();
                            b.hide();
                        }
                    } else {
                        saveSubject();
                        b.hide();
                    }
                }
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void saveSubject() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("subj_id", subject.getId());
        params.put("subj_title", name);
        params.put("subj_description", description);
        params.put("subj_level", level);
        params.put("subj_semester", semester);
        params.put("subj_school_year", schoolYear);

        HttpProvider.post(context, "controller_educator/update_subject_details.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD(str);
                if (str.contains("success")) {
                    Toasty.success(context, "Saved.").show();
                    tvHeader.setText(name);
                    subject.setTitle(name);
                    subject.setDescription(description);
                    subject.setSection(section);
                    subject.setLevel(level);
                    subject.setSemester(semester);
                    setSubjectDetailsField();
                } else
                    Toasty.warning(context, "Failed").show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void setSubjectDetailsField() {
        etName.setText(subject.getTitle());
        etDescription.setText(subject.getDescription());
        etSection.setText(subject.getSection());
//        spSection.post(new Runnable() {
//            @Override
//            public void run() {
//                spSection.setSelectedIndex(sId.indexOf(subject.getSection_id()));
//                section = sId.get(spSection.getSelectedIndex());
//            }
//        });
        spLevel.post(new Runnable() {
            @Override
            public void run() {
                spLevel.setSelectedIndex(sLevel.indexOf(subject.getLevel()));
                level = sLevel.get(spLevel.getSelectedIndex());
            }
        });
        if (!subject.getSemester().isEmpty()) {
            spSemester.setVisibility(View.VISIBLE);
            spSemester.post(new Runnable() {
                @Override
                public void run() {
                    spSemester.setSelectedIndex(sSemester.indexOf(subject.getSemester()));
                    semester = sSemester.get(spSemester.getSelectedIndex());
                }
            });
        }
    }

    private void sideMenuDialog(View v){
        PopupMenu popup = new PopupMenu(context, v);
        popup.getMenuInflater().inflate(R.menu.menu_pop_up, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.dashboard:
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.profile:
                        Intent intent2 = new Intent(context, UserProfileActivity.class);
                        startActivity(intent2);
                        finish();
                        return true;
                }
                return false;
            }
        });
        popup.show();//showing popup menu
    }

    private void openStudentRequestDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.dialog_student_subject_request);
        final ImageView iv_SearchBack;

        iv_SearchBack = dialog.findViewById(R.id.iv_SearchBack);
        rv_StudentRequest = dialog.findViewById(R.id.rv_StudentRequest);
        swipeRefreshLayout1 = dialog.findViewById(R.id.swipe_StudentRequest);
        emptyIndicator1 = dialog.findViewById(R.id.view_EmptyRecord);

        swipeRefreshLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllStudentSubjectRequest();
            }
        });

        iv_SearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        getAllStudentSubjectRequest();
    }

    private void getAllStudentSubjectRequest(){
        studentArrayList.clear();

        swipeRefreshLayout1.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("subj_id", subject.getId());

        Debugger.logD("subj_id " + subject.getId());
        HttpProvider.post(context, "controller_educator/get_pending_students_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                swipeRefreshLayout1.setRefreshing(false);
                Debugger.logD("responseBody " + responseBody);

                String str = new String(responseBody);
                Debugger.logD("str " + str);
                if (str.equals(""))
                    emptyIndicator1.setVisibility(View.VISIBLE);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(str);
                    Debugger.logD("jsonArray " + jsonArray);
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
                    Collections.reverse(studentArrayList);

                    rv_StudentRequest.setLayoutManager(new LinearLayoutManager(context));

                    studentRequestAdapter = new StudentRequestAdapter(context, studentArrayList);
                    studentRequestAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            student = studentArrayList.get(position);
                            if (fromButton == 1){

                            }else if (fromButton == 2){
                                acceptStudentRequest(student.getUser_id());
                            }
                        }
                    });

                    rv_StudentRequest.setAdapter(studentRequestAdapter);
                    emptyIndicator1.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout1.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void acceptStudentRequest(String stud_id){
        ProgressPopup.showProgress(context);
        RequestParams params = new RequestParams();
        params.put("stud_id", stud_id);
        params.put("subj_id", subject.getId());
        Debugger.logD("subj_id " + subject.getId());

        HttpProvider.post(context, "controller_educator/accept_student_request.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str " + str);
                if (str.contains("success")) {
                    Toasty.success(context, "Student Accepted").show();
                    getAllStudentSubjectRequest();
                } else{
                    Toasty.warning(context, "Failed").show();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
}
