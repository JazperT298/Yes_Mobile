package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.activities.NewsfeedCommentActivity;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Newsfeed;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Newsfeed> newsfeedArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    private int[] imageArray;
    private int currentIndex;
    private int endIndex;

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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Newsfeed newsfeed = newsfeedArrayList.get(i);

        viewHolder.tvFullname.setText(newsfeed.getNf_fullname());
        viewHolder.tvDateTime.setText(newsfeed.getNf_date());
        viewHolder.tvDetails.setText(newsfeed.getNf_details());

        if (newsfeed.getNf_filetype().equals("video")) {
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

        viewHolder.constraintComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsfeedCommentActivity.class);
                intent.putExtra("NEWSFEED_ID", newsfeed.getNf_id());
                intent.putExtra("NEWSFEED_TOKEN", newsfeed.getNf_token());
                context.startActivity(intent);
            }
        });

        viewHolder.constraintYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageArray = new int[8];
                imageArray[0] = R.drawable.ic_yes_02;
                imageArray[1] = R.drawable.ic_yes_03;
                imageArray[2] = R.drawable.ic_yes_04;
                imageArray[3] = R.drawable.ic_yes_05;
                imageArray[4] = R.drawable.ic_yes_06;
                imageArray[5] = R.drawable.ic_yes_07;
                imageArray[6] = R.drawable.ic_yes_08;
                imageArray[7] = R.drawable.ic_yes_09;

                endIndex = 7;
                currentIndex = 0;

                nextImage(viewHolder.ivYes);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsfeedArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage, ivProfile, ivType, ivYes;
        private TextView tvDetails, tvFullname, tvDateTime, tvYes;
        private VideoView videoView;
        private ConstraintLayout constraintYes, constraintComment;

        public ViewHolder(View view) {
            super(view);

            videoView = view.findViewById(R.id.vv_ListrowHomeVideo);
            ivImage = view.findViewById(R.id.iv_ListrowHomeImage);
            ivProfile = view.findViewById(R.id.iv_ListrowHomeProfile);
            ivType = view.findViewById(R.id.iv_ListrowHomeType);
            tvDetails = view.findViewById(R.id.tv_ListrowHomeDetails);
            tvFullname = view.findViewById(R.id.tv_ListrowHomeFullname);
            tvDateTime = view.findViewById(R.id.tv_ListrowHomeDateTime);
            tvYes = view.findViewById(R.id.tv_ListrowHomeYes);
            ivYes = view.findViewById(R.id.iv_ListrowHomeYes);
            constraintYes = view.findViewById(R.id.constraint_ListrowHomeYes);
            constraintComment = view.findViewById(R.id.constraint_ListrowHomeComments);

            constraintYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition());
                }
            });

            constraintComment.setOnClickListener(new View.OnClickListener() {
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

    private void nextImage(final ImageView imageView){
        imageView.setImageResource(imageArray[currentIndex]);
        currentIndex++;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                if (currentIndex <= endIndex) {
                    nextImage(imageView);
                }
            }
        }, 20);
    }
}
