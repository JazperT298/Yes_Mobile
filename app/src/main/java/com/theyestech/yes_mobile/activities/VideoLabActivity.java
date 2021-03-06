package com.theyestech.yes_mobile.activities;



import android.content.Context;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.VideoLabAdapter;
import com.theyestech.yes_mobile.adapters.VideoLabRecyclerView;
import com.theyestech.yes_mobile.adapters.VideoLabViewHolder;
import com.theyestech.yes_mobile.models.VideoLab;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;
import com.theyestech.yes_mobile.utils.VerticalSpacingItemDecorator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class VideoLabActivity extends AppCompatActivity {
    private Context context;
    private String role;
    private String userToken;
    private String userId;

    private VideoLab selectedVideo = new VideoLab();
    private ImageView ivBack,ivSearch;
    private SwipeRefreshLayout swipeRefreshLayout;
    private VideoLabAdapter videoLabAdapter;
    private VideoLabViewHolder videoLabViewHolder;
    private ArrayList<VideoLab> videoLabArrayList = new ArrayList<>();
    private VideoLab videoLab;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator,constraintLayout2;
    private EditText et_SelectCourses;
    private MaterialSpinner sp_FilterVideo;

    private ArrayList<String> filterVideo = new ArrayList<>();

    private String filterVideos = "";

    private VideoLabRecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lab);

        context = this;
        role = UserRole.getRole(context);

        filterVideo.add("Kiddie Smart Academy(5 years old below)");
        filterVideo.add("Leap and Learn(15 years old below)");
        filterVideo.add("Yes Learning(15 years old above)");

        initializeUI();

        swipeRefreshLayout.setRefreshing(true);
    }

    private void initializeUI(){
        ivBack = findViewById(R.id.iv_VideoLabBack);
        ivSearch = findViewById(R.id.iv_VideoLabSearch);
        swipeRefreshLayout = findViewById(R.id.swipe_VideoLab);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);
        et_SelectCourses = findViewById(R.id.et_SelectCourses);
        constraintLayout2 = findViewById(R.id.constraintLayout2);
        sp_FilterVideo = findViewById(R.id.sp_FilterVideo);
        mRecyclerView = findViewById(R.id.rv_VideoLab);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toggle
                if (constraintLayout2.getVisibility() == View.VISIBLE){
                    constraintLayout2.setVisibility(View.GONE);
                    ivSearch.setImageResource(R.drawable.ic_search_white);
                }
                else{
                    constraintLayout2.setVisibility(View.VISIBLE);
                    ivSearch.setImageResource(R.drawable.ic_clear_white_24dp);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllVideoLabs();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_SelectCourses.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    Toasty.warning(context,"Searching...").show();
                    return true;
                }
                return false;
            }
        });
        sp_FilterVideo.setItems(filterVideo);
        sp_FilterVideo.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                filterVideos = filterVideo.get(position);
            }
        });
        getAllVideoLabs();
    }

    private void getAllVideoLabs(){
        videoLabArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();

        HttpProvider.post(context, "controller_educator/GetAllVideoLabs.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);

                String str = new String(responseBody, StandardCharsets.UTF_8);

                Debugger.logD("Videos " + str);

                if (str.equals("") || str.contains("No notes available"))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {
                    JSONArray jsonArray = new JSONArray(str);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String vh_title = jsonObject.getString("vh_title");
                        String vh_id = jsonObject.getString("vh_id");
                        String educator_fullname = jsonObject.getString("educator_fullname");
                        String video_price = jsonObject.getString("video_price");
                        String video_title = jsonObject.getString("video_title");
                        String video_dateupload = jsonObject.getString("video_dateupload");
                        String video_type = jsonObject.getString("video_type");
                        String video_id = jsonObject.getString("video_id");
                        String video_filename = jsonObject.getString("video_filename");

                        videoLab = new VideoLab();
                        videoLab.setVh_title(vh_title);
                        videoLab.setVh_id(vh_id);
                        videoLab.setEducator_fullname(educator_fullname);
                        videoLab.setVideo_price(video_price);
                        videoLab.setVideo_title(video_title);
                        videoLab.setVideo_dateupload(video_dateupload);
                        videoLab.setVideo_type(video_type);
                        videoLab.setVideo_id(video_id);
                        videoLab.setVideo_filename(video_filename);
                        videoLabArrayList.add(videoLab);

                    }
                    Collections.reverse(videoLabArrayList);

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
                    mRecyclerView.addItemDecoration(itemDecorator);
                    mRecyclerView.setVideoLabs(videoLabArrayList);
                    mRecyclerView.setHasFixedSize(true);
                    videoLabAdapter = new VideoLabAdapter(videoLabArrayList, initGlide());

                    mRecyclerView.setAdapter(videoLabAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }
    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    protected void onDestroy() {
        if(mRecyclerView!=null)
            mRecyclerView.releasePlayer();
        super.onDestroy();
    }

}
