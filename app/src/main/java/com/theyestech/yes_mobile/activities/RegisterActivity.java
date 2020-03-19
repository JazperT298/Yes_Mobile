package com.theyestech.yes_mobile.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private Context context;

    private EditText etEmail, etPassword, etConfirmPassword;
    private FloatingActionButton floatingActionButton;
    private ImageView ivBack;
    private CheckBox checkBox;
    private ProgressBar progressBar;

    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        ivBack = findViewById(R.id.iv_RegisterBack);
        etEmail = findViewById(R.id.et_RegisterEmail);
        etPassword = findViewById(R.id.et_RegisterPassword);
        etConfirmPassword = findViewById(R.id.et_RegisterConfirmPassword);
        checkBox = findViewById(R.id.cb_RegisterTermsAndConditions);
        floatingActionButton = findViewById(R.id.fab_RegisterSave);
        progressBar = findViewById(R.id.progress_RegisterLoading);

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
                    else if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString()))
                        Toasty.warning(context, "Password didn't match.").show();
                    else if (!checkBox.isChecked()) {
                        Toasty.warning(context, "Please check and read Terms and Conditions.").show();
                    } else {
                        switch (role) {
                            case "1":
                                registerEducator();
                                break;
                            case "2":
                                registerStudent();
                                break;
                        }
                    }
                }
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void accessingServer(boolean isAccessing) {
        etEmail.setEnabled(!isAccessing);
        etPassword.setEnabled(!isAccessing);
        floatingActionButton.setVisibility(isAccessing ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isAccessing ? View.VISIBLE : View.GONE);
    }

    private void registerEducator() {
        accessingServer(true);

        RequestParams params = new RequestParams();
        params.put("e_email_address", etEmail.getText().toString());
        params.put("e_password", etConfirmPassword.getText().toString());

        HttpProvider.post(context, "controller_educator/register_as_educator_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                accessingServer(false);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                if (!str.contains("exists")) {
                    finish();
                    Toasty.success(context, "Successfully registered.").show();
                } else {
                    etEmail.requestFocus();
                    Toasty.warning(context, str).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                accessingServer(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void registerStudent() {
        accessingServer(true);

        RequestParams params = new RequestParams();
        params.put("s_email_address", etEmail.getText().toString());
        params.put("s_password", etConfirmPassword.getText().toString());

        HttpProvider.post(context, "controller_student/register_as_student_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                accessingServer(false);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                if (str.equals("success")) {
                    finish();
                    Toasty.success(context, "Successfully registered.").show();
                } else
                    etEmail.requestFocus();

                Toasty.warning(context, str).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                accessingServer(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private boolean fieldsAreEmpty() {
        if (etEmail.getText().toString().isEmpty() &&
                etPassword.getText().toString().isEmpty() &&
                etConfirmPassword.getText().toString().isEmpty())
            return true;
        else
            return false;
    }
}
