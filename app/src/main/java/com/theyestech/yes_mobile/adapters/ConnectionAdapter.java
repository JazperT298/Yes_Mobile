package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.theyestech.yes_mobile.models.Note;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<UserEducator> userEducatorArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public ConnectionAdapter(Context context, ArrayList<UserEducator> userEducatorArrayList) {
        this.context = context;
        this.userEducatorArrayList = userEducatorArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_connection, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        UserEducator userEducator = userEducatorArrayList.get(i);
        viewHolder.tv_ConnectionFullname.setText(userEducator.getFirsname() + " " + userEducator.getLastname());

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + userEducator.getImage())
                .apply(GlideOptions.getOptions())
                .into(viewHolder.iv_ConnectionProfile);
    }

    @Override
    public int getItemCount() {
        return userEducatorArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_ConnectionProfile;
        private TextView tv_ConnectionFullname;
        private ConstraintLayout constraint_Connection;
        public ViewHolder(@NonNull View view) {
            super(view);
            iv_ConnectionProfile = view.findViewById(R.id.iv_ConnectionProfile);
            tv_ConnectionFullname = view.findViewById(R.id.tv_ConnectionFullname);
            constraint_Connection = view.findViewById(R.id.constraint_Connection);

            constraint_Connection.setOnClickListener(new View.OnClickListener() {
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
