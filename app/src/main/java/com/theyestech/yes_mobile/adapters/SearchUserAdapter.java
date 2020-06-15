package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Note;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<UserStudent> userStudentArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public SearchUserAdapter(Context context, ArrayList<UserStudent> userStudentArrayList) {
        this.context = context;
        this.userStudentArrayList = userStudentArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_searched_user, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        UserStudent userStudent = userStudentArrayList.get(i);
        viewHolder.tv_UserFullname.setText(userStudent.getFirsname() + " " + userStudent.getMiddlename() + " " + userStudent.getLastname());

        if (userStudent.getUser_role().contains("1")){
            viewHolder.tv_UserEducator.setText("Educator");
        }else{
            viewHolder.tv_UserEducator.setText("Student");
        }
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + userStudent.getImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                .apply(GlideOptions.getOptions())
                .into(viewHolder.iv_UserImage);
    }

    @Override
    public int getItemCount() {
        return userStudentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_UserImage;
        public TextView tv_UserFullname, tv_UserEducator;
        public ConstraintLayout constraint;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_UserImage = itemView.findViewById(R.id.iv_UserImage);
            tv_UserFullname = itemView.findViewById(R.id.tv_UserFullname);
            tv_UserEducator = itemView.findViewById(R.id.tv_UserEducator);
            constraint = itemView.findViewById(R.id.constraint);

            constraint.setOnClickListener(new View.OnClickListener() {
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
