package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.Question;
import com.theyestech.yes_mobile.models.Quiz;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectQuizzesTakeActivity extends AppCompatActivity {

    private Context context;

    private String role;
    private Quiz quiz;
    private String quizType;
    private String answer;

    private int questionCounter = 0;

    private ArrayList<Question> questionArrayList = new ArrayList<>();
    private Question selectedQuestion = new Question();

    private TextView tvMultipleHeader, tvMultipleQuestion;
    private RadioGroup rgMultiple;
    private RadioButton rbMultiple1, rbMultiple2, rbMultiple3, rbMultiple4;
    private Button btnMultipleNextDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        quiz = getIntent().getParcelableExtra("QUIZ");
        quizType = getIntent().getStringExtra("QUIZ_TYPE");

        switch (quizType) {
            case "Multiple":
                initializeMultipleUI();
                break;
            case "TrueOrFalse":
                initializeTrueOrFalseUI();
                break;
        }

        getQuestions();
    }

    private void initializeMultipleUI() {
        setContentView(R.layout.activity_subject_quizzes_take_multiple_choice);

        tvMultipleHeader = findViewById(R.id.tv_TakeQuizMultipleHeader);
        tvMultipleQuestion = findViewById(R.id.tv_TakeQuizMultipleQuestion);
        rgMultiple = findViewById(R.id.rg_TakeQuizMultiple);
        rbMultiple1 = findViewById(R.id.rb_TakeQuizMultiple1);
        rbMultiple2 = findViewById(R.id.rb_TakeQuizMultiple2);
        rbMultiple3 = findViewById(R.id.rb_TakeQuizMultiple3);
        rbMultiple4 = findViewById(R.id.rb_TakeQuizMultiple4);
        btnMultipleNextDone = findViewById(R.id.btn_TakeQuizMultipleNextDone);

        rbMultiple1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = rbMultiple1.getText().toString();
            }
        });

        rbMultiple2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = rbMultiple2.getText().toString();
            }
        });

        rbMultiple3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = rbMultiple3.getText().toString();
            }
        });

        rbMultiple4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = rbMultiple4.getText().toString();
            }
        });

        btnMultipleNextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rgMultiple.getCheckedRadioButtonId() == -1)
                    Toasty.warning(context, "Please select answer.").show();
                else
                    submitAnswer();
            }
        });
    }

    private void initializeTrueOrFalseUI() {
        setContentView(R.layout.activity_subject_quizzes_take_true_or_false);

    }

    private void getQuestions() {
        questionArrayList.clear();

        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("quiz_id", quiz.getQuiz_id());
        params.put("user_id", UserStudent.getID(context));

        HttpProvider.post(context, "controller_global/GetQuestionsAndAnswers.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();

                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals("")) {
                    Toasty.info(context, "No questions available for this quiz.").show();
                    finish();
                }

                try {
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
                            String choice_id = jsonObject1.getString("choice_id");
                            String choice_value = jsonObject1.getString("choice_value");
                            String choice_isCorrect = jsonObject1.getString("choice_isCorrect");

                            Question.Answer answer = new Question.Answer();
                            answer.setChoice_id(choice_id);
                            answer.setChoice_value(choice_value);
                            answer.setChoice_isCorrect(choice_isCorrect);

                            answerArrayList.add(answer);
                        }

                        question.setQuestion_id(question_id);
                        question.setQuestion_quiz_id(question_quiz_id);
                        question.setQuestion_value(question_value);
                        question.setQuestion_correct_answer(question_correct_answer);
                        question.setAnswers(answerArrayList);

                        questionArrayList.add(question);
                    }

                    switch (quizType) {
                        case "Multiple":
                            displayMultipleQuestion();
                            break;
                        case "TrueOrFalse":
//                                initializeTrueOrFalseUI();
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void displayMultipleQuestion() {
        rbMultiple1.setChecked(false);
        rbMultiple2.setChecked(false);
        rbMultiple3.setChecked(false);
        rbMultiple4.setChecked(false);

        selectedQuestion = questionArrayList.get(questionCounter);

        tvMultipleHeader.setText("#" + (questionCounter + 1));
        tvMultipleQuestion.setText(selectedQuestion.getQuestion_value());
        rbMultiple1.setText(selectedQuestion.getAnswers().get(0).getChoice_value());
        rbMultiple2.setText(selectedQuestion.getAnswers().get(1).getChoice_value());
        rbMultiple3.setText(selectedQuestion.getAnswers().get(2).getChoice_value());
        rbMultiple4.setText(selectedQuestion.getAnswers().get(3).getChoice_value());

        if ((questionCounter + 1) == questionArrayList.size())
            btnMultipleNextDone.setText("Done");

    }

    private void submitAnswer() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_id", UserStudent.getID(context));
        params.put("question_id", selectedQuestion.getQuestion_id());
        params.put("quiz_id", quiz.getQuiz_id());
        params.put("selectedAnswer", answer);

        HttpProvider.post(context, "controller_student/SubmitQuizAnswer.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                if (str.contains("success")) {
                    questionCounter++;
                    if (btnMultipleNextDone.getText().toString().equals("Done"))
                        submitDoneQuiz();
                    else
                        displayMultipleQuestion();
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

    private void submitDoneQuiz() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_id", UserStudent.getID(context));
        params.put("quiz_id", quiz.getQuiz_id());

        HttpProvider.post(context, "controller_student/SubmitFinishedQuiz.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                if (str.contains("success")) {
                    Toasty.success(context, "Done").show();
                    finish();
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
}
