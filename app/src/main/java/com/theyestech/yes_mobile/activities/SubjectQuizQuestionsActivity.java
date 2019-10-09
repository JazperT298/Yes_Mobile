package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.QuestionsAdapter;
import com.theyestech.yes_mobile.dialogs.OkayClosePopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Question;
import com.theyestech.yes_mobile.models.Quiz;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SubjectQuizQuestionsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private TextView tvHeader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private ArrayList<Question> questionArrayList = new ArrayList<>();
    private QuestionsAdapter questionsAdapter;
    private Question selectedQuestion = new Question();

    private Quiz quiz;
    private String quiizType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_quiz_questions);

        quiz = getIntent().getParcelableExtra("QUIZ");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();

        tvHeader.setText(quiz.getQuiz_title());
        quiizType = quiz.getQuiz_type();

    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_QuizQuestionsBack);
        tvHeader = findViewById(R.id.tv_QuizQuestionsTitle);
        swipeRefreshLayout = findViewById(R.id.swipe_QuizQuestions);
        recyclerView = findViewById(R.id.rv_QuizQuestions);
        floatingActionButton = findViewById(R.id.fab_QuizQuestions);

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
                getQuestionDetails();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getQuestionDetails() {
        questionArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);
        floatingActionButton.setEnabled(false);

        RequestParams params = new RequestParams();
        params.put("quiz_id", quiz.getQuiz_id());
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_educator/GetQuestionsAndAnswers.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                floatingActionButton.setEnabled(true);
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("QUESTION: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String question_id = jsonObject.getString("question_id");
                        String question_quiz_id = jsonObject.getString("question_quiz_id");
                        String question_value = jsonObject.getString("question_value");
                        String question_correct_answer = jsonObject.getString("question_correct_answer");
                        String answers = jsonObject.getString("answers");

                        Question question = new Question();
                        ArrayList<Question.Answer> answerArrayList = new ArrayList<>();

                        JSONArray jsonArray1 = new JSONArray(answers);
                        for (int j = 0; j <= jsonArray1.length() - 1; j++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                            String answer_id = jsonObject1.getString("answer_id");
                            String answer_value = jsonObject1.getString("answer_value");
                            String answer_isCorrect = jsonObject1.getString("answer_isCorrect");

                            Question.Answer answer = new Question.Answer();
                            answer.setAnswer_id(answer_id);
                            answer.setAnswer_value(answer_value);
                            answer.setAnswer_isCorrect(answer_isCorrect);

                            answerArrayList.add(answer);
                        }

                        question.setQuestion_id(question_id);
                        question.setQuestion_quiz_id(question_quiz_id);
                        question.setQuestion_value(question_value);
                        question.setQuestion_correct_answer(question_correct_answer);
                        question.setAnswers(answerArrayList);

                        questionArrayList.add(question);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    questionsAdapter = new QuestionsAdapter(context, questionArrayList);
                    questionsAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position) {
                            selectedQuestion = questionArrayList.get(position);

                        }
                    });

                    recyclerView.setAdapter(questionsAdapter);

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

    @Override
    protected void onResume() {
        super.onResume();

        getQuestionDetails();
    }
}
