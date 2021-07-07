package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
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
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Topic;

import java.util.ArrayList;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Topic> topicArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    private int[] imageArray;
    private int currentIndex;
    private int endIndex;
    private Topic topic;

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
        topic = topicArrayList.get(i);

        viewHolder.tvDetails.setText(topic.getTopic_details());

        if (topic.getTopic_filetype().equalsIgnoreCase("image")){
            Glide.with(context)
                    .load(HttpProvider.getTopicDir() + topic.getTopic_file())
                    .into(viewHolder.ivImage);
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        } else if (topic.getTopic_filetype().equalsIgnoreCase("word")){
            Glide.with(context)
                    .load("http://ec2-3-0-89-51.ap-southeast-1.compute.amazonaws.com/img/word-template.jpg")
                    .into(viewHolder.ivImage);
            viewHolder.tvDetails.setText(topic.getTopic_details() + " " + "\n" + "\nThere is no preview for this file type");
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        }else if (topic.getTopic_filetype().equalsIgnoreCase("pdf")){
            Glide.with(context)
                    .load("http://ec2-3-0-89-51.ap-southeast-1.compute.amazonaws.com/img/pdf-template.jpg")
                    .into(viewHolder.ivImage);
            viewHolder.tvDetails.setText(topic.getTopic_details() + " " + "\n" + "\nThere is no preview for this file type");
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        }else if (topic.getTopic_filetype().equalsIgnoreCase("pptx")){
            Glide.with(context)
                    .load("http://ec2-3-0-89-51.ap-southeast-1.compute.amazonaws.com/img/pp-template.jpg")
                    .into(viewHolder.ivImage);
            viewHolder.tvDetails.setText(topic.getTopic_details() + " " + "\n" + "\nThere is no preview for this file type");
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        }else{
            viewHolder.videoView.setVideoURI(Uri.parse(HttpProvider.getTopicDir() + topic.getTopic_file()));
            viewHolder.videoView.setMediaController(new MediaController(context));
//            viewHolder.videoView.start();
            viewHolder.videoView.setVisibility(View.VISIBLE);
        }

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

//        viewHolder.constraintComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, SubjectTopicsCommentActivity.class);
////                intent.putExtra("TOPIC_ID", topic.getTopic_id());
////                Debugger.logD("TOPIC_ID " + topic.getTopic_id());
////                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return topicArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage, ivYes,ivDelete;
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
            ivDelete = view.findViewById(R.id.iv_ListrowSubjectTopicsDelete);
            constraintYes = view.findViewById(R.id.constraint_ListrowSubjectTopicsYes);
            constraintComment = view.findViewById(R.id.constraint_ListrowSubjectTopicsComments);

            constraintYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 1);
                }
            });

            constraintComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 2);
                }
            });
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 3);
                }
            });
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 4);
                }
            });
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 4);
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
