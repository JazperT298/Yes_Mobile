package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.fragments.HomeFragment;
import com.theyestech.yes_mobile.models.Section;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.UserRole;

public class ProfileActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivProfile, ivBack, ivDetails, ivSubjects, ivSections, ivVideos, ivAssessments, ivAnnouncements, ivLogout;
    private TextView tvFullname, tvEmail, tvPostCount, tvSubjectCount, tvSectionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        role = UserRole.getRole(context);

        if (role.equals(UserRole.Educator()))
            initializeEducatorUI();
    }

    private void initializeEducatorUI() {
        setContentView(R.layout.activity_profile_educator);

        ivProfile = findViewById(R.id.iv_ProfileImage);
        ivBack = findViewById(R.id.iv_ProfileBack);
        ivDetails = findViewById(R.id.iv_ProfileDetails);
        ivSubjects = findViewById(R.id.iv_ProfileSubjects);
        ivSections = findViewById(R.id.iv_ProfileSections);
        ivVideos = findViewById(R.id.iv_ProfileVideos);
        ivAssessments = findViewById(R.id.iv_ProfileAssessment);
        ivAnnouncements = findViewById(R.id.iv_ProfileAnnouncement);
        ivLogout = findViewById(R.id.iv_ProfileLogout);
        tvFullname = findViewById(R.id.tv_ProfileFullname);
        tvEmail = findViewById(R.id.tv_ProfileEmail);
        tvPostCount = findViewById(R.id.tv_ProfilePosts);
        tvSubjectCount = findViewById(R.id.tv_ProfileSubjects);
        tvSectionCount = findViewById(R.id.tv_ProfileSections);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailsActivity.class);
                startActivity(intent);
            }
        });

        ivSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectActivity.class);
                startActivity(intent);
            }
        });

        ivSections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SectionActivity.class);
                startActivity(intent);
            }
        });

        ivVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivAssessments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivAnnouncements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutDialog();
            }
        });

        setEducatorDetails();
    }

    private void initializeStudentUI() {

    }

    private void setEducatorDetails() {
        tvFullname.setText(UserEducator.getFirstname(context) + " " + UserEducator.getMiddlename(context) + " " + UserEducator.getLastname(context));
        tvEmail.setText(UserEducator.getEmail(context));
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
    }

    private void openLogoutDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Logout")
                .setIcon(R.drawable.ic_logout)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }

    private void logoutUser() {
        if (role.equals(UserRole.Educator())) {
            UserEducator.clearSession(context);
            UserRole.clearRole(context);
            HomeFragment homeFragment = new HomeFragment();
            homeFragment.checkSession();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setEducatorDetails();
    }
}
