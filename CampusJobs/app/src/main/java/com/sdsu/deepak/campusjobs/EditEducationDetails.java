package com.sdsu.deepak.campusjobs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sdsu.deepak.campusjobs.model.Education;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to enable the user to edit his/her education details
 * @author Deepak
 */
public class EditEducationDetails extends Fragment implements AdapterView.OnItemClickListener{

    private static final String ARG_PARAM1 = "Education";
    private HashMap<String, Education> educations;
    private List<String> degreeList;
    private List<String> majorList;
    ListView eduListView;
    EducationListAdapter educationListAdapter;
    Button addEducation;
    private OnEducationEditListener mListener;
    Button saveChanges;

    public EditEducationDetails() {
        // Required empty public constructor
    }

    public static EditEducationDetails newInstance(HashMap<String,Education> educations) {
        EditEducationDetails fragment = new EditEducationDetails();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, educations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            educations = (HashMap<String,Education>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_education_details, container, false);
        eduListView = (ListView) v.findViewById(R.id.educationDetailsList);
        addEducation = (Button) v.findViewById(R.id.addEducation);
        majorList =  Arrays.asList(getResources().getStringArray(R.array.majorList));
        degreeList = Arrays.asList(getResources().getStringArray(R.array.degreeList));
        educationListAdapter = new EducationListAdapter(educations);
        eduListView.setAdapter(educationListAdapter);
        eduListView.setOnItemClickListener(this);

        // add new education details
        addEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Education edu = new Education();
                final Dialog dialog = new Dialog(getContext());
                Window dialogWindow = dialog.getWindow();
                if(dialogWindow!=null){
                    dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
                }
                dialog.setContentView(R.layout.add_education_dialog);
                final EditText school = (EditText) dialog.findViewById(R.id.universityName);
                final Spinner degree = (Spinner) dialog.findViewById(R.id.degree);
                Spinner major = (Spinner) dialog.findViewById(R.id.major);
                final EditText start = (EditText) dialog.findViewById(R.id.startDate);
                final EditText end = (EditText) dialog.findViewById(R.id.endDate);
                Button ok = (Button) dialog.findViewById(R.id.ok);
                final SwitchCompat inProgress = (SwitchCompat) dialog.findViewById(R.id.inProgress);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);


                inProgress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if(checked){
                            edu.inProgress = true;
                            end.setEnabled(false);
                        } else {
                            edu.inProgress = false;
                            end.setEnabled(true);
                        }
                    }
                });

                degree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        edu.degree = adapterView.getItemAtPosition(i).toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });

               major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       edu.major = adapterView.getItemAtPosition(i).toString();
                   }
                   @Override
                   public void onNothingSelected(AdapterView<?> adapterView) {}
               });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isInputValid()){
                            edu.schoolName = school.getText().toString().trim();
                            edu.startDate = start.getText().toString().trim();
                            edu.endDate = end.getText().toString().trim();
                            educations.put(edu.degree, edu);
                            eduListView.setAdapter(new EducationListAdapter(educations));
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
                        }
                    }
                    private boolean isInputValid(){
                        if(TextUtils.equals(school.getText().toString().trim(),"")) return false;
                        else if (TextUtils.equals(start.getText().toString().trim(),"")) return false;
                        else if(edu.major.equals("Major")) return false;
                        else if(edu.degree.equals("Degree")) return false;
                        else if(!edu.inProgress && TextUtils.equals(end.getText().toString().trim(),"")) return false;
                        else return true;
                    }

                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        saveChanges = (Button) v.findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEducationAdded(educations);
            }
        });

        return v;
    }
    // Dialog window to enable user to edit the existing education detail
    private void showEducationEditor(final Education edu){

        final Dialog dialog = new Dialog(getContext());
        Window dialogWindow = dialog.getWindow();

        if(dialogWindow!=null){
            dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setContentView(R.layout.add_education_dialog);
        final EditText school = (EditText) dialog.findViewById(R.id.universityName);
        final Spinner degree = (Spinner) dialog.findViewById(R.id.degree);
        Spinner major = (Spinner) dialog.findViewById(R.id.major);
        final EditText start = (EditText) dialog.findViewById(R.id.startDate);
        final EditText end = (EditText) dialog.findViewById(R.id.endDate);
        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        SwitchCompat inProgress = (SwitchCompat) dialog.findViewById(R.id.inProgress);
        school.setText(edu.schoolName);
        degree.setSelection(getDegreeItemId(edu.degree));
        major.setSelection(getMajorItemId(edu.major));
        start.setText(edu.startDate);

        if(edu.inProgress){
            inProgress.setChecked(true);
            end.setEnabled(false);
        } else {
            inProgress.setChecked(false);
            end.setText(edu.endDate);
            end.setEnabled(true);
        }

        degree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                edu.degree = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                edu.major = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        inProgress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    end.setEnabled(false);
                    edu.inProgress = true;
                } else {
                    end.setEnabled(true);
                    edu.inProgress = false;
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()){
                    edu.schoolName = school.getText().toString().trim();
                    edu.startDate = start.getText().toString().trim();
                    edu.endDate = end.getText().toString().trim();
                    educations.put(edu.degree,edu);
                    eduListView.setAdapter(new EducationListAdapter(educations));
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
                }
            }

            private boolean isInputValid(){
                if(TextUtils.equals(school.getText().toString().trim(),"")) return false;
                else if (TextUtils.equals(start.getText().toString().trim(),"")) return false;
                else if(!edu.inProgress && TextUtils.equals(end.getText().toString().trim(),"")) return false;
                else if(edu.major.equals("Major")) return false;
                else if(edu.degree.equals("Degree")) return false;
                else return true;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    // Used to set the previously selected menu item
    private int getMajorItemId(String item){
        for(int i =0 ; i < majorList.size() ; i++){
            if(item.equalsIgnoreCase(majorList.get(i))){
                return i;
            }
        }
        return 1;
    }

    private int getDegreeItemId(String item){
        for(int i =0 ; i < degreeList.size() ; i++){
            if(item.equalsIgnoreCase(degreeList.get(i))){
                return i;
            }
        }
        return 1;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEducationEditListener) {
            mListener = (OnEducationEditListener) context;
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        HashMap.Entry<String, Education> entry = (HashMap.Entry<String, Education>) adapterView.getItemAtPosition(i);
        Education selectedEdu = entry.getValue();
        showEducationEditor(selectedEdu);
    }

    public interface OnEducationEditListener{
        void onEducationAdded(HashMap<String,Education> updatedEduList);
    }

    /**
     * Education List Adapter
     */

    private class EducationListAdapter extends BaseAdapter {
        private final ArrayList mData;

        EducationListAdapter(Map<String, Education> map) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<String, Education> getItem(int position) {
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
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.education_background, parent, false);
            } else {
                result = convertView;
            }
            final Map.Entry<String, Education> item = getItem(position);
            Education education = item.getValue();
            ((TextView) result.findViewById(R.id.universityName)).setText(education.schoolName);
            ((TextView) result.findViewById(R.id.degree_and_major)).setText(education.degree + ", " + education.major);
            String duration;
            if(education.inProgress){
                duration = education.startDate + " - " + "Present";
            } else {
                duration = education.startDate +" - " + education.endDate;
            }
            ((TextView) result.findViewById(R.id.duration)).setText(duration);
            return result;
        }
    }
}
