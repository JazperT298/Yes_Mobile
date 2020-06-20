package com.theyestech.yes_mobile.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class YestechCourseActivity extends AppCompatActivity {
    private Context context;
    private String role;
    private String userToken;
    private String userId;

    private ImageView ivBack,ivSearch;
    private SwipeRefreshLayout swipeRefreshLayout;

    private EditText et_SelectCourses;
    private MaterialSpinner sp_FilterVideo;
    private ConstraintLayout emptyIndicator,constraintLayout2;

    private ArrayList<String> filterVideo = new ArrayList<>();

    private String filterVideos = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yestech_course);

        context = this;
        role = UserRole.getRole(context);

        filterVideo.add("Kiddie Smart Academy(5 years old below)");
        filterVideo.add("Leap and Learn(15 years old below)");
        filterVideo.add("Yes Learning(15 years old above)");

        initializeUI();

        if (role.equals(UserRole.Educator())) {
            if (UserEducator.getFirstname(context) == null) {
            ShowEducatorIntro("Search", "Search your saved courses here", R.id.iv_YestechSearch, 1);
            }
        }
        else {
            if (UserStudent.getFirstname(context) == null) {
            ShowEducatorIntro("Search", "Search your saved courses here", R.id.iv_YestechSearch, 1);
            }
        }

    }
    private void initializeUI(){
        ivBack = findViewById(R.id.imageView35);
        ivSearch = findViewById(R.id.iv_YestechSearch);
        swipeRefreshLayout = findViewById(R.id.swipe_YestechCourse);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);
        et_SelectCourses = findViewById(R.id.et_SelectCourses);
        constraintLayout2 = findViewById(R.id.constraintLayout2);
        sp_FilterVideo = findViewById(R.id.sp_FilterVideo);
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
                swipeRefreshLayout.setRefreshing(false);
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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emptyIndicator.setVisibility(View.VISIBLE);
    }

    private void ShowEducatorIntro(String title, String text, int viewId, final int type) {

        new GuideView.Builder(context)
                .setTitle(title)
                .setContentText(text)
                .setTargetView(findViewById(viewId))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                    }
                })
                .build()
                .show();
    }
}
