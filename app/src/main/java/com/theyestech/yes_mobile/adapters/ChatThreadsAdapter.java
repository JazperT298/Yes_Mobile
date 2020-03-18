package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatThreadsAdapter extends RecyclerView.Adapter<ChatThreadsAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<ChatThread> threadArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public ChatThreadsAdapter(Context context, ArrayList<ChatThread> threadArrayList) {
        this.context = context;
        this.threadArrayList = threadArrayList;
        this.layoutInflater = LayoutInflater.from(context);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = this.firebaseAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_chat_threads, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChatThread thread = threadArrayList.get(i);
        Contact contact = thread.contact;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        String date = simpleDateFormat.format(thread.getLastMessageDateCreated());

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + contact.getPhotoName())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                .apply(GlideOptions.getOptions())
                .into(viewHolder.ivProfile);

        viewHolder.tvName.setText(contact.getFullName().equals("") ? contact.getEmail() : contact.getFullName());
        viewHolder.tvLastMessage.setText(thread.getLastMessage());
        viewHolder.tvDate.setText(date);

        if (!contact.getStatus().equals("online")){
            viewHolder.ivIsOnline.setVisibility(View.GONE);
        }

        if (!thread.getSenderId().equals(firebaseUser.getUid())){
            if (!thread.isSeen){
                viewHolder.tvName.setTypeface(null, Typeface.BOLD);
                viewHolder.tvLastMessage.setTypeface(null, Typeface.BOLD);
                viewHolder.tvDate.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    @Override
    public int getItemCount() {
        return threadArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile, ivIsOnline;
        TextView tvName, tvLastMessage, tvDate;
        ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            super(view);

            ivProfile = view.findViewById(R.id.iv_ListrowChatThreadProfile);
            ivIsOnline = view.findViewById(R.id.iv_ListrowChatThreadIsOnline);
            tvName = view.findViewById(R.id.tv_ListrowChatThreadName);
            tvLastMessage = view.findViewById(R.id.tv_ListrowChatThreadLastMessage);
            tvDate = view.findViewById(R.id.tv_ListrowChatThreadDate);
            constraintLayout = view.findViewById(R.id.constraint_ListrowChatThread);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
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
