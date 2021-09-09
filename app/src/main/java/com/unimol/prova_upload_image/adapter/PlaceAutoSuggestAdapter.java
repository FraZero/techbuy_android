package com.unimol.prova_upload_image.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.unimol.prova_upload_image.models.PlaceApi;

import java.util.ArrayList;

public class PlaceAutoSuggestAdapter extends ArrayAdapter implements Filterable {

    ArrayList<String> result;
    Context context;
    int resource;

    PlaceApi placeApi = new PlaceApi();

    public PlaceAutoSuggestAdapter(Context context, int resId) {
        super(context,resId);
        this.context = context;
        this.resource = resId;
    }

    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public String getItem(int pos){
        return result.get(pos);
    }

    @Override
    public Filter getFilter(){
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint!=null){
                    result = placeApi.autoComplete(constraint.toString());
                    filterResults.values = result;
                    filterResults.count = result.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count >0){
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

        };
        return filter;
    }

}
