package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Quiz;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;

public class QuizzesAdapter extends RecyclerView.Adapter<QuizzesAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Quiz> quizArrayList;
    private OnClickRecyclerView onClickRecyclerView;
    private String role;

    public QuizzesAdapter(Context context, ArrayList<Quiz> quizArrayList, String role) {
        this.context = context;
        this.role = role;
        this.quizArrayList = quizArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subject_quizzes, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Quiz quiz = quizArrayList.get(i);

        viewHolder.tvTitle.setText(quiz.getQuiz_title());

        if (role.equals(UserRole.Educator()))
            viewHolder.btnTakeView.setText("Manage");
        else {
            if (quiz.getQuiz_done().contains("true"))
                viewHolder.btnTakeView.setText("View Answers");
        }

        Glide.with(context)
                .load(HttpProvider.getQuizDir() + quiz.getQuiz_image())
                .into(viewHolder.ivImage);

    }

    @Override
    public int getItemCount() {
        return quizArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvTitle;
        private Button btnTakeView;

        public ViewHolder(View view) {
            super(view);

            ivImage = view.findViewById(R.id.iv_ListrowQuizImage);
            tvTitle = view.findViewById(R.id.tv_ListrowQuizTitle);
            btnTakeView = view.findViewById(R.id.btn_ListrowQuizTakeView);

            btnTakeView.setOnClickListener(new View.OnClickListener() {
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
