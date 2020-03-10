package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;

public class ConnectionActivity extends AppCompatActivity {
    private Context context;
    private String role;

    private ImageView ivBack, ivClose;
    private TextView tvHeader;
    private CardView cvDetails, cvStudents, cvTopics, cvQuiz, cvStickers, cvAwards, cvAssessment;

    private EditText etName, etDescription, etSection;
    private MaterialSpinner spSection, spLevel, spSemester;
    private TextView tvHeaderDialog;
    private Button btnSave;

    private Subject subject;

    private ArrayList<String> sLevel = new ArrayList<>();
    private ArrayList<String> sSemester = new ArrayList<>();

    private String name = "", description = "", section = "", level = "", semester = "", schoolYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

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

    private void initializeUI(){

    }
}
