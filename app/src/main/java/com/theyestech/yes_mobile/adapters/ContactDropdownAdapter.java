package com.theyestech.yes_mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

//public class ContactDropdownAdapter extends RecyclerView.Adapter<ContactDropdownAdapter.ViewHolder> {
//
//    private Context context;
//    private LayoutInflater layoutInflater;
//    private ArrayList<Contact> contactArrayList;
//    private OnClickRecyclerView onClickRecyclerView;
//
//    public ContactDropdownAdapter(Context context, ArrayList<Contact> contactArrayList) {
//        this.context = context;
//        this.contactArrayList = contactArrayList;
//        this.layoutInflater = LayoutInflater.from(context);
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        layoutInflater = LayoutInflater.from(context);
//        View view = layoutInflater.inflate(R.layout.listrow_contacts_dropdown, viewGroup, false);
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        Contact contact = contactArrayList.get(i);
//
//        Glide.with(context)
//                .load(HttpProvider.getProfileDir() + contact.getPhotoName())
//                .apply(GlideOptions.getOptions())
//                .into(viewHolder.ivProfile);
//
//        viewHolder.tvFullname.setText(contact.getFullName());
//        viewHolder.tvEmail.setText(contact.getEmail());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return contactArrayList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView tvFullname, tvEmail;
//        public ImageView ivProfile;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvFullname = itemView.findViewById(R.id.tv_ListrowContactDropdownFullname);
//            tvEmail = itemView.findViewById(R.id.tv_ListrowContactDropdownEmail);
//            ivProfile = itemView.findViewById(R.id.iv_ListrowContactDropdownProfile);
//        }
//    }
//
//    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
//        this.onClickRecyclerView = onClickRecyclerView;
//    }
//}

public class ContactDropdownAdapter extends ArrayAdapter {

    private ArrayList<Contact> contactArrayList;
    private Context context;
    private int itemLayout;

    private ListFilter listFilter = new ListFilter();
    private ArrayList<Contact> allContactArraylist;

    public ContactDropdownAdapter(Context context, int resource, ArrayList<Contact> contactArrayList) {
        super(context, resource, contactArrayList);
        this.contactArrayList = contactArrayList;
        this.context = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return contactArrayList.size();
    }

    @Override
    public Contact getItem(int position) {
        return contactArrayList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        TextView tvFullname = view.findViewById(R.id.tv_ListrowContactDropdownFullname);
        TextView tvEmail = view.findViewById(R.id.tv_ListrowContactDropdownEmail);
        ImageView ivProfile = view.findViewById(R.id.iv_ListrowContactDropdownProfile);

        Contact contact = contactArrayList.get(position);

        Glide.with(context)
                .load(HttpProvider.getProfileDir() + contact.getPhotoName())
                .apply(GlideOptions.getOptions())
                .into(ivProfile);

        tvFullname.setText(contact.getFullName());
        tvEmail.setText(contact.getEmail());

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private final Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (allContactArraylist == null) {
                synchronized (lock) {
                    allContactArraylist = new ArrayList<Contact>(contactArrayList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = allContactArraylist;
                    results.count = allContactArraylist.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<Contact> matchValues = new ArrayList<Contact>();

                for (Contact contact : allContactArraylist) {
                    if (contact.getSearch().toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(contact);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                contactArrayList = (ArrayList<Contact>) results.values;
            } else {
                contactArrayList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
