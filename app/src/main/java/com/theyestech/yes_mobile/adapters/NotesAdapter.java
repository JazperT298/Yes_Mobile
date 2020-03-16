package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Note;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Note> noteArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public NotesAdapter(Context context, ArrayList<Note> noteArrayList) {
        this.context = context;
        this.noteArrayList = noteArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_notes, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Note note = noteArrayList.get(i);

        switch (note.getType()) {
            case "pdf":
                Glide.with(context)
                        .load(R.drawable.ic_note_pdf)
                        .into(viewHolder.ivImage);
                break;
            case "word":
                Glide.with(context)
                        .load(R.drawable.ic_note_word)
                        .into(viewHolder.ivImage);
                break;
            case "excel":
                Glide.with(context)
                        .load(R.drawable.ic_note_excel)
                        .into(viewHolder.ivImage);
                break;
            case "powerpoint":
                Glide.with(context)
                        .load(R.drawable.ic_note_powerpoint)
                        .into(viewHolder.ivImage);
                break;
        }

        viewHolder.tvTitle.setText(note.getTitle());
    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage, ivDelete;
        public TextView tvTitle;
        public Button btnOpen, btnDelete;

        public ViewHolder(View view) {
            super(view);

            ivImage = view.findViewById(R.id.iv_ListrowNotesImage);
            tvTitle = view.findViewById(R.id.tv_ListrowNotesTitle);
            ivDelete = view.findViewById(R.id.iv_ListrowNotesDelete);
            btnOpen = view.findViewById(R.id.btn_ListrowNotesOpen);

            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickRecyclerView != null)
                        onClickRecyclerView.onItemClick(v, getAdapterPosition(), 1);
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
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
