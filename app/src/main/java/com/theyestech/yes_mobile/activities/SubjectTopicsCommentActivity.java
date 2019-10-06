package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.CommentsAdapter;
import com.theyestech.yes_mobile.dialogs.OkayClosePopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Comment;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SubjectTopicsCommentActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private ArrayList<Comment> commentArrayList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private Comment selectedComment = new Comment();

    private String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_topics_comment);

        topicId = getIntent().getExtras().getString("TOPIC_ID");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_CommentBack);
        swipeRefreshLayout = findViewById(R.id.swipe_Comment);
        recyclerView = findViewById(R.id.rv_Comments);

        swipeRefreshLayout.setRefreshing(true);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCommentDetails();
            }
        });

        getCommentDetails();
    }

    private void getCommentDetails() {
        commentArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("token", UserEducator.getToken(context));
        params.put("topic_id", topicId);
        Debugger.logD(topicId);

        HttpProvider.post(context, "controller_educator/get_topic_comments.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                String aw = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("COMMENTS: " + aw);
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("COMMENTS: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String tc_id = jsonObject.getString("tc_id");
                        String tc_topic_id = jsonObject.getString("tc_topic_id");
                        String tc_user_id = jsonObject.getString("tc_user_id");
                        String tc_details = jsonObject.getString("tc_details");
                        String tc_file = jsonObject.getString("tc_file");
                        String tc_datetime = jsonObject.getString("tc_datetime");
                        String user_image = jsonObject.getString("user_image");
                        String user_fullname = jsonObject.getString("user_fullname");

                        Comment comment = new Comment();
                        comment.setTc_id(tc_id);
                        comment.setTc_topic_id(tc_topic_id);
                        comment.setTc_user_id(tc_user_id);
                        comment.setTc_details(tc_details);
                        comment.setTc_file(tc_file);
                        comment.setTc_datetime(tc_datetime);
                        comment.setUser_image(user_image);
                        comment.setUser_image(user_fullname);

                        commentArrayList.add(comment);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    commentsAdapter = new CommentsAdapter(context, commentArrayList);
                    commentsAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position) {
//                            selectedComment = commentArrayList.get(position);
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("SUBJECT", selectedComment);
//                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(commentsAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
}
