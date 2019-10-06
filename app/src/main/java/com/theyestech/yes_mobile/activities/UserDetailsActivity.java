package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.dialogs.OkayClosePopup;
import com.theyestech.yes_mobile.dialogs.ProgressPopup;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class UserDetailsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivProfile, ivBack;
    private EditText etFirstname, etMiddlename, etLastname, etSuffix, etEmail, etContactnumber;
    private RadioButton rbMale, rbFemale;
    private RadioGroup radioGroup;
    private TextView tvHeader;
    private Button btnEditSave, btnCancel;

    private boolean isEdit = true;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
        setFieldsMode(false);

        if (role.equals(UserRole.Educator()))
            setEducatorFieldsData();

    }

    private void initializeUI() {
        tvHeader = findViewById(R.id.tv_UserDetailsHeader);
        ivProfile = findViewById(R.id.iv_UserDetailsImage);
        ivBack = findViewById(R.id.iv_UserDetailsBack);
        etFirstname = findViewById(R.id.et_UserDetailsFirstname);
        etMiddlename = findViewById(R.id.et_UserDetailsMiddlename);
        etLastname = findViewById(R.id.et_UserDetailsLastname);
        etSuffix = findViewById(R.id.et_UserDetailsSuffix);
        etEmail = findViewById(R.id.et_UserDetailsEmail);
        etContactnumber = findViewById(R.id.et_UserDetailsContactnumber);
        rbMale = findViewById(R.id.rb_UserDetailsMale);
        rbFemale = findViewById(R.id.rb_UserDetailsFemale);
        radioGroup = findViewById(R.id.rg_UserDetails);
        btnEditSave = findViewById(R.id.btn_UserDetailsEditSave);
        btnCancel = findViewById(R.id.btn_UserDetailsCancel);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_UserDetailsMale:
                        gender = "Male";
                        break;
                    case R.id.rb_UserDetailsFemale:
                        gender = "Female";
                        break;
                }
            }
        });

        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    setFieldsMode(isEdit);
                    isEdit = false;
                    btnCancel.setVisibility(View.VISIBLE);
                } else {
                    updateEducatorDetails();
                    btnCancel.setVisibility(View.GONE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEducatorFieldsData();
                setFieldsMode(false);
                btnCancel.setVisibility(View.GONE);
                isEdit = true;
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateEducatorDetails() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("teach_id", UserEducator.getID(context));
        params.put("teach_token", UserEducator.getToken(context));
        params.put("teach_email_address", etEmail.getText().toString());
        params.put("teach_contact_number", etContactnumber.getText().toString());
        params.put("teach_lastname", etLastname.getText().toString());
        params.put("teach_lastname", etLastname.getText().toString());
        params.put("teach_firstname", etFirstname.getText().toString());
        params.put("teach_middlename", etMiddlename.getText().toString());
        params.put("teach_suffixes", etSuffix.getText().toString());
        params.put("teach_gender", gender);

        HttpProvider.post(context, "controller_educator/update_basic_details.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.getString("result").contains("success")) {
                        Toasty.success(context, "Saved.").show();
                        setFieldsMode(isEdit);
                        isEdit = true;
                        updateEducatorSession();
                    } else
                        Toasty.error(context, "Saving failed.").show();

                } catch (JSONException e) {
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

    private void updateEducatorSession() {
        UserEducator userEducator = new UserEducator();
        userEducator.setId(UserEducator.getID(context));
        userEducator.setToken(UserEducator.getToken(context));
        userEducator.setPassword(UserEducator.getPassword(context));
        userEducator.setFirsname(etFirstname.getText().toString());
        userEducator.setMiddlename(etMiddlename.getText().toString());
        userEducator.setLastname(etLastname.getText().toString());
        userEducator.setSuffix(etSuffix.getText().toString());
        userEducator.setEmail_address(etEmail.getText().toString());
        userEducator.setContact_number(etEmail.getText().toString());
        userEducator.setGender(gender);
        userEducator.setImage(UserEducator.getImage(context));
        userEducator.setEducational_attainment(UserEducator.getEducationalAttainment(context));
        userEducator.setSubj_major(UserEducator.getSubjectMajor(context));
        userEducator.setCurrent_school(UserEducator.getCurrentSchool(context));
        userEducator.setPosition(UserEducator.getPosition(context));
        userEducator.saveUserSession(context);

        setEducatorFieldsData();
    }

    private void setEducatorFieldsData() {
        etFirstname.setText(UserEducator.getFirstname(context));
        etMiddlename.setText(UserEducator.getMiddlename(context));
        etLastname.setText(UserEducator.getLastname(context));
        etSuffix.setText(UserEducator.getSuffix(context));
        etEmail.setText(UserEducator.getEmail(context));
        etContactnumber.setText(UserEducator.getContactNumber(context));

        switch (UserEducator.getGender(context)) {
            case "Male":
                radioGroup.check(R.id.rb_UserDetailsMale);
                break;
            case "Female":
                radioGroup.check(R.id.rb_UserDetailsFemale);
        }

        if (role.equals(UserRole.Educator()))
            tvHeader.setText("Educator Details");
        else
            tvHeader.setText("Student Details");
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);

    }

    private void setFieldsMode(boolean enable) {
        etFirstname.setEnabled(enable);
        etMiddlename.setEnabled(enable);
        etLastname.setEnabled(enable);
        etSuffix.setEnabled(enable);
        etEmail.setEnabled(enable);
        etContactnumber.setEnabled(enable);
        rbMale.setEnabled(enable);
        rbFemale.setEnabled(enable);

        etFirstname.requestFocus();
        btnEditSave.setText(enable ? "Save" : "Edit");
    }
}
