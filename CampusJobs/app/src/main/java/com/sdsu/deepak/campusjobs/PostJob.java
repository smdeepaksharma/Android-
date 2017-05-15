package com.sdsu.deepak.campusjobs;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.sdsu.deepak.campusjobs.database.DatabaseHandler;
import com.sdsu.deepak.campusjobs.model.JobListingModel;
import com.sdsu.deepak.campusjobs.model.JobPoster;
import java.util.Date;

public class PostJob extends AppCompatActivity implements JobPostMenu.OnJobPostMenuSelectionListener, JobPostFragment.OnJobPostCompletionListener,
        GoogleApiClient.OnConnectionFailedListener{

    MenuItem cartItem;
    FirebaseAuth auth;
    int jobsCount;
    FirebaseUser currentUser;
    Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        auth = FirebaseAuth.getInstance();
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();
        if(currentUser!=null){
            JobPostMenu jobPostMenu = new JobPostMenu();
            getSupportFragmentManager().beginTransaction().replace(R.id.jobpost_container,jobPostMenu).commit();
            jobsCount = new DatabaseHandler(this).getJobCartCount(currentUser.getUid());
            if(jobsCount > 0) {
                ActionItemBadge.update(cartItem,jobsCount);
            }
        } else {
            finish();
        }
    }

    @Override
    public void startNewJobPost(String jobTitle, String jobDescription) {
        JobPostFragment jobPostFragment = JobPostFragment.newInstance(jobTitle,jobDescription);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit);
        fragmentTransaction.replace(R.id.jobpost_container,jobPostFragment).commit();
    }

    @Override
    public void managePostedJobs() {
        Intent manageJobs = new Intent(this, ManageJobsActivity.class);
        startActivity(manageJobs);
        overridePendingTransition(R.anim.fragment_right_enter,R.anim.fragment_left_exit);
    }

    @Override
    public void onJobPostSubmit(JobListingModel job) {
        String key = String.valueOf(new Date().getTime());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_JOB_POSTINGS);
        //String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(job);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("JobPosters");
        db.child(currentUser.getUid()).child(key).
                setValue(new JobPoster(job.getJobId(),String.valueOf(new Date().getTime()),job.getJobTitle()));
        JobPostMenu jobPostMenu = new JobPostMenu();
        Toast.makeText(this,"Posted Successfully",Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit)
                .replace(R.id.jobpost_container,jobPostMenu).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        cartItem = menu.findItem(R.id.item_samplebadge);
        ActionItemBadge.update(this, menu.findItem(R.id.item_samplebadge), FontAwesome.Icon.faw_shopping_cart, ActionItemBadge.BadgeStyles.RED, jobsCount);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                Intent profile = new Intent(this,ProfileActivity.class);
                startActivity(profile);
                break;
            case R.id.item_samplebadge:
                Toast.makeText(this,"Cart",Toast.LENGTH_SHORT).show();
                Intent cart = new Intent(this, JobCart.class);
                cart.putExtra("id",currentUser.getUid());
                startActivity(cart);
                break;
            case R.id.signout:
            for(UserInfo user : currentUser.getProviderData()){
                if (user.getProviderId().equals("google.com")) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    Intent intent = new Intent(PostJob.this, CloseActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                } else{
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(PostJob.this, CloseActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Alert");
        dialog.setContentView(R.layout.alert_dialog);
        Button exit = (Button) dialog.findViewById(R.id.exit);
        Button cancel = (Button) dialog.findViewById(R.id.resume);
        dialog.show();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostJob.super.onBackPressed();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
