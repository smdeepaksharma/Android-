package com.sdsu.deepak.sdsuhometownlocations;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

/**
 * FilterFragment is a Fragment containing country, state and year filter.
 * Users can search for friends using these three conditions.
 * @author Deepak
 */
public class FilterFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String ARG_PARAM1 = "Country Names List";
    private static final String ARG_PARAM2 = "Year List";
    String selectedCountryName;
    String selectedStateName;
    String yearSelected;
    private ArrayList<String> countryList;
    private ArrayList<String> stateList;
    private ArrayList<String> yearList;
    Spinner stateSpinner;
    private OnFilterChangeListener mListener;

    int check = 0;
    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(ArrayList<String> countryNames, ArrayList<String> yearList) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, countryNames);
        args.putStringArrayList(ARG_PARAM2, yearList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateList = new ArrayList<>();
        if (getArguments() != null) {
            countryList = getArguments().getStringArrayList(ARG_PARAM1);
            yearList = getArguments().getStringArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_filter, container, false);
        // Populating the country list spinner
        Spinner countryListSpinner = (Spinner) v.findViewById(R.id.country_list_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, countryList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        countryListSpinner.setAdapter(adapter);
        countryListSpinner.setOnItemSelectedListener(this);

        // Population the year list spinner
        Spinner yearListSpinner = (Spinner) v.findViewById(R.id.year_spinner);
        final ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        yearListSpinner.setAdapter(yearAdapter);
        yearListSpinner.setOnItemSelectedListener(this);

        // setting up state spinner
        stateSpinner = (Spinner) v.findViewById(R.id.state_list_spinner);
        stateSpinner.setOnItemSelectedListener(this);

        // Setting up the search button
        Button searchButton = (Button) v.findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle filterData = new Bundle();
                filterData.putString("Country",selectedCountryName);
                filterData.putString("State",selectedStateName);
                filterData.putString("Year",yearSelected);
                Log.i("MainActivity","Bundle : "+selectedCountryName+selectedStateName+yearSelected);
                mListener.onFragmentInteraction(filterData);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterChangeListener) {
            mListener = (OnFilterChangeListener) context;
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        if(check++!=0) {
            switch (parent.getId()) {
                case R.id.country_list_spinner:
                    selectedCountryName = parent.getItemAtPosition(position).toString();
                    getStateListFromServer(selectedCountryName);
                    break;
                case R.id.state_list_spinner:
                    selectedStateName = parent.getItemAtPosition(position).toString();
                    break;
                case R.id.year_spinner:
                    yearSelected = parent.getItemAtPosition(position).toString();
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void statePopulate()
    {
        ArrayAdapter adapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, stateList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
    }

    private void getStateListFromServer(String selectedCountry){
        if(!selectedCountry.equalsIgnoreCase("Select Country")) {
            final ArrayList<String> stateNamesList = new ArrayList<>();
            stateNamesList.add("All");
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    try{
                        for(int i=0;i<response.length();i++){
                            stateNamesList.add(response.getString(i));
                        }
                        stateList = stateNamesList;
                        statePopulate();
                        Log.i("MainActivity",response.toString());
                    }
                    catch(JSONException e){
                    Log.i("MainActivity","EmptyList");
                    }
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
                }
            };
            JsonArrayRequest request = new JsonArrayRequest( Url.STATE_NAMES+selectedCountry, success, failure);
            VolleyQueue.instance(this.getActivity()).add(request);
        }
    }

    public interface OnFilterChangeListener {
        void onFragmentInteraction(Bundle filterOptions);
    }
}
