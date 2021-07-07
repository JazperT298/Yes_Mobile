package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Sticker;
import com.theyestech.yes_mobile.utils.Debugger;

import java.util.ArrayList;

public class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Sticker> stickerArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public StickersAdapter(Context context, ArrayList<Sticker> stickerArrayList) {
        this.context = context;
        this.stickerArrayList = stickerArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_subject_stickers, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Sticker sticker = stickerArrayList.get(i);

        Glide.with(context)
                .load("https://theyestech.com/img/" + sticker.getName())
                .into(viewHolder.ivImage);

        Debugger.logD("https://theyestech.com/img/" + sticker.getName());
    }

    @Override
    public int getItemCount() {
        return stickerArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage;
        public Button btnSend;

        public ViewHolder(View view) {
            super(view);

            ivImage = view.findViewById(R.id.iv_ListrowSubjectStickersImage);
            btnSend = view.findViewById(R.id.btn_ListrowSubjectStickersSend);

            btnSend.setOnClickListener(new View.OnClickListener() {
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
