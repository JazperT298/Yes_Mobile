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
import com.theyestech.yes_mobile.utils.Debugger;

import java.util.ArrayList;

public class SubjectSearchAdapter extends RecyclerView.Adapter<SubjectSearchAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Subject> subjectArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public SubjectSearchAdapter(Context context, ArrayList<Subject> subjectArrayList) {
        this.context = context;
        this.subjectArrayList = subjectArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subject_search, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectSearchAdapter.ViewHolder viewHolder, int i) {
        Subject subject = subjectArrayList.get(i);

        viewHolder.tvName.setText(String.format("%s %s", subject.getUser_firstname(), subject.getUser_lastname()));
        viewHolder.tvSchoolYear.setText(subject.getSchool_year());
        viewHolder.tvSubject.setText(subject.getTitle());
        Debugger.logD("title " + subject.getTitle());

    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvSubject, tvSchoolYear;
        private Button btnSend;

        public ViewHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.tv_ListrowSubjectSearchEducatorName);
            tvSubject = view.findViewById(R.id.tv_ListrowSubjectsSearchSubjectName);
            tvSchoolYear = view.findViewById(R.id.tv_ListrowSubjectsSearchSchoolYear);
            btnSend = view.findViewById(R.id.btn_ListrowSubjectsSearchSend);

            btnSend.setOnClickListener(new View.OnClickListener() {
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
