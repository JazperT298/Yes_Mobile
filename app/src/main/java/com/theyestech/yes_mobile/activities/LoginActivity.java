package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.KeyboardHandler;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private View view;
    private Context context;

    private TextView tvSignUp;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    private int roleId;
    //Firebase
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent extras = getIntent();
        Bundle bundle = extras.getExtras();
        roleId = bundle.getInt("ROLE_ID");

        context = this;

        initializeUI();
    }

    private void initializeUI() {
        etEmail = findViewById(R.id.et_LoginEmail);
        etPassword = findViewById(R.id.et_LoginPassword);
        btnLogin = findViewById(R.id.btn_LoginSignIn);
        tvSignUp = findViewById(R.id.tv_LoginSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsAreEmpty())
                    Toasty.warning(context, "Complete the fields.").show();
                else {
                    if (!etEmail.getText().toString().contains("@"))
                        Toasty.warning(context, "Invalid email.").show();
                    else
                        switch (roleId) {
                            case 1:
                                loginEducator();
                                break;
                            case 2:
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
                intent.putExtra("ROLE_ID", roleId);
                startActivity(intent);
            }
        });
    }

    private void loginEducator() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("login_e_email_address", etEmail.getText().toString());
        params.put("login_e_password", etPassword.getText().toString());

        HttpProvider.post(context, "controller_educator/login_as_educator_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String result = jsonObject.getString("result");
                    String user_id = jsonObject.getString("user_id");
                    String user_token = jsonObject.getString("user_token");

                    UserEducator userEducator = new UserEducator();
                    userEducator.setId(user_id);
                    userEducator.setToken(user_token);
                    Debugger.logD(result);

                    if (result.contains("success"))
                        getEducatorDetails(userEducator);
                    else
                        Toasty.warning(context, result).show();

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

    private void loginStudent() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("login_s_email_address", etEmail.getText().toString());
        params.put("login_s_password", etPassword.getText().toString());

        HttpProvider.post(context, "controller_student/login_as_student_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String result = jsonObject.getString("result");
                    String user_id = jsonObject.getString("user_id");
                    String user_token = jsonObject.getString("user_token");

                    UserStudent userStudent = new UserStudent();
                    userStudent.setId(user_id);
                    userStudent.setToken(user_token);
                    Debugger.logD(result);

                    if (result.contains("success"))
                        getStudentDetails(userStudent);
                    else
                        Toasty.warning(context, result).show();

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
                    Debugger.logD(str);
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
                    userEducator.saveUserSession(context);

                    UserRole userRole = new UserRole();
                    userRole.setUserRole(UserRole.Educator());
                    userRole.saveRole(context);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    Debugger.logD(str);
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

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
    //-----------------------------------------Firebase----------------------------------------//

    private void doFirebaseLoginEducator() {
        String txt_email = etEmail.getText().toString();
        String txt_password = etPassword.getText().toString();

        if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
            Toast.makeText(context, "All fileds are required", Toast.LENGTH_SHORT).show();
        } else {

            auth.signInWithEmailAndPassword(txt_email, txt_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String firebaseUser = FirebaseAuth.getInstance().getUid();
                                KeyboardHandler.closeKeyboard(view, context);
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //finish();
                            } else {
                                Toast.makeText(context, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void doFirebaseLoginStudent() {
        String txt_email = etEmail.getText().toString();
        String txt_password = etPassword.getText().toString();

        if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
            Toast.makeText(context, "All fileds are required", Toast.LENGTH_SHORT).show();
        } else {

            auth.signInWithEmailAndPassword(txt_email, txt_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String firebaseUser = FirebaseAuth.getInstance().getUid();
                                KeyboardHandler.closeKeyboard(view, context);
                                Toasty.success(context, "Success.").show();
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
//                                finish();
                            } else {
                                Toast.makeText(context, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
