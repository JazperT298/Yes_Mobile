package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.RequestManager;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Newsfeed;
import com.theyestech.yes_mobile.models.VideoLab;

import java.util.ArrayList;

public class VideoLabAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<VideoLab> videoLabArrayList;
    private RequestManager requestManager;
//    private Context context;
//    private LayoutInflater layoutInflater;
//    private ArrayList<VideoLab> videoLabArrayList;
//    private OnClickRecyclerView onClickRecyclerView;
//    private String role;
//
//    private int[] videoArray;
//    private int currentIndex;
//    private int endIndex;

    public VideoLabAdapter(ArrayList<VideoLab> videoLabArrayList, RequestManager requestManager) {
        this.videoLabArrayList = videoLabArrayList;
        this.requestManager = requestManager;
    }

//    public VideoLabAdapter(Context context, ArrayList<VideoLab> videoLabArrayList, String role) {
//        this.context = context;
//        this.role = role;
//        this.videoLabArrayList = videoLabArrayList;
//        this.layoutInflater = LayoutInflater.from(context);
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        layoutInflater = LayoutInflater.from(context);
//        View view = layoutInflater.inflate(R.layout.listrow_video_lab, viewGroup, false);
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;
        return new VideoLabViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listrow_video_lab, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((VideoLabViewHolder)viewHolder).onBind(videoLabArrayList.get(i), requestManager);

//        final VideoLab videoLab = videoLabArrayList.get(i);
//
//        viewHolder.tv_VideoTitle.setText(videoLab.getVh_title());
//        viewHolder.tv_Fullname.setText(videoLab.getEducator_fullname());
//        viewHolder.tv_VideoPrice.setText("Video Price : " + "₱ " + videoLab.getVideo_price());

//        viewHolder.v_MainVideos.setVideoURI(Uri.parse(HttpProvider.getVideoLabDir() + videoLab.getVideo_filename()));
//        //viewHolder.v_MainVideos.setMediaController(new MediaController(context));
//        MediaController mediaController = new MediaController(context);
//        viewHolder.v_MainVideos.setMediaController(mediaController);
//        mediaController.setAnchorView(viewHolder.v_MainVideos);
    }

    @Override
    public int getItemCount() {
        return videoLabArrayList.size();
    }

//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private TextView tv_VideoTitle, tv_Fullname, tv_VideoPrice, tv_VideoPreview;
//        private VideoView v_MainVideos;
//        private ConstraintLayout constraintYes, constraintComment;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tv_VideoTitle = itemView.findViewById(R.id.tv_VideoTitle);
//            tv_Fullname = itemView.findViewById(R.id.tv_Fullname);
//            tv_VideoPrice = itemView.findViewById(R.id.tv_VideoPrice);
//            tv_VideoPreview = itemView.findViewById(R.id.tv_VideoPreview);
//            //v_MainVideos = itemView.findViewById(R.id.v_MainVideos);
//
//            tv_VideoPreview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onClickRecyclerView != null)
//                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 1);
//                }
//            });
//        }
//    }
//    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
//        this.onClickRecyclerView = onClickRecyclerView;
//    }
}
