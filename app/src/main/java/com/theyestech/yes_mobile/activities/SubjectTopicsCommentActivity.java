package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.TopicCommentsAdapter;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.TopicComment;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.KeyboardHandler;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectTopicsCommentActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack, ivSend;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private EditText etComment;
    private ConstraintLayout emptyIndicator;

    private ArrayList<TopicComment> commentArrayList = new ArrayList<>();
    private TopicCommentsAdapter commentsAdapter;
    private TopicComment selectedComment = new TopicComment();

    private String commentDetails;

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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorPeterriver));
//        }

        ivBack = findViewById(R.id.iv_TopicCommentBack);
        ivSend = findViewById(R.id.iv_TopicCommentSend);
        swipeRefreshLayout = findViewById(R.id.swipe_TopicComment);
        recyclerView = findViewById(R.id.rv_TopicComment);
        etComment = findViewById(R.id.et_TopicCommentComment);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);

        swipeRefreshLayout.setRefreshing(true);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDetails = etComment.getText().toString();
                if (commentDetails.isEmpty()) {
                    Toasty.warning(context, "Please input comment.").show();
                } else {
                    KeyboardHandler.closeKeyboard(etComment, context);
                    etComment.setText("");
                    if (role.equals(UserRole.Educator()))
                        saveComment();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (role.equals(UserRole.Educator()))
                    getEducatorCommentDetails();
            }
        });

        if (role.equals(UserRole.Educator()))
            getEducatorCommentDetails();
    }

    private void getEducatorCommentDetails() {
        commentArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("topic_id", topicId);
        params.put("topic_id", UserEducator.getToken(context));

        HttpProvider.post(context, "controller_educator/get_topic_comments.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                Debugger.logD("responseBody " +responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str " +str);
                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {

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

                        TopicComment comment = new TopicComment();
                        comment.setTc_id(tc_id);
                        comment.setTc_topic_id(tc_topic_id);
                        comment.setTc_user_id(tc_user_id);
                        comment.setTc_details(tc_details);
                        comment.setTc_file(tc_file);
                        comment.setTc_datetime(tc_datetime);
                        comment.setUser_image(user_image);
                        comment.setUser_fullname(user_fullname);

                        commentArrayList.add(comment);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    commentsAdapter = new TopicCommentsAdapter(context, commentArrayList);
                    commentsAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
//                            selectedComment = commentArrayList.get(position);
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("SUBJECT", selectedComment);
//                            startActivity(intent);
                        }
                    });

                    recyclerView.setAdapter(commentsAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void saveComment() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("topic_id", topicId);
        params.put("topic_comment", commentDetails);
        Debugger.logD("topic_id " + topicId);
        Debugger.logD("topic_comment " + commentDetails);

        HttpProvider.post(context, "controller_educator/post_comment_topic.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody " + responseBody);
                String aw = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("aw " + aw);
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String result = jsonObject.getString("result");
                    Debugger.logD("result " + result);
                    if (result.contains("success")) {
                        Toasty.success(context, "Saved.").show();
                    } else
                        Toasty.warning(context, result).show();
                    getEducatorCommentDetails();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Debugger.logD("e " + e);
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
