package com.sdsu.deepak.campusjobs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sdsu.deepak.campusjobs.model.Education;
import com.sdsu.deepak.campusjobs.model.Experience;
import com.sdsu.deepak.campusjobs.model.JobPoster;
import com.sdsu.deepak.campusjobs.model.StudentProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ViewApplicantProfile extends Fragment {

    private static final String ARG_APPLICANT_DATA = "ApplicantData";
    private static final String ARG_JOB_POST = "JobPost";
    private StudentProfile applicantProfile;
    private OnApplicantDecisionMade mListener;
    TextView studentName;
    TextView email, phone;
    Button scheduleInterview;
    JobPoster postedJob;

    public ViewApplicantProfile() {
        // Required empty public constructor
    }

    public static ViewApplicantProfile newInstance(JobPoster postedJob, StudentProfile applicantProfile) {
        ViewApplicantProfile fragment = new ViewApplicantProfile();
        Bundle args = new Bundle();
        args.putSerializable(ARG_APPLICANT_DATA, applicantProfile);
        args.putSerializable(ARG_JOB_POST,postedJob);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            applicantProfile = (StudentProfile) getArguments().getSerializable(ARG_APPLICANT_DATA);
            postedJob = (JobPoster) getArguments().getSerializable(ARG_JOB_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_applicant_profile, container, false);

        // bind the views
        studentName = (TextView) view.findViewById(R.id.name);
        email = (TextView) view.findViewById(R.id.studentEmailId);
        phone = (TextView) view.findViewById(R.id.studentPhone);
        LinearLayout educationLayout = (LinearLayout) view.findViewById(R.id.educationBackground);
        LinearLayout experienceLayout = (LinearLayout) view.findViewById(R.id.experienceBackground);
        LinearLayout skillsLayout = (LinearLayout) view.findViewById(R.id.skillSet);

        // initializing views
        studentName.setText(applicantProfile.getName());
        email.setText(applicantProfile.getEmailId());

        // check if user has set up the profile details and display only saved profile data
        // checking contact number
        if(applicantProfile.getContactNumber()==null || applicantProfile.getContactNumber().equalsIgnoreCase("")){
            view.findViewById(R.id.contactDetails).setVisibility(View.GONE);
        } else {
            phone.setText(applicantProfile.getContactNumber());
        }
        // checking education details
        if(!applicantProfile.getEducationDetails().isEmpty()){
            addEducationLayout(educationLayout,applicantProfile.getEducationDetails());
        } else {
            view.findViewById(R.id.educationBackground_card).setVisibility(View.GONE);
        }
        // checking employment details
        if(!applicantProfile.getEmploymentDetails().isEmpty()){
            addExperienceLayout(experienceLayout,applicantProfile.getEmploymentDetails());
        } else {
            view.findViewById(R.id.experienceBackground_card).setVisibility(View.GONE);
        }
        // checking skills
        if(!applicantProfile.getSkills().isEmpty()) {
            addSkill(skillsLayout,applicantProfile.getSkills());
        } else {
            view.findViewById(R.id.skills_card).setVisibility(View.GONE);
        }

        scheduleInterview = (Button) view.findViewById(R.id.message);
        scheduleInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.notifyStudent(applicantProfile);
                String emailSubject = postedJob.jobTitle + " Interview Call";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{applicantProfile.getEmailId()});
                i.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                String emailBody = applicantProfile.getName() + ",\n\nWe would like to know more about your work and discuss possible projects that would benefit from your expertise.\n" +
                        "\n" + "Can we meet this ";
                i.putExtra(Intent.EXTRA_TEXT   , emailBody);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    /**
     * This method is used to populate the Education Card View dynamically based on the
     * education list size.
     * @param parentView: Education Details View
     * @param educationList: List of user's education details
     */
    private void addEducationLayout(LinearLayout parentView, HashMap<String,Education> educationList){
        // inflate the custom education view
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // for each education record inflate a custom view
            Set entrySet = educationList.entrySet();
            Iterator it = entrySet.iterator();
            while(it.hasNext()){
                Map.Entry me = (Map.Entry)it.next();
                Education e = (Education) me.getValue();
                View view = inflater.inflate(R.layout.education_background,parentView,false);
                TextView universityName = (TextView) view.findViewById(R.id.universityName);
                TextView degree = (TextView) view.findViewById(R.id.degree_and_major);
                TextView duration = (TextView) view.findViewById(R.id.duration);
                universityName.setText(e.schoolName);
                degree.setText(e.degree +", " + e.major);
                if(e.inProgress){
                    duration.setText(e.startDate + " - " + "Present");
                } else {
                    duration.setText(e.startDate+" - " + e.endDate);
                }
                parentView.addView(view);

        }
    }

    /**
     * This method adds users experience details to the profile view
     * @param experienceLayout: Layout where the details has to be showed
     * @param experiences: List containing user's experience details
     */
    private void addExperienceLayout(LinearLayout experienceLayout, HashMap<String,Experience> experiences){

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Set entrySet = experiences.entrySet();
            Iterator it = entrySet.iterator();
            while(it.hasNext()){
                Map.Entry me = (Map.Entry)it.next();
                Experience e = (Experience) me.getValue();
                View subView = inflater.inflate(R.layout.experience_background,experienceLayout,false);
                ImageView icon = (ImageView) subView.findViewById(R.id.workIcon);
                TextView universityName = (TextView) subView.findViewById(R.id.companyName);
                TextView degreeAndMajor = (TextView) subView.findViewById(R.id.position);
                TextView duration = (TextView) subView.findViewById(R.id.duration);
                universityName.setText(e.companyName);
                degreeAndMajor.setText(e.title);
                icon.setImageResource(R.drawable.work);
                if(e.isCurrentEmployment){
                    String timeSpan = e.startDate + " - Present";
                    duration.setText(timeSpan);
                } else {
                    duration.setText(e.startDate+" - " + e.endDate);
                }
                experienceLayout.addView(subView);
        }
    }

    /**
     * The addSkill() method populates the skill set view in the user profile
     * @param skillLayout: Layout where the skills are to be displayed
     * @param skills: List containing user's skill set
     */
    private void addSkill(LinearLayout skillLayout, ArrayList<String> skills){
        // Skill layout displays only up to 4 skills.
        // Remaining list is shown in a separate fragment
         if(skills.size() < 4) {
            for (int i = 0; i < skills.size(); i++) {
                String s = skills.get(i);
                TextView textView = new TextView(getContext());
                textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setText(s);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8,8,8,8);
                skillLayout.addView(textView);
            }
        }else {
            for (int i = 0; i < 4; i++) {
                String s = skills.get(i);
                TextView textView = new TextView(getContext());
                textView.setText(s);
                textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8,8,8,8);
                skillLayout.addView(textView);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnApplicantDecisionMade) {
            mListener = (OnApplicantDecisionMade) context;
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

    interface OnApplicantDecisionMade {
        void notifyStudent(StudentProfile studentProfile);
    }
}
