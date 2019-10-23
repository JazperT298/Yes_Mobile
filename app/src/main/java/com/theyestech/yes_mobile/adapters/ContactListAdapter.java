package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.ContactList;


import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ContactList> mList;

    public ContactListAdapter(Context context, ArrayList<ContactList> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(R.layout.listrow_contact_list,viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListAdapter.ViewHolder viewHolder, int i) {
        final ContactList alltimeModel = mList.get(i);
        ImageView image = viewHolder.profile_image;
        TextView text1, text2, text3;

        text1 = viewHolder.username;
        text2 = viewHolder.section;

        image.setImageResource(alltimeModel.getImage());

        text1.setText(alltimeModel.getText1());
        text2.setText(alltimeModel.getText2());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile_image;
        TextView username, section;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = (ImageView) itemView.findViewById(R.id.iv_ProfileEducatorImage);
            username = (TextView) itemView.findViewById(R.id.tv_ProfileEducatorFullname);
            section = (TextView) itemView.findViewById(R.id.section);
        }
    }
}
