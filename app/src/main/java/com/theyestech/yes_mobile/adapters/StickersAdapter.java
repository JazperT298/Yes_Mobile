package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Stickers;

import java.util.ArrayList;

public class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Stickers> stickersArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public StickersAdapter(Context context, ArrayList<Stickers> stickersArrayList) {
        this.context = context;
        this.stickersArrayList = stickersArrayList;
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
        Stickers sticker = stickersArrayList.get(i);

        Glide.with(context)
                .load(HttpProvider.getStickerDir() + sticker.getName())
                .into(viewHolder.ivImage);
    }

    @Override
    public int getItemCount() {
        return stickersArrayList.size();
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
