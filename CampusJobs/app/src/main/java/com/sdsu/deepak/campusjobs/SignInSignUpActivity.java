package com.sdsu.deepak.campusjobs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsu.deepak.campusjobs.model.StudentProfile;

public class SignInSignUpActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, SignUpFragment.OnSignUpFragmentInteractionListener, SignInFragment.OnSignInFragmentInteractionListener {

    private static final String LOG_TAG = "Login";
    private static final int RC_SIGN_IN = 111;
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        Window window = progressDialog.getWindow();
        if(window!=null){
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        // check if the user has already signed in
        if(auth.getCurrentUser()!=null){
            // if the user has already signed in
            Intent intent = new Intent(SignInSignUpActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            // if user has not signed in
            setContentView(R.layout.activity_signinsignup);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
            // Displaying Sign In page
            SignInFragment signInFragment = new SignInFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,signInFragment);
            fragmentTransaction.addToBackStack(Constants.SIGN_IN_FRAGMENT);
            fragmentTransaction.commit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        }
    }

    /**
     * Sign In using google Provider
     */

    @Override
    public void googleSignIn() {
        progressDialog.setMessage("Signing in with Google");
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
     // handle the result from Google Provider Sign In
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            Toast.makeText(this,"Invalid",Toast.LENGTH_SHORT).show();
        }
    }
    // authenticate user in fire base
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(LOG_TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInSignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    // update the UI if user is authenticated
    private void updateUI(FirebaseUser acct){
        if(checkIfExistingUser(acct)){
            progressDialog.dismiss();
            Intent home = new Intent(this, HomeActivity.class);
            home.putExtra("Username",acct.getDisplayName());
            startActivity(home);
        } else{
            StudentProfile studentProfile = new StudentProfile();
            studentProfile.setName(acct.getDisplayName());
            studentProfile.setEmailId(acct.getEmail());
            studentProfile.setStudentId(acct.getUid());
            storeUserProfile(studentProfile,acct);
        }
    }


    private boolean checkIfExistingUser(FirebaseUser firebaseUser){
        SharedPreferences preferences = getSharedPreferences("LOGGED_IN_USERS",MODE_PRIVATE);
        if(preferences!=null){
            return preferences.getBoolean(firebaseUser.getUid(),false);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("LOGGED_IN_USERS",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(firebaseUser.getUid(),true);
            editor.apply();
            return false;
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Connection Failed. Network Error",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void createAccount(StudentProfile studentProfile, String password) {
        progressDialog.setMessage("Signing Up");
        progressDialog.show();
        createNewAccount(studentProfile,password);
    }

    // Method enables registered users to go back to login page
    @Override
    public void backToLogin() {
        SignInFragment signInFragment = new SignInFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,R.anim.fragment_right_exit);
        fragmentTransaction.replace(R.id.fragment_container,signInFragment).commit();
    }

    /**
     * Fire base sign in
     * @param email : user email
     * @param password: password for authentication
     */
    @Override
    public void signInUser(String email, String password) {
        progressDialog.show();
        Log.i(LOG_TAG,"Create Account");
        auth.signInWithEmailAndPassword(email, password )
                .addOnCompleteListener(SignInSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInSignUpActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Intent intent = new Intent(SignInSignUpActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    /**
     * This method is used to inflate the SignUp Fragment.
     * Allows users to register.
     */
    @Override
    public void updateSignUpUI() {
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_right_enter,R.anim.fragment_left_exit);
        fragmentTransaction.replace(R.id.fragment_container,signUpFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportFragmentManager().popBackStack(Constants.SIGN_IN_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * This method is used to set the user profile in Fire base.
     * @param studentProfile: personal information of the user
     * @param password: credential
     */
    private void createNewAccount(final StudentProfile studentProfile, String password){
        auth.createUserWithEmailAndPassword(studentProfile.getEmailId(), password)
                .addOnCompleteListener(SignInSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInSignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        } else {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user!=null){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(studentProfile.getName())
                                        .build();
                                try {
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        storeUserProfile(studentProfile, user);
                                                    } else {
                                                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } catch (NullPointerException e) {
                                    Log.d(LOG_TAG, "Something went wrong");
                                }
                            }
                        }
                    }
                });
    }

    /**
     * This method is used to store user information in fire base
     * @param studentProfile:
     * @param credentials:
     */
    private void storeUserProfile(StudentProfile studentProfile, FirebaseUser credentials){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_STUDENT_LIST).child(credentials.getUid()).setValue(studentProfile);
        progressDialog.dismiss();
        Intent home = new Intent(SignInSignUpActivity.this, HomeActivity.class);
        home.putExtra("Username",credentials.getDisplayName());
        startActivity(home);
        finish();
    }
}
