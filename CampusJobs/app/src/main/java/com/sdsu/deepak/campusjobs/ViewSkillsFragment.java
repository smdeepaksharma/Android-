package com.sdsu.deepak.campusjobs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class ViewSkillsFragment extends Fragment {

    private static final String ARG_SKILLS = "Skills";
    private ArrayList<String> skillsList;
    private OnSkillsAddedListener mListener;
    ListView skillsListView;
    EditText addSkillEditTextView;
    Button addSkillButton, saveChanges;

    public ViewSkillsFragment() {
        // Required empty public constructor
    }

    public static ViewSkillsFragment newInstance(ArrayList<String> skillsList) {
        ViewSkillsFragment fragment = new ViewSkillsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_SKILLS, skillsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            skillsList = getArguments().getStringArrayList(ARG_SKILLS);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_skills, container, false);
        skillsListView = (ListView) v.findViewById(R.id.skills);
        addSkillEditTextView = (EditText) v.findViewById(R.id.addNewSkill);
        addSkillButton = (Button) v.findViewById(R.id.add);
        saveChanges = (Button) v.findViewById(R.id.saveAllSkills);

        final ArrayAdapter<String> skillSetAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,skillsList);
        skillsListView.setAdapter(skillSetAdapter);

        addSkillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String skill = addSkillEditTextView.getText().toString().trim();
                if(TextUtils.equals(skill,"")){
                    Toast.makeText(getContext(),"Invalid skill",Toast.LENGTH_SHORT).show();
                } else{
                    addSkillEditTextView.setText("");
                    skillsList.add(skill);
                    skillSetAdapter.notifyDataSetChanged();
                }
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSkillAdded(skillsList);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSkillsAddedListener) {
            mListener = (OnSkillsAddedListener) context;
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

    interface OnSkillsAddedListener {
        void onSkillAdded(ArrayList<String> updatedSkillSet);
    }
}
