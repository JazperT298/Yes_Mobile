package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.StickersAdapter;
import com.theyestech.yes_mobile.models.Stickers;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;

public class SubjectAwardsActivity extends AppCompatActivity {

    private Context context;
    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Stickers> stickersArrayList = new ArrayList<>();
    private StickersAdapter stickersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_awards);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();

    }

    private void initializeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorGreensea));
        }
    }
}
