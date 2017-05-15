package com.sdsu.deepak.campusjobs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdsu.deepak.campusjobs.model.StudentProfile;

public class SignUpFragment extends Fragment implements View.OnClickListener{

    EditText name;
    EditText emailId;
    EditText password;
    EditText phoneNumber;
    Button createAccountButton;
    Button backToLoginButton;

    private OnSignUpFragmentInteractionListener mSignUpListener;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        name = (EditText) view.findViewById(R.id.name);
        emailId = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        phoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        createAccountButton = (Button) view.findViewById(R.id.create_account);
        backToLoginButton = (Button) view.findViewById(R.id.back_to_login);
        createAccountButton.setOnClickListener(this);
        backToLoginButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpFragmentInteractionListener) {
            mSignUpListener = (OnSignUpFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSignUpListener = null;
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        switch(itemId){
            case R.id.create_account:
                if(isValidInput()){
                    StudentProfile studentProfile = new StudentProfile(name.getText().toString(),
                            emailId.getText().toString(),phoneNumber.getText().toString().trim());
                    mSignUpListener.createAccount(studentProfile, password.getText().toString());
                } else {
                    Toast.makeText(getContext(),"Invalid data",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back_to_login:
                mSignUpListener.backToLogin();
                break;
        }
    }

    /**
     * isValidInput() is used to evaluate the user input while registration
     * @return true if all the fields are valid, false otherwise
     */
    private boolean isValidInput(){
        String studentName = name.getText().toString();
        String email = emailId.getText().toString();
        String pwd = password.getText().toString();
        String empty = "";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(TextUtils.equals(studentName,empty)){
            name.setError(getResources().getString(R.string.empty));
            return false;
        }
         if(TextUtils.equals(email,empty)) {
             emailId.setError(getResources().getString(R.string.empty));
             return false;
         }
        if(!email.matches(emailPattern)){
            emailId.setError(getResources().getString(R.string.empty));
            return false;
        }
        if(phoneNumber.getText().toString().trim().equals(empty)){
            phoneNumber.setText(getResources().getString(R.string.empty));
            return false;
        }
         if(TextUtils.equals(pwd,empty)){
            password.setError(getResources().getString(R.string.empty));
            return false;
         }
        if(pwd.length() < 3){
            password.setError(getResources().getString(R.string.passwordRule));
            return false;
        }
        return true;
    }

    /**
     * Interface to interact with the hosting activity
     */
     interface OnSignUpFragmentInteractionListener {
         // For creating new account
        void createAccount(StudentProfile studentProfile, String password);
         // if already registers take the user back to login page
        void backToLogin();
    }
}
