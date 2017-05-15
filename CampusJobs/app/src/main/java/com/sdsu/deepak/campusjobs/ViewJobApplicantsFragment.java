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
import com.sdsu.deepak.campusjobs.model.JobApplicant;
import com.sdsu.deepak.campusjobs.model.JobPoster;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class ViewJobApplicantsFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final String ARG_JOB_APPLICANTS_LIST = "JobApplicants";
    private static final String ARG_JOB_POST = "JobPost";

    private ArrayList<JobApplicant> jobApplicants;
    ListView jobApplicantsListView;
    TextView applicantsCount;
    JobPoster jobPost;
    TextView jobPostTitle;

    private OnApplicantSelectedListener mListener;

    public ViewJobApplicantsFragment() {
        // Required empty public constructor
    }

    public static ViewJobApplicantsFragment newInstance(JobPoster job, ArrayList<JobApplicant> jobApplicants) {
        ViewJobApplicantsFragment fragment = new ViewJobApplicantsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_JOB_APPLICANTS_LIST, jobApplicants);
        args.putSerializable(ARG_JOB_POST,job);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobApplicants = (ArrayList<JobApplicant>) getArguments().getSerializable(ARG_JOB_APPLICANTS_LIST);
            jobPost = (JobPoster) getArguments().getSerializable(ARG_JOB_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_job_applicants, container, false);
        jobApplicantsListView = (ListView) v.findViewById(R.id.jobApplicantsList);
        JobApplicantAdapter jobApplicantAdapter = new JobApplicantAdapter(getContext(),jobApplicants);
        jobApplicantsListView.setAdapter(jobApplicantAdapter);
        jobApplicantsListView.setOnItemClickListener(this);
        jobPostTitle = (TextView) v.findViewById(R.id.jobTitle);
        jobPostTitle.setText(jobPost.jobTitle);
        applicantsCount = (TextView) v.findViewById(R.id.applicantsCount);
        String appsCount = jobApplicants.size() + " Applications Found";
        applicantsCount.setText(appsCount);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnApplicantSelectedListener) {
            mListener = (OnApplicantSelectedListener) context;
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
        JobApplicant selectedApplicant = (JobApplicant) adapterView.getItemAtPosition(i);
        mListener.viewApplicantProfile(jobPost,selectedApplicant);

    }

    interface OnApplicantSelectedListener {
        void viewApplicantProfile(JobPoster jobPoster,JobApplicant jobApplicant);
    }

    private class JobApplicantAdapter extends ArrayAdapter<JobApplicant> {

        ArrayList<JobApplicant> postedJobs = new ArrayList<>();

        JobApplicantAdapter(@NonNull Context context, @NonNull ArrayList<JobApplicant> objects) {
            super(context,R.layout.jobpost, objects);
            this.postedJobs = objects;
        }
        private class ViewHolder{
            TextView studentName;
            TextView appliedOn;
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
            vh.appliedOn = (TextView)view.findViewById(R.id.department);
            vh.studentName = (TextView) view.findViewById(R.id.jobTitle);
            JobApplicant jobApplicant = getItem(position);
            if(jobApplicant!=null){
                vh.studentName.setText(jobApplicant.name);
                String postDate = getResources().getString(R.string.appliedOn) + getDate(jobApplicant.appliedOn);
                vh.appliedOn.setText(postDate);
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
        public JobApplicant getItem(int position) {
            return postedJobs.get(position);
        }
    }
}
