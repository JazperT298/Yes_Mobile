package com.theyestech.yes_mobile.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class UserDetailsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivProfile, ivBack, ivCamera;
    private EditText etFirstname, etMiddlename, etLastname, etSuffix, etEmail, etContactnumber, etMotto, etEducationalAttainment, etSubjectMajor, etCurrentSchool, etSchoolPosition, etFacebook, etTwitter, etInstagram;
    private RadioButton rbMale, rbFemale;
    private RadioGroup radioGroup;
    private TextView tvEditSave;

    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;

    private String storagePermission[];
    private Uri selectedFile;
    private String selectedFilePath = "";
    private File myFile;

    private boolean isEdit = true;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        context = this;
        role = UserRole.getRole(context);

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        initializeUI();
        setFieldsMode(false);

        if (role.equals(UserRole.Educator())) {
            setEducatorFieldsData();
        }else {
            setStudentFieldsData();
        }
    }

    private void initializeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorNephritis));
        }

        ivProfile = findViewById(R.id.iv_UserDetailsImage);
        ivCamera = findViewById(R.id.iv_UserDetailsCamera);
        ivBack = findViewById(R.id.iv_UserDetailsBack);
        tvEditSave = findViewById(R.id.tv_UserDetailsEditSave);
        etFirstname = findViewById(R.id.et_UserDetailsFirstname);
        etMiddlename = findViewById(R.id.et_UserDetailsMiddlename);
        etLastname = findViewById(R.id.et_UserDetailsLastname);
        etSuffix = findViewById(R.id.et_UserDetailsSuffix);
        etEmail = findViewById(R.id.et_UserDetailsEmail);
        etContactnumber = findViewById(R.id.et_UserDetailsContactnumber);
        etMotto = findViewById(R.id.et_UserDetailsMotto);
        etEducationalAttainment = findViewById(R.id.et_UserDetailsEducationalAttainment);
        etSubjectMajor = findViewById(R.id.et_UserDetailsSubjectMajor);
        etCurrentSchool = findViewById(R.id.et_UserDetailsCurrentSchool);
        etSchoolPosition = findViewById(R.id.et_UserDetailsSchoolPosition);
        etFacebook = findViewById(R.id.et_UserDetailsFacebook);
        etTwitter = findViewById(R.id.et_UserDetailsTwitter);
        etInstagram = findViewById(R.id.et_UserDetailsInstagram);
        rbMale = findViewById(R.id.rb_UserDetailsMale);
        rbFemale = findViewById(R.id.rb_UserDetailsFemale);
        radioGroup = findViewById(R.id.rg_UserDetails);

        etEmail.setEnabled(false);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAction();
            }
        });

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

        tvEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    setFieldsMode(isEdit);
                    isEdit = false;
                } else {
                    if (role.equals(UserRole.Educator()))
                        updateEducatorDetails();
                    else
                        updateStudentDetails();
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void selectAction() {
        String[] items = {" View ", " Change "};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Photo");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        selectedFilePath = "";
                        pickImageGallery();
                    }
                }
            }
        });
        dialog.create().show();
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


    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void updateEducatorDetails() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_id", UserEducator.getID(context));
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_email_address", etEmail.getText().toString());
        params.put("user_contact_number", etContactnumber.getText().toString());
        params.put("user_lastname", etLastname.getText().toString());
        params.put("user_firstname", etFirstname.getText().toString());
        params.put("user_middlename", etMiddlename.getText().toString());
        params.put("user_suffixes", etSuffix.getText().toString());
        params.put("user_gender", gender);

        HttpProvider.post(context, "controller_global/update_basic_details.php", params, new AsyncHttpResponseHandler() {
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
//                        updateEducatorSession();
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

    private void updateStudentDetails() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_id", UserStudent.getID(context));
        params.put("user_token", UserStudent.getToken(context));
        params.put("user_email_address", etEmail.getText().toString());
        params.put("user_contact_number", etContactnumber.getText().toString());
        params.put("user_lastname", etLastname.getText().toString());
        params.put("user_firstname", etFirstname.getText().toString());
        params.put("user_middlename", etMiddlename.getText().toString());
        params.put("user_suffixes", etSuffix.getText().toString());
        params.put("user_gender", gender);

        HttpProvider.post(context, "controller_global/update_basic_details.php", params, new AsyncHttpResponseHandler() {
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
                        updateStudentSession();
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
        userEducator.setContact_number(etContactnumber.getText().toString());
        userEducator.setGender(gender);
        userEducator.setImage(UserEducator.getImage(context));
        userEducator.setMotto(etMotto.getText().toString());
        userEducator.setEducational_attainment(etEducationalAttainment.getText().toString());
        userEducator.setSubj_major(etSubjectMajor.getText().toString());
        userEducator.setCurrent_school(etCurrentSchool.getText().toString());
        userEducator.setPosition(etSchoolPosition.getText().toString());
        userEducator.setFacebook(etFacebook.getText().toString());
        userEducator.setTwitter(etTwitter.getText().toString());
        userEducator.setInstagram(etInstagram.getText().toString());

        UserEducator.clearSession(context);

        userEducator.saveUserSession(context);

        setEducatorFieldsData();
    }

    private void updateStudentSession() {
        UserEducator userEducator = new UserEducator();
        userEducator.setId(UserEducator.getID(context));
        userEducator.setToken(UserEducator.getToken(context));
        userEducator.setPassword(UserEducator.getPassword(context));
        userEducator.setFirsname(etFirstname.getText().toString());
        userEducator.setMiddlename(etMiddlename.getText().toString());
        userEducator.setLastname(etLastname.getText().toString());
        userEducator.setSuffix(etSuffix.getText().toString());
        userEducator.setEmail_address(etEmail.getText().toString());
        userEducator.setContact_number(etContactnumber.getText().toString());
        userEducator.setGender(gender);
        userEducator.setImage(UserStudent.getImage(context));
        userEducator.setEducational_attainment(UserEducator.getEducationalAttainment(context));
        userEducator.setSubj_major(UserEducator.getSubjectMajor(context));
        userEducator.setCurrent_school(UserEducator.getCurrentSchool(context));
        userEducator.setPosition(UserEducator.getPosition(context));
        userEducator.saveUserSession(context);

        setStudentFieldsData();
    }

    private void setEducatorFieldsData() {
        etFirstname.setText(UserEducator.getFirstname(context));
        etMiddlename.setText(UserEducator.getMiddlename(context));
        etLastname.setText(UserEducator.getLastname(context));
        etSuffix.setText(UserEducator.getSuffix(context));
        etEmail.setText(UserEducator.getEmail(context));
        etContactnumber.setText(UserEducator.getContactNumber(context));
        etMotto.setText(UserEducator.getMotto(context));
        etEducationalAttainment.setText(UserEducator.getEducationalAttainment(context));
        etSubjectMajor.setText(UserEducator.getSubjectMajor(context));
        etCurrentSchool.setText(UserEducator.getCurrentSchool(context));
        etSchoolPosition.setText(UserEducator.getPosition(context));
        etFacebook.setText(UserEducator.getFacebook(context));
        etTwitter.setText(UserEducator.getTwitter(context));
        etInstagram.setText(UserEducator.getInstragram(context));

        switch (UserEducator.getGender(context)) {
            case "Male":
                radioGroup.check(R.id.rb_UserDetailsMale);
                break;
            case "Female":
                radioGroup.check(R.id.rb_UserDetailsFemale);
        }

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
    }

    private void setStudentFieldsData() {
        etFirstname.setText(UserStudent.getFirstname(context));
        etMiddlename.setText(UserStudent.getMiddlename(context));
        etLastname.setText(UserStudent.getLastname(context));
        etSuffix.setText(UserStudent.getSuffix(context));
        etEmail.setText(UserStudent.getEmail(context));
        etContactnumber.setText(UserStudent.getContactNumber(context));

        switch (UserStudent.getGender(context)) {
            case "Male":
                radioGroup.check(R.id.rb_UserDetailsMale);
                break;
            case "Female":
                radioGroup.check(R.id.rb_UserDetailsFemale);
        }

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserStudent.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
    }

    private void setFieldsMode(boolean enable) {
        etFirstname.setEnabled(enable);
        etMiddlename.setEnabled(enable);
        etLastname.setEnabled(enable);
        etSuffix.setEnabled(enable);
        etContactnumber.setEnabled(enable);
        etMotto.setEnabled(enable);
        etEducationalAttainment.setEnabled(enable);
        etSubjectMajor.setEnabled(enable);
        etCurrentSchool.setEnabled(enable);
        etSchoolPosition.setEnabled(enable);
        etFacebook.setEnabled(enable);
        etTwitter.setEnabled(enable);
        etInstagram.setEnabled(enable);
        rbMale.setEnabled(enable);
        rbFemale.setEnabled(enable);
        ivProfile.setEnabled(enable);

        if (enable) {
            ivCamera.setImageResource(R.drawable.ic_user_details_camera);
        } else {
            ivCamera.setImageResource(R.drawable.ic_user_details_camera_disable);
        }

        etFirstname.requestFocus();
        tvEditSave.setText(enable ? "Save" : "Edit");
    }

    private void updateEducatorProfileImage() throws FileNotFoundException {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_id", UserEducator.getID(context));
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_image", myFile);

        HttpProvider.post(context, "controller_global/update_profile_pic.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.getString("result").contains("success")) {
                        Toasty.success(context, "Saved.").show();
                        getEducatorDetails();
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

    private void updateStudentProfileImage() throws FileNotFoundException {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_id", UserEducator.getID(context));
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_image", myFile);

        HttpProvider.post(context, "controller_global/update_profile_pic.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.getString("result").contains("success")) {
                        Toasty.success(context, "Saved.").show();
                        getStudentDetails();
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

    private void getEducatorDetails() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_global/get_user_details.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    Debugger.logD(str);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String user_id = jsonObject.getString("user_id");
                    String user_token = jsonObject.getString("user_token");
                    String user_email_address = jsonObject.getString("user_email_address");
                    String user_password = jsonObject.getString("user_password");
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

                    UserEducator userEducator = new UserEducator();
                    userEducator.setId(user_id);
                    userEducator.setToken(user_token);
                    userEducator.setEmail_address(user_email_address);
                    userEducator.setPassword(user_password);
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
                    userEducator.saveUserSession(context);

                    setEducatorFieldsData();

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

    private void getStudentDetails() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_token", UserStudent.getToken(context));
        params.put("user_id", UserStudent.getID(context));

        HttpProvider.post(context, "controller_global/get_user_details.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Debugger.logD(str);
                    String user_id = jsonObject.getString("user_id");
                    String user_token = jsonObject.getString("user_token");
                    String user_email_address = jsonObject.getString("user_email_address");
                    String user_password = jsonObject.getString("user_password");
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

                    UserStudent userStudent = new UserStudent();
                    userStudent.setToken(user_token);
                    userStudent.setId(user_id);
                    userStudent.setEmail_address(user_email_address);
                    userStudent.setPassword(user_password);
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

                    setStudentFieldsData();

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickImageGallery();
                    } else {
                        Toasty.error(context, "Permission denied ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                selectedFile = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = context.getContentResolver().query(selectedFile,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedFilePath = cursor.getString(columnIndex);
                myFile = new File(selectedFilePath);

                Glide.with(context)
                        .load(myFile)
                        .apply(GlideOptions.getOptions())
                        .into(ivProfile);

                if (role.equals(UserRole.Educator())) {
                    try {
                        updateEducatorProfileImage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        updateStudentProfileImage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
