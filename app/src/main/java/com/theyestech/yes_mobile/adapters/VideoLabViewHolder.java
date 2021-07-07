package com.theyestech.yes_mobile.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.VideoLab;

public class VideoLabViewHolder extends RecyclerView.ViewHolder  {


    FrameLayout media_container;
    TextView title;
    ImageView thumbnail, volumeControl,tv_VideoPreview;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;
    TextView tv_VideoTitle, tv_Fullname, tv_VideoPrice;

    public VideoLabViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        title = itemView.findViewById(R.id.title);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);
        tv_VideoTitle = itemView.findViewById(R.id.tv_VideoTitle);
        tv_Fullname = itemView.findViewById(R.id.tv_Fullname);
        tv_VideoPrice = itemView.findViewById(R.id.tv_VideoPrice);
        tv_VideoPreview = itemView.findViewById(R.id.tv_VideoPreview);

    }

    public void onBind(VideoLab videoLab, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
        tv_VideoTitle.setText(videoLab.getVh_title());
        tv_Fullname.setText("Educator: " + videoLab.getEducator_fullname());
        tv_VideoPrice.setText("Video Price : " + "₱ " + videoLab.getVideo_price());
        this.requestManager
                .load(R.drawable.yestech_banner)
                .into(thumbnail);
    }
}
