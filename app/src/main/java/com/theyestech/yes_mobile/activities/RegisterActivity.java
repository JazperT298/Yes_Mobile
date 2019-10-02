package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.dialogs.OkayClosePopup;
import com.theyestech.yes_mobile.dialogs.ProgressPopup;
import com.theyestech.yes_mobile.utils.Debugger;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private Context context;

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSave;

    private int roleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent extras = getIntent();
        Bundle bundle = extras.getExtras();
        roleId = bundle.getInt("ROLE_ID");

        context = this;

        initializeUI();
    }

    private void initializeUI() {
        etEmail = findViewById(R.id.et_RegisterEmail);
        etPassword = findViewById(R.id.et_RegisterPassword);
        etConfirmPassword = findViewById(R.id.et_RegisterConfirmPassword);
        btnSave = findViewById(R.id.btn_RegisterSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsAreEmpty())
                    Toasty.warning(context, "Complete the fields.").show();
                else {
                    if (!etEmail.getText().toString().contains("@"))
                        Toasty.warning(context, "Invalid email.").show();
                    else if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString()))
                        Toasty.warning(context, "Password didn't match.").show();
                    else
                        switch (roleId) {
                            case 1:
                                registerEducator();
                                break;
                            case 2:
                                registerStudent();
                                break;
                        }
                }
            }
        });
    }

    private void registerEducator() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("e_email_address", etEmail.getText().toString());
        params.put("e_password", etConfirmPassword.getText().toString());

        HttpProvider.post(context, "controller_educator/register_as_educator_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD(str);
                if (str.equals("success")) {
                    finish();
                    Toasty.success(context, "Saved.").show();
                } else
                    etEmail.requestFocus();
                    Toasty.warning(context, str).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void registerStudent() {

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
