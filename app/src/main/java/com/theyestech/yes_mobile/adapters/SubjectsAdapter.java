package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Subject;

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
    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvName;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);

            ivImage = view.findViewById(R.id.iv_ListrowSubjectsImage);
            tvName = view.findViewById(R.id.tv_ListrowSubjectsName);
            cardView = view.findViewById(R.id.cv_ListrowSubjects);

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