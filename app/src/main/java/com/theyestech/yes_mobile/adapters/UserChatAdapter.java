package com.theyestech.yes_mobile.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    private View view;
    private Context context;
    private ArrayList<Contact> contactArrayList;
    private boolean ischat;
    private LayoutInflater layoutInflater;
    private OnClickRecyclerView onClickRecyclerView;

    String theLastMessage;

    public UserChatAdapter(Context context, ArrayList<Contact> contactArrayList, boolean ischat){
        this.contactArrayList = contactArrayList;
        this.context = context;
        this.ischat = ischat;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.user_chat_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Contact contact = contactArrayList.get(i);

        if (contact.getFullName().isEmpty() || contact.getFullName().equals("  ")){
            if (contact.getEmail().length() <= 6){
                viewHolder.name.setText(contact.getEmail());
            }else{
                viewHolder.name.setText(contact.getEmail().substring(0, 6) + "." );
            }
        }else{
            if (contact.getFullName().length() <= 6){
                viewHolder.name.setText(contact.getFullName());
            }else{
                viewHolder.name.setText(contact.getFullName().substring(0, 6) + "." );
            }
        }
        Glide.with(context)
                .load(HttpProvider.getProfileDir()  + contact.getPhotoName())
                .apply(GlideOptions.getOptions())
                .into(viewHolder.profile_image);
        if (contact.getStatus().equals("online")){
            viewHolder.img_on.setVisibility(View.VISIBLE);
            viewHolder.img_off.setVisibility(View.GONE);
        } else {
            viewHolder.img_on.setVisibility(View.GONE);
            viewHolder.img_off.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);

            profile_image.setOnClickListener(new View.OnClickListener() {
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
