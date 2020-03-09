package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.AssessmentQuizAdapter;
import com.theyestech.yes_mobile.adapters.AssessmentsAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Assessment;
import com.theyestech.yes_mobile.models.Quiz;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SubjectAssessmentsQuizActivity extends AppCompatActivity {
    private Context context;
    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Quiz> quizArrayList = new ArrayList<>();
    private AssessmentQuizAdapter assessmentQuizAdapter;
    private Quiz selectedQuiz = new Quiz();

    private ArrayList<Assessment> assessmentArrayList = new ArrayList<>();
    private AssessmentsAdapter assessmentsAdapter;

    private Subject subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_assessments_quiz);

        subject = getIntent().getParcelableExtra("SUBJECT");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();

    }

    private void initializeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorCarrot));
        }

        ivBack = findViewById(R.id.iv_SubjectAssessmentQuizBack);
        swipeRefreshLayout = findViewById(R.id.swipe_AssessmentQuiz);
        recyclerView = findViewById(R.id.rv_AssessmentQuiz);
        emptyIndicator = findViewById(R.id.view_Empty);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQuizzes();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getQuizzes();
    }

    private void getQuizzes() {
        quizArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);
        RequestParams params = new RequestParams();
        params.put("user_id", UserEducator.getID(context));
        params.put("subj_id", subject.getId());

        HttpProvider.post(context, "controller_educator/get_quizzes_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);

                final String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("QUIZ: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String quiz_id = jsonObject.getString("quiz_id");
                        String subject_id = jsonObject.getString("subject_id");
                        String user_id = jsonObject.getString("user_id");
                        String quiz_title = jsonObject.getString("quiz_title");
                        String quiz_type = jsonObject.getString("quiz_type");
                        String quiz_item = jsonObject.getString("quiz_item");
                        String quiz_time = jsonObject.getString("quiz_time");
                        String quiz_image = jsonObject.getString("image");
                        String quiz_done = jsonObject.getString("quiz_done");

                        Quiz quiz = new Quiz();
                        quiz.setQuiz_id(quiz_id);
                        quiz.setSubject_id(subject_id);
                        quiz.setUser_id(user_id);
                        quiz.setQuiz_title(quiz_title);
                        quiz.setQuiz_type(quiz_type);
                        quiz.setQuiz_item(quiz_item);
                        quiz.setQuiz_time(quiz_time);
                        quiz.setQuiz_image(quiz_image.substring(1));
                        quiz.setQuiz_done(quiz_done);

                        quizArrayList.add(quiz);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    assessmentQuizAdapter = new AssessmentQuizAdapter(context, quizArrayList);
                    assessmentQuizAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedQuiz = quizArrayList.get(position);
                            getStudentAssessment();
                        }
                    });

                    recyclerView.setAdapter(assessmentQuizAdapter);
                    emptyIndicator.setVisibility(View.GONE);

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

    private void getStudentAssessment() {
        assessmentArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("quizId", selectedQuiz.getQuiz_id());

        HttpProvider.post(context, "controller_educator/GetQuizAssessment.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);

                final String str = new String(responseBody, StandardCharsets.UTF_8);

                try {
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("ASSESSMENT: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String user_fullname = jsonObject.getString("user_fullname");
                        String totalScore = jsonObject.getString("totalScore");
                        String overAllScore = jsonObject.getString("overAllScore");
                        String percentage = jsonObject.getString("percentage");

                        Assessment assessment = new Assessment();
                        assessment.setFullname(user_fullname);
                        assessment.setTotalScore(totalScore);
                        assessment.setOverAllScore(overAllScore);
                        assessment.setPercentage(percentage);

                        assessmentArrayList.add(assessment);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }

                openAssessmentStudentsDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void openAssessmentStudentsDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_view_assessment, null);

        ImageView ivClose = dialogView.findViewById(R.id.iv_ViewAssessmentClose);
        TextView tvHeader = dialogView.findViewById(R.id.tv_ViewAssessmentHeader);
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_ViewAssessment);
        ConstraintLayout eIndicator = dialogView.findViewById(R.id.view_Empty);

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        tvHeader.setText(selectedQuiz.getQuiz_title());

        if (assessmentArrayList.size() <= 0)
            eIndicator.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        assessmentsAdapter = new AssessmentsAdapter(context, assessmentArrayList);
        recyclerView.setAdapter(assessmentsAdapter);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
