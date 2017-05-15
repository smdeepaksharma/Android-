package com.sdsu.deepak.campusjobs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class JobPostMenu extends Fragment {


    private OnJobPostMenuSelectionListener mListener;

    public JobPostMenu() {
        // Required empty public constructor
    }

    public static JobPostMenu newInstance() {
        JobPostMenu fragment = new JobPostMenu();
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
        View view = inflater.inflate(R.layout.fragment_job_post_menu, container, false);
        Button post = (Button) view.findViewById(R.id.post);
        Button manage = (Button) view.findViewById(R.id.manage);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.jobpost_dialog);
                dialog.setTitle(R.string.app_name);

                final EditText jobTitle = (EditText) dialog.findViewById(R.id.dialog_job_title);
                final EditText jobDescription = (EditText) dialog.findViewById(R.id.dialog_job_description);

                Button nextButton = (Button) dialog.findViewById(R.id.next);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(TextUtils.equals(jobTitle.getText().toString(),"") )
                        {
                            jobTitle.setError(getResources().getString(R.string.empty));
                        } else if(TextUtils.equals(jobDescription.getText().toString(),"")) {
                            jobDescription.setError(getResources().getString(R.string.empty));
                        }else{
                            mListener.startNewJobPost(jobTitle.getText().toString(),jobDescription.getText().toString());
                            dialog.dismiss();
                        }

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.managePostedJobs();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJobPostMenuSelectionListener) {
            mListener = (OnJobPostMenuSelectionListener) context;
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


    interface OnJobPostMenuSelectionListener {

        void startNewJobPost(String jobTitle, String jobDescription);
        void managePostedJobs();
    }
}
