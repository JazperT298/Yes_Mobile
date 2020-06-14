package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class StudentRequestAdapter extends RecyclerView.Adapter<StudentRequestAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Student> studentArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public StudentRequestAdapter(Context context, ArrayList<Student> studentArrayList) {
        this.context = context;
        this.studentArrayList = studentArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_student_request, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentRequestAdapter.ViewHolder viewHolder, int i) {
        Student student = studentArrayList.get(i);

        viewHolder.tvName.setText(student.getUser_firstname() + " " + student.getUser_lastname());
        viewHolder.tvEmail.setText(student.getUser_email_address());

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + student.getUser_image())
                .apply(GlideOptions.getOptions())
                .into(viewHolder.ivImage);
    }

    @Override
    public int getItemCount() {
        return studentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;
        private TextView tvName, tvEmail, tvAccept,tvDecline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_ListrowSubjectStudentsProfile);
            tvName = itemView.findViewById(R.id.tv_ListrowSubjectStudentsFullname);
            tvEmail = itemView.findViewById(R.id.tv_ListrowSubjectStudentsEmail);
            tvAccept = itemView.findViewById(R.id.tv_AcceptRequest);
            tvDecline = itemView.findViewById(R.id.tv_DeclineRequest);
            tvDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 1);
                }
            });
            tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 2);
                }
            });
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }
}
