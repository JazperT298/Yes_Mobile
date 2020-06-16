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
import com.theyestech.yes_mobile.adapters.NewsfeedCommentsAdapter;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.NewsfeedComment;
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

public class NewsfeedCommentActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack, ivSend;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private EditText etComment;
    private ConstraintLayout emptyIndicator;

    private ArrayList<NewsfeedComment> commentArrayList = new ArrayList<>();
    private NewsfeedCommentsAdapter commentsAdapter;
    private NewsfeedComment selectedComment = new NewsfeedComment();

    private String commentDetails;

    private String newsfeedId;
    private String newsfeedToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed_comment);

        newsfeedId = getIntent().getExtras().getString("NEWSFEED_ID");
        newsfeedToken = getIntent().getExtras().getString("NEWSFEED_TOKEN");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_NewsfeedCommentBack);
        ivSend = findViewById(R.id.iv_NewsfeedCommentSend);
        swipeRefreshLayout = findViewById(R.id.swipe_NewsfeedComment);
        recyclerView = findViewById(R.id.rv_NewsfeedComment);
        etComment = findViewById(R.id.et_NewsfeedCommentComment);
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
                    else
                        saveComment();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (role.equals(UserRole.Educator()))
                    getEducatorCommentDetails();
                else
                    getEducatorCommentDetails();
            }
        });

        if (role.equals(UserRole.Educator()))
            getEducatorCommentDetails();
        else
            getEducatorCommentDetails();
    }

    private void getEducatorCommentDetails() {
        commentArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("nf_id", newsfeedId);

        HttpProvider.post(context, "controller_educator/get_newsfeed_comments.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {

                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("COMMENTS: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String tn_id = jsonObject.getString("tn_id");
                        String tn_newsfeed_id = jsonObject.getString("tn_newsfeed_id");
                        String tn_user_id = jsonObject.getString("tn_user_id");
                        String tn_details = jsonObject.getString("tn_details");
                        String tn_file = jsonObject.getString("tn_file");
                        String tn_datetime = jsonObject.getString("tn_datetime");
                        String user_image = jsonObject.getString("user_image");
                        String user_fullname = jsonObject.getString("user_fullname");

                        NewsfeedComment comment = new NewsfeedComment();
                        comment.setTn_id(tn_id);
                        comment.setTn_newsfeed_id(tn_newsfeed_id);
                        comment.setTn_user_id(tn_user_id);
                        comment.setTn_details(tn_details);
                        comment.setTn_file(tn_file);
                        comment.setTn_datetime(tn_datetime);
                        comment.setUser_image(user_image);
                        comment.setUser_fullname(user_fullname);

                        commentArrayList.add(comment);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    commentsAdapter = new NewsfeedCommentsAdapter(context, commentArrayList);
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
        params.put("nf_token", newsfeedToken);
        params.put("nf_id", newsfeedId);
        params.put("newsfeed_comment", commentDetails);

        HttpProvider.post(context, "controller_educator/post_comment_newsfeed.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String aw = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD(aw);
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String result = jsonObject.getString("result");
                    if (result.contains("success")) {
//                        Toasty.success(context, "Saved.").show();
                    } else
                        Toasty.warning(context, result).show();
                    getEducatorCommentDetails();
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
}
