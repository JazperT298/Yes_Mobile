package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.StickersAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Sticker;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SubjectStickersActivity extends AppCompatActivity {

    private Context context;
    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Sticker> stickerArrayList = new ArrayList<>();
    private StickersAdapter stickersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_stickers);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();

    }

    private void initializeUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorMidnightblue));
//        }

        ivBack = findViewById(R.id.iv_SubjectStickersBack);
        swipeRefreshLayout = findViewById(R.id.swipe_Stickers);
        recyclerView = findViewById(R.id.rv_DialogSendToStudent);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllStickers();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAllStickers();
    }

    private void getAllStickers() {
        stickerArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("teach_id", UserEducator.getID(context));

        HttpProvider.post(context, "controller_educator/GetAllStickers.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);

                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {
                    JSONArray jsonArray = new JSONArray(str);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String sticker_id = jsonObject.getString("sticker_id");
                        String sticker_name = jsonObject.getString("sticker_name");

                        Sticker sticker = new Sticker();
                        sticker.setId(sticker_id);
                        sticker.setName(sticker_name);

                        stickerArrayList.add(sticker);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    stickersAdapter = new StickersAdapter(context, stickerArrayList);
                    stickersAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
//                            Toasty.success(context, "Sent.").show();
                            openSendStickerDialog();
                        }
                    });

                    recyclerView.setAdapter(stickersAdapter);
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

    private void openSendStickerDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_send_sticker_to_student, null);

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
