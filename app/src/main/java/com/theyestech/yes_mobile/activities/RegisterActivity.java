package com.theyestech.yes_mobile.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private Context context;

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSave;

    private int roleId;

    //Firebase
    private FirebaseAuth auth;
    private DatabaseReference reference;

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

        auth = FirebaseAuth.getInstance();

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
                                //firebaseRegisterEducator(etEmail.getText().toString(), etPassword.getText().toString());
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
                if (!str.contains("exists")) {
                    firebaseRegisterEducator(etEmail.getText().toString(), etPassword.getText().toString(),etEmail.getText().toString());
                    finish();
                    Toasty.success(context, "Successfully registered.").show();
                } else{
                    etEmail.requestFocus();
                    Toasty.warning(context, str).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void registerStudent() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("s_email_address", etEmail.getText().toString());
        params.put("s_password", etConfirmPassword.getText().toString());

        HttpProvider.post(context, "controller_student/register_as_student_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD(str);
                if (str.equals("success")) {
                    //finish();
//                    firebaseRegisterEducator(etEmail.getText().toString(), etPassword.getText().toString());
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

    private boolean fieldsAreEmpty() {
        if (etEmail.getText().toString().isEmpty() &&
                etPassword.getText().toString().isEmpty() &&
                etConfirmPassword.getText().toString().isEmpty())
            return true;
        else
            return false;
    }

    //Firebase Database
    private void firebaseRegisterStudent(final String username, String email, String password, final ProgressDialog progressDialog) {
        //final UserSessionEducator userSessionEducator = null;
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Student").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("search", username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.hide();
                                    if (task.isSuccessful()) {
                                        //openHomeFragment(role);
//                                        String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().toString();
//                                        userSessionEducator.setFirebaseToken(firebaseUser);
                                        Intent intent = new Intent(context, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        Toasty.success(context, "Successfully registered.").show();
                                    }
                                }
                            });
                        } else {
                            progressDialog.hide();
                            Toasty.warning(context, "Email address already exists.").show();
                        }
                    }
                });
    }

    //Firebase Database
    private void firebaseRegisterEducator(final String email, String password, final String gmail) {
        ProgressPopup.showProgress(context);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ProgressPopup.hideProgress();
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            //assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Educator").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("gmail", gmail.toLowerCase());
                            hashMap.put("status", "offline");
                            hashMap.put("search", email.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toasty.success(context, "Successfully registered.").show();
                                    }
                                }
                            });
                        } else {
                            Toasty.warning(context, "Email address already exists.").show();
                        }
                    }
                });
    }

}
