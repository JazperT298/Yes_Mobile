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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.Conversation;
import com.theyestech.yes_mobile.utils.DateTimeHandler;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private boolean isFromSender;

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Conversation> conversationArrayList;
    private OnClickRecyclerView onClickRecyclerView;
    private Contact contact;
    private String photo_name;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public ChatConversationAdapter(Context context, ArrayList<Conversation> conversationArrayList, String photo_name) {
        this.context = context;
        this.conversationArrayList = conversationArrayList;
        this.layoutInflater = LayoutInflater.from(context);
        //this.contact = contact;
        this.photo_name = photo_name;

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = this.firebaseAuth.getCurrentUser();

        Debugger.printO(conversationArrayList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        if (i == MSG_TYPE_RIGHT) {
            View view = layoutInflater.inflate(R.layout.listrow_conversation_right, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.listrow_conversation_left, viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Conversation conversation = conversationArrayList.get(i);

        if (isFromSender){
            Glide.with(context)
                    .load(HttpProvider.getProfileDir() + photo_name)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                    .apply(GlideOptions.getOptions())
                    .into(viewHolder.ivProfile);
        }

        viewHolder.tvMessage.setText(conversation.getMessage());
        viewHolder.tvDateTime.setText(DateTimeHandler.getMessageDateDisplay(conversation.getMessageDateCreated()));

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
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (conversationArrayList.get(position).getSenderId().equals(firebaseUser.getUid())) {
            isFromSender = false;
            return MSG_TYPE_RIGHT;
        } else {
            isFromSender = true;
            return MSG_TYPE_LEFT;
        }
    }
}
