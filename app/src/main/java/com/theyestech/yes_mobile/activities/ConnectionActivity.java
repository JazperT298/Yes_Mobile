package com.theyestech.yes_mobile.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ConnectionAdapter;
import com.theyestech.yes_mobile.adapters.ConnectionRequestAdapter;
import com.theyestech.yes_mobile.adapters.StudentRequestAdapter;
import com.theyestech.yes_mobile.adapters.StudentsAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.models.Subject;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ConnectionActivity extends AppCompatActivity {
    private Context context;
    private String role, count;

    private ImageView imageView35,iv_connection;
    private TextView tv_count;

    private ConstraintLayout emptyIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_Connection;
    private ConnectionAdapter connectionAdapter;
    private ArrayList<UserStudent> userStudentArrayList = new ArrayList<>();
    private UserStudent userStudent;

    private RecyclerView rv_StudentRequest;
    private SwipeRefreshLayout swipeRefreshLayout1;
    private ConstraintLayout emptyIndicator1;
    private ConnectionRequestAdapter connectionRequestAdapter;

    //Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference registerRef;
    private DatabaseReference userRef;
    private DatabaseReference threadRef;
    private FirebaseAuth firebaseAuth;

    private String id, fullname, photoname, threadId,senderId;
    private Contact contact;
    private ChatThread chatThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI(){
        imageView35 = findViewById(R.id.imageView35);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);
        swipeRefreshLayout = findViewById(R.id.swipe_Connections);
        rv_Connection = findViewById(R.id.rv_Connection);
        tv_count = findViewById(R.id.tv_count);
        iv_connection = findViewById(R.id.iv_connection);
        iv_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConnectionRequestDialog();
            }
        });
        imageView35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emptyIndicator.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserConnection();
                getRequestCount();
            }
        });
        getUserConnection();
        getRequestCount();
    }
    private void getRequestCount(){
        RequestParams params = new RequestParams();
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_global/GetUserRequestsConnections.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                String str = new String(responseBody);
                Debugger.logD("str " + str);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(str);
                    if(String.valueOf(jsonArray.length()).equals("0")){
                        tv_count.setText(String.valueOf(jsonArray.length()));
                        tv_count.setVisibility(View.VISIBLE);
                    }else{
                        tv_count.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
    private void getUserConnection(){
        userStudentArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_global/GetUserConnections.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                Debugger.logD("responseBody: " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str: " + str);
                if (str.equals("") || str.equals("[]" )|| str.contains("NO RECORD FOUND"))
                    emptyIndicator.setVisibility(View.VISIBLE);

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(str);
                    Debugger.logD("jsonArray " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i );
                        String user_id = jsonObject.getString("user_id");
                        String user_token = jsonObject.getString("user_token");
                        String user_code = jsonObject.getString("user_code");
                        String user_email_address = jsonObject.getString("user_email_address");
                        String user_password = jsonObject.getString("user_password");
                        String user_fullname = jsonObject.getString("user_fullname");
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
                        String result = jsonObject.getString("result");
                        String connection = jsonObject.getString("connection");

                        UserStudent userStudent = new UserStudent();
                        userStudent.setId(user_id);
                        userStudent.setToken(user_token);
                        userStudent.setCode(user_code);
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
                        userStudent.setFacebook(user_facebook);
                        userStudent.setInstagram(user_instagram);
                        userStudent.setTwitter(user_twitter);
                        userStudent.setGmail(user_gmail);
                        userStudent.setMotto(user_motto);
                        userStudent.setUser_activation(user_activation);
                        userStudent.setUser_role(user_role);
                        userStudent.setValidated(validated);
                        userStudent.setConnection(connection);

                        userStudentArrayList.add(userStudent);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rv_Connection.setLayoutManager(new LinearLayoutManager(context));
                rv_Connection.setHasFixedSize(true);
                connectionAdapter = new ConnectionAdapter(context, userStudentArrayList);
                connectionAdapter.setClickListener(new OnClickRecyclerView() {
                    @Override
                    public void onItemClick(View view, int position, int fromButton) {
                        userStudent = userStudentArrayList.get(position);
                        openUsersProfileDialog();
                        Debugger.logD("user_fullname " + userStudent.getId());
                    }
                });

                rv_Connection.setAdapter(connectionAdapter);
                emptyIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
    private void openUsersProfileDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.search_user_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query =  userRef.orderByChild("email").equalTo(userStudent.getEmail_address());
        Debugger.logD("query " + query);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Contact contact = postSnapshot.getValue(Contact.class);
                    id = contact.getId();
                    fullname = contact.getFullName();
                    photoname = contact.getPhotoName();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Debugger.logD("Failed" );
            }
        });

        final ImageView iv_UserProfileImage, iv_UserProfileBackground, iv_UserProfileClose;
        final TextView tv_UserProfileFullname, tv_UserProfileEmail, tv_UserProfileInfoFullname, tv_UserProfileInfoGender, tv_UserProfileInfoPhone, tv_UserProfileInfoEmail, tv_UserProfileInfoMotto;
        final TextView tv_UserProfileInfoEducationalAttainment, tv_UserProfileInfoSubjectMajor, tv_UserProfileInfoCurrentSchool, tv_UserProfileInfoSchoolPosition;
        final TextView tv_UserProfileInfoFacebook, tv_UserProfileInfoTwitter, tv_UserProfileInfoInstagram,tv_SendMessage,tv_SendRequest;

        iv_UserProfileImage = dialog.findViewById(R.id.iv_UserProfileImage);
        iv_UserProfileBackground = dialog.findViewById(R.id.iv_UserProfileBackground);
        iv_UserProfileClose = dialog.findViewById(R.id.iv_UserProfileClose);
        tv_UserProfileFullname = dialog.findViewById(R.id.tv_UserProfileFullname);
        tv_UserProfileEmail = dialog.findViewById(R.id.tv_UserProfileEmail);
        tv_UserProfileInfoFullname = dialog.findViewById(R.id.tv_UserProfileInfoFullname);
        tv_UserProfileInfoGender = dialog.findViewById(R.id.tv_UserProfileInfoGender);
        tv_UserProfileInfoPhone = dialog.findViewById(R.id.tv_UserProfileInfoPhone);
        tv_UserProfileInfoEmail = dialog.findViewById(R.id.tv_UserProfileInfoEmail);
        tv_UserProfileInfoMotto = dialog.findViewById(R.id.tv_UserProfileInfoMotto);
        tv_UserProfileInfoEducationalAttainment = dialog.findViewById(R.id.tv_UserProfileInfoEducationalAttainment);
        tv_UserProfileInfoSubjectMajor = dialog.findViewById(R.id.tv_UserProfileInfoSubjectMajor);
        tv_UserProfileInfoCurrentSchool = dialog.findViewById(R.id.tv_UserProfileInfoCurrentSchool);
        tv_UserProfileInfoSchoolPosition = dialog.findViewById(R.id.tv_UserProfileInfoSchoolPosition);
        tv_UserProfileInfoFacebook = dialog.findViewById(R.id.tv_UserProfileInfoFacebook);
        tv_UserProfileInfoTwitter = dialog.findViewById(R.id.tv_UserProfileInfoTwitter);
        tv_UserProfileInfoInstagram = dialog.findViewById(R.id.tv_UserProfileInfoInstagram);
//        tv_SendMessage = dialog.findViewById(R.id.tv_SendMessage);
        tv_SendRequest = dialog.findViewById(R.id.tv_SendRequest);

        tv_UserProfileFullname.setText(userStudent.getFirsname() + " " + userStudent.getMiddlename() + " " + userStudent.getLastname());
        tv_UserProfileEmail.setText(userStudent.getEmail_address());
        tv_UserProfileInfoFullname.setText(userStudent.getFirsname() + " " + userStudent.getMiddlename() + " " + userStudent.getLastname() + " " + userStudent.getSuffix() );
        if (userStudent.getGender().equals("1")){
            tv_UserProfileInfoGender.setText("Male");
        }else if (userStudent.getGender().equals("2")){
            tv_UserProfileInfoGender.setText("Female");
        }else{
            tv_UserProfileInfoGender.setText("N/A");
        }
        if (userStudent.getConnection().equals("connected")){
            tv_SendRequest.setText("   Connected   ");
            tv_SendRequest.setEnabled(false);
        }else if (userStudent.getConnection().equals("requestsent")){
            tv_SendRequest.setText("   Request sent   ");
            tv_SendRequest.setEnabled(false);
        }else if (userStudent.getConnection().equals("none")){
            tv_SendRequest.setText("   Send Request   ");
        }
        tv_UserProfileInfoPhone.setText(userStudent.getContact_number());
        tv_UserProfileInfoEmail.setText(userStudent.getEmail_address());
        tv_UserProfileInfoMotto.setText(userStudent.getMotto());
        tv_UserProfileInfoEducationalAttainment.setText(userStudent.getEducational_attainment());
        tv_UserProfileInfoSubjectMajor.setText(userStudent.getSubj_major());
        tv_UserProfileInfoCurrentSchool.setText(userStudent.getCurrent_school());
        tv_UserProfileInfoSchoolPosition.setText(userStudent.getPosition());
        tv_UserProfileInfoFacebook.setText(userStudent.getFacebook());
        tv_UserProfileInfoTwitter.setText(userStudent.getTwitter());
        tv_UserProfileInfoInstagram.setText(userStudent.getInstagram());
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + userStudent.getImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                .into(iv_UserProfileBackground);
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + userStudent.getImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                .apply(GlideOptions.getOptions())
                .into(iv_UserProfileImage);
        iv_UserProfileClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//        tv_SendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, MessageActivity.class);
//                intent.putExtra("userid", id);
//                startActivity(intent);
//            }
//        });
        tv_SendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendUserConnection();
            }
        });

        dialog.show();
    }
    private void openConnectionRequestDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.dialog_connection_request);
        Objects.requireNonNull(dialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        dialog.getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        final ImageView iv_SearchBack;

        iv_SearchBack = dialog.findViewById(R.id.iv_SearchBack);
        rv_StudentRequest = dialog.findViewById(R.id.rv_StudentRequest);
        swipeRefreshLayout1 = dialog.findViewById(R.id.swipe_StudentRequest);
        emptyIndicator1 = dialog.findViewById(R.id.view_EmptyRecord);

        swipeRefreshLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllConnectionRequest();
            }
        });

        iv_SearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        getAllConnectionRequest();
        dialog.show();
    }
    private void getAllConnectionRequest(){
        userStudentArrayList.clear();

        swipeRefreshLayout1.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_global/GetUserRequestsConnections.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                swipeRefreshLayout1.setRefreshing(false);
                Debugger.logD("responseBody " + responseBody);

                String str = new String(responseBody);
                Debugger.logD("str " + str);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(str);
                    Debugger.logD("jsonArray " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i );
                        String user_id = jsonObject.getString("user_id");
                        String user_token = jsonObject.getString("user_token");
                        String user_code = jsonObject.getString("user_code");
                        String user_email_address = jsonObject.getString("user_email_address");
                        String user_password = jsonObject.getString("user_password");
                        String user_fullname = jsonObject.getString("user_fullname");
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
                        String result = jsonObject.getString("result");
                        String connection = jsonObject.getString("connection");

                        UserStudent userStudent = new UserStudent();
                        userStudent.setId(user_id);
                        userStudent.setToken(user_token);
                        userStudent.setCode(user_code);
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
                        userStudent.setFacebook(user_facebook);
                        userStudent.setInstagram(user_instagram);
                        userStudent.setTwitter(user_twitter);
                        userStudent.setGmail(user_gmail);
                        userStudent.setMotto(user_motto);
                        userStudent.setUser_activation(user_activation);
                        userStudent.setUser_role(user_role);
                        userStudent.setValidated(validated);
                        userStudent.setConnection(connection);

                        userStudentArrayList.add(userStudent);
                    }
                    Debugger.logD("asd " + userStudentArrayList.size());
                    count = String.valueOf(userStudentArrayList.size());
                    tv_count.setText(count);
                    tv_count.setVisibility(View.VISIBLE);


                    rv_StudentRequest.setLayoutManager(new LinearLayoutManager(context));

                    connectionRequestAdapter = new ConnectionRequestAdapter(context, userStudentArrayList);
                    connectionRequestAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            userStudent = userStudentArrayList.get(position);
                            if (fromButton == 1){
                                //Decline
                                deleteConnectionRequest();
                            }else if (fromButton == 2){
                                //Accept
                                acceptConnectionRequest(userStudent.getId());
                            }
                        }
                    });

                    rv_StudentRequest.setAdapter(connectionRequestAdapter);
                    emptyIndicator1.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout1.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
    private void acceptConnectionRequest(String user_id){
        ProgressPopup.showProgress(context);
        RequestParams params = new RequestParams();
        params.put("userOtherId", user_id);
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_global/AcceptRequestConnection.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str " + str);
                if (str.contains("success")) {
                    Toasty.success(context, "New Connection Accepted").show();
                    getAllConnectionRequest();
                } else{
                    Toasty.warning(context, "Failed").show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
    private void deleteConnectionRequest(){
        ProgressPopup.showProgress(context);
        RequestParams params = new RequestParams();
        params.put("userOtherId", userStudent.getId());
        params.put("user_id", UserEducator.getID(context));
        HttpProvider.post(context, "controller_global/CancelConnectionRequest.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str " + str);
                if (str.contains("success")) {
                    Toasty.success(context, "New Connection Cancelled").show();
                    getAllConnectionRequest();
                } else{
                    Toasty.warning(context, "Failed").show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

}
