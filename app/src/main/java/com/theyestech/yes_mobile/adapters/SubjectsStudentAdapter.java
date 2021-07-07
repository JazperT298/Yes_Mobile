package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Subject;

import java.util.ArrayList;

public class SubjectsStudentAdapter extends RecyclerView.Adapter<SubjectsStudentAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Subject> subjectArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public SubjectsStudentAdapter(Context context, ArrayList<Subject> subjectArrayList) {
        this.context = context;
        this.subjectArrayList = subjectArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subjects_student, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Subject subject = subjectArrayList.get(i);

        viewHolder.tvSubjectName.setText(subject.getTitle());
        viewHolder.tvSchoolYear.setText(subject.getSchool_year());
        viewHolder.tvFullname.setText(String.format("%s %s", subject.getUser_firstname(), subject.getUser_lastname()));
    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSubjectName, tvSchoolYear, tvFullname;
        private Button btnExplore;

        public ViewHolder(View view) {
            super(view);

            tvSubjectName = view.findViewById(R.id.tv_ListrowSubjectsNameStudent);
            tvSchoolYear = view.findViewById(R.id.tv_ListrowSubjectsSchoolYearStudent);
            tvFullname = view.findViewById(R.id.tv_ListrowSubjectsFullNameStudent);
            btnExplore = view.findViewById(R.id.btn_ListrowSubjectsExploreStudent);

            btnExplore.setOnClickListener(new View.OnClickListener() {
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
