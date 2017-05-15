package com.sdsu.deepak.campusjobs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdsu.deepak.campusjobs.model.JobListingModel;


public class JobPostFragment extends Fragment {

    private static final String ARG_TITLE = "Title";
    private static final String ARG_DEPARTMENT = "Department";
    private String title;
    private String department;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    RadioGroup employmentTypeGroup ;
    RadioButton fullTimeButton ;
    RadioButton partTimeButton ;
    RadioGroup workPermit ;
    RadioButton workStudy ;
    RadioButton nonWorkStudy ;
    EditText jobDescription ;
    EditText skills ;
    EditText pay ;

    private OnJobPostCompletionListener mListener;

    public JobPostFragment() {
        // Required empty public constructor
    }

    public static JobPostFragment newInstance(String param1, String param2) {
        JobPostFragment fragment = new JobPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, param1);
        args.putString(ARG_DEPARTMENT, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            department = getArguments().getString(ARG_DEPARTMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_job_post, container, false);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        TextView titleView = (TextView) v.findViewById(R.id.jobTitle);
        titleView.setText(title);

        TextView departmentView = (TextView) v.findViewById(R.id.department);
        departmentView.setText(department);

        employmentTypeGroup = (RadioGroup) v.findViewById(R.id.employmentType);
        fullTimeButton = (RadioButton) v.findViewById(R.id.fullTime);
         partTimeButton = (RadioButton) v.findViewById(R.id.partTime);

        workPermit = (RadioGroup) v.findViewById(R.id.permit);
        workStudy = (RadioButton) v.findViewById(R.id.workStudy);
        nonWorkStudy = (RadioButton) v.findViewById(R.id.nonWorkStudy);

        jobDescription = (EditText) v.findViewById(R.id.jobDescription);
        skills = (EditText) v.findViewById(R.id.requiredSkills);
        pay = (EditText) v.findViewById(R.id.pay);

        Button submitButton = (Button) v.findViewById(R.id.submit_job);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    JobListingModel job= new JobListingModel();
                    job.setContactPersonEmail(currentUser.getEmail());
                    job.setContactPersonName(currentUser.getDisplayName());
                    job.setJobdescription(jobDescription.getText().toString());
                    job.setPay(pay.getText().toString());
                    job.setJobTitle(title);
                    job.setDepartment(department);
                    job.setSkillsRequired(skills.getText().toString());

                    // prepare sort version
                    String departmentSortVersion = department.replaceAll("\\s", "").toLowerCase();
                    String titleSortVersion = title.replaceAll("\\s","").toLowerCase();

                    job.setDepartmentSortVersion(departmentSortVersion);
                    job.setJobTitleSortVersion(titleSortVersion);
                    mListener.onJobPostSubmit(job);
                }
            }
        });
        return v;
    }

    private boolean validateForm(){
        String empty = "";
        if(employmentTypeGroup.getCheckedRadioButtonId()!=R.id.fullTime
                && employmentTypeGroup.getCheckedRadioButtonId()!=R.id.partTime){
            employmentTypeGroup.setBackgroundColor(Color.RED);
            return false;
        }
        if(workPermit.getCheckedRadioButtonId()!=R.id.workStudy
                && workPermit.getCheckedRadioButtonId()!=R.id.nonWorkStudy){
            workPermit.setBackgroundColor(Color.RED);
            return false;
        }
        if(TextUtils.equals(jobDescription.getText().toString(),empty)){
            jobDescription.setError(getResources().getString(R.string.empty));
            return false;
        }
        if(TextUtils.equals(skills.getText().toString(),empty)){
            skills.setError(getResources().getString(R.string.empty));
            return false;
        }
        if(TextUtils.equals(pay.getText().toString(),empty)){
            pay.setError(getResources().getString(R.string.empty));
            return false;
        }
        return true;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJobPostCompletionListener) {
            mListener = (OnJobPostCompletionListener) context;
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

    interface OnJobPostCompletionListener {
        void onJobPostSubmit(JobListingModel job);
    }
}
