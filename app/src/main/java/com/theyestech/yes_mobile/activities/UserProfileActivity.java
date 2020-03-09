package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.UserRole;

public class UserProfileActivity extends AppCompatActivity {
    private Context context;

    private String role;

    private ImageView ivBackground, ivProfile;
    private TextView tvFullname, tvEmail, tvConnectionsCount, tvSubjectsCount, tvStudentsCount;
    private TextView tvInfoFullname, tvInfoGender, tvInfoPhone, tvInfoEmail, tvInfoMotto;
    private TextView tvInfoEducationalAttainment, tvInfoSubjectMajor, tvInfoCurrentSchool, tvInfoSchoolPosition;
    private TextView tvInfoFacebook, tvInfoTwitter, tvInfoInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();

        if (role.equals(UserRole.Educator()))
            setEducatorProfile();
    }

    private void initializeUI() {
        ivBackground = findViewById(R.id.iv_UserProfileBackground);
        ivProfile = findViewById(R.id.iv_UserProfileImage);
        tvFullname = findViewById(R.id.tv_UserProfileFullname);
        tvEmail = findViewById(R.id.tv_UserProfileEmail);
        tvConnectionsCount = findViewById(R.id.tv_UserProfileConnectionCount);
        tvSubjectsCount = findViewById(R.id.tv_UserProfileSubjectsCount);
        tvStudentsCount = findViewById(R.id.tv_UserProfileStudentsCount);
        tvInfoFullname = findViewById(R.id.tv_UserProfileInfoFullname);
        tvInfoGender = findViewById(R.id.tv_UserProfileInfoGender);
        tvInfoPhone = findViewById(R.id.tv_UserProfileInfoPhone);
        tvInfoEmail = findViewById(R.id.tv_UserProfileInfoEmail);
        tvInfoMotto = findViewById(R.id.tv_UserProfileInfoMotto);
        tvInfoEducationalAttainment = findViewById(R.id.tv_UserProfileInfoEducationalAttainment);
        tvInfoSubjectMajor = findViewById(R.id.tv_UserProfileInfoSubjectMajor);
        tvInfoCurrentSchool = findViewById(R.id.tv_UserProfileInfoCurrentSchool);
        tvInfoSchoolPosition = findViewById(R.id.tv_UserProfileInfoSchoolPosition);
        tvInfoFacebook = findViewById(R.id.tv_UserProfileInfoFacebook);
        tvInfoTwitter = findViewById(R.id.tv_UserProfileInfoTwitter);
        tvInfoInstagram = findViewById(R.id.tv_UserProfileInfoInstagram);


    }

    private void setEducatorProfile(){

    }

}
