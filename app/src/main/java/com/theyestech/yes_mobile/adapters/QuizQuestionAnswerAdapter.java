package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Question;

import java.util.ArrayList;

public class QuizQuestionAnswerAdapter extends RecyclerView.Adapter<QuizQuestionAnswerAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Question> questionArrayList;
    private String quizType;
    private OnClickRecyclerView onClickRecyclerView;

    public QuizQuestionAnswerAdapter(Context context, ArrayList<Question> questionArrayList, String quizType) {
        this.context = context;
        this.quizType = quizType;
        this.questionArrayList = questionArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public QuizQuestionAnswerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subject_quizzes_take_answers, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuizQuestionAnswerAdapter.ViewHolder viewHolder, int i) {
        Question question = questionArrayList.get(i);

        viewHolder.tvQuestion.setText(String.format("#%d %s", i + 1, question.getQuestion_value()));

        if (quizType.equals("Multiple")) {
            viewHolder.rb1.setText(question.getAnswers().get(0).getChoice_value());
            viewHolder.rb2.setText(question.getAnswers().get(1).getChoice_value());
            viewHolder.rb3.setText(question.getAnswers().get(2).getChoice_value());
            viewHolder.rb4.setText(question.getAnswers().get(3).getChoice_value());
        }

        for (Question.Answer answer : question.getAnswers()) {
            if (answer.getSelected_answer().equals("1")) {

            }
        }
    }

    @Override
    public int getItemCount() {
        return questionArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvQuestion, tvScore;
        private RadioButton rb1, rb2, rb3, rb4;

        public ViewHolder(View view) {
            super(view);

            tvQuestion = view.findViewById(R.id.tv_ListrowTakeAnswersQuestion);
            rb1 = view.findViewById(R.id.rb_ListrowTakeAnswers1);
            rb2 = view.findViewById(R.id.rb_ListrowTakeAnswers2);
            rb3 = view.findViewById(R.id.rb_ListrowTakeAnswers3);
            rb4 = view.findViewById(R.id.rb_ListrowTakeAnswers4);
        }
    }
}
