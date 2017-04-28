package com.sdsu.deepak.mytabapplication;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserListFragment extends Fragment implements AdapterView.OnItemClickListener, LoadMoreDataImpl.FragmentCommunicator{

    private static final String USER_LIST = "UserList";
    private static final String STATE = "State";
    private static final String YEAR = "Year";
    private static final String CONDITION = "Condition";
    private static final String USER_DATA ="User Data";
    private static final String CURRENT_URL = "CurrentUrl";
    private static final String CURRENT_QUERY = "CurrentQuery";
    private static final String TAG ="Hometown | UserList";
    User firstVisibleUser;
    private String countryName;
    private String stateName;
    private String year;
    private String currentUrl;
    private String currentSQLQuery;
    private ArrayList<User> usersList;
    private ArrayList<FireBaseUser> userForChat;
    ListView userListView;
    ProgressBar progressBar;
    TextView endOfResults;
    int pageNumber;
    boolean isLoadingData = false;
    boolean isCheckingForUpdates = false;
    CustomListAdapter customListAdapter;
    SwipeRefreshLayout baseLayout;
    int nextId, nextIdInDB;

    private OnUserSelectionListener onUserSelectionListener;

    FloatingActionButton allUserButton;
    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment newInstance(ArrayList<User> userList, String filterOptionsUrl, String sqlQuery) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(USER_LIST, userList);
        args.putString(CURRENT_URL,filterOptionsUrl);
        args.putString(CURRENT_QUERY,sqlQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = 0;
            usersList= getArguments().getParcelableArrayList(USER_LIST);
            currentSQLQuery = getArguments().getString(CURRENT_QUERY);
            currentUrl = getArguments().getString(CURRENT_URL);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_users_list, container, false);

        baseLayout = (SwipeRefreshLayout) view.findViewById(R.id.base_frame_layout);
        baseLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                refreshUserList(currentUrl);
            }
        });


        userListView = (ListView) view.findViewById(R.id.userList);
        userListView.setOnItemClickListener(this);
        progressBar = new ProgressBar(getContext());

        userForChat = new ArrayList<>();
        userForChat = getAllUsersFromFirebase();

       // allUserButton = (FloatingActionButton) view.findViewById(R.id.view_all_users);
        userListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //Log.i(TAG,"ScrollStateChanged i : "+scrollState);
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
               Log.i(TAG,"FV "+ firstVisibleItem +" VIC : "+visibleItemCount+" T : "+totalItemCount);
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(!isLoadingData)
                    {
                            isLoadingData = true;
                            Log.i(TAG,"Load more data..."+(totalItemCount - 1));
                            loadMoreData(totalItemCount - 1);
                    }
                }
            }
        });
        return view;
    }


    private void loadMoreData(int totalItemCount){
        LoadMoreDataImpl loadMoreData = new LoadMoreDataImpl(this, getContext());
        loadMoreData.execute(currentSQLQuery,String.valueOf(totalItemCount));
    }

    @Override
    public void sendMoreDataToFragment(ArrayList<User> moreUserData) {
        Log.i(TAG,"sendMoreDataToFragment");
        if(moreUserData.isEmpty()){
            isLoadingData = true;
            Log.i(TAG,"No data in database");
            pageNumber = pageNumber + 1;
            Log.i(TAG,"Using Server API "+currentUrl+pageNumber);
            getMoreUserDataFromServer(currentUrl+pageNumber);
            // TODO : get Data from server
        } else {
            isLoadingData = false;
            for(User user : moreUserData){
                usersList.add(user);
            }
            customListAdapter.notifyDataSetChanged();
        }
    }

    private void refreshUserList(String filterOptions){
        Log.i(TAG,"Refreshing data");
        final ArrayList<User> newUserData = new ArrayList<>();
        final ArrayList<User> swap = new ArrayList<>();

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.i(TAG, response.toString());
                int responseLength = response.length();
                try{
                    if(responseLength > 0){
                        Log.i(TAG,"Found new users");
                        swap.addAll(usersList);
                        Log.i(TAG,response.toString());
                        for(int i = 0 ; i < response.length();i++) {
                            newUserData.add(new Gson().fromJson(response.get(i).toString(), User.class));
                        }
                        usersList.clear();
                        usersList.addAll(newUserData);
                        usersList.addAll(swap);
                        customListAdapter.notifyDataSetChanged();
                        isLoadingData = false;
                        baseLayout.setRefreshing(false);
                        Snackbar.make(baseLayout, responseLength + " new users",
                                Snackbar.LENGTH_LONG)
                                .show();
                        DataBaseHelper db = new DataBaseHelper(getContext());
                        db.addUserTable(newUserData);
                    } else {
                        Log.i(TAG,"No new users found");
                        baseLayout.setRefreshing(false);
                    }
                }catch(JSONException e){
                    Log.i(TAG,e.toString());
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                int status = error.networkResponse.statusCode;
                if(status == 404 || status == -1){
                    Toast errorMessage = Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT);
                    errorMessage.show();
                }
            }
        };
        Log.i(TAG,"Url:"+filterOptions +0+ "&afterid=" + usersList.get(0).getId());
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, filterOptions + "&afterid=" + usersList.get(0).getId(), null, success,failure);
        VolleyQueue.instance(getContext()).add(jsObjRequest);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       Log.i(TAG,"onActivityCreated");
        displayListOfUsers(usersList);
        // Locates all the users on the map
     /*   allUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent allUserOnMap = new Intent(getActivity(),UserLocationMapActivity.class);
                allUserOnMap.putExtra(CONDITION,"All Users");
                allUserOnMap.putExtra(USER_DATA,usersList);
                startActivity(allUserOnMap);
            }
        });*/
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
        Log.i(TAG,"onItemClick");
        User selectedUser = (User)adapterView.getItemAtPosition(i);
        Log.i(TAG,"selcted "+selectedUser.getNickname());
        ArrayList<FireBaseUser> availableForChat = getAllUsersFromFirebase();
        for(FireBaseUser user : userForChat){
            Log.i(TAG,"Fb name"+user.displayName);
            if(user.displayName.equalsIgnoreCase(selectedUser.getNickname())){
                Log.i(TAG,"Inside firebase");
                Intent chat = new Intent(this.getActivity(),ChatActivity.class);
                chat.putExtra(USER_DATA,selectedUser);
                chat.putExtra("userid",user.uid);
                startActivity(chat);
            }
        }
    }



    public interface OnUserSelectionListener {
        void onFragmentInteraction(Uri uri);
       // ArrayList<User> loadMoreData(int nextId);

    }


    // This method is invoked while displaying the users list
    private void displayListOfUsers(ArrayList<User> userList){
        Log.i(TAG,"displayListOfUsers");
        userListView.addFooterView(progressBar);
        customListAdapter=new CustomListAdapter(this.getActivity(), userList);
        userListView.setAdapter(customListAdapter);

    }


    public void getMoreUserDataFromServer(String filterOptions){
        Log.i(TAG,"URL :"+filterOptions);
        final ArrayList<User> newUserData = new ArrayList<>();
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.i(TAG, response.toString());
                try{
                    if(response.length() > 0){
                        Log.i(TAG,response.toString());
                        for(int i = 0 ; i < response.length();i++) {
                            newUserData.add(new Gson().fromJson(response.get(i).toString(), User.class));
                            usersList.add(new Gson().fromJson(response.get(i).toString(), User.class));
                        }
                        customListAdapter.notifyDataSetChanged();
                        isLoadingData = false;
                        //TODO : store new data in database
                        DataBaseHelper db = new DataBaseHelper(getContext());
                        db.addUserTable(newUserData);
              } else {
                        endOfResults = new TextView(getContext());
                        endOfResults.setText(usersList.size() + " users found!");
                        endOfResults.setTextSize(20);
                        endOfResults.setGravity(Gravity.CENTER);
                       // progressBar.setVisibility(View.GONE);
                        userListView.removeFooterView(progressBar);
                        userListView.addFooterView(endOfResults);

                    }
                }catch(JSONException e){
                    Log.i(TAG,e.toString());
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                int status = error.networkResponse.statusCode;
                if(status == 404 || status == -1){
                    Toast errorMessage = Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT);
                    errorMessage.show();
                }
            }
        };
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, filterOptions, null, success,failure);
        VolleyQueue.instance(getContext()).add(jsObjRequest);
    }

    public ArrayList<FireBaseUser> getAllUsersFromFirebase() {
        Log.i(TAG,"getAllUsersFromFirebase");
        final ArrayList<FireBaseUser> users = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    FireBaseUser user = dataSnapshotChild.getValue(FireBaseUser.class);
                    Log.i(TAG,"FB user"+user.displayName);
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        userForChat.add(user);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return users;
    }



}
