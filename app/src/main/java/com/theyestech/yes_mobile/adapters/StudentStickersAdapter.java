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
import com.theyestech.yes_mobile.models.Sticker;

import java.util.ArrayList;

public class StudentStickersAdapter extends RecyclerView.Adapter<StudentStickersAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Sticker> stickerArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public StudentStickersAdapter(Context context, ArrayList<Sticker> stickerArrayList) {
        this.context = context;
        this.stickerArrayList = stickerArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_student_stickers, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Sticker sticker = stickerArrayList.get(i);

        Glide.with(context)
                .load(HttpProvider.getStickerDir() + sticker.getName())
                .into(viewHolder.ivImage);
    }

    @Override
    public int getItemCount() {
        return stickerArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.iv_ListrowSubjectStickersImage);
        }
    }

    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }
}
