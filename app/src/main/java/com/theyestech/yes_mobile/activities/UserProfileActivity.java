package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.UserRole;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class UserProfileActivity extends AppCompatActivity {
    private Context context;

    private String role;

    private ImageView ivBackground, ivProfile, ivBack, ivOptions;
    private TextView tvFullname, tvEmail, tvConnectionsCount, tvSubjectsCount, tvStudentsCount;
    private TextView tvInfoFullname, tvInfoGender, tvInfoPhone, tvInfoEmail, tvInfoMotto;
    private TextView tvInfoEducationalAttainment, tvInfoSubjectMajor, tvInfoCurrentSchool, tvInfoSchoolPosition;
    private TextView tvInfoFacebook, tvInfoTwitter, tvInfoInstagram;

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
        }
        else {
            setStudentProfile();
        }
    }

    private void initializeUI() {
        ivBackground = findViewById(R.id.iv_UserProfileBackground);
        ivProfile = findViewById(R.id.iv_UserProfileImage);
        ivBack = findViewById(R.id.iv_UserProfileBack);
        ivOptions = findViewById(R.id.iv_UserProfileOptions);
        tvFullname = findViewById(R.id.tv_UserProfileFullname);
        tvEmail = findViewById(R.id.tv_UserProfileEmail);
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
        dialog.setTitle("");
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

    private void setEducatorProfile() {
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

    private void setStudentProfile() {
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserStudent.getImage(context))
                .into(ivBackground);
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserStudent.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
        tvFullname.setText(String.format("%s %s %s", UserStudent.getFirstname(context), UserStudent.getLastname(context), UserStudent.getSuffix(context)));
        tvEmail.setText(UserStudent.getEmail(context));

        tvInfoFullname.setText(String.format("%s %s %s", UserStudent.getFirstname(context), UserStudent.getLastname(context), UserStudent.getSuffix(context)));
        tvInfoGender.setText(UserStudent.getGender(context));
        tvInfoPhone.setText(UserStudent.getContactNumber(context));
        tvInfoEmail.setText(UserStudent.getEmail(context));
        tvInfoMotto.setText(UserStudent.getMotto(context));

        tvInfoEducationalAttainment.setText(UserStudent.getEducationalAttainment(context));
        tvInfoSubjectMajor.setText(UserStudent.getSubjectMajor(context));
        tvInfoCurrentSchool.setText(UserStudent.getCurrentSchool(context));
        tvInfoSchoolPosition.setText(UserStudent.getDreamJob(context));

        tvInfoFacebook.setText(UserStudent.getFacebook(context));
        tvInfoTwitter.setText(UserStudent.getTwitter(context));
        tvInfoInstagram.setText(UserStudent.getInstragram(context));
    }

    private void ShowEducatorIntro(String title, String text, int viewId, final int type) {

        new GuideView.Builder(context)
                .setTitle(title)
                .setContentText(text)
                .setTargetView(findViewById(viewId))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                    }
                })
                .build()
                .show();
    }

}
