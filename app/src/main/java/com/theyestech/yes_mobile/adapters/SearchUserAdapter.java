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
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<UserEducator> userEducatorArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public SearchUserAdapter(Context context, ArrayList<UserEducator> userEducatorArrayList) {
        this.context = context;
        this.userEducatorArrayList = userEducatorArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.dialog_search_user, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        UserEducator userEducator = userEducatorArrayList.get(i);
        viewHolder.tv_UserFullname.setText(userEducator.getFirsname() + " " + userEducator.getMiddlename() + " " + userEducator.getLastname());

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + userEducator.getImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                .apply(GlideOptions.getOptions())
                .into(viewHolder.iv_UserImage);
    }

    @Override
    public int getItemCount() {
        return userEducatorArrayList.size();
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
