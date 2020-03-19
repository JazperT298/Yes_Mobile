package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.UserRole;

public class UserProfileActivity extends AppCompatActivity {
    private Context context;

    private String role;

    private ImageView ivBackground, ivProfile, ivBack, ivOptions;
    private TextView tvFullname, tvEmail, tvConnectionsCount, tvSubjectsCount, tvStudentsCount;
    private TextView tvInfoFullname, tvInfoGender, tvInfoPhone, tvInfoEmail, tvInfoMotto;
    private TextView tvInfoEducationalAttainment, tvInfoSubjectMajor, tvInfoCurrentSchool, tvInfoSchoolPosition;
    private TextView tvInfoFacebook, tvInfoTwitter, tvInfoInstagram;

    private String actionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (role.equals(UserRole.Educator())) {
            setEducatorProfile();
            actionTitle = "Educator";
        }
        else
            setStudentProfile();
    }

    private void initializeUI() {
        ivBackground = findViewById(R.id.iv_UserProfileBackground);
        ivProfile = findViewById(R.id.iv_UserProfileImage);
        ivBack = findViewById(R.id.iv_UserProfileBack);
        ivOptions = findViewById(R.id.iv_UserProfileOptions);
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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailsActivity.class);
                startActivity(intent);

//                selectAction();
            }
        });

    }

    private void selectAction() {
        String[] items = {" Edit "};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(actionTitle);
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(context, UserDetailsActivity.class);
                    startActivity(intent);
                }
            }
        });
        dialog.create().show();
    }

    private void setEducatorProfile(){
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .into(ivBackground);
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
        tvFullname.setText(String.format("%s %s %s", UserEducator.getFirstname(context), UserEducator.getLastname(context), UserEducator.getSuffix(context)));
        tvEmail.setText(UserEducator.getEmail(context));

        tvInfoFullname.setText(String.format("%s %s %s", UserEducator.getFirstname(context), UserEducator.getLastname(context), UserEducator.getSuffix(context)));
        tvInfoGender.setText(UserEducator.getGender(context));
        tvInfoPhone.setText(UserEducator.getContactNumber(context));
        tvInfoEmail.setText(UserEducator.getEmail(context));
        tvInfoMotto.setText(UserEducator.getMotto(context));

        tvInfoEducationalAttainment.setText(UserEducator.getEducationalAttainment(context));
        tvInfoSubjectMajor.setText(UserEducator.getSubjectMajor(context));
        tvInfoCurrentSchool.setText(UserEducator.getCurrentSchool(context));
        tvInfoSchoolPosition.setText(UserEducator.getPosition(context));

        tvInfoFacebook.setText(UserEducator.getFacebook(context));
        tvInfoTwitter.setText(UserEducator.getTwitter(context));
        tvInfoInstagram.setText(UserEducator.getInstragram(context));
    }

    private void setStudentProfile(){

    }

}
