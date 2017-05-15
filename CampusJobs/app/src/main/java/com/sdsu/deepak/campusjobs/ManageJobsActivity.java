package com.sdsu.deepak.campusjobs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsu.deepak.campusjobs.model.Education;
import com.sdsu.deepak.campusjobs.model.Experience;
import com.sdsu.deepak.campusjobs.model.JobApplicant;
import com.sdsu.deepak.campusjobs.model.JobPoster;
import com.sdsu.deepak.campusjobs.model.StudentProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ManageJobsActivity extends AppCompatActivity implements ViewPostedJobsFragment.OnManagePostingInteraction,
        ViewJobApplicantsFragment.OnApplicantSelectedListener, ViewApplicantProfile.OnApplicantDecisionMade {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ArrayList<JobPoster> postedJobs;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_jobs);
        progressDialog = new ProgressDialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.manage));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        postedJobs = new ArrayList<>();
        getPostedJobs();

    }

    private void getPostedJobs(){
        progressDialog.setMessage("Fetching posted jobs");
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_JOB_POSTERS).child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    Log.i("Manage",dataSnapshot.toString());
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    JobPoster job = dataSnapshotChild.getValue(JobPoster.class);
                    job.jobId = dataSnapshotChild.getKey();
                    postedJobs.add(job);
                }
                if(postedJobs.isEmpty()){
                    progressDialog.dismiss();
                    final Dialog dialog = new Dialog(ManageJobsActivity.this);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog);
                    Button exit = (Button) dialog.findViewById(R.id.exit);
                    Button cancel = (Button) dialog.findViewById(R.id.resume);
                    TextView message = (TextView) dialog.findViewById(R.id.alterText);
                    message.setText(getResources().getString(R.string.noposts));
                    cancel.setVisibility(View.GONE);
                    dialog.show();
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                            dialog.dismiss();
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    ViewPostedJobsFragment viewPostedJobsFragment = ViewPostedJobsFragment.newInstance(postedJobs);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_right_enter,R.anim.fragment_left_exit, R.anim.fragment_left_enter,R.anim.fragment_right_exit)
                            .replace(R.id.manageJobsContainer,viewPostedJobsFragment).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void viewStudentsApplications(final JobPoster selectedJobPost) {

        progressDialog.setMessage("Fetching student applications");
        progressDialog.show();

        final ArrayList<JobApplicant> applicantsList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_APPLIED_JOBS_LIST).child(selectedJobPost.jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Manage",dataSnapshot.toString());
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    JobApplicant applicant = dataSnapshotChild.getValue(JobApplicant.class);
                    applicant.jobId = dataSnapshot.getKey();
                    applicant.userId = dataSnapshotChild.getKey();
                    applicantsList.add(applicant);
                }

                if(applicantsList.isEmpty()){
                    progressDialog.dismiss();
                    final Dialog dialog = new Dialog(ManageJobsActivity.this);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog);
                    Button exit = (Button) dialog.findViewById(R.id.exit);
                    Button cancel = (Button) dialog.findViewById(R.id.resume);
                    TextView message = (TextView) dialog.findViewById(R.id.alterText);
                    message.setText(getResources().getString(R.string.noApplications));
                    cancel.setVisibility(View.GONE);
                    dialog.show();
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getFragmentManager().popBackStackImmediate();
                            dialog.dismiss();
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    ViewJobApplicantsFragment viewJobApplicantsFragment = ViewJobApplicantsFragment.newInstance(selectedJobPost,applicantsList);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_right_enter,R.anim.fragment_left_exit, R.anim.fragment_left_enter,R.anim.fragment_right_exit)
                            .replace(R.id.manageJobsContainer,viewJobApplicantsFragment).addToBackStack(null)
                            .commit();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void viewApplicantProfile(final JobPoster jobPoster,JobApplicant jobApplicant) {
        progressDialog.setMessage("Fetching Student Profile");
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_STUDENT_LIST).child(jobApplicant.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StudentProfile studentProfile = dataSnapshot.getValue(StudentProfile.class);
                if(studentProfile.getSkills()==null){
                    studentProfile.setSkills(new ArrayList<String>());
                }
                if(studentProfile.getEmploymentDetails()==null) {
                    studentProfile.setEmploymentDetails(new HashMap<String, Experience>());
                }
                if(studentProfile.getEducationDetails()==null){
                    studentProfile.setEducationDetails(new HashMap<String, Education>());
                }
                showApplicantProfile(jobPoster,studentProfile);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void showApplicantProfile(JobPoster postedJob, StudentProfile studentProfile){
        progressDialog.dismiss();
        ViewApplicantProfile viewApplicantProfile = ViewApplicantProfile.newInstance(postedJob,studentProfile);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_right_enter,R.anim.fragment_left_exit, R.anim.fragment_left_enter,R.anim.fragment_right_exit)
                .replace(R.id.manageJobsContainer,viewApplicantProfile)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void notifyStudent(StudentProfile studentProfile) {

    }
}
