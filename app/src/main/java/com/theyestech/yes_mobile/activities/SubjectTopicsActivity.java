package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.TopicsAdapter;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.Topic;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SubjectTopicsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Topic> topicArrayList = new ArrayList<>();
    private TopicsAdapter topicsAdapter;
    private Topic selectedTopic = new Topic();

    private Subject subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_topics);

        subject = getIntent().getParcelableExtra("SUBJECT");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorPeterriver));
//        }

        ivBack = findViewById(R.id.iv_SubjectTopicsBack);
        swipeRefreshLayout = findViewById(R.id.swipe_SubjectTopics);
        recyclerView = findViewById(R.id.rv_SubjectTopics);
        floatingActionButton = findViewById(R.id.fab_SubjectTopicsAdd);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTopicDetails();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewTopicActivity.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });
    }

    private void getTopicDetails() {
        topicArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("subj_id", subject.getId());

        HttpProvider.post(context, "controller_educator/get_topics.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {

                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("TOPIC: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String topic_id = jsonObject.getString("topic_id");
                        String topic_subj_id = jsonObject.getString("topic_subj_id");
                        String topic_file = jsonObject.getString("topic_file");
                        String topic_filetype = jsonObject.getString("topic_filetype");
                        String topic_details = jsonObject.getString("topic_details");

                        Topic topic = new Topic();
                        topic.setTopic_id(topic_id);
                        topic.setTopic_subj_id(topic_subj_id);
                        topic.setTopic_details(topic_details);
                        topic.setTopic_filetype(topic_filetype);
                        topic.setTopic_file(topic_file);

                        topicArrayList.add(topic);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    topicsAdapter = new TopicsAdapter(context, topicArrayList);
                    topicsAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedTopic = topicArrayList.get(position);
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("TOPIC", topic);
//                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(topicsAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getTopicDetails();
    }
}
