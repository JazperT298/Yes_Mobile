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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.Conversation;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {

    private static final int MSG_TYPE_SENDER = 0;
    private static final int MSG_TYPE_RECEIVER = 1;

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Conversation> conversationArrayList;
    private OnClickRecyclerView onClickRecyclerView;
    private Contact contact;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public ChatConversationAdapter(Context context, ArrayList<Conversation> conversationArrayList, Contact contact) {
        this.context = context;
        this.conversationArrayList = conversationArrayList;
        this.layoutInflater = LayoutInflater.from(context);
        this.contact = contact;

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = this.firebaseAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view;
        if (i == MSG_TYPE_SENDER)
            view = layoutInflater.inflate(R.layout.chat_item_left, viewGroup, false);
        else
            view = layoutInflater.inflate(R.layout.chat_item_right, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Conversation conversation = conversationArrayList.get(i);

        DateFormat dateFormat = new SimpleDateFormat("MMM-dd-yy | hh:mm:ss aa");
        String date = dateFormat.format(conversation.getMessageDateCreated());

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + contact.getPhotoName())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                .apply(GlideOptions.getOptions())
                .into(viewHolder.ivProfile);

        viewHolder.tvMessage.setText(conversation.getMessage());
        viewHolder.tvDateTime.setText(date);

    }

    @Override
    public int getItemCount() {
        return conversationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvMessage, tvDateTime;

        public ViewHolder(View view) {
            super(view);

            ivProfile = view.findViewById(R.id.iv_ChatConversationLeftProfile);
            tvMessage = view.findViewById(R.id.tv_ChatConversationMessage);
            tvDateTime = view.findViewById(R.id.tv_ChatConversationDateTime);


//            tvMessage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onClickRecyclerView != null)
//                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 1);
//                }
//            });

            tvMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvDateTime.getVisibility() == View.GONE)
                        tvDateTime.setVisibility(View.VISIBLE);
                    else
                        tvDateTime.setVisibility(View.GONE);
                }
            });
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (conversationArrayList.get(position).getSenderId().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RECEIVER;
        } else {
            return MSG_TYPE_SENDER;
        }
    }
}
