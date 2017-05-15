package com.sdsu.deepak.campusjobs;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sdsu.deepak.campusjobs.model.Education;
import com.sdsu.deepak.campusjobs.model.Experience;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class hosts the EditEducationDetails, EditExperienceDetails and ViewSkills fragments
 * @author Deepak
 */
public class EditProfile extends AppCompatActivity implements
        EditEducationDetails.OnEducationEditListener,GoogleApiClient.OnConnectionFailedListener,
        EditExperienceDetails.OnEditExperienceListener, ViewSkillsFragment.OnSkillsAddedListener{

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Edit Profile");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        Intent intent = getIntent();
        String action = intent.getStringExtra(Constants.ACTION);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (action){
            case Constants.ACTION_EDIT_EDUCATION:
                HashMap<String,Education> educationDetails =
                        (HashMap<String,Education>) intent.getSerializableExtra(Constants.ACTION_PARAMETER);
                EditEducationDetails editEducationDetails = EditEducationDetails.newInstance(educationDetails);
                fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.editProfileContainer,editEducationDetails).commit();
                break;
            case Constants.ACTION_EDIT_EXPERIENCE:
                toolbar.setTitle(getResources().getString(R.string.experienceLabel));
                HashMap<String,Experience> experiences =
                        (HashMap<String,Experience>) intent.getSerializableExtra(Constants.ACTION_PARAMETER);
                EditExperienceDetails editExperienceDetails = EditExperienceDetails.newInstance(experiences);
                fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.editProfileContainer,editExperienceDetails).commit();
                break;
            case Constants.ACTION_EDIT_SKILLS:
                toolbar.setTitle(getResources().getString(R.string.skillSetLabel));
                ArrayList<String> skillSet = intent.getStringArrayListExtra(Constants.ACTION_PARAMETER);
                ViewSkillsFragment viewSkillsFragment = ViewSkillsFragment.newInstance(skillSet);
                fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.editProfileContainer,viewSkillsFragment).commit();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onExperienceAdded(HashMap<String, Experience> updatedExperienceList) {
        toolbar.setTitle(getResources().getString(R.string.profile));
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.ARG_EXPERIENCE, updatedExperienceList);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onEducationAdded(HashMap<String, Education> updatedEduList) {
        toolbar.setTitle(getResources().getString(R.string.profile));
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.ARG_EDUCATION, updatedEduList);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onSkillAdded(ArrayList<String> updatedSkillSet) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.ARG_SKILLS_LIST, updatedSkillSet);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack();
        super.onBackPressed();
    }
}
