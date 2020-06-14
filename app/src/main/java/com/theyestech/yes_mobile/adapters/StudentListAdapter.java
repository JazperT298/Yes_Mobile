package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Student> studentArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public StudentListAdapter(Context context, ArrayList<Student> studentArrayList) {
        this.context = context;
        this.studentArrayList = studentArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_student_list, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Student student = studentArrayList.get(i);

        viewHolder.tvName.setText(student.getUser_firstname() + " " + student.getUser_lastname());

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
        private TextView tvName;
        private CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_ListrowSubjectStudentsProfile);
            tvName = itemView.findViewById(R.id.tv_ListrowSubjectStudentsFullname);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (onClickRecyclerView != null)
//                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 1);
                    if(isChecked){
                        onClickRecyclerView.onItemClick(buttonView, getAdapterPosition(), 1);
                    }
                }
            });
        }
    }
    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }
}