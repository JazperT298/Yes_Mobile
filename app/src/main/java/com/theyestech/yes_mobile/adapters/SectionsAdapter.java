package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Section;

import java.util.ArrayList;

public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Section> sectionArrayList;
    private OnClickRecyclerView onClickRecyclerView;

    public SectionsAdapter(Context context, ArrayList<Section> sectionArrayList) {
        this.context = context;
        this.sectionArrayList = sectionArrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.listrow_sections, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Section section = sectionArrayList.get(i);

        viewHolder.tvName.setText(section.getName());
        viewHolder.tvSchoolYear.setText(section.getSchool_year());
    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tvName, tvSchoolYear;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);

            tvSchoolYear = view.findViewById(R.id.tv_ListrowSectionSchoolYear);
            tvName = view.findViewById(R.id.tv_ListrowSectionName);
            cardView = view.findViewById(R.id.cv_ListrowSections);

            cardView.setOnClickListener(new View.OnClickListener() {
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
