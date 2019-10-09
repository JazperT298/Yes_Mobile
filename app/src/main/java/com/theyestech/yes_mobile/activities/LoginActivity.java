package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.dialogs.OkayClosePopup;
import com.theyestech.yes_mobile.dialogs.ProgressPopup;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private Context context;

    private TextView tvSignUp;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    private int roleId;

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

    private boolean fieldsAreEmpty() {
        if (etEmail.getText().toString().isEmpty() &&
                etPassword.getText().toString().isEmpty())
            return true;
        else
            return false;
    }
}
