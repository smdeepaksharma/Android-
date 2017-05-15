package com.sdsu.deepak.campusjobs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SignInFragment extends Fragment implements View.OnClickListener{

    ImageView googleSignIn;
    EditText emailId;
    EditText password;
    Button signInButton;
    Button signUpButton;

    private OnSignInFragmentInteractionListener mSignInListener;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_sign_in, container, false);
        // Binding the views
        emailId = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        signInButton = (Button) view.findViewById(R.id.button_sign_in);
        googleSignIn = (ImageView) view.findViewById(R.id.google_sign_in);
        signUpButton = (Button) view.findViewById(R.id.button_sign_up);
        // attach listeners
        signUpButton.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignInFragmentInteractionListener) {
            mSignInListener = (OnSignInFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSignInListener = null;
    }

    @Override
    public void onClick(View view) {
        int item = view.getId();
        switch(item){
            case R.id.button_sign_in:
                if(validateCredentials()){
                    mSignInListener.signInUser(emailId.getText().toString(),password.getText().toString());
                }
                break;
            case R.id.button_sign_up:
                mSignInListener.updateSignUpUI();
                break;
            case R.id.google_sign_in:
                mSignInListener.googleSignIn();
                break;
            default:
                break;
        }
    }

    /**
     * This is method is invoked while signing in to validate the user input
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateCredentials(){
        if(TextUtils.equals(emailId.getText().toString(),"")){
            emailId.setError("Enter email id");
            return false;
        }
        if(TextUtils.equals(password.getText().toString(),"")) {
            password.setError("Enter password");
            return false;
        }
        return true;
    }

    interface OnSignInFragmentInteractionListener {
        // Sign in user with fire base authentication
        void signInUser(String email, String password);
        // Sign in user with Google Provider
        void googleSignIn();
        // Register user
        void updateSignUpUI();
    }
}
