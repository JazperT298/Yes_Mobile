package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.SubjectSearchAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.KeyboardHandler;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectSearchActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack, ivSearch;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Subject> subjectArrayList = new ArrayList<>();
    private SubjectSearchAdapter subjectSearchAdapter;
    private Subject selectedSubject = new Subject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_search);

        context = this;

        role = UserRole.getRole(context);

        initializeUI();

    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_SubjectSearchBack);
        ivSearch = findViewById(R.id.iv_SubjectSearchSearch);
        etSearch = findViewById(R.id.et_SubjectSearchSearch);
        recyclerView = findViewById(R.id.rv_SubjectSearch);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);

        etSearch.requestFocus();

        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSubject();
                KeyboardHandler.closeKeyboard(v, context);
            }
        });

//        searchSubject();
    }

    private void searchSubject() {
        ProgressPopup.showProgress(context);

        subjectArrayList.clear();

        RequestParams params = new RequestParams();
        params.put("stud_token", UserStudent.getToken(context));
        params.put("stud_id", UserStudent.getID(context));
        params.put("search_text", etSearch.getText().toString());

        HttpProvider.post(context, "controller_student/search_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();

                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.contains("NO RECORD FOUND"))
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
                        String user_firstname = jsonObject.getString("user_firstname");
                        String user_lastname = jsonObject.getString("user_lastname");

                        Subject subject = new Subject();
                        subject.setId(subj_id);
                        subject.setLevel(subj_level);
                        subject.setUser_id(user_id);
                        subject.setSection(section_id);
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
                    subjectSearchAdapter = new SubjectSearchAdapter(context, subjectArrayList);
                    subjectSearchAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedSubject = subjectArrayList.get(position);
                            sendRequest();
                        }
                    });

                    recyclerView.setAdapter(subjectSearchAdapter);
                    emptyIndicator.setVisibility(View.GONE);

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

    private void sendRequest(){
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("stud_token", UserStudent.getToken(context));
        params.put("stud_id", UserStudent.getID(context));
        params.put("subj_code", selectedSubject.getCode());

        HttpProvider.post(context, "controller_student/request_join_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();

                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.contains("success"))
                    Toasty.success(context, "Request to join sent.").show();
                else
                    Toasty.success(context, str).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
}
