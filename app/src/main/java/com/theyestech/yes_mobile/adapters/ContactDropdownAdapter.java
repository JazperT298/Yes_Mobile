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
import com.bumptech.glide.request.RequestOptions;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.util.ArrayList;

public class ContactDropdownAdapter extends ArrayAdapter {

    private ArrayList<Contact> contactArrayList;
    private Context context;
    private int itemLayout;
    private ListFilter listFilter = new ListFilter();
    private ArrayList<Contact> allContactArrayList;

    public ContactDropdownAdapter(Context context, int resource, ArrayList<Contact> contactArrayList) {
        super(context, resource, contactArrayList);
        this.contactArrayList = contactArrayList;
        this.context = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        assert contactArrayList != null;
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
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
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
            if (allContactArrayList == null) {
                synchronized (lock) {
                    allContactArrayList = new ArrayList<>(contactArrayList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = allContactArrayList;
                    results.count = allContactArrayList.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<Contact> matchValues = new ArrayList<>();

                for (Contact contact : allContactArrayList) {
                    if (contact.getFullName().toLowerCase().contains(searchStrLowerCase)) {
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
