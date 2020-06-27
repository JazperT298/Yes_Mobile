package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.theyestech.yes_mobile.adapters.SubjectSearchAdapter;
import com.theyestech.yes_mobile.adapters.SubjectsEducatorAdapter;
import com.theyestech.yes_mobile.adapters.SubjectsStudentAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
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
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class SubjectActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Subject> subjectArrayList = new ArrayList<>();
    private SubjectsEducatorAdapter subjectsEducatorAdapter;
    private SubjectsStudentAdapter subjectsStudentAdapter;
    private Subject selectedSubject = new Subject();

    private ArrayList<String> sLevel = new ArrayList<>();
    private ArrayList<String> sSemester = new ArrayList<>();

    private String name = "", description = "", section = "", level = "", semester = "", schoolYear = "";

    private SubjectSearchAdapter subjectSearchAdapter;
    private SwipeRefreshLayout swipeRefreshLayout1;
    private ConstraintLayout emptyIndicator1;
    private RecyclerView rv_Search;


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
        if (role.equals(UserRole.Educator())) {
            if (UserEducator.getFirstname(context).equals("")) {
            ShowEducatorIntro("Add Subject", "Add subjects to your collection", R.id.fab_SubjectsAdd, 1);
            }
        }
        else {
            if (UserStudent.getFirstname(context).equals("")) {
            ShowEducatorIntro("Search Subject", "Search your desire subject here", R.id.fab_SubjectsAdd, 1);
            }
        }
    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_SubjectsBack);
        swipeRefreshLayout = findViewById(R.id.swipe_Subjects);
        recyclerView = findViewById(R.id.rv_Subjects);
        floatingActionButton = findViewById(R.id.fab_SubjectsAdd);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);

        if (!role.equals(UserRole.Educator()))
            floatingActionButton.setImageResource(R.drawable.ic_search_white);

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
                if (role.equals(UserRole.Educator()))
                    openAddSubjectDialog();
                else {
                    openSearchSubjectDialog();
//                    Intent intent = new Intent(context, SubjectSearchActivity.class);
//                    startActivity(intent);
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
                    for (int i = 0 ; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String subj_id = jsonObject.getString("subj_id");
                        String subj_level = jsonObject.getString("subj_level");
                        String user_id = jsonObject.getString("user_id");
                        String subj_section = jsonObject.getString("subj_section");
                        String subj_title = jsonObject.getString("subj_title");
                        String subj_description = jsonObject.getString("subj_description");
                        String subj_semester = jsonObject.getString("subj_semester");
                        String subj_school_year = jsonObject.getString("subj_school_year");
                        String subj_file = jsonObject.getString("subj_file");
                        String subj_code = jsonObject.getString("subj_code");
                        String studentCount = jsonObject.getString("studentCount");

                        Subject subject = new Subject();
                        subject.setId(subj_id);
                        subject.setLevel(subj_level);
                        subject.setUser_id(user_id);
                        subject.setSection(subj_section);
                        subject.setTitle(subj_title);
                        subject.setDescription(subj_description);
                        subject.setSemester(subj_semester);
                        subject.setSchool_year(subj_school_year);
                        subject.setImage(subj_file);
                        subject.setCode(subj_code);
                        subject.setStud_count(studentCount);

                        subjectArrayList.add(subject);
                    }

                    Collections.reverse(subjectArrayList);

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    subjectsEducatorAdapter = new SubjectsEducatorAdapter(context, subjectArrayList);
                    subjectsEducatorAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedSubject = subjectArrayList.get(position);

                            if (fromButton == 1) {
                                Intent intent = new Intent(context, SubjectDetailsActivity.class);
                                intent.putExtra("SUBJECT", selectedSubject);
                                startActivity(intent);
                            } else if (fromButton == 2) {
                                openDeleteDialog();
                            }
                        }
                    });

                    recyclerView.setAdapter(subjectsEducatorAdapter);
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
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String subj_id = jsonObject.getString("subj_id");
                        String subj_level = jsonObject.getString("subj_level");
                        String user_id = jsonObject.getString("user_id");
                        String subj_section = jsonObject.getString("subj_section");
                        String subj_title = jsonObject.getString("subj_title");
                        String subj_description = jsonObject.getString("subj_description");
                        String subj_semester = jsonObject.getString("subj_semester");
                        String subj_school_year = jsonObject.getString("subj_school_year");
                        String subj_file = jsonObject.getString("subj_file");
                        String subj_code = jsonObject.getString("subj_code");
                        String user_firstname = jsonObject.getString("user_firstname");
                        String user_lastname = jsonObject.getString("user_lastname");

                        Subject subject = new Subject();
                        subject.setId(subj_id);
                        subject.setLevel(subj_level);
                        subject.setUser_id(user_id);
                        subject.setSection(subj_section);
                        subject.setTitle(subj_title);
                        subject.setDescription(subj_description);
                        subject.setSemester(subj_semester);
                        subject.setSchool_year(subj_school_year);
                        subject.setImage(subj_file);
                        subject.setCode(subj_code);
                        subject.setUser_firstname(user_firstname);
                        subject.setUser_lastname(user_lastname);

                        subjectArrayList.add(subject);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    subjectsStudentAdapter = new SubjectsStudentAdapter(context, subjectArrayList);
                    subjectsStudentAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedSubject = subjectArrayList.get(position);
                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
                            intent.putExtra("SUBJECT", selectedSubject);
                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(subjectsStudentAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
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
        params.put("subj_section", section);
        params.put("subj_semester", semester);
        params.put("subj_school_year", schoolYear);
        params.put("subj_file", "");

        HttpProvider.post(context, "controller_educator/add_subjects.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
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

    private void deleteSubject() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_token", UserEducator.getToken(context));
        params.put("subj_id", selectedSubject.getId());

        HttpProvider.post(context, "controller_educator/DeleteSubjectById.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                if (str.contains("success")) {
                    Toasty.success(context, "Deleted").show();
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

    private void openAddSubjectDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_edit_subject, null);
        final EditText etName, etDescription, etSection;
        final MaterialSpinner spLevel, spSemester;
        final TextView tvHeader;
        final Button btnSave;
        final ImageView ivClose;

        etName = dialogView.findViewById(R.id.et_AddEditSubjectName);
        etDescription = dialogView.findViewById(R.id.et_AddEditSubjectDescription);
        etSection = dialogView.findViewById(R.id.et_AddEditSubjectSection);
        spLevel = dialogView.findViewById(R.id.sp_AddEditSubjectLevel);
        spSemester = dialogView.findViewById(R.id.sp_AddEditSubjectSemester);
        tvHeader = dialogView.findViewById(R.id.tv_AddEditSubjectHeader);
        btnSave = dialogView.findViewById(R.id.btn_AddEditSubjectSave);
        ivClose = dialogView.findViewById(R.id.iv_AddEditSubjectClose);

        spSemester.setVisibility(View.GONE);

        level = sLevel.get(0);

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
                section = etSection.getText().toString();

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

    private void openDeleteDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete_colored)
                .setMessage("Are you sure you want to delete \n" + selectedSubject.getTitle() + "?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSubject();
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }

    private void openSearchSubjectDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.dialog_search_subject);
        final EditText et_SearchSubject;
        final ImageView iv_SearchBack, iv_SearchIcon;

        et_SearchSubject = dialog.findViewById(R.id.et_SearchSubject);
        iv_SearchBack = dialog.findViewById(R.id.iv_SearchBack);
        iv_SearchIcon = dialog.findViewById(R.id.iv_SearchIcon);
        rv_Search = dialog.findViewById(R.id.rv_Search);
        swipeRefreshLayout1 = dialog.findViewById(R.id.swipe_Search);
        emptyIndicator1 = dialog.findViewById(R.id.view_EmptyRecord);


        iv_SearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        iv_SearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_text = et_SearchSubject.getText().toString();
                //Toasty.warning(context, text).show();

                swipeRefreshLayout1.setRefreshing(true);

                ProgressPopup.showProgress(context);

                subjectArrayList.clear();

                RequestParams params = new RequestParams();
                params.put("stud_token", UserStudent.getToken(context));
                params.put("stud_id", UserStudent.getID(context));
                params.put("search_text", search_text);

                HttpProvider.post(context, "controller_student/search_subject.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        ProgressPopup.hideProgress();
                        swipeRefreshLayout1.setRefreshing(false);
                        String str = new String(responseBody, StandardCharsets.UTF_8);
                        if (str.contains("NO RECORD FOUND"))
                            emptyIndicator1.setVisibility(View.VISIBLE);
                        try {
                            JSONArray jsonArray = new JSONArray(str);
                            Debugger.logD("SUBJECTS: " + jsonArray);
                            for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String subj_id = jsonObject.getString("subj_id");
                                String subj_level = jsonObject.getString("subj_level");
                                String user_id = jsonObject.getString("user_id");
                                String subj_section = jsonObject.getString("subj_section");
                                String subj_title = jsonObject.getString("subj_title");
                                String subj_description = jsonObject.getString("subj_description");
                                String subj_semester = jsonObject.getString("subj_semester");
                                String subj_school_year = jsonObject.getString("subj_school_year");
                                String subj_file = jsonObject.getString("subj_file");
                                String subj_code = jsonObject.getString("subj_code");
                                String user_firstname = jsonObject.getString("user_firstname");
                                String user_lastname = jsonObject.getString("user_lastname");


                                Subject subject = new Subject();
                                subject.setId(subj_id);
                                subject.setLevel(subj_level);
                                subject.setUser_id(user_id);
                                subject.setSection(subj_section);
                                subject.setTitle(subj_title);
                                subject.setDescription(subj_description);
                                subject.setSemester(subj_semester);
                                subject.setSchool_year(subj_school_year);
                                subject.setImage(subj_file);
                                subject.setCode(subj_code);
                                subject.setUser_firstname(user_firstname);
                                subject.setUser_lastname(user_lastname);

                                subjectArrayList.add(subject);
                            }

                            rv_Search.setLayoutManager(new LinearLayoutManager(context));
                            rv_Search.setHasFixedSize(true);
                            subjectSearchAdapter = new SubjectSearchAdapter(context, subjectArrayList);
                            subjectSearchAdapter.setClickListener(new OnClickRecyclerView() {
                                @Override
                                public void onItemClick(View view, int position, int fromButton) {
                                    selectedSubject = subjectArrayList.get(position);
                                    sendSubjectRequest();
                                }
                            });

                            rv_Search.setAdapter(subjectSearchAdapter);
                            emptyIndicator1.setVisibility(View.GONE);

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
        });

        dialog.show();
    }

    private void sendSubjectRequest(){
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("stud_token", UserStudent.getToken(context));
        params.put("stud_id", UserStudent.getID(context));
        params.put("subj_code", selectedSubject.getCode());

        HttpProvider.post(context, "controller_student/request_join_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody: " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str: " + str);
                if (str.contains("success"))
                    Toasty.success(context, "Request to join sent.").show();
                else
                    Toasty.warning(context, "You're already enrolled").show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void ShowEducatorIntro(String title, String text, int viewId, final int type) {

        new GuideView.Builder(context)
                .setTitle(title)
                .setContentText(text)
                .setTargetView(findViewById(viewId))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                    }
                })
                .build()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (role.equals(UserRole.Educator()))
            getEducatorSubjectDetails();
        else
            getStudentSubjectDetails();
    }
}
