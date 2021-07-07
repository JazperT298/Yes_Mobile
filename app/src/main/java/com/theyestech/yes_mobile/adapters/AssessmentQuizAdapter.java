package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Quiz;

import java.util.ArrayList;

public class AssessmentQuizAdapter extends RecyclerView.Adapter<AssessmentQuizAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Quiz> quizArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public AssessmentQuizAdapter(Context context, ArrayList<Quiz> quizArrayList) {
        this.context = context;
        this.quizArrayList = quizArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subject_assessment_quizzes, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Quiz quiz = quizArrayList.get(i);

        viewHolder.tvTitle.setText(quiz.getQuiz_title());

    }

    @Override
    public int getItemCount() {
        return quizArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tv_ListrowSubjectAssessmentQuizTitle);
            cardView = view.findViewById(R.id.cv_ListrowSubjectAssessment);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 1);
                }
            });
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }
}
