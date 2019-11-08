package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.QuestionsAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Question;
import com.theyestech.yes_mobile.models.Quiz;
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
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectQuizQuestionsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private TextView tvHeader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Question> questionArrayList = new ArrayList<>();
    private QuestionsAdapter questionsAdapter;
    private Question selectedQuestion = new Question();

    private Quiz quiz;
    private String quizType;

    private boolean isEdit = false;

    private String questionId;
    private String question;
    private String choice1, choice2, choice3, choice4;
    private String answer;
    private ArrayList<String> choicesArrayList = new ArrayList<>();
    private ArrayList<Integer> isCorrectArrayList = new ArrayList<>();

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_quiz_questions);

        quiz = getIntent().getParcelableExtra("QUIZ");

        context = this;
        role = UserRole.getRole(context);

        if (role.equals(UserRole.Educator()))
            userId = UserEducator.getID(context);
        else
            userId = UserStudent.getID(context);

        initializeUI();

        tvHeader.setText(quiz.getQuiz_title());
        quizType = quiz.getQuiz_type();

    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_QuizQuestionsBack);
        tvHeader = findViewById(R.id.tv_QuizQuestionsTitle);
        swipeRefreshLayout = findViewById(R.id.swipe_QuizQuestions);
        recyclerView = findViewById(R.id.rv_QuizQuestions);
        floatingActionButton = findViewById(R.id.fab_QuizQuestions);
        emptyIndicator = findViewById(R.id.view_Empty);

        if (role.equals(UserRole.Student())) {
            swipeRefreshLayout.setEnabled(false);
            floatingActionButton.setVisibility(View.GONE);
        }

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
                isEdit = false;
                switch (quizType) {
                    case "Multiple":
                        openAddEditMultipleChoiceDialog();
                        break;
                    case "TrueOrFalse":
                        openAddEditTrueOrFalseDialog();
                        break;
                }
            }
        });
    }

    private void getQuestionDetails() {
        questionArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);
        floatingActionButton.setEnabled(false);

        RequestParams params = new RequestParams();
        params.put("quiz_id", quiz.getQuiz_id());
        params.put("user_id", userId);

        HttpProvider.post(context, "controller_global/GetQuestionsAndAnswers.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                floatingActionButton.setEnabled(true);

                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

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

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    questionsAdapter = new QuestionsAdapter(context, questionArrayList, role);
                    questionsAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position) {
                            selectedQuestion = questionArrayList.get(position);

                            if (role.equals(UserRole.Student())){
                                Intent intent = new Intent(context, SubjectQuizQuestionsActivity.class);
//                                intent.putExtra("QUIZ", selectedQuestion);
                                startActivity(intent);
                            } else {
                                isEdit = true;
                                selectAction();
                            }
                        }
                    });

                    recyclerView.setAdapter(questionsAdapter);
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

    private void saveUpdateQuestion() {
        ProgressPopup.showProgress(context);
        JSONArray jArray = new JSONArray();
        try {
            for (int i = 0; i < choicesArrayList.size(); i++) {
                JSONObject jGroup = new JSONObject();
                jGroup.put("choice", choicesArrayList.get(i));
                jGroup.put("isCorrect", isCorrectArrayList.get(i));
                jArray.put(jGroup);
            }
            Debugger.logD(jArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams();
        params.put("quiz_type", quizType);
        params.put("selectedAnswer", answer);
        params.put("question", question);
        params.put("quiz_id", quiz.getQuiz_id());
        params.put("question_id", questionId);
        params.put("choices", jArray);

        Debugger.printO(params);
        Debugger.logD("ANSWER:" + answer);

        HttpProvider.post(context, "controller_educator/AddUpdateQuestion.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(str);
                    String result = jsonObject.getString("result");
                    if (result.contains("added")) {
                        Toasty.success(context, "Saved.").show();
                    } else if (result.contains("updated")) {
                        Toasty.success(context, "Updated.").show();
                    } else
                        Toasty.warning(context, "Failed").show();
                    getQuestionDetails();
                } catch (JSONException e) {
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

    private void selectAction() {
        String items[] = {" Edit ", " Delete ", " Cancel "};
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);
        dialog.setTitle("Select action");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    switch (quizType) {
                        case "Multiple":
                            openAddEditMultipleChoiceDialog();
                            break;
                        case "TrueOrFalse":
                            openAddEditTrueOrFalseDialog();
                            break;
                    }
                }
                if (which == 1) {
                    openDeleteDialog();
                }
                if (which == 2) {
                    dialog.dismiss();
                }
            }
        });
        dialog.create().show();
    }

    private void openAddEditTrueOrFalseDialog() {
        choicesArrayList.clear();
        isCorrectArrayList.clear();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_edit_question_true_or_false, null);
        final EditText etQuestion;
        final Button btnSave;
        final ImageView ivClose;
        final RadioGroup radioGroup;
        final RadioButton rbTrue, rbFalse;
        final TextView tvQuizHeader;

        tvQuizHeader = dialogView.findViewById(R.id.tv_QuizTrueOrFalseHeader);
        etQuestion = dialogView.findViewById(R.id.et_QuizTrueOrFalseQuestion);
        btnSave = dialogView.findViewById(R.id.btn_QuizTrueOrFalseSave);
        ivClose = dialogView.findViewById(R.id.iv_QuizTrueOrFalseBack);
        radioGroup = dialogView.findViewById(R.id.rg_QuizTrueOrFalse);
        rbTrue = dialogView.findViewById(R.id.rb_QuizTrueOrFalseTrue);
        rbFalse = dialogView.findViewById(R.id.rb_QuizTrueOrFalseFalse);

        if (isEdit) {
            etQuestion.setText(selectedQuestion.getQuestion_value());
            if (selectedQuestion.getQuestion_correct_answer().equals("True"))
                rbTrue.setChecked(true);
            else
                rbFalse.setChecked(true);
            tvQuizHeader.setText("Edit Question");
        } else {
            tvQuizHeader.setText("Add Question");
            etQuestion.setText("");
        }

        rbTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "True";
                isCorrectArrayList.add(1);
                isCorrectArrayList.add(0);
            }
        });

        rbFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = "False";
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(1);
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
                question = etQuestion.getText().toString();
                choicesArrayList.add("True");
                choicesArrayList.add("False");

                if (rbTrue.isSelected())
                    answer = "True";
                else if (rbFalse.isSelected())
                    answer = "False";

                if (isEdit) {
                    questionId = selectedQuestion.getQuestion_id();
                    saveUpdateQuestion();
                    b.hide();
                } else {
                    questionId = "0";
                    if (question.isEmpty()) {
                        Toasty.warning(context, "Please input question.").show();
                    } else {
                        if (radioGroup.getCheckedRadioButtonId() == -1) {
                            Toasty.warning(context, "Please select answer.").show();
                        } else {
                            saveUpdateQuestion();
                            b.hide();
                        }
                    }
                }
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void openAddEditMultipleChoiceDialog() {
        choicesArrayList.clear();
        isCorrectArrayList.clear();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_edit_question_multiple_choice, null);
        final EditText etQuestion, etChoice1, etChoice2, etChoice3, etChoice4;
        final Button btnSave;
        final ImageView ivClose;
        final RadioGroup radioGroup;
        final RadioButton rbChoice1, rbChoice2, rbChoice3, rbChoice4;
        final TextView tvQuizHeader;

        tvQuizHeader = dialogView.findViewById(R.id.tv_QuizMultipleChoiceHeader);
        etQuestion = dialogView.findViewById(R.id.et_QuizMultipleChoiceQuestion);
        etChoice1 = dialogView.findViewById(R.id.et_QuizMultipleChoice1);
        etChoice2 = dialogView.findViewById(R.id.et_QuizMultipleChoice2);
        etChoice3 = dialogView.findViewById(R.id.et_QuizMultipleChoice3);
        etChoice4 = dialogView.findViewById(R.id.et_QuizMultipleChoice4);
        radioGroup = dialogView.findViewById(R.id.rg_QuizMultipleChoice);
        rbChoice1 = dialogView.findViewById(R.id.rb_QuizMultipleChoice1);
        rbChoice2 = dialogView.findViewById(R.id.rb_QuizMultipleChoice2);
        rbChoice3 = dialogView.findViewById(R.id.rb_QuizMultipleChoice3);
        rbChoice4 = dialogView.findViewById(R.id.rb_QuizMultipleChoice4);
        btnSave = dialogView.findViewById(R.id.btn_QuizMultipleChoiceSave);
        ivClose = dialogView.findViewById(R.id.iv_QuizMultipleChoiceBack);

        if (isEdit) {
            etQuestion.setText(selectedQuestion.getQuestion_value());
            etChoice1.setText(selectedQuestion.getAnswers().get(0).getChoice_value());
            etChoice2.setText(selectedQuestion.getAnswers().get(1).getChoice_value());
            etChoice3.setText(selectedQuestion.getAnswers().get(2).getChoice_value());
            etChoice4.setText(selectedQuestion.getAnswers().get(3).getChoice_value());
            if (selectedQuestion.getQuestion_correct_answer().equals(etChoice1.getText().toString()))
                rbChoice1.setChecked(true);
            else if (selectedQuestion.getQuestion_correct_answer().equals(etChoice2.getText().toString()))
                rbChoice2.setChecked(true);
            else if (selectedQuestion.getQuestion_correct_answer().equals(etChoice3.getText().toString()))
                rbChoice3.setChecked(true);
            else if (selectedQuestion.getQuestion_correct_answer().equals(etChoice4.getText().toString()))
                rbChoice4.setChecked(true);
            tvQuizHeader.setText("Edit Question");
        } else {
            tvQuizHeader.setText("Add Question");
            etQuestion.setText("");
        }

        rbChoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice1 = etChoice1.getText().toString();
                answer = choice1;
                isCorrectArrayList.add(1);
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(0);
            }
        });

        rbChoice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice2 = etChoice2.getText().toString();
                answer = choice2;
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(1);
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(0);
            }
        });

        rbChoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice3 = etChoice3.getText().toString();
                answer = choice3;
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(1);
                isCorrectArrayList.add(0);
            }
        });

        rbChoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice4 = etChoice4.getText().toString();
                answer = choice4;
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(0);
                isCorrectArrayList.add(1);
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
                question = etQuestion.getText().toString();
                choice1 = etChoice1.getText().toString();
                choice2 = etChoice2.getText().toString();
                choice3 = etChoice3.getText().toString();
                choice4 = etChoice4.getText().toString();
                choicesArrayList.add(choice1);
                choicesArrayList.add(choice2);
                choicesArrayList.add(choice3);
                choicesArrayList.add(choice4);

                if (isEdit) {
                    questionId = selectedQuestion.getQuestion_id();
                    saveUpdateQuestion();
                    b.hide();
                } else {
                    questionId = "0";
                    if (question.isEmpty() ||
                            choice1.isEmpty() ||
                            choice2.isEmpty() ||
                            choice3.isEmpty() ||
                            choice4.isEmpty()) {
                        Toasty.warning(context, "Please complete the fields.").show();
                    } else {
                        if (radioGroup.getCheckedRadioButtonId() == -1) {
                            Toasty.warning(context, "Please select answer.").show();
                        } else {
                            saveUpdateQuestion();
                            b.hide();
                        }
                    }
                }
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void deleteQuizQuestion() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("quiz_id", quiz.getQuiz_id());
        params.put("question_id", questionId);

        HttpProvider.post(context, "controller_educator/DeleteQuestionAndAnswer.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(str);
                    String result = jsonObject.getString("result");
                    if (result.contains("deleted")) {
                        Toasty.success(context, "Deleted.").show();

                    } else
                        Toasty.warning(context, "Failed").show();
                    getQuestionDetails();
                } catch (JSONException e) {
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

    private void openDeleteDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure you want to delete question?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        questionId = selectedQuestion.getQuestion_id();
                        deleteQuizQuestion();
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getQuestionDetails();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}
