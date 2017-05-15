package com.sdsu.deepak.campusjobs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.sdsu.deepak.campusjobs.database.DatabaseHandler;
import com.sdsu.deepak.campusjobs.model.JobListingModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *  HomeActivity hosts the home menu fragment. Users interactions with the menu
 *  are handled in this activity.
 */
public class HomeActivity extends AppCompatActivity implements
        HomeMenuFragment.OnHomeMenuFragmentListener,
        GoogleApiClient.OnConnectionFailedListener{

    BottomNavigationView bottomNavigation;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    MenuItem cartItem;
    int jobsCount;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<JobListingModel> jobs;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        Window progressDialogWindow = progressDialog.getWindow();
        if(progressDialogWindow!=null){
            progressDialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
        }

        // show home screen menu
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit)
                .replace(R.id.main_container,new HomeMenuFragment())
                .commit();
        jobs = new ArrayList<>();

        // binding and initializing bottom navigation bar
        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigation.inflateMenu(R.menu.bottom_nav_menu);
        fragmentManager = getSupportFragmentManager();
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.home:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit)
                                .replace(R.id.main_container,new HomeMenuFragment())
                                .commit();
                        break;
                    case R.id.jobs:
                        Intent jobIntent = new Intent(HomeActivity.this, JobDescription.class);
                        jobIntent.putExtra("list",new HashMap<String,JobListingModel>());
                        startActivity(jobIntent);
                        overridePendingTransition(R.anim.fragment_right_enter,R.anim.fragment_left_exit);
                        break;
                    case R.id.postJob:
                        Intent postJob = new Intent(HomeActivity.this, PostJob.class);
                        startActivity(postJob);
                        overridePendingTransition(R.anim.top_enter,R.anim.bottom_exit);
                        break;
                }
                return true;
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();
        // check if user is authenticated and logged in
        currentUser = auth.getCurrentUser();
        if(currentUser!=null){
            // update the job cart count
            jobsCount = new DatabaseHandler(this).getJobCartCount(currentUser.getUid());
            if(jobsCount >= 0) {
                ActionItemBadge.update(cartItem,jobsCount);
            }
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Intent intent = new Intent(HomeActivity.this, CloseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
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
                 Intent profile = new Intent(this, ProfileActivity.class);
                 startActivity(profile);
                 break;
             case R.id.item_samplebadge:
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
                         Intent intent = new Intent(HomeActivity.this, CloseActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(intent);
                     }
                 }
                 break;
         }
         super.onOptionsItemSelected(item);
         return true;
     }


     @Override
     public void onBackPressed() {
         super.onBackPressed(); finish();
     }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void getSuggestedJob(String department) {
        Intent webIntent = new Intent(HomeActivity.this, WebActivity.class);
        webIntent.putExtra("url",department);
        startActivity(webIntent);
    }

    /**
     * Search Jobs() is used to search user queried job on fire base
     * @param options:
     */
    @Override
    public void searchJobs(Bundle options) {
        progressDialog.setMessage("Searching");
        progressDialog.show();
        String empty = "";
        String department = options.getString(Constants.BUNDLE_ARG_DEPARTMENT);
        final String title = options.getString(Constants.BUNDLE_ARG_TITLE);
        // check if department is empty.
        if(TextUtils.equals(department,empty)){
            // searching jobs based on Job Title
            searchByJobTitle(title);
        } else if(TextUtils.equals(title,empty)){
            // if title is empty, search jobs by department
            searchByDepartment(department);
        } else {
            // if both title and department are empty display all jobs
            final HashMap<String, JobListingModel> jobs = new HashMap<>();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query searchQuery = databaseReference.child(Constants.ARG_JOB_POSTINGS)
                    .orderByChild(Constants.ARG_FB_DEPT_SORT_VERSION).startAt(department).endAt(department);
            searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                    while (dataSnapshots.hasNext()) {
                        DataSnapshot dataSnapshotChild = dataSnapshots.next();
                        JobListingModel job = dataSnapshotChild.getValue(JobListingModel.class);
                        job.setJobId(dataSnapshotChild.getKey());
                        if (job.getJobTitleSortVersion().equalsIgnoreCase(title)) {
                            jobs.put(job.getJobId(), job);
                        } else {
                            Log.i("Home","No Jobs matched");
                        }
                    }
                    Intent searchIntent = new Intent(HomeActivity.this, JobDescription.class);
                    searchIntent.putExtra("list", jobs);
                    startActivity(searchIntent);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    /**
     * Method to search jobs by Job Title
     * @param title:
     */
    private void searchByJobTitle(String title){
        final HashMap<String,JobListingModel> jobs = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query searchQuery = databaseReference.child(Constants.ARG_JOB_POSTINGS)
                .orderByChild(Constants.ARG_FB_TITLE_SORT_VERSION).startAt(title).endAt(title);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    JobListingModel job = dataSnapshotChild.getValue(JobListingModel.class);
                    job.setJobId(dataSnapshotChild.getKey());
                    jobs.put(job.getJobId(),job);
                }
                progressDialog.dismiss();
                Intent searchIntent = new Intent(HomeActivity.this,JobDescription.class);
                searchIntent.putExtra("list",jobs);
                startActivity(searchIntent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Method is used to search jobs by department
     * @param department:
     */
    private void searchByDepartment(String department){
        final HashMap<String,JobListingModel> jobs = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query searchQuery = databaseReference.child(Constants.ARG_JOB_POSTINGS)
                .orderByChild(Constants.ARG_FB_DEPT_SORT_VERSION).startAt(department).endAt(department);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    JobListingModel job = dataSnapshotChild.getValue(JobListingModel.class);
                    job.setJobId(dataSnapshotChild.getKey());
                    jobs.put(job.getJobId(),job);
                }
                progressDialog.dismiss();
                Intent searchIntent = new Intent(HomeActivity.this,JobDescription.class);
                searchIntent.putExtra("list",jobs);
                startActivity(searchIntent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}




