package com.sdsu.deepak.sdsuhometownlocations;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String COUNTRY = "Country";
    private static final String STATE = "State";
    private static final String YEAR = "Year";
    private static final String CONDITION = "Condition";
    private static final String USER_DATA ="User Data";

    private String countryName;
    private String stateName;
    private String year;
    private ArrayList<User> usersList;
    private ArrayList<Parcelable> parcelableUserList;
    ListView userListView;
    private OnUserSelectionListener onUserSelectionListener;
    FloatingActionButton allUserButton;
    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment newInstance(String country, String state,String year) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(COUNTRY, country);
        args.putString(STATE, state);
        args.putString(YEAR,year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            countryName = getArguments().getString(COUNTRY);
            stateName = getArguments().getString(STATE);
            year = getArguments().getString(YEAR);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        usersList = new ArrayList<>();
        View view =  inflater.inflate(R.layout.fragment_user_list, container, false);
        userListView = (ListView) view.findViewById(R.id.userList);
        userListView.setOnItemClickListener(this);
        allUserButton = (FloatingActionButton) view.findViewById(R.id.view_all_users);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String url;
        // Initial condition for evaluating the search query
        if(countryName==null){countryName="Select Country";}
        if(stateName==null){stateName="All";}
        else {
            try {
                stateName = URLEncoder.encode(stateName, "utf-8");
            } catch (Exception e) {
                Log.i("MainActivity", "Invalid URL");
            }
        }
        if(year==null){year="Select Year";}
        // Setting up url based on values selected by the user
        if(!stateName.equals("All") && !countryName.equals("Select Country") && year.equals("Select Year")){
            // Country and State
            url = Url.STATE_FILTER+stateName;
        }
        else if(stateName.equals("All") && !countryName.equals("Select Country") && !year.equals("Select Year")) {
            // Country and Year
            url = Url.COUNTRY_FILTER+countryName+"&year="+year;
        }
        else if(stateName.equals("All") && !countryName.equals("Select Country") && year.equals("Select Year")){
            // Only Country
            url = Url.COUNTRY_FILTER+countryName;
        }
        else if(stateName.equals("All") && countryName.equals("Select Country") && !year.equals("Select Year")){
            // Only year
            url = Url.YEAR_FILTER+year;
        }
        else if(!stateName.equals("All") && !countryName.equals("Select Country") && !year.equals("Select Year")){
            // Country, State and  Year
            url = Url.COUNTRY_FILTER+countryName+"&state="+stateName+"&year="+year;
        }
        else{
            // All users
            url = Url.BASE_URL;
        }
        Log.i("MainActivity",url);
        getUserListFromServer(url);
        // Locates all the users on the map
        allUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent allUserOnMap = new Intent(getActivity(),UserLocationMapActivity.class);
                allUserOnMap.putExtra(CONDITION,"All Users");
                allUserOnMap.putExtra(USER_DATA,usersList);
                startActivity(allUserOnMap);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserSelectionListener) {
            onUserSelectionListener = (OnUserSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUserSelectionListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        User selectedUser = (User)adapterView.getItemAtPosition(i);
        // Locates the selected user on the map
        Intent map = new Intent(this.getActivity(),UserLocationMapActivity.class);
        map.putExtra(CONDITION,"Single User");
        map.putExtra(USER_DATA,selectedUser);
        startActivity(map);
    }

    public interface OnUserSelectionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * This method is used to retrieve the users information from the server
     * @param url
     */
    private void getUserListFromServer(String url){
        Log.i("User Fragment","Query :"+url);
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.i("Main Activity", response.toString());
                try{
                    for(int i = 0 ; i < response.length();i++) {
                        usersList.add(new Gson().fromJson(response.get(i).toString(), User.class));
                    }
                    Log.i("MainActivity",response.toString());
                    if(usersList.isEmpty()){
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Oops!");
                        alertDialog.setMessage("No People Found!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        getFragmentManager().popBackStack();
                                    }
                                });
                        alertDialog.show();
                    }
                    else{
                    displayListOfUsers(usersList);}
                }catch(JSONException e){
                    Log.i("Main Activity",e.toString());
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.i("Main Activity", error.toString());
                int status = error.networkResponse.statusCode;
                if(status == 404){
                    Toast errorMessage = Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT);
                    errorMessage.show();
                }
                else if(status == -1){
                    Toast errorMessage = Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT);
                    errorMessage.show();
                }
                else {
                    Toast errorMessage = Toast.makeText(getContext(),"No Error",Toast.LENGTH_SHORT);
                    errorMessage.show();
                }
            }
        };
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, success,failure);
        VolleyQueue.instance(this.getActivity()).add(jsObjRequest);
    }
    // This method is invoked while displaying the users list
    private void displayListOfUsers(ArrayList<User> userList){
        CustomListAdapter customListAdapter=new CustomListAdapter(this.getActivity(), userList);
        userListView.setAdapter(customListAdapter);
    }

}
