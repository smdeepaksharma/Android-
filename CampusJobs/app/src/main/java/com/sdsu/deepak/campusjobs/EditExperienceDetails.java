package com.sdsu.deepak.campusjobs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsu.deepak.campusjobs.model.Education;
import com.sdsu.deepak.campusjobs.model.Experience;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EditExperienceDetails extends Fragment implements AdapterView.OnItemClickListener{

    HashMap<String,Experience> experienceHashMap;
    private static final String ARG_EXPERIENCE_LIST = "ExperienceList";
    private OnEditExperienceListener mListener;
    ListView experienceListView;
    ExperienceListAdapter experienceListAdapter;
    Button commit;
    Button addExperienceButton;
    public EditExperienceDetails() {
        // Required empty public constructor
    }

    public static EditExperienceDetails newInstance(HashMap<String, Experience> edStringExperienceHashMap) {
        EditExperienceDetails fragment = new EditExperienceDetails();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXPERIENCE_LIST, edStringExperienceHashMap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            experienceHashMap = (HashMap<String,Experience>) getArguments().getSerializable(ARG_EXPERIENCE_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_experience_details, container, false);
        experienceListView = (ListView) v.findViewById(R.id.experienceDetailsList);
        experienceListAdapter = new ExperienceListAdapter(experienceHashMap);
        experienceListView.setAdapter(experienceListAdapter);
        experienceListView.setOnItemClickListener(this);
        addExperienceButton = (Button) v.findViewById(R.id.addExperience);
        commit = (Button) v.findViewById(R.id.commit);

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onExperienceAdded(experienceHashMap);
            }
        });

        addExperienceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Experience experience = new Experience();
                final Dialog dialog = new Dialog(getContext());
                Window dialogWindow = dialog.getWindow();
                if(dialogWindow!=null){
                    dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
                    dialogWindow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

                }
                dialog.setContentView(R.layout.add_experience_dialog);

                final EditText companyName = (EditText) dialog.findViewById(R.id.company);
                final EditText position = (EditText) dialog.findViewById(R.id.position);
                final EditText start = (EditText) dialog.findViewById(R.id.startDate);
                final EditText end = (EditText) dialog.findViewById(R.id.endDate);
                Button ok = (Button) dialog.findViewById(R.id.ok);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);

                companyName.setText(experience.companyName);
                position.setText(experience.title);
                start.setText(experience.startDate);
                end.setText(experience.endDate);


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isInputValid()){
                            experience.companyName = companyName.getText().toString().trim();
                            experience.title = position.getText().toString().trim();
                            experience.startDate = start.getText().toString().trim();
                            experience.endDate = end.getText().toString().trim();
                            experienceHashMap.put(experience.companyName + experienceHashMap.size()+1 ,experience);
                            experienceListView.setAdapter(new ExperienceListAdapter(experienceHashMap));
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
                        }
                    }

                    private boolean isInputValid(){
                        if(TextUtils.equals(companyName.getText().toString().trim(),"")) return false;
                        else if (TextUtils.equals(start.getText().toString().trim(),"")) return false;
                        else if(TextUtils.equals(end.getText().toString().trim(),"")) return false;
                        else if(TextUtils.equals(position.getText().toString().trim(),"")) return false;
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
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        HashMap.Entry<String, Experience> entry = (HashMap.Entry<String, Experience>) adapterView.getItemAtPosition(i);
        Experience selectedExp = entry.getValue();
        showExperienceEditor(entry.getKey(),selectedExp);
    }

    private void showExperienceEditor(final String key, final Experience experience){

        final Dialog dialog = new Dialog(getContext());
        Window dialogWindow = dialog.getWindow();
        if(dialogWindow!=null){
            dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setContentView(R.layout.add_experience_dialog);

        final EditText companyName = (EditText) dialog.findViewById(R.id.company);
        final EditText position = (EditText) dialog.findViewById(R.id.position);
        final EditText start = (EditText) dialog.findViewById(R.id.startDate);
        final EditText end = (EditText) dialog.findViewById(R.id.endDate);
        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        companyName.setText(experience.companyName);
        position.setText(experience.title);
        start.setText(experience.startDate);
        end.setText(experience.endDate);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()){
                    experience.companyName = companyName.getText().toString().trim();
                    experience.title = position.getText().toString().trim();
                    experience.startDate = start.getText().toString().trim();
                    experience.endDate = end.getText().toString().trim();
                    experienceHashMap.put(key ,experience);
                    experienceListView.setAdapter(new ExperienceListAdapter(experienceHashMap));
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
                }
            }

            private boolean isInputValid(){
                if(TextUtils.equals(companyName.getText().toString().trim(),"")) return false;
                else if (TextUtils.equals(start.getText().toString().trim(),"")) return false;
                else if(TextUtils.equals(end.getText().toString().trim(),"")) return false;
                else if(TextUtils.equals(position.getText().toString().trim(),"")) return false;
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




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditExperienceListener) {
            mListener = (OnEditExperienceListener) context;
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


    public interface OnEditExperienceListener {

        void onExperienceAdded(HashMap<String, Experience> updatedExperienceList);
    }

    private class ExperienceListAdapter extends BaseAdapter {
        private final ArrayList mData;

        ExperienceListAdapter(Map<String, Experience> map) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<String, Experience> getItem(int position) {
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
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.experience_background, parent, false);
            } else {
                result = convertView;
            }
            final Map.Entry<String, Experience> item = getItem(position);
            ((TextView) result.findViewById(R.id.companyName)).setText(item.getValue().companyName);
            ((TextView) result.findViewById(R.id.position)).setText(item.getValue().title);
            ((TextView) result.findViewById(R.id.duration)).setText(item.getValue().startDate + " - " + item.getValue().endDate);
            return result;
        }
    }
}
