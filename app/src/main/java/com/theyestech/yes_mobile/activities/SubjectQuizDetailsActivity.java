package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.Quiz;
import com.theyestech.yes_mobile.utils.UserRole;

public class SubjectQuizDetailsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private TextView tvHeader;

    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_quiz_details);

        quiz = getIntent().getParcelableExtra("QUIZ");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();

    }

    private void initializeUI () {
        ivBack = findViewById(R.id.iv_QuizDetailsBack);
        tvHeader = findViewById(R.id.tv_QuizDetailsTitle);

        tvHeader.setText(quiz.getQuiz_id());
    }
}
