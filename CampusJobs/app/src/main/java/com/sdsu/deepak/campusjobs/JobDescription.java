package com.sdsu.deepak.campusjobs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.sdsu.deepak.campusjobs.database.DBContract;
import com.sdsu.deepak.campusjobs.database.DatabaseHandler;
import com.sdsu.deepak.campusjobs.model.JobApplicant;
import com.sdsu.deepak.campusjobs.model.JobListingModel;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class JobDescription extends AppCompatActivity implements
        JobDetailsFragment.OnJobInteractionListener,
        JobListing.OnJobListingInteractionListener,
        GoogleApiClient.OnConnectionFailedListener{

    Toolbar toolbar;
    int jobsCount = 0;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    HashMap<String, JobApplicant> jobsInCart = new HashMap<>();
    HashMap<String, JobApplicant> appliedJobs = new HashMap<>();
    HashMap<String, JobListingModel> jobs = new HashMap<>();
    MenuItem cartItem;
    String currentUserId;
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_description);

        progressDialog = new ProgressDialog(this);
        Window progressDialogWindow = progressDialog.getWindow();
        if(progressDialogWindow!=null){
            progressDialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } else {
            Toast.makeText(this,"Somethings wrong!",Toast.LENGTH_SHORT).show();
        }
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        jobs = new HashMap<>();
        Intent intent = getIntent();
        jobs = (HashMap<String,JobListingModel>)intent.getSerializableExtra("list");
        if(jobs.isEmpty() || jobs == null){
            getJobs();
            progressDialog.setMessage("Fetching jobs");
            progressDialog.show();
        } else{
            showJobListingFragment();
        }
    }

    private void getJobs(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_JOB_POSTINGS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    JobListingModel job = dataSnapshotChild.getValue(JobListingModel.class);
                    job.setJobId(dataSnapshotChild.getKey());
                    Log.i("Home",job.getJobTitle());
                    jobs.put(job.getJobId(),job);
                }
                progressDialog.dismiss();
                showJobListingFragment();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();
        if(currentUser!=null) {
            currentUserId = currentUser.getUid();
            jobsInCart = new DatabaseHandler(this).getJobsInCart(DBContract.GET_JOBS_IN_CART + "'" + currentUserId + "'");
            jobsCount = jobsInCart.size();
            appliedJobs = new DatabaseHandler(this).getAppliedJobs(DBContract.GET_APPLIED_JOBS + "'" + currentUserId + "'");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jobsInCart = new DatabaseHandler(this).getJobsInCart(DBContract.GET_JOBS_IN_CART + "'" + currentUserId + "'");
        setNotifCount(jobsInCart.size());
    }

    private void showJobListingFragment(){
        // creating fragment instance
        JobListing jobListing = JobListing.newInstance(jobs);
        // replacing fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit);
        fragmentTransaction.replace(R.id.job_container,jobListing);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showJobDescription(JobListingModel job, boolean isApplied, boolean isInCart){
        // creating fragment instance
        JobDetailsFragment jobDetails = JobDetailsFragment.newInstance(job,isApplied,isInCart);
        // replacing fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_right_enter,R.anim.fragment_left_exit, R.anim.fragment_left_enter,R.anim.fragment_right_exit);
        fragmentTransaction.replace(R.id.job_container,jobDetails);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        cartItem = menu.findItem(R.id.item_samplebadge);
        ActionItemBadge.update(this, menu.findItem(R.id.item_samplebadge), FontAwesome.Icon.faw_shopping_cart, ActionItemBadge.BadgeStyles.RED, jobsCount);
        return true;
    }

    private void setNotifCount(int count){
        jobsCount = count;
        ActionItemBadge.update(cartItem,count);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    Intent parentIntent = NavUtils.getParentActivityIntent(this);
                    parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(parentIntent);
                    finish();
                }
                return true;
            case R.id.profile:
                Intent profile = new Intent(this,ProfileActivity.class);
                startActivity(profile);
                break;
            case R.id.item_samplebadge:
               // Toast.makeText(this,"Cart",Toast.LENGTH_SHORT).show();
                Intent cart = new Intent(this, JobCart.class);
                cart.putExtra("id",currentUser.getUid());
                startActivity(cart);
                break;
            case R.id.signout:
                for(UserInfo user : currentUser.getProviderData()){
                    if (user.getProviderId().equals("google.com")) {
                        signOut();
                    } else{
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(JobDescription.this, CloseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                break;
        }
        super.onOptionsItemSelected(item);
        return true;
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Intent intent = new Intent(JobDescription.this, CloseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }


    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            //Toast.makeText(getApplicationContext(),"Stack count"+fm.getBackStackEntryCount(),Toast.LENGTH_SHORT).show();
            fm.popBackStack();
        }
        else {
            //Log.i("MainActivity", "nothing on back stack, calling super");
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public void applyToJob(JobListingModel applyToJob) {

        // Create a job application
        JobApplicant application = new JobApplicant();
        application.appliedOn = String.valueOf(new Date().getTime());
        application.name = currentUser.getDisplayName();
        application.department = applyToJob.getDepartment();
        application.jobId = applyToJob.getJobId();
        application.jobTitle = applyToJob.getJobTitle();
        application.userId = currentUser.getUid();
        appliedJobs.put(application.jobId, application);
        // store the application for future reference
        HashMap<String, JobApplicant> app = new HashMap<>();
        app.put(application.jobId,application);

        DatabaseInteraction databaseInteraction = new DatabaseInteraction(app);
        databaseInteraction.execute(Constants.ADD_APPLIED_JOBS);
        // store the application in fire base
        applyJob(applyToJob);
    }


    private void applyJob(JobListingModel job){
        JobApplicant jobApplicant = new JobApplicant();
        jobApplicant.name = currentUser.getDisplayName();
        jobApplicant.appliedOn = String.valueOf(new Date().getTime());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_APPLIED_JOBS_LIST).child(job.getJobId()).child(currentUserId).setValue(jobApplicant);
    }


    @Override
    public void addToCart(JobListingModel toCart) {

        setNotifCount(++jobsCount);
        JobApplicant jobApplicant = new JobApplicant();
        jobApplicant.userId = currentUserId;
        jobApplicant.jobId = toCart.getJobId();
        jobApplicant.jobTitle = toCart.getJobTitle();
        jobApplicant.department = toCart.getDepartment();
        jobsInCart.put(jobApplicant.jobId, jobApplicant);
        HashMap<String,JobApplicant> application = new HashMap<>();
        application.put(jobApplicant.jobId,jobApplicant);
        DatabaseInteraction databaseInteraction = new DatabaseInteraction(application);
        databaseInteraction.execute(Constants.ADD_JOBS_TO_CART);
    }

    @Override
    public void dismissDescription() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onJobSelection(JobListingModel selectedJob) {
        boolean inCart = false, isApplied = false;
        if(!jobsInCart.isEmpty()){
            if(jobsInCart.containsKey(selectedJob.getJobId())){
                inCart = true;
            }
        }
        if(!appliedJobs.isEmpty()){
            if(appliedJobs.containsKey(selectedJob.getJobId())){
                isApplied = true;
            }
        }
        showJobDescription(selectedJob,isApplied,inCart);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private class DatabaseInteraction extends AsyncTask<String,Void,Void>{
        HashMap<String, JobApplicant> jobApplicants;
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        DatabaseInteraction( HashMap<String, JobApplicant>  job){
            this.jobApplicants = job;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String action = strings[0];
            switch (action){
                case Constants.ADD_APPLIED_JOBS:
                    databaseHandler.addToAppliedTable(jobApplicants);
                    break;
                case Constants.ADD_JOBS_TO_CART:
                    databaseHandler.addJobToCart(jobApplicants);
                    break;
            }
            return null;
        }
    }
}
