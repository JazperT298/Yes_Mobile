package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.NewsfeedCommentActivity;
import com.theyestech.yes_mobile.activities.SubjectTopicsCommentActivity;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Newsfeed;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.ViewHolder>{

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Newsfeed> newsfeedArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public NewsfeedAdapter(Context context, ArrayList<Newsfeed> newsfeedArrayList) {
        this.context = context;
        this.newsfeedArrayList = newsfeedArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_home_newsfeed, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Newsfeed newsfeed = newsfeedArrayList.get(i);

        viewHolder.tvFullname.setText(newsfeed.getNf_fullname());
        viewHolder.tvDateTime.setText(newsfeed.getNf_date());
        viewHolder.tvDetails.setText(newsfeed.getNf_details());

        if (newsfeed.getNf_filetype().equals("video")){
            viewHolder.videoView.setVideoURI(Uri.parse(HttpProvider.getNewsfeedDir() + newsfeed.getNf_files()));
            viewHolder.videoView.setMediaController(new MediaController(context));
//            viewHolder.videoView.start();
            viewHolder.videoView.setVisibility(View.VISIBLE);
            viewHolder.ivType.setImageResource(R.drawable.ic_video);
        } else {
            Glide.with(context)
                    .load(HttpProvider.getNewsfeedDir() + newsfeed.getNf_files())
                    .into(viewHolder.ivImage);
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + newsfeed.getNf_image())
                .apply(GlideOptions.getOptions())
                .into(viewHolder.ivProfile);

        viewHolder.fabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsfeedCommentActivity.class);
                intent.putExtra("NEWSFEED_ID", newsfeed.getNf_id());
                intent.putExtra("NEWSFEED_TOKEN", newsfeed.getNf_token());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsfeedArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage, ivProfile, ivType;
        private TextView tvDetails, tvFullname, tvDateTime;
        private VideoView videoView;
        private FloatingActionButton fabYes, fabComment;

        public ViewHolder(View view) {
            super(view);

            videoView = view.findViewById(R.id.vv_ListrowHomeVideo);
            ivImage = view.findViewById(R.id.iv_ListrowHomeImage);
            ivProfile = view.findViewById(R.id.iv_ListrowHomeProfile);
            ivType = view.findViewById(R.id.iv_ListrowHomeType);
            tvDetails = view.findViewById(R.id.tv_ListrowHomeDetails);
            tvFullname = view.findViewById(R.id.tv_ListrowHomeFullname);
            tvDateTime = view.findViewById(R.id.tv_ListrowHomeDateTime);
            fabYes = view.findViewById(R.id.fab_ListrowHomeYes);
            fabComment = view.findViewById(R.id.fab_ListrowHomeComment);

            fabYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition());
                }
            });

            fabComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }
}
