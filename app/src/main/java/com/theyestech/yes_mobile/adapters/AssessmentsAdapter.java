package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Assessment;

import java.util.ArrayList;

public class AssessmentsAdapter extends RecyclerView.Adapter<AssessmentsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Assessment> assessmentArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public AssessmentsAdapter(Context context, ArrayList<Assessment> quizArrayList) {
        this.context = context;
        this.assessmentArrayList = quizArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_assessment_students, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Assessment assessment = assessmentArrayList.get(i);

        viewHolder.tvName.setText(assessment.getFullname());
        viewHolder.tvScore.setText(String.format("%s/%s", assessment.getTotalScore(), assessment.getOverAllScore()));
        viewHolder.tvPercentage.setText(String.format("%s%%", assessment.getPercentage()));
    }

    @Override
    public int getItemCount() {
        return assessmentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvScore, tvPercentage;

        public ViewHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.student_name);
            tvScore = view.findViewById(R.id.quiz_score);
            tvPercentage = view.findViewById(R.id.total_percentage);
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }
}
