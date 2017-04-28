package com.sdsu.deepak.homework2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
/**
 * Created by Deepak on 2/9/2017.
 * CountryList is a List Fragment hosted by CountryActivity
 * @see android.widget.AdapterView.OnItemClickListener
 * @see android.support.v4.app.ListFragment
 */

public class CountryList extends ListFragment implements AdapterView.OnItemClickListener {

    ArrayList<String> countries;
    ArrayList<String> stateNamesList;
    String countrySelected;
    String readFile;
    FragmentTransaction fragmentTransaction;
    CountrySelectionListener countrySelectionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        stateNamesList = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_country_list, container, false);
    }
    /*
    The country list fragment is populated using a ArrayAdapter.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Retrieving country list from the bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            countries = bundle.getStringArrayList(CountryActivity.COUNTRY_LIST_KEY);
            if(countries.isEmpty()){
                Log.i("Country List","Country List is Empty");
            }
            else {
                Log.i("Country List","Country List not Empty");
                ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, countries);
                setListAdapter(adapter);}
        }
        getListView().setOnItemClickListener(this);
    }
    /*
    The hosting activity's context is obtained in the onAttach() method to create an instance.
    This instance is further used to pass the selected country name back to CountryActivity.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CountrySelectionListener) {
            countrySelectionListener = (CountrySelectionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + "Implement CountrySelectionListener");
        }
    }
    /*  onItemCLick is invoked when user taps on a list item Ex: United States
        Country name received is then used to read state names of the corresponding country
        and fragment manager is used to replace the country_list_fragment with state_list_fragment */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        view.setSelected(true);
        countrySelected=countries.get(position);
        countrySelectionListener.onCountrySelection(countrySelected);
        try{
            InputStream stateFile = getActivity().getAssets().open(countrySelected);
            BufferedReader in = new BufferedReader( new InputStreamReader(stateFile));
            while((readFile=in.readLine())!=null) {
                stateNamesList.add(readFile);
                Log.i("State Name :",readFile);
            }
        }
        catch (IOException e) {
            Log.e("rew", "read Error", e);
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.country_list_container, StateListFragment.newInstance(stateNamesList));
        fragmentTransaction.commit();
    }

    // Interface to communicate with the hosting activity class
    public interface CountrySelectionListener{
        public void onCountrySelection(String countryName);
    }
}
