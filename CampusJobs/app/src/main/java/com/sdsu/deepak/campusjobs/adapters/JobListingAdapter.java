package com.sdsu.deepak.campusjobs.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.sdsu.deepak.campusjobs.R;
import com.sdsu.deepak.campusjobs.model.JobListingModel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Custom adapter for displaying job listings
 * Created by Deepak on 4/24/2017.
 */

public class JobListingAdapter extends BaseAdapter {

    private HashMap<String,JobListingModel> jobs;
    private HashMap<String,JobListingModel> filteredList;
    private Context context;
    private String[] mKeys;
    private static LayoutInflater inflater=null;

    // View lookup cache
    private static class ViewHolder {
        TextView jobTitle;
        TextView department;
    }

    public JobListingAdapter(@NonNull Context context, HashMap<String,JobListingModel> jobListing) {
        Log.i("Adapter","Constructor");
        this.context = context;
        this.jobs = jobListing;
        this.filteredList = jobListing;
        mKeys = jobListing.keySet().toArray(new String[jobListing.size()]);
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        JobListingModel job = getItem(position);
        ViewHolder holder=new ViewHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.jobpost, null);
        holder.jobTitle=(TextView) rowView.findViewById(R.id.jobTitle);
        holder.department=(TextView) rowView.findViewById(R.id.department);
        holder.jobTitle.setText(job.getJobTitle());
        holder.department.setText(job.getDepartment());
        return rowView;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Nullable
    @Override
    public JobListingModel getItem(int position) {
        return filteredList.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

   /* @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = jobs;
                } else {
                    HashMap<String, JobListingModel> filteredJobs = new HashMap<>();
                    for (JobListingModel job : jobs) {
                        if (job.getJobTitle().toLowerCase().contains(charString)
                                || job.getDepartment().toLowerCase().contains(charString)) {
                            Log.i("Filter",job.getJobTitle()+"found");
                            filteredJobs.add(job);
                        }
                    }
                    filteredList = filteredJobs;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (HashMap<String,JobListingModel>) filterResults.values;
                Log.i("Filter",filteredList.toString());
                notifyDataSetChanged();
            }
        };*/
    //}
}
