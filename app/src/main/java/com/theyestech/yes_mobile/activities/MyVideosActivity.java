package com.theyestech.yes_mobile.activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.UserRole;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class MyVideosActivity extends AppCompatActivity {
    private Context context;
    private String role;
    private String userToken;
    private String userId;

    private ImageView ivBack,ivSearch;
    private TextView tv_VideoTitle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator,constraintLayout2;
    private EditText et_SelectCourses;
    private MaterialSpinner sp_FilterVideo;

    private ArrayList<String> filterVideo = new ArrayList<>();

    private String filterVideos = "";

    private String video_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_videos);
        context = this;
        role = UserRole.getRole(context);

        Intent extras = getIntent();
        Bundle bundle = extras.getExtras();
        assert bundle != null;
        video_title = bundle.getString("MY_TITLE");


        filterVideo.add("Kiddie Smart Academy(5 years old below)");
        filterVideo.add("Leap and Learn(15 years old below)");
        filterVideo.add("Yes Learning(15 years old above)");

        initializeUI();

    }

    @SuppressLint("RestrictedApi")
    private void initializeUI(){
        ivBack = findViewById(R.id.imageView35);
        tv_VideoTitle = findViewById(R.id.tv_VideoTitle);
        ivSearch = findViewById(R.id.iv_VideosSearch);
        swipeRefreshLayout = findViewById(R.id.swipe_MyVideos);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);
        et_SelectCourses = findViewById(R.id.et_SelectCourses);
        constraintLayout2 = findViewById(R.id.constraintLayout2);
        sp_FilterVideo = findViewById(R.id.sp_FilterVideo);
        recyclerView = findViewById(R.id.rv_MyVideos);
        floatingActionButton = findViewById(R.id.fab_Videos);

        if (video_title.equals("1")){
            tv_VideoTitle.setText("My Videos");
        }else{
            tv_VideoTitle.setText("My Upload");
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPreviewDialog();

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setCancelable(false);
                dialog.setTitle("Do You Want to be a Premium Member?");
                dialog.setMessage("Being a premium member will allow you to upload Video Tutorials to make some money, Just pay 300 pesos to be a premium member.\n" +
                        "Want to know more?  Click Here" );
                dialog.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                        dialog.cancel();
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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

    private void openPreviewDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_premium_view, null);

        final ImageView imageView33;

        imageView33 = dialogView.findViewById(R.id.imageView33);

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        imageView33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
