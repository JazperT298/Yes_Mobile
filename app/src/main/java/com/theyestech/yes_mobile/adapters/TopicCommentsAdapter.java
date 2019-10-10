package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.theyestech.yes_mobile.models.TopicComment;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class TopicCommentsAdapter extends RecyclerView.Adapter<TopicCommentsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<TopicComment> commentArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public TopicCommentsAdapter(Context context, ArrayList<TopicComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_comments, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TopicComment comment = commentArrayList.get(i);

        viewHolder.tvFullname.setText(comment.getUser_fullname());
        viewHolder.tvDetails.setText(comment.getTc_details());
        viewHolder.tvDateTime.setText(comment.getTc_datetime());

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + comment.getUser_image())
                .apply(GlideOptions.getOptions())
                .into(viewHolder.ivImage);
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvDetails, tvFullname, tvDateTime;

        public ViewHolder(View view) {
            super(view);

            ivImage = view.findViewById(R.id.iv_ListrowCommentProfile);
            tvDetails = view.findViewById(R.id.tv_ListrowCommentDetails);
            tvFullname = view.findViewById(R.id.tv_ListrowCommentFullname);
            tvDateTime = view.findViewById(R.id.tv_ListrowCommentDateTime);
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }
}
