package com.sdsu.deepak.homework2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link StateListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StateListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    StateSelectionListener stateSelectionListener;
    ArrayList<String> stateList = new ArrayList<>();

    public StateListFragment() {
        // Required empty public constructor
    }

    public static StateListFragment newInstance(ArrayList<String> stateList) {
        StateListFragment fragment = new StateListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("StateList",stateList);
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_state_list, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stateSelectionListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            stateList = bundle.getStringArrayList("StateList");
                Log.i("Country List","Country List not Empty");
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,stateList);
                setListAdapter(adapter);
        }
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StateSelectionListener) {
            stateSelectionListener = (StateSelectionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + "Implement CountrySelectionListener");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        view.setSelected(true);
        stateSelectionListener.onStateSelection(stateList.get(position));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface StateSelectionListener{
        public void onStateSelection(String stateName);
    }
}
