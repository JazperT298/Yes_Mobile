package com.theyestech.yes_mobile.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.SubjectsAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Section;
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
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Subject> subjectArrayList = new ArrayList<>();
    private SubjectsAdapter subjectsAdapter;
    private Subject selectedSubject = new Subject();

    private ArrayList<Section> sectionArrayList = new ArrayList<>();

    private ArrayList<String> sName = new ArrayList<>();
    private ArrayList<String> sId = new ArrayList<>();
    private ArrayList<String> sLevel = new ArrayList<>();
    private ArrayList<String> sSemester = new ArrayList<>();

    private String name = "", description = "", sectionId = "", level = "", semester = "", schoolYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        context = this;
        role = UserRole.getRole(context);

        sLevel.add("Primary Level");
        sLevel.add("Secondary Level");
        sLevel.add("Tertiary Level");

        sSemester.add("1st Semester");
        sSemester.add("2nd Semester");
        sSemester.add("Summer");

        initializeUI();

        swipeRefreshLayout.setRefreshing(true);
    }

    @SuppressLint("RestrictedApi")
    private void initializeUI() {
        ivBack = findViewById(R.id.iv_SubjectsBack);
        swipeRefreshLayout = findViewById(R.id.swipe_Subjects);
        recyclerView = findViewById(R.id.rv_Subjects);
        floatingActionButton = findViewById(R.id.fab_SubjectsAdd);
        emptyIndicator = findViewById(R.id.view_Empty);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (role.equals(UserRole.Educator()))
                    getEducatorSubjectDetails();
                else
                    getStudentSubjectDetails();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sId.isEmpty())
                    Toasty.warning(context, "Add section before creating subject.").show();
                else {
                    if (role.equals(UserRole.Educator()))
                        openAddSubjectDialog();
                    else
                        openRequestSubjectDialog();
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getEducatorSubjectDetails() {
        subjectArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);
        floatingActionButton.setEnabled(false);

        RequestParams params = new RequestParams();
        params.put("teach_token", UserEducator.getToken(context));
        params.put("teach_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_educator/get_subjects.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                floatingActionButton.setEnabled(true);
                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {

                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("SUBJECTS: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String subj_id = jsonObject.getString("subj_id");
                        String subj_level = jsonObject.getString("subj_level");
                        String user_id = jsonObject.getString("user_id");
                        String section_id = jsonObject.getString("section_id");
                        String subj_title = jsonObject.getString("subj_title");
                        String subj_description = jsonObject.getString("subj_description");
                        String subj_semester = jsonObject.getString("subj_semester");
                        String subj_school_year = jsonObject.getString("subj_school_year");
                        String subj_file = jsonObject.getString("subj_file");
                        String subj_code = jsonObject.getString("subj_code");

                        Subject subject = new Subject();
                        subject.setId(subj_id);
                        subject.setLevel(subj_level);
                        subject.setUser_id(user_id);
                        subject.setSection_id(section_id);
                        subject.setTitle(subj_title);
                        subject.setDescription(subj_description);
                        subject.setSemester(subj_semester);
                        subject.setSchool_year(subj_school_year);
                        subject.setImage(subj_file);
                        subject.setCode(subj_code);

                        subjectArrayList.add(subject);
                    }

                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    recyclerView.setHasFixedSize(true);
                    subjectsAdapter = new SubjectsAdapter(context, subjectArrayList);
                    subjectsAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position) {
                            selectedSubject = subjectArrayList.get(position);
                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
                            intent.putExtra("SUBJECT", selectedSubject);
                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(subjectsAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                floatingActionButton.setEnabled(true);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void getStudentSubjectDetails() {
        subjectArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);
        floatingActionButton.setEnabled(false);

        RequestParams params = new RequestParams();
        params.put("stud_token", UserStudent.getToken(context));
        params.put("stud_id", UserStudent.getID(context));

        HttpProvider.post(context, "controller_student/get_student_subjects.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                floatingActionButton.setEnabled(true);
                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {

                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("SUBJECTS: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String subj_id = jsonObject.getString("subj_id");
                        String subj_level = jsonObject.getString("subj_level");
                        String user_id = jsonObject.getString("user_id");
                        String section_id = jsonObject.getString("section_id");
                        String subj_title = jsonObject.getString("subj_title");
                        String subj_description = jsonObject.getString("subj_description");
                        String subj_semester = jsonObject.getString("subj_semester");
                        String subj_school_year = jsonObject.getString("subj_school_year");
                        String subj_file = jsonObject.getString("subj_file");
                        String subj_code = jsonObject.getString("subj_code");

                        Subject subject = new Subject();
                        subject.setId(subj_id);
                        subject.setLevel(subj_level);
                        subject.setUser_id(user_id);
                        subject.setSection_id(section_id);
                        subject.setTitle(subj_title);
                        subject.setDescription(subj_description);
                        subject.setSemester(subj_semester);
                        subject.setSchool_year(subj_school_year);
                        subject.setImage(subj_file);
                        subject.setCode(subj_code);

                        subjectArrayList.add(subject);
                    }

                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    recyclerView.setHasFixedSize(true);
                    subjectsAdapter = new SubjectsAdapter(context, subjectArrayList);
                    subjectsAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position) {
                            selectedSubject = subjectArrayList.get(position);
                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
                            intent.putExtra("SUBJECT", selectedSubject);
                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(subjectsAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                floatingActionButton.setEnabled(true);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void saveSubject() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("teach_token", UserEducator.getToken(context));
        params.put("teach_id", UserEducator.getID(context));
        params.put("subj_title", name);
        params.put("subj_description", description);
        params.put("subj_level", level);
        params.put("section_id", sectionId);
        params.put("subj_semester", semester);
        params.put("subj_school_year", schoolYear);
        params.put("subj_file", "");

        HttpProvider.post(context, "controller_educator/add_subjects.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD(str);
                if (str.contains("success")) {
                    Toasty.success(context, "Saved.").show();
                } else
                    Toasty.warning(context, "Failed").show();
                getEducatorSubjectDetails();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void sendRequestToJoin(String subjectCode) {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("stud_token", UserStudent.getToken(context));
        params.put("stud_id", UserStudent.getID(context));
        params.put("subj_code", subjectCode);

        HttpProvider.post(context, "controller_student/request_join_subject.php", params, new AsyncHttpResponseHandler() {
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
                    getSectionDetails();
                } catch (JSONException e) {
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

    private void getSectionDetails() {
        sectionArrayList.clear();
        sName.clear();
        sId.clear();

        RequestParams params = new RequestParams();
        params.put("teach_token", UserEducator.getToken(context));
        params.put("teach_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_educator/get_sections.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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

                        sName.add(section_name);
                        sId.add(section_id);

                        sectionArrayList.add(section);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD("SECTION: " + e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void openAddSubjectDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_edit_subject, null);
        final EditText etName, etDescription;
        final MaterialSpinner spSection, spLevel, spSemester;
        final TextView tvHeader;
        final Button btnSave;
        final ImageView ivClose;

        etName = dialogView.findViewById(R.id.et_AddEditSubjectName);
        etDescription = dialogView.findViewById(R.id.et_AddEditSubjectDescription);
        spSection = dialogView.findViewById(R.id.sp_AddEditSubjectSection);
        spLevel = dialogView.findViewById(R.id.sp_AddEditSubjectLevel);
        spSemester = dialogView.findViewById(R.id.sp_AddEditSubjectSemester);
        tvHeader = dialogView.findViewById(R.id.tv_AddEditSubjectHeader);
        btnSave = dialogView.findViewById(R.id.btn_AddEditSubjectSave);
        ivClose = dialogView.findViewById(R.id.iv_AddEditSubjectClose);

        spSemester.setVisibility(View.GONE);

        sectionId = sId.get(0);
        level = sLevel.get(0);

        spSection.setItems(sName);
        spSection.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                sectionId = sId.get(position);
            }
        });

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

        tvHeader.setText("Add Subject");

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

    private void openRequestSubjectDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_request_subject_code, null);
        final EditText etCode;
        final Button btnSend;
        final ImageView ivClose;

        etCode = dialogView.findViewById(R.id.et_RequestSubjectCode);
        btnSend = dialogView.findViewById(R.id.btn_RequestSubjectCodeSend);
        ivClose = dialogView.findViewById(R.id.iv_RequestSubjectCodeClose);

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCode.getText().toString().isEmpty())
                    Toasty.warning(context, "Please input subject code.").show();
                else {
                    sendRequestToJoin(etCode.getText().toString());
                    b.hide();
                }
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (role.equals(UserRole.Educator()))
            getEducatorSubjectDetails();
        else
            getStudentSubjectDetails();

        getSectionDetails();
    }
}
