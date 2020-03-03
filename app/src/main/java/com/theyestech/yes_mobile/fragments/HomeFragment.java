package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.NewNewsfeedActivity;
import com.theyestech.yes_mobile.activities.ProfileActivity;
import com.theyestech.yes_mobile.activities.SubjectActivity;
import com.theyestech.yes_mobile.adapters.NewsfeedAdapter;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Newsfeed;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private View view;
    private Context context;

    private String role;

    private ImageView ivProfile;
    private TextView tvFirstname;
    private CardView cvSubjects, cvNotes, cvConnections, cvNewsfeeds, cvVideoLab, cvYestechCourse, cvMyVideos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Newsfeed> newsfeedArrayList = new ArrayList<>();
    private NewsfeedAdapter newsfeedAdapter;
    private Newsfeed selectedNewsfeed = new Newsfeed();

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

        initializeUI();
    }

    private void initializeUI() {
        ivProfile = view.findViewById(R.id.iv_HomeProfile);
//        ivSearch = view.findViewById(R.id.iv_HomeSearch);
//        ivNewPost = view.findViewById(R.id.iv_HomeNewPost);
        tvFirstname = view.findViewById(R.id.tv_HomeFirstname);
        cvSubjects = view.findViewById(R.id.cv_Home_Subjects);
        cvNotes = view.findViewById(R.id.cv_Home_Notes);
        cvConnections = view.findViewById(R.id.cv_Home_Connections);
        cvNewsfeeds = view.findViewById(R.id.cv_Home_Newsfeeds);
        cvVideoLab = view.findViewById(R.id.cv_Home_VideoLab);
        cvYestechCourse = view.findViewById(R.id.cv_Home_YestechCourse);
        cvMyVideos = view.findViewById(R.id.cv_Home_MyVideos);
        swipeRefreshLayout = view.findViewById(R.id.swipe_Home);
        recyclerView = view.findViewById(R.id.rv_Home);
        emptyIndicator = view.findViewById(R.id.view_Empty);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
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

            }
        });

        cvConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvNewsfeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvVideoLab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvYestechCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        swipeRefreshLayout.setRefreshing(false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (role.equals(UserRole.Educator()))
                    getEducatorNewsfeedDetails();
                else
                    getStudentNewsfeedDetails();
            }
        });



//        ivNewPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, NewNewsfeedActivity.class);
//                startActivity(intent);
//            }
//        });

        if (role.equals(UserRole.Educator()))
            setEducatorHeader();
        else
            setStudentHeader();
    }

    private void setEducatorHeader() {
        if (!UserEducator.getFirstname(context).equals(""))
            tvFirstname.setText(UserEducator.getFirstname(context));
        else
            tvFirstname.setText(UserEducator.getEmail(context));

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserEducator.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
    }

    private void setStudentHeader() {
        if (!UserStudent.getFirstname(context).equals(""))
            tvFirstname.setText(UserStudent.getFirstname(context));
        else
            tvFirstname.setText(UserStudent.getEmail(context));

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + UserStudent.getImage(context))
                .apply(GlideOptions.getOptions())
                .into(ivProfile);
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
                        public void onItemClick(View view, int position) {
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

    @Override
    public void onResume() {
        super.onResume();

        if (UserRole.getRole(context).equals(UserRole.Educator()))
            getEducatorNewsfeedDetails();
        else
            getStudentNewsfeedDetails();
    }
}