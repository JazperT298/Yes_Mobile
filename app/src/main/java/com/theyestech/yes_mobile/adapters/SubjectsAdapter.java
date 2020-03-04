package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.utils.Debugger;

import java.util.ArrayList;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Subject> subjectArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public SubjectsAdapter(Context context, ArrayList<Subject> subjectArrayList) {
        this.context = context;
        this.subjectArrayList = subjectArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subjects, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Subject subject = subjectArrayList.get(i);

        viewHolder.tvName.setText(subject.getTitle());
        viewHolder.tvCode.setText(subject.getCode());
        viewHolder.tvSchoolYear.setText(subject.getSchool_year());
        viewHolder.tvStudents.setText(subject.getStud_count());


//        if (subject.getImage().length() > 5) {
//            Glide.with(context)
//                    .load(HttpProvider.getSubjectDir() + subject.getImage())
//                    .into(viewHolder.ivImage);
//        }
    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivDelete;
        private TextView tvName, tvCode, tvSchoolYear, tvStudents;
        private Button btnManage;

        public ViewHolder(View view) {
            super(view);

            ivDelete = view.findViewById(R.id.iv_ListrowSubjectsDelete);
            tvName = view.findViewById(R.id.tv_ListrowSubjectsName);
            tvCode = view.findViewById(R.id.tv_ListrowSubjectsSchoolCode);
            tvSchoolYear = view.findViewById(R.id.tv_ListrowSubjectsSchoolYear);
            tvStudents = view.findViewById(R.id.tv_ListrowSubjectsStudentsEnrolled);
            btnManage = view.findViewById(R.id.btn_ListrowSubjectsManage);

            btnManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition());
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }
}
