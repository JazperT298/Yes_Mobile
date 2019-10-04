package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Topic topic = topicArrayList.get(i);

        viewHolder.tvDetails.setText(topic.getTopic_details());

        if (topic.getTopic_file().isEmpty()) {
            viewHolder.ivImage.setVisibility(View.GONE);
        } else {
            Glide.with(context)
                    .load(HttpProvider.getTopicDir() + topic.getTopic_file())
                    .into(viewHolder.ivImage);
        }

        viewHolder.constraintYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.constraintComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return topicArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvDetails;
        private ConstraintLayout constraintYes, constraintComment;

        public ViewHolder(View view) {
            super(view);

            ivImage = view.findViewById(R.id.iv_ListrowSubjectTopicsImage);
            tvDetails = view.findViewById(R.id.tv_ListrowSubjectTopicsDetails);
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
