package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private View view;
    private Context context;

    private TextView tvSignUp, tvForgorPassword;
    private EditText etEmail, etPassword;
    private ImageView ivBack;
    private FloatingActionButton floatingActionButton;

    private String role;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent extras = getIntent();
        Bundle bundle = extras.getExtras();
        assert bundle != null;
        role = bundle.getString("ROLE");

        context = this;

        initializeUI();
    }

    private void initializeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorWhite));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ivBack = findViewById(R.id.iv_LoginBack);
        etEmail = findViewById(R.id.et_LoginEmail);
        etPassword = findViewById(R.id.et_LoginPassword);
        floatingActionButton = findViewById(R.id.fab_LoginSignIn);
        tvSignUp = findViewById(R.id.tv_LoginSignUp);
        tvForgorPassword = findViewById(R.id.tv_LoginForgotPassword);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsAreEmpty())
                    Toasty.warning(context, "Complete the fields.").show();
                else {
                    if (!etEmail.getText().toString().contains("@"))
                        Toasty.warning(context, "Invalid email.").show();
                    else
                        switch (role) {
                            case "1":
                                loginEducator();
                                break;
                            case "2":
                                loginStudent();
                                break;
                        }
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterActivity.class);
                intent.putExtra("ROLE_ID", role);
                startActivity(intent);
            }
        });

        tvForgorPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void loginEducator() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("login_e_email_address", etEmail.getText().toString());
        params.put("login_e_password", etPassword.getText().toString());

        HttpProvider.postLogin(context, "controller_educator/login_as_educator_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();

                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.isEmpty()) {
                    OkayClosePopup.showDialog(context, "Something went wrong. Please try again.", "Close");
                }

                if (str.contains("success")) {
                    try {
                        JSONArray jsonArray = new JSONArray(str);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String result = jsonObject.getString("result");
                        String user_id = jsonObject.getString("user_id");
                        String user_token = jsonObject.getString("user_token");

                        UserEducator userEducator = new UserEducator();
                        userEducator.setId(user_id);
                        userEducator.setToken(user_token);

                        getEducatorDetails(userEducator);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Debugger.logD(e.toString());
                    }
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(str);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String result = jsonObject.getString("result");

                        Toasty.warning(context, result).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Debugger.logD(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void loginStudent() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("login_s_email_address", etEmail.getText().toString());
        params.put("login_s_password", etPassword.getText().toString());

        HttpProvider.post(context, "controller_student/login_as_student_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();

                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.contains("success")) {
                    try {
                        JSONArray jsonArray = new JSONArray(str);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String result = jsonObject.getString("result");
                        String user_id = jsonObject.getString("user_id");
                        String user_token = jsonObject.getString("user_token");

                        UserStudent userStudent = new UserStudent();
                        userStudent.setId(user_id);
                        userStudent.setToken(user_token);
                        Debugger.logD(result);

                        getStudentDetails(userStudent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Debugger.logD(e.toString());
                    }
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(str);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String result = jsonObject.getString("result");

                        Toasty.warning(context, result).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Debugger.logD(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void getEducatorDetails(final UserEducator userEducator) {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_token", userEducator.getToken());
        params.put("user_id", userEducator.getId());

        HttpProvider.post(context, "controller_global/get_user_details.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();

                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);

                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String user_email_address = jsonObject.getString("user_email_address");
                    String user_firstname = jsonObject.getString("user_firstname");
                    String user_lastname = jsonObject.getString("user_lastname");
                    String user_middlename = jsonObject.getString("user_middlename");
                    String user_suffixes = jsonObject.getString("user_suffixes");
                    String user_gender = jsonObject.getString("user_gender");
                    String user_contact_number = jsonObject.getString("user_contact_number");
                    String user_image = jsonObject.getString("user_image");
                    String user_educational_attainment = jsonObject.getString("user_educational_attainment");
                    String user_subj_major = jsonObject.getString("user_subj_major");
                    String user_current_school = jsonObject.getString("user_current_school");
                    String user_position = jsonObject.getString("user_position");
                    String user_facebook = jsonObject.getString("user_facebook");
                    String user_instagram = jsonObject.getString("user_instagram");
                    String user_twitter = jsonObject.getString("user_twitter");
                    String user_gmail = jsonObject.getString("user_gmail");
                    String user_motto = jsonObject.getString("user_motto");
                    String user_activation = jsonObject.getString("user_activation");
                    String user_role = jsonObject.getString("user_role");
                    String validated = jsonObject.getString("validated");
                    String connection = jsonObject.getString("connection");

                    if (user_gender.equals("1"))
                        user_gender = "Male";
                    else
                        user_gender = "Female";

                    userEducator.setEmail_address(user_email_address);
                    userEducator.setPassword(etPassword.getText().toString());
                    userEducator.setFirsname(user_firstname);
                    userEducator.setLastname(user_lastname);
                    userEducator.setMiddlename(user_middlename);
                    userEducator.setSuffix(user_suffixes);
                    userEducator.setGender(user_gender);
                    userEducator.setContact_number(user_contact_number);
                    userEducator.setImage(user_image);
                    userEducator.setEducational_attainment(user_educational_attainment);
                    userEducator.setSubj_major(user_subj_major);
                    userEducator.setCurrent_school(user_current_school);
                    userEducator.setPosition(user_position);
                    userEducator.setFacebook(user_facebook);
                    userEducator.setInstagram(user_instagram);
                    userEducator.setTwitter(user_twitter);
                    userEducator.setGmail(user_gmail);
                    userEducator.setMotto(user_motto);
                    userEducator.setUser_activation(user_activation);
                    userEducator.setUser_role(user_role);
                    userEducator.setValidated(validated);
                    userEducator.setConnection(connection);

                    UserRole userRole = new UserRole();
                    userRole.setUserRole(UserRole.Educator());
                    userRole.saveRole(context);

                    userEducator.saveUserSession(context);

                    if (firebaseAuth.getCurrentUser() == null) {
                        doFirebaseLogin(userEducator.getEmail_address(), userEducator.getPassword());
                    } else {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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

    private void getStudentDetails(final UserStudent userStudent) {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_token", userStudent.getToken());
        params.put("user_id", userStudent.getId());

        HttpProvider.post(context, "controller_global/get_user_details.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();

                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);

                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String user_email_address = jsonObject.getString("user_email_address");
                    String user_firstname = jsonObject.getString("user_firstname");
                    String user_lastname = jsonObject.getString("user_lastname");
                    String user_middlename = jsonObject.getString("user_middlename");
                    String user_suffixes = jsonObject.getString("user_suffixes");
                    String user_gender = jsonObject.getString("user_gender");
                    String user_contact_number = jsonObject.getString("user_contact_number");
                    String user_image = jsonObject.getString("user_image");
                    String user_educational_attainment = jsonObject.getString("user_educational_attainment");
                    String user_subj_major = jsonObject.getString("user_subj_major");
                    String user_current_school = jsonObject.getString("user_current_school");
                    String user_position = jsonObject.getString("user_position");

                    userStudent.setEmail_address(user_email_address);
                    userStudent.setPassword(etPassword.getText().toString());
                    userStudent.setFirsname(user_firstname);
                    userStudent.setLastname(user_lastname);
                    userStudent.setMiddlename(user_middlename);
                    userStudent.setSuffix(user_suffixes);
                    userStudent.setGender(user_gender);
                    userStudent.setContact_number(user_contact_number);
                    userStudent.setImage(user_image);
                    userStudent.setEducational_attainment(user_educational_attainment);
                    userStudent.setSubj_major(user_subj_major);
                    userStudent.setCurrent_school(user_current_school);
                    userStudent.setPosition(user_position);
                    userStudent.saveUserSession(context);

                    UserRole userRole = new UserRole();
                    userRole.setUserRole(UserRole.Student());
                    userRole.saveRole(context);

                    Toasty.success(context, "Success.").show();

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("ROLE_ID", 1);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private boolean fieldsAreEmpty() {
        if (etEmail.getText().toString().isEmpty() &&
                etPassword.getText().toString().isEmpty())
            return true;
        else
            return false;
    }

    private void doFirebaseLogin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            usersRef.child("status").setValue("online");

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Authentication failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
