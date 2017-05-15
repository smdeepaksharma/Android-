package com.sdsu.deepak.campusjobs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.sdsu.deepak.campusjobs.model.JobPoster;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ViewPostedJobsFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final String ARG_POSTED_JOBS = "PostedJobs";
    ArrayList<JobPoster> jobPosted;
    ListView postedJobsList;

    private OnManagePostingInteraction mListener;

    public ViewPostedJobsFragment() {
        // Required empty public constructor
    }


    public static ViewPostedJobsFragment newInstance(ArrayList<JobPoster> jobsPosted) {
        ViewPostedJobsFragment fragment = new ViewPostedJobsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSTED_JOBS, jobsPosted);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobPosted = (ArrayList<JobPoster>) getArguments().getSerializable(ARG_POSTED_JOBS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_posted_jobs, container, false);
        postedJobsList = (ListView) view.findViewById(R.id.postedJobsList);
        JobPostAdapter jobPostAdapter = new JobPostAdapter(getContext(),jobPosted);
        postedJobsList.setAdapter(jobPostAdapter);
        postedJobsList.setOnItemClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnManagePostingInteraction) {
            mListener = (OnManagePostingInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        JobPoster selectedJob = (JobPoster) adapterView.getItemAtPosition(i);
        mListener.viewStudentsApplications(selectedJob);
    }

     interface OnManagePostingInteraction {
        void viewStudentsApplications(JobPoster selectedJobPost);
    }

    private class JobPostAdapter extends ArrayAdapter<JobPoster>{
        ArrayList<JobPoster> postedJobs = new ArrayList<>();
        JobPostAdapter(@NonNull Context context, @NonNull ArrayList<JobPoster> objects) {
            super(context,R.layout.jobpost, objects);
            this.postedJobs = objects;
        }
        private class ViewHolder{
            TextView jobTitle;
            TextView postedOn;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view ;
            LayoutInflater inflater = ( LayoutInflater )getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView==null){
                view = inflater.inflate(R.layout.jobpost,null);
            } else {
                view = convertView;
            }

            ViewHolder vh = new ViewHolder();
            vh.jobTitle = (TextView)view.findViewById(R.id.jobTitle);
            vh.postedOn = (TextView) view.findViewById(R.id.department);
            JobPoster poster = getItem(position);
            if(poster!=null){
                vh.jobTitle.setText(poster.jobTitle);
                String postDate = getResources().getString(R.string.postedOn) + getDate(poster.postedOn);
                vh.postedOn.setText(postDate);
            }
            return view;
        }

         String getDate(String milliSeconds)
        {
            long ms = Long.parseLong(milliSeconds);
            // Create a DateFormatter object for displaying date in specified format.
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            // Create a calendar object that will convert the date and time value in milliseconds to date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ms);
            return formatter.format(calendar.getTime());
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return postedJobs.size();
        }

        @Nullable
        @Override
        public JobPoster getItem(int position) {
            return postedJobs.get(position);
        }
    }
}
