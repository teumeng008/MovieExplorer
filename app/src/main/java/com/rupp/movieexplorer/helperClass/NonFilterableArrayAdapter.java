package com.rupp.movieexplorer.helperClass;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.List;

public class NonFilterableArrayAdapter extends ArrayAdapter<String> {

    private final List<String> items;

    public NonFilterableArrayAdapter(Context context, int layout, List<String> items) {
        super(context, layout, items);
        this.items = items;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = items;
                results.count = items.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}