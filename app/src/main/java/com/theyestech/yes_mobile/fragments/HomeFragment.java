package com.theyestech.yes_mobile.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.ConnectionActivity;
import com.theyestech.yes_mobile.activities.MyVideosActivity;
import com.theyestech.yes_mobile.activities.NewNewsfeedActivity;
import com.theyestech.yes_mobile.activities.NotesActivity;
import com.theyestech.yes_mobile.activities.StartActivity;
import com.theyestech.yes_mobile.activities.SubjectActivity;
import com.theyestech.yes_mobile.activities.UserProfileActivity;
import com.theyestech.yes_mobile.activities.VideoLabActivity;
import com.theyestech.yes_mobile.activities.YestechCourseActivity;
import com.theyestech.yes_mobile.adapters.NewsfeedAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Newsfeed;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {
    private View view;
    private Context context;

    private String role;

    private ImageView ivProfile,iv_HomeSearch, iv_HomeChat;
    private TextView tvEmail, tvEducationalAttainment, tvStatSubjectCount, tvStatStudentCount, tvStatTopicCount, tvSubjectCount, tvStatistics;
    private CardView cvSubjects, cvNotes, cvConnections, cvNewsfeeds, cvVideoLab, cvYestechCourse, cvMyVideos, cvStickers, cvAwards, cvStatistics;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Newsfeed> newsfeedArrayList = new ArrayList<>();
    private NewsfeedAdapter newsfeedAdapter;
    private Newsfeed selectedNewsfeed = new Newsfeed();

    private String selectionTitle;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        context = getContext();

        role = UserRole.getRole(context);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        initializeUI();
    }

    private void initializeUI() {
        ivProfile = view.findViewById(R.id.iv_HomeProfile);
//        ivSearch = view.findViewById(R.id.iv_HomeSearch);
//        ivNewPost = view.findViewById(R.id.iv_HomeNewPost);
        tvEmail = view.findViewById(R.id.tv_HomeEmail);
        tvEducationalAttainment = view.findViewById(R.id.tv_Home_EducationalAttainment);
        tvStatStudentCount = view.findViewById(R.id.tv_HomeStatSStudentCount);
        tvStatSubjectCount = view.findViewById(R.id.tv_HomeStatSubjectCount);
        tvStatTopicCount = view.findViewById(R.id.tv_HomeStatTopicCount);
        tvSubjectCount = view.findViewById(R.id.tv_HomeSubjectCount);
        tvStatistics = view.findViewById(R.id.tv_HomeStatistics);
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
                Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
                dialog.setContentView(R.layout.dialog_search_user);

                dialog.show();
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
//                Intent intent = new Intent(context, UserProfileActivity.class);
//                startActivity(intent);
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
//                if (role.equals(UserRole.Educator()))
//                    getEducatorNewsfeedDetails();
//                else
//                    getStudentNewsfeedDetails();
            }
        });


//        ivNewPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, NewNewsfeedActivity.class);
//                startActivity(intent);
//            }
//        });

        if (role.equals(UserRole.Educator())) {
            setEducatorHeader();
            getEducatorStatistics();
        } else {
            setStudentHeader();
            displayStudentAccess();
        }
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

    private void setEducatorHeader() {
        tvEmail.setText(UserEducator.getEmail(context));
        tvEducationalAttainment.setText(UserEducator.getEducationalAttainment(context));

        selectionTitle = "Educator";

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
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


    public void getEducatorNewsfeedDetails() {
        newsfeedArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("teach_token", UserEducator.getToken(context));

        HttpProvider.post(context, "controller_educator/get_post.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("NEWSFEED: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nf_id = jsonObject.getString("nf_id");
                        String nf_token = jsonObject.getString("nf_token");
                        String nf_user_token = jsonObject.getString("nf_user_token");
                        String nf_details = jsonObject.getString("nf_details");
                        String nf_files = jsonObject.getString("nf_files");
                        String nf_filetype = jsonObject.getString("nf_filetype");
                        String nf_date = jsonObject.getString("nf_date");
                        String nf_fullname = jsonObject.getString("nf_fullname");
                        String nf_image = jsonObject.getString("nf_image");

                        Newsfeed newsfeed = new Newsfeed();
                        newsfeed.setNf_id(nf_id);
                        newsfeed.setNf_token(nf_token);
                        newsfeed.setNf_user_token(nf_user_token);
                        newsfeed.setNf_details(nf_details);
                        newsfeed.setNf_files(nf_files);
                        newsfeed.setNf_filetype(nf_filetype);
                        newsfeed.setNf_date(nf_date);
                        newsfeed.setNf_fullname(nf_fullname);
                        newsfeed.setNf_image(nf_image);

                        newsfeedArrayList.add(newsfeed);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    newsfeedAdapter = new NewsfeedAdapter(context, newsfeedArrayList, role);
                    newsfeedAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedNewsfeed = newsfeedArrayList.get(position);
                        }
                    });

                    recyclerView.setAdapter(newsfeedAdapter);

                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void getStudentNewsfeedDetails() {

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
}