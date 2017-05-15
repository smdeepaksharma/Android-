package com.sdsu.deepak.campusjobs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.sdsu.deepak.campusjobs.model.Education;
import com.sdsu.deepak.campusjobs.model.Experience;
import com.sdsu.deepak.campusjobs.model.StudentProfile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{


    Toolbar toolBar;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    StudentProfile studentProfile;
    TextView email, phone;
    Button editEducation, editExperience, editSkills;
    Button moreButton;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    LinearLayout educationLayout;
    LinearLayout experienceLayout ;
    LinearLayout skillsLayout;
    ImageView profilePicture;
    private GoogleApiClient mGoogleApiClient;
    CardView educationBackgroundCard, experienceBackgroundCard;
    CoordinatorLayout baseProfileLayout;

    private static final int EDIT_EDUCATION_REQUEST_CODE = 100;
    private static final int EDIT_EXPERIENCE_REQUEST_CODE = 101;
    private static final int EDIT_SKILL_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        baseProfileLayout = (CoordinatorLayout) findViewById(R.id.baseProfileLayout);
        educationBackgroundCard = (CardView) findViewById(R.id.educationBackground_card);
        experienceBackgroundCard = (CardView) findViewById(R.id.experienceBackground_card);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapseToolBar) ;
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        profilePicture = (ImageView) findViewById(R.id.profileImage);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        if(currentUser.getPhotoUrl()!=null) {
            String url = currentUser.getPhotoUrl().toString();
            LoadProfileImage loadProfileImage = new LoadProfileImage(profilePicture);
            loadProfileImage.execute(url);
        }

        editEducation = (Button) findViewById(R.id.editEducation);
        editExperience = (Button) findViewById(R.id.editExpereince);
        editSkills = (Button) findViewById(R.id.editSkills);
        editExperience.setOnClickListener(this);
        editEducation.setOnClickListener(this);
        editSkills.setOnClickListener(this);
        email = (TextView) findViewById(R.id.studentEmailId);
        phone = (TextView) findViewById(R.id.studentPhone);
        educationLayout = (LinearLayout) findViewById(R.id.educationBackground);
        experienceLayout = (LinearLayout) findViewById(R.id.experienceBackground);
        skillsLayout = (LinearLayout) findViewById(R.id.skillSet);

        getStudentProfile();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    private void displayUserProfile(StudentProfile studentProfile){
        this.studentProfile = studentProfile;
        collapsingToolbarLayout.setTitle(studentProfile.getName());
        email.setText(studentProfile.getEmailId());
        if(studentProfile.getContactNumber()!=null){
            phone.setText(studentProfile.getContactNumber());
        } else{
            phone.setVisibility(View.GONE);
        }
        addEducationLayout(educationLayout,studentProfile.getEducationDetails());
        addExperienceLayout(experienceLayout,studentProfile.getEmploymentDetails());
        addSkill(skillsLayout, studentProfile.getSkills());
    }

    private void getStudentProfile(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_STUDENT_LIST).child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StudentProfile studentProfile = dataSnapshot.getValue(StudentProfile.class);
              if(studentProfile==null){
                    studentProfile = new StudentProfile();
                    studentProfile.setName(currentUser.getDisplayName());
                    studentProfile.setEmploymentDetails(new HashMap<String, Experience>());
                    studentProfile.setEducationDetails(new HashMap<String, Education>());
                    studentProfile.setSkills(new ArrayList<String>());
                    studentProfile.setStudentId(currentUser.getUid());
                    studentProfile.setEmailId(currentUser.getEmail());
                    displayUserProfile(studentProfile);
                } else {
                    if(studentProfile.getSkills()==null){
                        studentProfile.setSkills(new ArrayList<String>());
                    }
                    if(studentProfile.getEmploymentDetails()==null) {
                        studentProfile.setEmploymentDetails(new HashMap<String, Experience>());
                    }
                    if(studentProfile.getEducationDetails()==null){
                        studentProfile.setEducationDetails(new HashMap<String, Education>());
                    }
                  displayUserProfile(studentProfile);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void addEducationLayout(LinearLayout parentView, HashMap<String,Education> educationList){
        // inflate the custom education view
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // for each education record inflate a custom view
        if(educationList.isEmpty()){
            TextView emptyMessageTextView = new TextView(this);
            emptyMessageTextView.setText(getResources().getText(R.string.emptyEducation));
            parentView.addView(emptyMessageTextView);
        } else {
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
    }


    private void addExperienceLayout(LinearLayout experienceLayout, HashMap<String,Experience> experiences){

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(experiences.isEmpty()){
            TextView emptyMessageTextView = new TextView(this);
            emptyMessageTextView.setText(getResources().getText(R.string.emptyExperience));
            experienceLayout.addView(emptyMessageTextView);
        } else {
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
    }

    private void addSkill(LinearLayout skillLayout, ArrayList<String> skills){

        if(skills.isEmpty()){
            TextView emptyMessageTextView = new TextView(this);
            emptyMessageTextView.setText(getResources().getText(R.string.emptySkills));
            skillLayout.addView(emptyMessageTextView);
        } else if(skills.size() < 4) {
            for (int i = 0; i < skills.size(); i++) {
                String s = skills.get(i);
                TextView textView = new TextView(this);
                textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setText(s);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8,8,8,8);
                skillLayout.addView(textView);
            }
        }else {
            for (int i = 0; i < 4; i++) {
                String s = skills.get(i);
                TextView textView = new TextView(this);
                textView.setText(s);
                textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(8,8,8,8);
                skillLayout.addView(textView);
            }
            moreButton = new Button(this);
            moreButton.setText(getResources().getString(R.string.viewMoreSkills));
            moreButton.setBackgroundColor(Color.TRANSPARENT);
            skillLayout.addView(moreButton);
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent skillIntent = new Intent (ProfileActivity.this, EditProfile.class);
                    skillIntent.putExtra(Constants.ACTION , Constants.ACTION_EDIT_SKILLS);
                    skillIntent.putExtra(Constants.ACTION_PARAMETER ,studentProfile.getSkills());
                    overridePendingTransition(R.anim.fragment_right_enter,R.anim.fragment_left_exit);
                    startActivityForResult(skillIntent,EDIT_SKILL_REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.profile).setVisible(false);
        menu.findItem(R.id.item_samplebadge).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();
                return true;
            case R.id.profile:
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);
                return true;
            case R.id.signout:
                for(UserInfo user : currentUser.getProviderData()){
                    if (user.getProviderId().equals("google.com")) {
                        signOut();
                    } else{
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(ProfileActivity.this, CloseActivity.class);
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
                        Intent intent = new Intent(ProfileActivity.this, CloseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }





/*
    @Override
    public void viewMoreSkills(ArrayList<String> skillSet) {

    }

    @Override
    public void editEducationDetails(HashMap<String,Education> educationDetails) {
        EditEducationDetails editEducationDetails = EditEducationDetails.newInstance(educationDetails);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit);
        fragmentTransaction.addToBackStack(null);
        //fragmentTransaction.replace(R.id.profile_container,editEducationDetails).commit();

    }

    @Override
    public void editExperience(HashMap<String,Experience> experiences) {
        EditExperienceDetails editExperienceDetails = EditExperienceDetails.newInstance(experiences);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit);
        fragmentTransaction.addToBackStack(null);
       // fragmentTransaction.replace(R.id.profile_container,editExperienceDetails).commit();
    }
*/


/*
    @Override
    public void onEducationAdded(HashMap<String,Education> updatedEduList) {
        toolBar.setTitle("Profile");
        getSupportFragmentManager().popBackStack();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        studentProfile.setEducationDetails(updatedEduList);
        databaseReference.child(Constants.ARG_STUDENT_LIST)
                .child(currentUser.getUid())
                .child(Constants.ARG_EDUCATION)
                .setValue(updatedEduList);
    }

    @Override
    public void onExperienceAdded(HashMap<String, Experience> updatedExperienceList) {
        toolBar.setTitle("Profile");
        getSupportFragmentManager().popBackStack();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        studentProfile.setEmploymentDetails(updatedExperienceList);
        databaseReference.child(Constants.ARG_STUDENT_LIST)
                .child(currentUser.getUid())
                .child(Constants.ARG_EXPERIENCE)
                .setValue(updatedExperienceList);
    }
*/


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.editEducation:
                Intent editIntent = new Intent (this, EditProfile.class);
                editIntent.putExtra(Constants.ACTION , Constants.ACTION_EDIT_EDUCATION);
                editIntent.putExtra(Constants.ACTION_PARAMETER ,studentProfile.getEducationDetails());
                overridePendingTransition(R.anim.fragment_right_enter,R.anim.fragment_left_exit);
                startActivityForResult(editIntent,EDIT_EDUCATION_REQUEST_CODE);
                break;
            case R.id.editExpereince:
                Intent expIntent = new Intent (this, EditProfile.class);
                expIntent.putExtra(Constants.ACTION , Constants.ACTION_EDIT_EXPERIENCE);
                expIntent.putExtra(Constants.ACTION_PARAMETER ,studentProfile.getEmploymentDetails());
                overridePendingTransition(R.anim.fragment_right_enter,R.anim.fragment_left_exit);
                startActivityForResult(expIntent,EDIT_EXPERIENCE_REQUEST_CODE);
                break;
            case R.id.editSkills:
                Intent skillIntent = new Intent (this, EditProfile.class);
                skillIntent.putExtra(Constants.ACTION , Constants.ACTION_EDIT_SKILLS);
                skillIntent.putExtra(Constants.ACTION_PARAMETER ,studentProfile.getSkills());
                overridePendingTransition(R.anim.fragment_right_enter,R.anim.fragment_left_exit);
                startActivityForResult(skillIntent,EDIT_SKILL_REQUEST_CODE);
                break;
            default: break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Snackbar snackbar = Snackbar
                .make(baseProfileLayout, getResources().getString(R.string.onSaveSuccessMessage), Snackbar.LENGTH_LONG);
        snackbar.show();

        switch (requestCode){
            case EDIT_EDUCATION_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    HashMap<String, Education> educationHashMap =
                            (HashMap<String, Education>) data.getSerializableExtra(Constants.ARG_EDUCATION);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    studentProfile.setEducationDetails(educationHashMap);
                    databaseReference.child(Constants.ARG_STUDENT_LIST)
                            .child(currentUser.getUid())
                            .child(Constants.ARG_EDUCATION)
                            .setValue(educationHashMap);
                    educationLayout.removeAllViews();
                    addEducationLayout(educationLayout, educationHashMap);
                }
                break;
            case EDIT_EXPERIENCE_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    HashMap<String, Experience> experienceHashMap =
                            (HashMap<String, Experience>) data.getSerializableExtra(Constants.ARG_EXPERIENCE);
                    Log.i("Profile","Expereince: "+ experienceHashMap.size());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    studentProfile.setEmploymentDetails(experienceHashMap);
                    databaseReference.child(Constants.ARG_STUDENT_LIST)
                            .child(currentUser.getUid())
                            .child(Constants.ARG_EXPERIENCE)
                            .setValue(experienceHashMap);
                    experienceLayout.removeAllViews();
                    addExperienceLayout(experienceLayout,experienceHashMap);
                }
                break;
            case EDIT_SKILL_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    ArrayList<String> updatedSkillSet = data.getStringArrayListExtra(Constants.ARG_SKILLS_LIST);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    studentProfile.setSkills(updatedSkillSet);
                    databaseReference.child(Constants.ARG_STUDENT_LIST)
                            .child(currentUser.getUid())
                            .child(Constants.ARG_SKILLS)
                            .setValue(updatedSkillSet);
                    skillsLayout.removeAllViews();
                    addSkill(skillsLayout,updatedSkillSet);
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

         LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... uri) {
            String url = uri[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (result != null) {
                Bitmap resized = Bitmap.createScaledBitmap(result,200,200, true);
                bmImage.setImageBitmap(resized);

            }
        }
    }
}
