package com.theyestech.yes_mobile.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.NotesAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Note;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

public class VideoLabPreviewActivity extends AppCompatActivity {

    private Context context;
    private String role;
    private String userToken;
    private String userId;
    private String video_id;
    private String video_file;
    private String video_title;
    private String video_price;
    private String video_educator;

    private VideoView v_MainVideo;
    private TextView tv_VideoTitle, tv_VideoPrice, tv_VideoPreview, tv_Fullname, tv_LearnText, tv_Educational, tv_Subject , tv_School;
    private ImageView iv_HomeProfile;

    private ProgressDialog bar;
    private MediaController ctlr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lab_preview);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        context = this;
        role = UserRole.getRole(context);
        Intent extras = getIntent();
        Bundle bundle = extras.getExtras();
        assert bundle != null;
        video_id = bundle.getString("VIDEO_ID");
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();
        getAllVideoLabByHeader();
    }
    private void initializeUI(){
        v_MainVideo = findViewById(R.id.v_MainVideo);
        tv_VideoTitle = findViewById(R.id.tv_VideoTitle);
        tv_VideoPrice = findViewById(R.id.tv_VideoPrice);
        tv_VideoPreview = findViewById(R.id.tv_VideoPreview);
        tv_Fullname = findViewById(R.id.tv_Fullname);
        tv_LearnText = findViewById(R.id.tv_LearnText);
        tv_Educational = findViewById(R.id.tv_Educational);
        tv_Subject = findViewById(R.id.tv_Subject);
        tv_School = findViewById(R.id.tv_School);
        iv_HomeProfile = findViewById(R.id.iv_HomeProfile);



        tv_VideoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/webapps/hermes?token=2BV15847XN401832N&useraction=commit&mfid=1591615929195_5ba7ea75a9698"));
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/webapps/hermes?token=2BV15847XN401832N&useraction=commit&mfid=1591615929195_5ba7ea75a9698")));
                }
            }
        });

    }


    private void getAllVideoLabByHeader(){
        RequestParams params = new RequestParams();
        params.put("vh_id", video_id);

        HttpProvider.post(context, "controller_educator/GetPreviewVideoDetails.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Debugger.logD("responseBody " + responseBody);

                String str = new String(responseBody);

                Debugger.logD("str " + str);

                if (str.equals("")){

                }else{
                    try {
                        JSONArray jsonArray = new JSONArray(str);
                        for (int i = 0; i <= jsonArray.length() - 1; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            tv_VideoTitle.setText(jsonObject.getString("vh_title"));
                            tv_VideoPrice.setText("Video Price : " + "₱ " + jsonObject.getString("vh_price"));
                            tv_Fullname.setText(jsonObject.getString("user_firstname") + " " + jsonObject.getString("user_lastname"));
                            tv_LearnText.setText(jsonObject.getString("vh_desc"));
                            tv_Educational.setText(jsonObject.getString("user_current_school"));
                            tv_Subject.setText(jsonObject.getString("user_subj_major"));
                            tv_School.setText(jsonObject.getString("user_current_school"));

                            Glide.with(context)
                                    .load(HttpProvider.getProfileDir() + jsonObject.getString("user_image"))
                                    .apply(GlideOptions.getOptions())
                                    .into(iv_HomeProfile);


                            byte[] data = Base64.decode(jsonObject.getString("video_filename"), Base64.DEFAULT);
                            String mediaUrl = new String(data, StandardCharsets.UTF_8);
                            mediaUrl = mediaUrl.replaceAll(" ", "%20");
                            bar=new ProgressDialog(context);
                            bar.setCancelable(false);
                            bar.show();

                            if(bar.isShowing()) {
                                v_MainVideo.setVideoURI(Uri.parse("https://theyestech.com/" + mediaUrl));
                                v_MainVideo.start();
                                ctlr = new MediaController(context);
                                ctlr.setMediaPlayer(v_MainVideo);
                                v_MainVideo.setMediaController(ctlr);
                                v_MainVideo.requestFocus();
                            }
                            bar.dismiss();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
}
