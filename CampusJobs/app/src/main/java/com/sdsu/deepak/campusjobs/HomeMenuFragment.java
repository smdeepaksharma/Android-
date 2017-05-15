package com.sdsu.deepak.campusjobs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeMenuFragment extends Fragment implements View.OnClickListener{

    TextView associatedStudents;
    TextView aztecShops;
    TextView workStudy;
    TextView titleQuery;
    TextView departmentQuery;
    Button searchButton;

    private OnHomeMenuFragmentListener mListener;

    public HomeMenuFragment() {
        // Required empty public constructor
    }

    public static HomeMenuFragment newInstance() {
        HomeMenuFragment fragment = new HomeMenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_menu, container, false);
        // binding views
        titleQuery = (TextView) view.findViewById(R.id.title_search);
        departmentQuery = (TextView) view.findViewById(R.id.deparment_search);
        searchButton = (Button) view.findViewById(R.id.searchButton);
        
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleQuery.getText().toString().trim();
                String department = departmentQuery.getText().toString().trim();
                department = department.replaceAll("\\s", "").toLowerCase();
                title = title.replaceAll("\\s","").toLowerCase();
                Bundle searchOptions = new Bundle();
                searchOptions.putString(Constants.BUNDLE_ARG_TITLE,title);
                searchOptions.putString(Constants.BUNDLE_ARG_DEPARTMENT,department);
                mListener.searchJobs(searchOptions);
            }
        });

        associatedStudents = (TextView) view.findViewById(R.id.associatedStudent);
        associatedStudents.setOnClickListener(this);
        aztecShops = (TextView) view.findViewById(R.id.aztezShops);
        aztecShops.setOnClickListener(this);
        workStudy = (TextView) view.findViewById(R.id.workStudy);
        workStudy.setOnClickListener(this);
        return  view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeMenuFragmentListener) {
            mListener = (OnHomeMenuFragmentListener) context;
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
    public void onClick(View view) {
        int itemId = view.getId();
        switch (itemId){
            case R.id.associatedStudent:
                // display Associated Students Union website
                mListener.getSuggestedJob(getResources().getString(R.string.associatedUrl));
                break;
            case R.id.aztezShops:
                // display aztec shops website
                mListener.getSuggestedJob(getResources().getString(R.string.aztecShopsUrl));
                break;
            case R.id.workStudy:
                // display work study jobs website
                mListener.getSuggestedJob(getResources().getString(R.string.workStudyUrl));
                break;
        }
    }

    interface OnHomeMenuFragmentListener {
        void getSuggestedJob(String department);
        void searchJobs(Bundle options);
    }
}
