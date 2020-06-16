package com.theyestech.yes_mobile.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.ConnectionActivity;
import com.theyestech.yes_mobile.activities.MyVideosActivity;
import com.theyestech.yes_mobile.activities.NewNewsfeedActivity;
import com.theyestech.yes_mobile.activities.NotesActivity;
import com.theyestech.yes_mobile.activities.StartActivity;
import com.theyestech.yes_mobile.activities.SubjectActivity;
import com.theyestech.yes_mobile.activities.SubjectDetailsActivity;
import com.theyestech.yes_mobile.activities.UserProfileActivity;
import com.theyestech.yes_mobile.activities.VideoLabActivity;
import com.theyestech.yes_mobile.activities.YestechCourseActivity;
import com.theyestech.yes_mobile.adapters.ConnectionAdapter;
import com.theyestech.yes_mobile.adapters.NewsfeedAdapter;
import com.theyestech.yes_mobile.adapters.NotesAdapter;
import com.theyestech.yes_mobile.adapters.SearchUserAdapter;
import com.theyestech.yes_mobile.adapters.StickersAdapter;
import com.theyestech.yes_mobile.adapters.StudentListAdapter;
import com.theyestech.yes_mobile.adapters.StudentStickersAdapter;
import com.theyestech.yes_mobile.adapters.SubjectsEducatorAdapter;
import com.theyestech.yes_mobile.adapters.VideoLabAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Newsfeed;
import com.theyestech.yes_mobile.models.Note;
import com.theyestech.yes_mobile.models.Sticker;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.models.VideoLab;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;
import com.theyestech.yes_mobile.utils.VerticalSpacingItemDecorator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {
    private View view;
    private Context context;

    private String role;

    private ImageView ivProfile,iv_HomeSearch, iv_HomeChat;
    private TextView tvEmail, tvEducationalAttainment, tvStatSubjectCount, tvStatStudentCount, tvStatTopicCount, tvSubjectCount, tvStatistics,tvStatNoteCount;
    private TextView tv_HomeConnectionCount, tv_HomeNewsfeedCount, tv_HomeVideolabCount, tv_HomeCourseCount, tv_HomeMyvideosCount;
    private CardView cvSubjects, cvNotes, cvConnections, cvNewsfeeds, cvVideoLab, cvYestechCourse, cvMyVideos, cvStickers, cvAwards, cvStatistics;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;

    private String selectionTitle;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private SearchUserAdapter searchUserAdapter;
    private UserEducator userEducator;
    private ArrayList<UserStudent> userStudentArrayList = new ArrayList<>();
    private UserStudent userStudent;


    private ArrayList<Sticker> stickerArrayList = new ArrayList<>();
    private StudentStickersAdapter studentStickersAdapter;
    private RecyclerView rv_Stickers;
    private SwipeRefreshLayout swipeRefreshLayout1;
    private ConstraintLayout emptyIndicator1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();

        role = UserRole.getRole(context);
        if(role.equals(UserRole.Educator())){
            view = inflater.inflate(R.layout.fragment_home, container, false);
            initializeEducatorUI();
            setEducatorHeader();
            getEducatorStatistics();
            getAllEducatorCounts();
        }else{
            view = inflater.inflate(R.layout.fragment_home_student, container, false);
            initializeStudentUI();
            setStudentHeader();
            displayStudentAccess();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        context = getContext();
//        role = UserRole.getRole(context);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    //    Educator
    private void initializeEducatorUI() {
        ivProfile = view.findViewById(R.id.iv_HomeProfile);
        tvEmail = view.findViewById(R.id.tv_HomeEmail);
        tvEducationalAttainment = view.findViewById(R.id.tv_Home_EducationalAttainment);
        tvStatStudentCount = view.findViewById(R.id.tv_HomeStatSStudentCount);
        tvStatSubjectCount = view.findViewById(R.id.tv_HomeStatSubjectCount);
        tvStatTopicCount = view.findViewById(R.id.tv_HomeStatTopicCount);
        tvSubjectCount = view.findViewById(R.id.tv_HomeSubjectCount);
        tvStatistics = view.findViewById(R.id.tv_HomeStatistics);
        tvStatNoteCount = view.findViewById(R.id.tv_HomeNoteCount);
        tv_HomeConnectionCount = view.findViewById(R.id.tv_HomeConnectionCount);
        tv_HomeNewsfeedCount = view.findViewById(R.id.tv_HomeNewsfeedCount);
        tv_HomeVideolabCount = view.findViewById(R.id.tv_HomeVideolabCount);
        tv_HomeCourseCount = view.findViewById(R.id.tv_HomeCourseCount);
        tv_HomeMyvideosCount = view.findViewById(R.id.tv_HomeMyvideosCount);
        cvSubjects = view.findViewById(R.id.cv_Home_Subjects);
        cvNotes = view.findViewById(R.id.cv_Home_Notes);
        cvConnections = view.findViewById(R.id.cv_Home_Connections);
        cvNewsfeeds = view.findViewById(R.id.cv_Home_Newsfeeds);
        cvVideoLab = view.findViewById(R.id.cv_Home_VideoLab);
        cvYestechCourse = view.findViewById(R.id.cv_Home_YestechCourse);
        cvMyVideos = view.findViewById(R.id.cv_Home_MyVideos);
        cvStickers = view.findViewById(R.id.cv_Home_Stickers);
        cvAwards = view.findViewById(R.id.cv_Home_Awards);
        cvStatistics = view.findViewById(R.id.cv_Home_Stats);
        swipeRefreshLayout = view.findViewById(R.id.swipe_Home);
        recyclerView = view.findViewById(R.id.rv_Home);
        emptyIndicator = view.findViewById(R.id.view_EmptyRecord);
        iv_HomeSearch = view.findViewById(R.id.iv_HomeSearch);
        iv_HomeChat = view.findViewById(R.id.iv_HomeChat);

        iv_HomeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchUserDialog();
            }
        });
        iv_HomeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.warning(context, "Chat is unavailable").show();
            }
        });
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAction();
            }
        });

        cvSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectActivity.class);
                startActivity(intent);
            }
        });

        cvNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotesActivity.class);
                startActivity(intent);
            }
        });

        cvConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConnectionActivity.class);
                startActivity(intent);
            }
        });

        cvNewsfeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewNewsfeedActivity.class);
                startActivity(intent);
            }
        });

        cvVideoLab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoLabActivity.class);
                startActivity(intent);
            }
        });

        cvYestechCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, YestechCourseActivity.class);
                startActivity(intent);
            }
        });

        cvMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMyAction();
            }
        });

        cvStickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvAwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        swipeRefreshLayout.setRefreshing(false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });
    }
    private void setEducatorHeader() {
        tvEmail.setText(UserEducator.getEmail(context));
        tvEducationalAttainment.setText(UserEducator.getEducationalAttainment(context));

        selectionTitle = "Educator";

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
    }
    private void getEducatorStatistics() {
        RequestParams params = new RequestParams();
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_educator/CountSubjectsAndStudents.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String student_count = jsonObject.getString("student_count");
                    String subject_count = jsonObject.getString("subject_count");

                    tvStatSubjectCount.setText(String.format("%s Subjects", subject_count));
                    tvStatStudentCount.setText(String.format("%s Students", student_count));

                    tvSubjectCount.setText(subject_count);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
    private void openSearchUserDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.dialog_search_user);
        final EditText et_SearchUser;
        final ImageView iv_SearchBack, iv_SearchIcon;
        final RecyclerView rv_Search;
        final SwipeRefreshLayout swipeRefreshLayout;
        final ConstraintLayout emptyIndicator;

        et_SearchUser = dialog.findViewById(R.id.et_SearchUser);
        iv_SearchBack = dialog.findViewById(R.id.iv_SearchBack);
        iv_SearchIcon = dialog.findViewById(R.id.iv_SearchIcon);
        rv_Search = dialog.findViewById(R.id.rv_Search);
        swipeRefreshLayout = dialog.findViewById(R.id.swipe_Search);
        emptyIndicator = dialog.findViewById(R.id.view_EmptyRecord);


        iv_SearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        iv_SearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_text = et_SearchUser.getText().toString();
                //Toasty.warning(context, text).show();

                userStudentArrayList.clear();

                swipeRefreshLayout.setRefreshing(true);

                RequestParams params = new RequestParams();
                params.put("user_id", UserEducator.getID(context));
                params.put("user_token", UserEducator.getToken(context));
                params.put("search_text", search_text);
                Debugger.logD("user_token " + UserEducator.getToken(context));
                Debugger.logD("user_id " + UserEducator.getID(context));
                Debugger.logD("search_text " + search_text);
                HttpProvider.post(context, "controller_global/SearchUsers.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        swipeRefreshLayout.setRefreshing(false);
                        Debugger.logD("responseBody " + responseBody);

                        String str = new String(responseBody);
                        Debugger.logD("str " + str);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(str);
                            Debugger.logD("jsonArray " + jsonArray);
                            for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String user_id = jsonObject.getString("user_id");
                                String user_token = jsonObject.getString("user_token");
                                String user_code = jsonObject.getString("user_code");
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
                        rv_Search.setLayoutManager(new LinearLayoutManager(context));
                        rv_Search.setHasFixedSize(true);
                        searchUserAdapter = new SearchUserAdapter(context, userStudentArrayList);
                        searchUserAdapter.setClickListener(new OnClickRecyclerView() {
                            @Override
                            public void onItemClick(View view, int position, int fromButton) {
                                userStudent = userStudentArrayList.get(position);
                                openUsersProfileDialog();
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("STUDENT", student);
//                            startActivity(intent);
                            }
                        });

                        rv_Search.setAdapter(searchUserAdapter);
                        emptyIndicator.setVisibility(View.GONE);

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        swipeRefreshLayout.setRefreshing(false);
                        OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
                    }
                });
            }
        });

        dialog.show();
    }
    private void openUsersProfileDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.search_user_profile);
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
        tv_SendMessage = dialog.findViewById(R.id.tv_SendMessage);
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
                .apply(GlideOptions.getOptions())
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
        tv_SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.warning(context, "Chat is Unavailable").show();
            }
        });
        tv_SendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserConnection();
            }
        });

        dialog.show();
    }
    private void sendUserConnection(){
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("userOtherId", userStudent.getId());
        params.put("user_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_global/SendConnectionRequest.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody " +responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("TEST " +str);
                if (str.contains("success")) {
                    Toasty.success(context, "Request Sent.").show();
                } else
                    Toasty.warning(context, "Failed").show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
    private void getAllEducatorCounts(){
        RequestParams params = new RequestParams();
        params.put("user_token", UserEducator.getToken(context));
        params.put("user_id", UserEducator.getID(context));
        HttpProvider.post(context, "controller_global/GetUserNotes.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody, StandardCharsets.UTF_8);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    tvStatNoteCount.setText(String.valueOf(jsonArray.length()));
                    Debugger.logD(String.valueOf(jsonArray.length()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        HttpProvider.post(context, "controller_global/GetUserConnections.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody, StandardCharsets.UTF_8);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    tv_HomeConnectionCount.setText(String.valueOf(jsonArray.length()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        RequestParams params1 = new RequestParams();
        params1.put("teach_token", UserEducator.getToken(context));
        HttpProvider.post(context, "controller_educator/get_post.php", params1, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody, StandardCharsets.UTF_8);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    tv_HomeNewsfeedCount.setText(String.valueOf(jsonArray.length()));
                    Debugger.logD(String.valueOf(jsonArray.length()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        RequestParams params2 = new RequestParams();
        HttpProvider.post(context, "controller_educator/GetAllVideoLabs.php", params2, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody, StandardCharsets.UTF_8);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    tv_HomeVideolabCount.setText(String.valueOf(jsonArray.length()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        tv_HomeCourseCount.setText("0");
        tv_HomeMyvideosCount.setText("0");
    }

    //    Student
    private void initializeStudentUI() {
        ivProfile = view.findViewById(R.id.iv_HomeProfile);
        tvEmail = view.findViewById(R.id.tv_HomeEmail);
        tvEducationalAttainment = view.findViewById(R.id.tv_Home_EducationalAttainment);
        tvStatStudentCount = view.findViewById(R.id.tv_HomeStatSStudentCount);
        tvStatSubjectCount = view.findViewById(R.id.tv_HomeStatSubjectCount);
        tvStatTopicCount = view.findViewById(R.id.tv_HomeStatTopicCount);
        tvSubjectCount = view.findViewById(R.id.tv_HomeSubjectCount);
        tvStatistics = view.findViewById(R.id.tv_HomeStatistics);
        tvStatNoteCount = view.findViewById(R.id.tv_HomeNoteCount);
        cvSubjects = view.findViewById(R.id.cv_Home_Subjects);
        cvNotes = view.findViewById(R.id.cv_Home_Notes);
        cvConnections = view.findViewById(R.id.cv_Home_Connections);
        cvNewsfeeds = view.findViewById(R.id.cv_Home_Newsfeeds);
        cvVideoLab = view.findViewById(R.id.cv_Home_VideoLab);
        cvYestechCourse = view.findViewById(R.id.cv_Home_YestechCourse);
        cvMyVideos = view.findViewById(R.id.cv_Home_MyVideos);
        cvStickers = view.findViewById(R.id.cv_Home_Stickers);
        cvAwards = view.findViewById(R.id.cv_Home_Awards);
        cvStatistics = view.findViewById(R.id.cv_Home_Stats);
        iv_HomeChat = view.findViewById(R.id.iv_HomeChat);

        //tvStatSubjectCount.setText(UserStudent.getCode(context));

        iv_HomeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.warning(context, "Chat is unavailable").show();
            }
        });
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAction();
            }
        });

        cvSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectActivity.class);
                startActivity(intent);
            }
        });

        cvNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotesActivity.class);
                startActivity(intent);
            }
        });

        cvConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStickersDialog();
            }
        });

        cvNewsfeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewNewsfeedActivity.class);
                startActivity(intent);
            }
        });

        cvVideoLab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAwardsDialog();
            }
        });

        cvYestechCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, YestechCourseActivity.class);
                startActivity(intent);
            }
        });

        cvMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaidCourses();
            }
        });

        cvStickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStickersDialog();
            }
        });

        cvAwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAwardsDialog();
            }
        });

    }
    private void setStudentHeader() {
        tvEmail.setText(UserStudent.getEmail(context));
        tvEducationalAttainment.setText(UserStudent.getEducationalAttainment(context));

        selectionTitle = "Student";

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserStudent.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
    }
    private void displayStudentAccess(){
        cvVideoLab.setVisibility(View.GONE);
        cvConnections.setVisibility(View.GONE);
        cvYestechCourse.setVisibility(View.GONE);
        cvMyVideos.setVisibility(View.GONE);
        cvStatistics.setVisibility(View.GONE);
        tvStatistics.setVisibility(View.GONE);

        cvStickers.setVisibility(View.VISIBLE);
        cvAwards.setVisibility(View.VISIBLE);
    }
    private void openStickersDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.dialog_student_stickers);
        final ImageView iv_SearchBack;
//        final RecyclerView rv_Stickers;
//        final SwipeRefreshLayout swipeRefreshLayout;
//        final ConstraintLayout emptyIndicator;

        iv_SearchBack = dialog.findViewById(R.id.iv_SearchBack);
        rv_Stickers = dialog.findViewById(R.id.rv_Stickers);
        swipeRefreshLayout1 = dialog.findViewById(R.id.swipe_Search);
        emptyIndicator1 = dialog.findViewById(R.id.view_EmptyRecord);

        swipeRefreshLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllStudentStickers();
            }
        });

        iv_SearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        getAllStudentStickers();
    }
    private void openAwardsDialog(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.dialog_student_awards);
        final EditText et_SearchUser;
        final ImageView iv_SearchBack, iv_SearchIcon;
        final RecyclerView rv_Search;
        final SwipeRefreshLayout swipeRefreshLayout;
        final ConstraintLayout emptyIndicator;

        iv_SearchBack = dialog.findViewById(R.id.iv_SearchBack);

        iv_SearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void getAllStudentCounts(){
    }
    private void getAllStudentStickers(){
        stickerArrayList.clear();

        swipeRefreshLayout1.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("studentId", UserStudent.getID(context));

        Debugger.logD("studentId " + UserEducator.getToken(context));
        HttpProvider.post(context, "controller_student/GetAllStickersByStudentId.php", params, new AsyncHttpResponseHandler() {
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
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String sticker_id = jsonObject.getString("sticker_id");
                        String sticker_name = jsonObject.getString("sticker_name");

                        Sticker sticker = new Sticker();
                        sticker.setId(sticker_id);
                        sticker.setName(sticker_name);

                        stickerArrayList.add(sticker);
                    }
                    rv_Stickers.setLayoutManager(new GridLayoutManager(context, 2));

                    studentStickersAdapter = new StudentStickersAdapter(context, stickerArrayList);

                    rv_Stickers.setAdapter(studentStickersAdapter);
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

    private void selectAction() {
        String[] items = {" Profile ", " Logout "};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(selectionTitle);
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    startActivity(intent);
                }
                if (which == 1) {
                    openLogoutDialog();
                }
            }
        });

        dialog.create().show();
    }
    private void selectMyAction() {
        String[] items = {" My Course ", " My Upload "};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("My Video");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(context, MyVideosActivity.class);
                    intent.putExtra("MY_TITLE",  "1");
                    startActivity(intent);
                }
                if (which == 1) {
                    Intent intent = new Intent(context, MyVideosActivity.class);
                    intent.putExtra("MY_TITLE",  "2");
                    startActivity(intent);
                }
            }
        });

        dialog.create().show();
    }
    private void selectPaidCourses() {
        String[] items = {" Video Lab ", " Yestech "};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Paid Courses");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(context, MyVideosActivity.class);
                    intent.putExtra("MY_TITLE",  "1");
                    startActivity(intent);
                }
                if (which == 1) {
                    Intent intent = new Intent(context, MyVideosActivity.class);
                    intent.putExtra("MY_TITLE",  "2");
                    startActivity(intent);
                }
            }
        });

        dialog.create().show();
    }
    private void logoutUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            usersRef.child("status").setValue("offline");
            firebaseAuth.signOut();
        }

        if (role.equals(UserRole.Educator())) {
            UserEducator.clearSession(context);
            UserRole.clearRole(context);
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        } else {
            UserStudent.clearSession(context);
            UserRole.clearRole(context);
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        }
    }
    private void openLogoutDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Logout")
                .setIcon(R.drawable.ic_logout_colored)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllEducatorCounts();
        getEducatorStatistics();
    }
}