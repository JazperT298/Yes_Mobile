package com.theyestech.yes_mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.NewNewsfeedActivity;
import com.theyestech.yes_mobile.activities.ProfileActivity;
import com.theyestech.yes_mobile.activities.StartActivity;
import com.theyestech.yes_mobile.adapters.NewsfeedAdapter;
import com.theyestech.yes_mobile.dialogs.OkayClosePopup;
import com.theyestech.yes_mobile.dialogs.ProgressPopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Newsfeed;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private View view;
    private Context context;

    private String role;

    private ImageView ivProfile, ivSearch, ivNewPost;
    private TextView tvFirstname;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

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
        ivSearch = view.findViewById(R.id.iv_HomeSearch);
        ivNewPost = view.findViewById(R.id.iv_HomeNewPost);
        tvFirstname = view.findViewById(R.id.tv_HomeFirstname);
        swipeRefreshLayout = view.findViewById(R.id.swipe_Home);
        recyclerView = view.findViewById(R.id.rv_Home);

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

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            }
        });

        ivNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewNewsfeedActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getEducatorNewsfeedDetails() {
        newsfeedArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("teach_token", UserEducator.getToken(context));

        HttpProvider.post(context, "controller_educator/get_post.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
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
                    newsfeedAdapter = new NewsfeedAdapter(context, newsfeedArrayList);
                    newsfeedAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position) {
                            selectedNewsfeed = newsfeedArrayList.get(position);
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("TOPIC", topic);
//                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(newsfeedAdapter);

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

        if (!UserRole.getRole(context).isEmpty())
            if (UserRole.getRole(context).equals(UserRole.Educator()))
                getEducatorNewsfeedDetails();
            else
                getStudentNewsfeedDetails();
    }
}