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
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private Context context;

    private String role;

    //Educator widgets
    private ImageView ivProfileEducator, ivBackEducator, ivDetailsEducator, ivSubjectsEducator, ivSectionsEducator, ivNotesEducator, ivAssessmentsEducator, ivAnnouncementsEducator, ivLogoutEducator;
    private TextView tvFullnameEducator, tvEmailEducator, tvPostCountEducator, tvSubjectCountEducator, tvStudentCountEducator;

    //Student widgets
    private ImageView ivProfileStudent, ivBackStudent, ivDetailsStudent, ivSubjectsStudent, ivEducatorsStudent, ivStickersStudent, ivLogoutStudent;
    private TextView tvFullnameStudent, tvEmailStudent, tvStickerCountStudent, tvSubjectCountStudent, tvEducatorCountStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        role = UserRole.getRole(context);

        if (role.equals(UserRole.Educator()))
            initializeEducatorUI();
        else
            initializeStudentUI();
    }

    private void initializeEducatorUI() {
        setContentView(R.layout.activity_profile_educator);

        ivProfileEducator = findViewById(R.id.iv_ProfileEducatorImage);
        ivBackEducator = findViewById(R.id.iv_ProfileEducatorBack);
        ivDetailsEducator = findViewById(R.id.iv_ProfileEducatorDetails);
        ivSubjectsEducator = findViewById(R.id.iv_ProfileEducatorSubjects);
        ivSectionsEducator = findViewById(R.id.iv_ProfileEducatorSections);
        ivNotesEducator = findViewById(R.id.iv_ProfileEducatorNotes);
        ivAssessmentsEducator = findViewById(R.id.iv_ProfileEducatorRewards);
        ivAnnouncementsEducator = findViewById(R.id.iv_ProfileEducatorConnections);
        ivLogoutEducator = findViewById(R.id.iv_ProfileEducatorLogout);
        tvFullnameEducator = findViewById(R.id.tv_ProfileEducatorFullname);
        tvEmailEducator = findViewById(R.id.tv_ProfileEducatorEmail);
        tvPostCountEducator = findViewById(R.id.tv_ProfileEducatorPosts);
        tvSubjectCountEducator = findViewById(R.id.tv_ProfileEducatorSubjects);
        tvStudentCountEducator = findViewById(R.id.tv_ProfileEducatorStudents);

        ivBackEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivDetailsEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailsActivity.class);
                startActivity(intent);
            }
        });

        ivSubjectsEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectActivity.class);
                startActivity(intent);
            }
        });

        ivSectionsEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SectionActivity.class);
                startActivity(intent);
            }
        });

        ivNotesEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivAssessmentsEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivAnnouncementsEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivLogoutEducator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutDialog();
            }
        });

        setEducatorDetails();
    }

    private void initializeStudentUI() {
        setContentView(R.layout.activity_profile_student);

        ivProfileStudent = findViewById(R.id.iv_ProfileStudentImage);
        ivBackStudent = findViewById(R.id.iv_ProfileStudentBack);
        ivDetailsStudent = findViewById(R.id.iv_ProfileStudentDetails);
        ivSubjectsStudent = findViewById(R.id.iv_ProfileStudentSubjects);
        ivEducatorsStudent = findViewById(R.id.iv_ProfileStudentEducators);
        ivStickersStudent = findViewById(R.id.iv_ProfileStudentStickers);
        ivLogoutStudent = findViewById(R.id.iv_ProfileStudentLogout);
        tvFullnameStudent = findViewById(R.id.tv_ProfileStudentFullname);
        tvEmailStudent = findViewById(R.id.tv_ProfileStudentEmail);
        tvStickerCountStudent = findViewById(R.id.tv_ProfileStudentStickers);
        tvSubjectCountStudent = findViewById(R.id.tv_ProfileStudentSubjects);
        tvEducatorCountStudent = findViewById(R.id.tv_ProfileStudentEducators);

        ivBackStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivDetailsStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailsActivity.class);
                startActivity(intent);
            }
        });

        ivSubjectsStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectActivity.class);
                startActivity(intent);
            }
        });

        ivLogoutStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutDialog();
            }
        });

        setStudentDetails();
    }

    private void setEducatorDetails() {
        tvFullnameEducator.setText(String.format("%s %s", UserEducator.getFirstname(context), UserEducator.getLastname(context)));
        tvEmailEducator.setText(UserEducator.getEmail(context));
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfileEducator);

        getEducatorSubjectsAndStudentsCount();
    }

    private void setStudentDetails() {
        tvFullnameStudent.setText(String.format("%s %s", UserStudent.getFirstname(context), UserStudent.getLastname(context)));
        tvEmailStudent.setText(UserStudent.getEmail(context));
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserStudent.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfileStudent);
    }

    private void getEducatorSubjectsAndStudentsCount(){


        RequestParams params = new RequestParams();
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_educator/CountSubjectsAndStudents.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String student_count = jsonObject.getString("student_count");
                    String subject_count = jsonObject.getString("subject_count");

                    tvStudentCountEducator.setText(student_count);
                    tvSubjectCountEducator.setText(subject_count);

                } catch (JSONException e) {
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
            //Firebase Logout
            FirebaseAuth.getInstance().signOut();

            UserEducator.clearSession(context);
            UserRole.clearRole(context);
            MainActivity mainActivity = new MainActivity();
            mainActivity.checkEducatorSession();
        } else {
            //Firebase Logout
            FirebaseAuth.getInstance().signOut();

            UserStudent.clearSession(context);
            UserRole.clearRole(context);
            MainActivity mainActivity = new MainActivity();
            mainActivity.checkStudentSession();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (role.equals(UserRole.Educator()))
            setEducatorDetails();
        else
            setStudentDetails();
    }
}
