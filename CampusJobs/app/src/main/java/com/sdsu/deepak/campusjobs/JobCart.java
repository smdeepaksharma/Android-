package com.sdsu.deepak.campusjobs;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsu.deepak.campusjobs.database.DBContract;
import com.sdsu.deepak.campusjobs.database.DatabaseHandler;
import com.sdsu.deepak.campusjobs.model.Experience;
import com.sdsu.deepak.campusjobs.model.JobApplicant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JobCart extends AppCompatActivity{

    Toolbar toolbar;
    CartAdapter adapter;
    HashMap<String,JobApplicant> cartList;
    TextView count;
    Button applyNow;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_cart);

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

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Intent intent = getIntent();
        String userId = intent.getStringExtra("id");

        cartList = new DatabaseHandler(this).
                getJobsInCart(DBContract.GET_JOBS_IN_CART + "'"+ userId + "'");

        count = (TextView) findViewById(R.id.cartTotal);
        count.setText(getResources().getString(R.string.count) + " : "+cartList.size());
        applyNow = (Button) findViewById(R.id.applyAll);
        ListView jobsCart = (ListView) findViewById(R.id.jobsInCart);
        adapter = new CartAdapter(cartList);
        jobsCart.setAdapter(adapter);

        applyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FireBaseInteract fireBaseInteract = new FireBaseInteract();
                fireBaseInteract.execute();
            }
        });

    }

    private class FireBaseInteract extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {

            Set entrySet = cartList.entrySet();
            Iterator it = entrySet.iterator();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            while(it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                JobApplicant job = (JobApplicant) me.getValue();
                job.name = currentUser.getDisplayName();
                job.appliedOn = String.valueOf(new Date().getTime());
                databaseReference.child(Constants.ARG_APPLIED_JOBS_LIST).child(job.jobId).child(currentUser.getUid()).setValue(job);
                new DatabaseHandler(getApplicationContext()).deleteJobFromCart(job.jobId,currentUser.getUid());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Applied Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class CartAdapter extends BaseAdapter {
        private final ArrayList mData;

        CartAdapter(Map<String, JobApplicant> map) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<String, JobApplicant> getItem(int position) {
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_cart, parent, false);
            } else {
                result = convertView;
            }
            final Map.Entry<String, JobApplicant> item = getItem(position);
            ((TextView) result.findViewById(R.id.jobTitle)).setText(item.getValue().jobTitle);
            ((TextView) result.findViewById(R.id.department)).setText(item.getValue().department);
            Button delete = (Button) result.findViewById(R.id.deleteJob);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mData.remove(item);
                    notifyDataSetChanged();
                    count.setText(getResources().getString(R.string.count) + " : " + mData.size());
                    new DatabaseHandler(getApplicationContext()).deleteJobFromCart(item.getValue().jobId,item.getValue().userId);
                }
            });
            return result;
        }
    }


}
