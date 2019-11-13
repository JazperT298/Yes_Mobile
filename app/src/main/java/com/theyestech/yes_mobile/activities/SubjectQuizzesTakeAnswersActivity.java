package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.QuizQuestionAnswerAdapter;
import com.theyestech.yes_mobile.models.Question;
import com.theyestech.yes_mobile.models.Quiz;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SubjectQuizzesTakeAnswersActivity extends AppCompatActivity {

    private Context context;
    private Quiz quiz;

    private ImageView ivBack;
    private TextView tvHeader, tvScore;
    private RecyclerView recyclerView;

    private ArrayList<Question> questionArrayList = new ArrayList<>();
    private QuizQuestionAnswerAdapter quizQuestionAnswerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_quizzes_take_answers);

        context = this;

        quiz = getIntent().getParcelableExtra("QUIZ");

        initializeUI();
    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_TakeQuizAnswersBack);
        tvHeader = findViewById(R.id.tv_TakeQuizAnswersHeader);
        tvScore = findViewById(R.id.tv_TakeQuizAnswersScore);
        recyclerView = findViewById(R.id.rv_TakeQuizAnswers);

        tvHeader.setText(quiz.getQuiz_title());

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAnswers();
    }

    private void getAnswers() {
        questionArrayList.clear();

        RequestParams params = new RequestParams();
        params.put("quiz_id", quiz.getQuiz_id());
        params.put("user_id", UserStudent.getID(context));

        HttpProvider.post(context, "controller_student/GetStudentAnswers.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String str = new String(responseBody, StandardCharsets.UTF_8);

                try {
//                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = new JSONObject(str);
                    String data = jsonObject.getString("data");
                    String totalScore = jsonObject.getString("totalScore");
                    String overAllScore = jsonObject.getString("overAllScore");

                    JSONArray jsonDataArray = new JSONArray(data);
                    for (int j = 0; j <= jsonDataArray.length() - 1; j++) {
                        JSONObject jsonQuestionArray = jsonDataArray.getJSONObject(j);
                        String question_id = jsonQuestionArray.getString("question_id");
                        String question_quiz_id = jsonQuestionArray.getString("question_quiz_id");
                        String question_value = jsonQuestionArray.getString("question_value");
                        String question_correct_answer = jsonQuestionArray.getString("question_correct_answer");
                        String answers = jsonQuestionArray.getString("answers");

                        Question question = new Question();
                        ArrayList<Question.Answer> answerArrayList = new ArrayList<>();

                        JSONArray jsonAnswerArray = new JSONArray(answers);
                        for (int k = 0; k <= jsonAnswerArray.length() - 1; k++) {
                            JSONObject jsonObject1 = jsonAnswerArray.getJSONObject(k);
                            String choice_id = jsonObject1.getString("choice_id");
                            String choice_value = jsonObject1.getString("choice_value");
                            String choice_isCorrect = jsonObject1.getString("choice_isCorrect");
                            String selected_answer = jsonObject1.getString("selected_answer");

                            Question.Answer answer = new Question.Answer();
                            answer.setChoice_id(choice_id);
                            answer.setChoice_value(choice_value);
                            answer.setChoice_isCorrect(choice_isCorrect);
                            answer.setSelected_answer(selected_answer);

                            answerArrayList.add(answer);
                        }

                        question.setQuestion_id(question_id);
                        question.setQuestion_quiz_id(question_quiz_id);
                        question.setQuestion_value(question_value);
                        question.setQuestion_correct_answer(question_correct_answer);
                        question.setAnswers(answerArrayList);

                        questionArrayList.add(question);
                    }

                    tvScore.setText(String.format("Score: %s/%s", totalScore, overAllScore));

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    quizQuestionAnswerAdapter = new QuizQuestionAnswerAdapter(context, questionArrayList, quiz.getQuiz_type());

                    recyclerView.setAdapter(quizQuestionAnswerAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
}
