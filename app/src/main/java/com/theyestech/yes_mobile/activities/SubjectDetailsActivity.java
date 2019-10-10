package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.models.Section;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectDetailsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack, ivDetails, ivStudents, ivTopics, ivQuizzes, ivDelete;
    private TextView tvHeader;

    private EditText etName, etDescription;
    private MaterialSpinner spSection, spLevel, spSemester;
    private TextView tvHeaderDialog;
    private Button btnSave;
    private ImageView ivClose;

    private Subject subject;

    private ArrayList<Section> sectionArrayList = new ArrayList<>();

    private ArrayList<String> sName = new ArrayList<>();
    private ArrayList<String> sId = new ArrayList<>();
    private ArrayList<String> sLevel = new ArrayList<>();
    private ArrayList<String> sSemester = new ArrayList<>();

    private String name = "", description = "", sectionId = "", level = "", semester = "", schoolYear = "";

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
        tvHeader = findViewById(R.id.tv_SubjectDetailsSHeader);
        ivBack = findViewById(R.id.iv_SubjectDetailsBack);
        ivDetails = findViewById(R.id.iv_SubjectDetailsViewDetails);
        ivStudents = findViewById(R.id.iv_SubjectDetailsViewStudents);
        ivTopics = findViewById(R.id.iv_SubjectDetailsViewTopics);
        ivQuizzes = findViewById(R.id.iv_SubjectDetailsViewQuizzes);
        ivDelete = findViewById(R.id.iv_SubjectDetailsDeleteSubject);

        tvHeader.setText(subject.getTitle());

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSectionDetails();
                openAddSubjectDialog();
            }
        });

        ivStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectStudentsActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        ivTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectTopicsActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        ivQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectQuizzesActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog();
            }
        });
    }

    private void getSectionDetails() {
        sectionArrayList.clear();
        sName.clear();
        sId.clear();

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

                        sName.add(section_name);
                        sId.add(section_id);

                        sectionArrayList.add(section);
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
        spSection = dialogView.findViewById(R.id.sp_AddEditSubjectSection);
        spLevel = dialogView.findViewById(R.id.sp_AddEditSubjectLevel);
        spSemester = dialogView.findViewById(R.id.sp_AddEditSubjectSemester);
        tvHeaderDialog = dialogView.findViewById(R.id.tv_AddEditSubjectHeader);
        btnSave = dialogView.findViewById(R.id.btn_AddEditSubjectSave);
        ivClose = dialogView.findViewById(R.id.iv_AddEditSubjectClose);

        tvHeaderDialog.setText("Edit Subject");

        spSemester.setVisibility(View.GONE);

        setSubjectDetailsField();

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
                    subject.setSection_id(sectionId);
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
        spSection.post(new Runnable() {
            @Override
            public void run() {
                spSection.setSelectedIndex(sId.indexOf(subject.getSection_id()));
                sectionId = sId.get(spSection.getSelectedIndex());
            }
        });
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

    private void openDeleteDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        logoutUser();
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }
}
