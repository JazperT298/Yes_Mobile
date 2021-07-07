package com.theyestech.yes_mobile.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class LoginService extends Service {
    private String role;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private UserEducator fireBaseEducator;
    private UserStudent fireBaseStudent;

    @Override
    public void onCreate() {
        super.onCreate();
        Debugger.logD("Service onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Debugger.logD("Service onStart");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        role = UserRole.getRole(getApplicationContext());
        if(role.equals(UserRole.Educator())){
            loginEducator();
        }else{
            loginStudent();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Debugger.logD("Service onDestroy");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void loginEducator() {

        RequestParams params = new RequestParams();
        params.put("login_e_email_address", UserEducator.getEmail(getApplicationContext()));
        params.put("login_e_password", UserEducator.getPassword(getApplicationContext()));

        HttpProvider.postLogin(getApplicationContext(), "controller_educator/login_as_educator_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody, StandardCharsets.UTF_8);
                if (str.isEmpty()) {
                    OkayClosePopup.showDialog(getApplicationContext(), "Something went wrong. Please try again.", "Close");
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

                        Toasty.warning(getApplicationContext(), result).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Debugger.logD(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void loginStudent() {

        RequestParams params = new RequestParams();
        params.put("login_s_email_address", UserStudent.getEmail(getApplicationContext()));
        params.put("login_s_password", UserStudent.getPassword(getApplicationContext()));

        HttpProvider.post(getApplicationContext(), "controller_student/login_as_student_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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

                        Toasty.warning(getApplicationContext(), result).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Debugger.logD(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void getEducatorDetails(final UserEducator userEducator) {
        RequestParams params = new RequestParams();
        params.put("user_token", userEducator.getToken());
        params.put("user_id", userEducator.getId());

        HttpProvider.post(getApplicationContext(), "controller_global/get_user_details.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                    userEducator.setPassword(UserEducator.getPassword(getApplicationContext()));
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

                    tryFirebaseLogin(userEducator, userRole);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void getStudentDetails(final UserStudent userStudent) {
        RequestParams params = new RequestParams();
        params.put("user_token", userStudent.getToken());
        params.put("user_id", userStudent.getId());

        HttpProvider.post(getApplicationContext(), "controller_global/get_user_details.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    Debugger.logD("str " + str);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String user_code = jsonObject.getString("user_code");
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
                    String user_nickname = jsonObject.getString("user_nickname");
                    String user_dreamjob = jsonObject.getString("user_dreamjob");
                    String user_role = jsonObject.getString("user_role");
                    String validated = jsonObject.getString("validated");
                    String connection = jsonObject.getString("connection");

                    if (user_gender.equals("1"))
                        user_gender = "Male";
                    else
                        user_gender = "Female";

                    userStudent.setCode(user_code);
                    userStudent.setEmail_address(user_email_address);
                    userStudent.setPassword(UserStudent.getPassword(getApplicationContext()));
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
                    userStudent.setInstagram(user_instagram);
                    userStudent.setTwitter(user_twitter);
                    userStudent.setFacebook(user_facebook);
                    userStudent.setGmail(user_gmail);
                    userStudent.setMotto(user_motto);
                    userStudent.setUser_activation(user_activation);
                    userStudent.setNickname(user_nickname);
                    userStudent.setDreamjob(user_dreamjob);
                    userStudent.setUser_role(user_role);
                    userStudent.setValidated(validated);
                    userStudent.setConnection(connection);
                    userStudent.saveUserSession(getApplicationContext());

                    UserRole userRole = new UserRole();
                    userRole.setUserRole(UserRole.Student());

                    tryFirebaseLogin(userStudent, userRole);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void tryFirebaseLogin(Object user, final UserRole userRole) {
        String email;
        String password;

        if (role.equals("1")) {
            fireBaseEducator = (UserEducator) user;
            email = fireBaseEducator.getEmail_address();
            password = fireBaseEducator.getPassword();
        } else {
            fireBaseStudent = (UserStudent) user;
            email = fireBaseStudent.getEmail_address();
            password = fireBaseStudent.getPassword();
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (role.equals(UserRole.Educator()))
                            fireBaseEducator.saveUserSession(getApplicationContext());
                        else
                            fireBaseStudent.saveUserSession(getApplicationContext());

                        userRole.saveRole(getApplicationContext());

                        Debugger.printO(task);
                    }
                });
    }
}
