package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.theyestech.yes_mobile.activities.SubjectTopicsCommentActivity;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Topic;

import java.util.ArrayList;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Topic> topicArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public TopicsAdapter(Context context, ArrayList<Topic> topicArrayList) {
        this.context = context;
        this.topicArrayList = topicArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subject_topics, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Topic topic = topicArrayList.get(i);

        viewHolder.tvDetails.setText(topic.getTopic_details());

        if (topic.getTopic_filetype().equalsIgnoreCase("image")){
            Glide.with(context)
                    .load(HttpProvider.getTopicDir() + topic.getTopic_file())
                    .into(viewHolder.ivImage);
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.videoView.setVideoURI(Uri.parse(HttpProvider.getTopicDir() + topic.getTopic_file()));
            viewHolder.videoView.setMediaController(new MediaController(context));
//            viewHolder.videoView.start();
            viewHolder.videoView.setVisibility(View.VISIBLE);
        }

        viewHolder.constraintYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.ivYes.setImageResource(R.drawable.ic_yes_up);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something here
                        viewHolder.ivYes.setImageResource(R.drawable.ic_yes_selected);
                        viewHolder.tvYes.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    }
                }, 500);
            }
        });

        viewHolder.constraintComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubjectTopicsCommentActivity.class);
                intent.putExtra("TOPIC_ID", topic.getTopic_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage, ivYes;
        private TextView tvDetails, tvYes;
        private VideoView videoView;
        private ConstraintLayout constraintYes, constraintComment;

        public ViewHolder(View view) {
            super(view);

            videoView = view.findViewById(R.id.vv_ListrowSubjectTopicVideo);
            ivImage = view.findViewById(R.id.iv_ListrowSubjectTopicsImage);
            ivYes = view.findViewById(R.id.iv_ListrowSubjectTopicYes);
            tvDetails = view.findViewById(R.id.tv_ListrowSubjectTopicsDetails);
            tvYes = view.findViewById(R.id.tv_ListrowSubjectTopicYes);
            constraintYes = view.findViewById(R.id.constraint_ListrowSubjectTopicsYes);
            constraintComment = view.findViewById(R.id.constraint_ListrowSubjectTopicsComments);

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
}
