package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Question;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Question> questionArrayList;
    private OnClickRecyclerView onClickRecyclerView;
    private String role;

    public QuestionsAdapter(Context context, ArrayList<Question> questionArrayList, String role) {
        this.context = context;
        this.role = role;
        this.questionArrayList = questionArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subject_quiz_question, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Question question = questionArrayList.get(i);

        viewHolder.tvQuestion.setText(String.format("Question: %s", question.getQuestion_value()));

        if (role.equals(UserRole.Educator()))
            viewHolder.tvAnswer.setText(String.format("Answer: %s", question.getQuestion_correct_answer()));
        else
            viewHolder.tvAnswer.setText("Not Answered");
    }

    @Override
    public int getItemCount() {
        return questionArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvQuestion, tvAnswer;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);

            tvQuestion = view.findViewById(R.id.tv_SubjectQuizQuestion);
            tvAnswer = view.findViewById(R.id.tv_SubjectQuizQuestionAnswer);
            cardView = view.findViewById(R.id.cv_SubjectQuizQuestions);

            cardView.setOnClickListener(new View.OnClickListener() {
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
