package com.sdsu.deepak.campusjobs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsu.deepak.campusjobs.adapters.JobListingAdapter;
import com.sdsu.deepak.campusjobs.model.JobListingModel;
import java.util.HashMap;
import java.util.Iterator;

public class JobListing extends Fragment implements AdapterView.OnItemClickListener {

    ListView jobListingView;
    JobListingAdapter jobListingAdapter;
    HashMap<String, JobListingModel> jobList = new HashMap<>();
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String JOB_LIST = "JobList";


    private OnJobListingInteractionListener mListener;

    public JobListing() {
        // Required empty public constructor
    }

    public static JobListing newInstance(HashMap<String,JobListingModel> jobList) {
        JobListing fragment = new JobListing();
        Bundle args = new Bundle();
        args.putSerializable(JOB_LIST,jobList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobList = (HashMap<String,JobListingModel>)(getArguments().getSerializable(JOB_LIST));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_listing, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_list);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        jobListingView = (ListView) view.findViewById(R.id.job_listing);
        jobListingView.setOnItemClickListener(this);

        if (jobList.isEmpty()) {
            getJobs();
        } else {
            populateListView();
        }
        Log.i("Job", "Size : " + jobList.size());
        return view;
    }

    private void getJobs(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_JOB_POSTINGS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    JobListingModel job = dataSnapshotChild.getValue(JobListingModel.class);
                    job.setJobId(dataSnapshotChild.getKey());
                    Log.i("Home",job.getJobTitle());
                    jobList.put(job.getJobId(),job);
                }
                populateListView();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void populateListView(){
        jobListingAdapter = new JobListingAdapter(this.getActivity(),jobList);
        jobListingView.setAdapter(jobListingAdapter);
        jobListingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJobListingInteractionListener) {
            mListener = (OnJobListingInteractionListener) context;
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
        JobListingModel selectedJob = (JobListingModel) adapterView.getItemAtPosition(i);
        mListener.onJobSelection(selectedJob);
    }

    interface OnJobListingInteractionListener {
        void onJobSelection(JobListingModel selectedJob);
    }
}
