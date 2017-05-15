package com.sdsu.deepak.campusjobs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.sdsu.deepak.campusjobs.model.JobListingModel;

import java.util.HashMap;

public class JobDetailsFragment extends Fragment {

    private static final String ARG_JOB_DESCRIPTION= "JobDescription";
    private static final String ARG_IN_CART = "InCart";
    private static final String ARG_APPLIED = "Applied";
    LinearLayout linearLayout;
    private OnJobInteractionListener mListener;
    private JobListingModel job;
    private boolean isApplied;
    private boolean isInCart;


    TextView jobTitleTextView;
    TextView departmentTextView;
    TextView jobDescriptionTextView;
    TextView payTextView;
    TextView contactPersonNameTextView;
    TextView contactPersonEmailTextView;
    TextView requiredSkillsTextView;
    Button applyButton;
    Button addToCart;

    public JobDetailsFragment() {
        // Required empty public constructor
    }

    public static JobDetailsFragment newInstance(JobListingModel jobDetails, boolean isApplied, boolean isInCart) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_JOB_DESCRIPTION, jobDetails);
        args.putBoolean(ARG_APPLIED,isApplied);
        args.putBoolean(ARG_IN_CART,isInCart);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            job = (JobListingModel) getArguments().getSerializable(ARG_JOB_DESCRIPTION);
            isApplied = getArguments().getBoolean(ARG_APPLIED);
            isInCart = getArguments().getBoolean(ARG_IN_CART);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_details, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.jobdetail_linearLayout);
        jobTitleTextView = (TextView) view.findViewById(R.id.jobTitle);
        departmentTextView = (TextView) view.findViewById(R.id.department);
        jobDescriptionTextView = (TextView) view.findViewById(R.id.jobDescription);
        payTextView = (TextView) view.findViewById(R.id.pay);
        contactPersonNameTextView = (TextView) view.findViewById(R.id.contactPersonName);
        contactPersonEmailTextView = (TextView) view.findViewById(R.id.contactPersonalEmail);
        requiredSkillsTextView = (TextView) view.findViewById(R.id.requiredSkills);
        TextView alreadyAppliedMsg = (TextView) view.findViewById(R.id.alreadyApplied);

        jobTitleTextView.setText(job.getJobTitle());
        jobDescriptionTextView.setText(job.getJobdescription());
        departmentTextView.setText(job.getDepartment());
        payTextView.setText(job.getPay());
        contactPersonEmailTextView.setText(job.getContactPersonEmail());
        contactPersonNameTextView.setText(job.getContactPersonName());
        requiredSkillsTextView.setText(job.getSkillsRequired());

        applyButton = (Button) view.findViewById(R.id.apply_button);
        addToCart = (Button) view.findViewById(R.id.cart_button);

        if(isInCart){
                addToCart.setEnabled(false);
                addToCart.setText(getResources().getString(R.string.inCart));
            }

        if(isApplied){
            alreadyAppliedMsg.setVisibility(View.VISIBLE);
            applyButton.setText(getResources().getString(R.string.applied));
            applyButton.setEnabled(false);
            addToCart.setEnabled(false);
        }

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyButton.setEnabled(false);
                addToCart.setEnabled(false);
                mListener.applyToJob(job);
                updateOnApply();
            }
        });
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart.setEnabled(false);
                mListener.addToCart(job);
                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Added to cart", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        return view;
    }


    private void updateOnApply(){
        final Dialog dialog = new Dialog(getContext());
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog);
        TextView message = (TextView) dialog.findViewById(R.id.alterText);
        message.setText("You have successfully applied to this position.You will hear from the respective department if you are eligible.\n\nGood Luck!");
        message.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        //message.setGravity(Gravity.CENTER);
        Button exit = (Button) dialog.findViewById(R.id.exit);
        Button cancel = (Button) dialog.findViewById(R.id.resume);
        cancel.setVisibility(View.GONE);
        dialog.show();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mListener.dismissDescription();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJobInteractionListener) {
            mListener = (OnJobInteractionListener) context;
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

    public interface OnJobInteractionListener {
        void applyToJob(JobListingModel applyToJob);
        void addToCart(JobListingModel toCart);
        void dismissDescription();
    }
}
