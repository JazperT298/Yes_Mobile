package com.theyestech.yes_mobile.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.utils.Debugger;

public class SubjectDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);

        Subject subject = (Subject) getIntent().getParcelableExtra("SUBJECT");
        if (subject != null) {
            Debugger.logD(subject.getTitle());
        }
    }
}
