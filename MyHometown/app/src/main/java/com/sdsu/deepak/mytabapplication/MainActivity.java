package com.sdsu.deepak.mytabapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements FilterFragment.OnFilterChangeListener, UserListFragment.OnUserSelectionListener{

    Toolbar toolbar;

    ArrayList<User> usersList = new ArrayList<>();
    ArrayList<User> moreUserDataList = new ArrayList<>();
    ArrayList<String> countryList = new ArrayList<>();
    ArrayList<String> yearList = new ArrayList<>();

    String currentUrl, currentSQLQuery;
    int totalUsersCount;

    // Fragment Names, used while adding fragments to back stack
    private static String USER_LIST_FRAGMENT = "User List Fragment";
    private static String USER_INFORMATION_FRAGMENT = "User Information Fragment";
    private static String FILTER_FRAGMENT = "Filter Fragment";

    // Database interactions
    private static final String PERSIST_USER_DATA = "PersistUserData";
    private static final String RETRIEVE_USER_DATA = "RetrieveUserData";
    private static final String LOAD_MORE_DATA = "LoadMoreData";
    private static final String GET_COUNTRY_LIST = "CountryList";
    private static final String STORE_COUNTRY_LIST = "StoreCountryList";

    // bismark Server Interactions
    private static final String COUNTRY_FILTER = "Country";
    private static final String COUNTRY_STATE_FILTER = "Country and State";
    private static final String COUNTRY_STATE_YEAR_FILTER = "Country, State and Year";

    // For intents
    private static final String USER_LIST = "UserList";
    private static final String CURRENT_URL ="Url";
    private static final String CURRENT_QUERY ="Query";

    // For logging
    private static final String TAG = "Hometown | MainActivity";
    private static final String SELECT ="Select";
    private static final String ALL = "All";
    DataBaseHelper databaseHelper;
    boolean isCheckingForUpdates = false;
    int nextIdInDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        databaseHelper = new DataBaseHelper(getApplicationContext());
        databaseHelper.displayDatabaseInfo();
        setUpSpinnerListData();

        // Get total count of users in the server
        new BismarkServerInteraction().getUserCount();

    }


    /**
     * this method will initialize the country and year spinner
     */
    private void setUpSpinnerListData(){
        // Setting up year spinner
        Log.i(TAG,"In setUpSpinnerListData");
        if(yearList.isEmpty()){
            yearList.add("Select");
            for(int i = 2017; i >= 1790; i--){
                yearList.add(String.valueOf(i));
            }
        }
       // Setting up country spinner
        DataHelper countryDataRequest = new DataHelper();
        countryDataRequest.execute(GET_COUNTRY_LIST);
    }


    /**
     * This method is invoked in the beginning to inflate the FilterFragment
     */
    public void showFilterFragment(){
        Log.i(TAG,"In showFilterFragment");
        FilterFragment filterFragment = FilterFragment.newInstance(countryList,yearList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.filter_fragment_container,filterFragment,FILTER_FRAGMENT);
        transaction.addToBackStack(FILTER_FRAGMENT);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this,
                                        "You have been signed out.",
                                        Toast.LENGTH_LONG)
                                        .show();
                                // Close activity
                                finish();
                            }
                        });
                break;
            case R.id.list_view:
                UserListFragment userListFragment = UserListFragment.newInstance(usersList,currentUrl,currentSQLQuery);
                getSupportFragmentManager().beginTransaction().replace(R.id.user_list_container,userListFragment).commit();
                break;
            case R.id.map_view:
                Intent intent = new Intent(MainActivity.this, MapViewActivity.class);
                intent.putStringArrayListExtra(Keys.COUNTRY_LIST,countryList);
                intent.putStringArrayListExtra(Keys.YEAR_LIST,yearList);
                startActivity(intent);
                finish();
                break;
        }
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //**************************************************************************************//
    /////////////////////////////// DATABASE INTERACTION ////////////////////////////////////
    //**************************************************************************************//

    private class DataHelper extends AsyncTask<String,User,String>{


        ArrayList<User> userArrayList = new ArrayList<>();
        String action;
        String postAction = "No Display";
        Activity currentActivity;

        public Activity getCurrentActivity() {
            return currentActivity;
        }

        public void setCurrentActivity(Activity currentActivity) {
            this.currentActivity = currentActivity;
        }

        void setUserArrayList(ArrayList<User> userArrayList){
            this.userArrayList = userArrayList;
        }
        public ArrayList<User> getUserArrayList(){
            return userArrayList;
        }

        @Override
        protected String doInBackground(String... params) {

            action = params[0];
            String filter[] = {};

            switch (action){

                case COUNTRY_FILTER:
                    userArrayList = databaseHelper.retrieveUserTable(DbContract.COUNTRY_QUERY);
                    postAction = "Display";
                    break;

                case PERSIST_USER_DATA:
                    databaseHelper.addUserTable(usersList);
                    break;

                case RETRIEVE_USER_DATA:
                    Log.i(TAG,"In Retrieve user Data");
                    // Prepare the sql query
                    String sqlQry = QueryAndUrlHelper.prepareSQLQuery(params[1],params[2],params[3]);
                    String url = QueryAndUrlHelper.prepareServerAPIUrl(params[1],params[2],params[3]);
                    currentUrl = url;
                    currentSQLQuery = sqlQry;
                    // fetching data from data base
                    usersList = databaseHelper.retrieveUserTable(sqlQry);
                    if(usersList.isEmpty()){
                        Log.i(TAG,"Fetching user data from server...");
                        Log.i(TAG,"Using URL"+url);
                        new BismarkServerInteraction().getUserDataFromServer(url);
                    } else {
                        postAction = "ShowUserList";
                    }
                    break;

                case LOAD_MORE_DATA :
                    // TODO : check db for more data, if not available get ot from server
                    moreUserDataList = databaseHelper.retrieveUserTable(DbContract.GET_MORE_USER_DATA+params[1]);


                case GET_COUNTRY_LIST:
                    Log.i(TAG,"In get country list");
                    countryList = databaseHelper.getCountryListFromDB();
                    if(countryList.isEmpty()){
                        Log.i(TAG,"In country list empty, fetching from server...");
                        new BismarkServerInteraction().getCountryListDataFromServer();
                    } else{
                        Log.i(TAG,"In country list not empty, displaying filter fragment");
                        showFilterFragment();
                    }
                    break;

                case STORE_COUNTRY_LIST :
                     databaseHelper.setCountryListInDB(countryList);
                    break;

            }
            return postAction ;
        }

        @Override
        protected void onPostExecute(String postAction) {
            super.onPostExecute(postAction);
            switch (postAction){
                case "Display":
               /*   Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                    Bundle bundle = new Bundle();
                    if(userArrayList.isEmpty()){Log.i("MainActivity","Empty");}
                    bundle.putParcelableArrayList("Data",userArrayList);
                    intent.putExtra("User Data",bundle);
                    startActivity(intent)*/;
                case "ShowUserList" :
                    UserListFragment userListFragment = UserListFragment.newInstance(usersList,currentUrl,currentSQLQuery);
                    getSupportFragmentManager().beginTransaction().replace(R.id.user_list_container,userListFragment).commit();

            }

        }
    }

    /**8************************** END OF DATABASE INTERACTION ******************************/

    //**************************************************************************************//
    ///////////////////////FRAGMENT INTERACTION LISTENERS////////////////////////////////////
    //**************************************************************************************//

    @Override
    public void onFragmentInteraction(Bundle filterOptions) {
        Log.i(TAG,"MActivity filter interaction");
        String country = filterOptions.getString("Country");
        String state = filterOptions.getString("State");
        String year = filterOptions.getString("Year");
        isCheckingForUpdates = true;
        DataBaseHelper db = new DataBaseHelper(this);
        nextIdInDB = db.getMaxIdFromDB();
        new BismarkServerInteraction().getNextIdFromServer(country,state,year,nextIdInDB);
    }

    @Override
    public void showUsersOmMap(Bundle filterOptions) {

    }




    //**************************************************************************************//
    /////////////////////////////   SERVER INTERACTION   ////////////////////////////////////
    //**************************************************************************************//
     private class BismarkServerInteraction {

        private static final String TAG = "ServerInteraction";
        int nextId;

        private int getNextId(){

            Response.Listener<String> success = new Response.Listener<String>() {
                public void onResponse(String response) {
                    Log.i(TAG, response);
                    nextId = Integer.parseInt(response);
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    int status = error.networkResponse.statusCode;
                    if(status == 404 || status == -1){
                        Toast errorMessage = Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT);
                        errorMessage.show();
                    }
                }
            };
            StringRequest jsObjRequest = new StringRequest(Request.Method.GET, Url.NEXT_ID, success,failure);
            VolleyQueue.instance(getApplicationContext()).add(jsObjRequest);
            return nextId;
        }

        public void getUserDataFromServer(final String filterOptions){
            Log.i(TAG,"Query :"+filterOptions);
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    Log.i(TAG, response.toString());
                    try{
                        if(response.length() > 0){
                            usersList.clear();
                            Log.i(TAG,response.toString());
                            for(int i = 0 ; i < response.length();i++) {
                                usersList.add(new Gson().fromJson(response.get(i).toString(), User.class));
                            }
                            Log.i(TAG,"Inflating UFL with "+currentUrl+0);
                            UserListFragment fragment = UserListFragment.newInstance(usersList,currentUrl+0,currentSQLQuery);
                            getSupportFragmentManager().beginTransaction().replace(R.id.user_list_container,fragment).commit();
                            DataHelper dataHelper = new DataHelper();
                            dataHelper.execute(PERSIST_USER_DATA);

                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
                            usersList.clear();
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
                        Toast errorMessage = Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT);
                        errorMessage.show();
                    }
                }
            };
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, filterOptions+0, null, success,failure);
            VolleyQueue.instance(getApplicationContext()).add(jsObjRequest);
        }


        private void getCountryListDataFromServer(){
            Log.i(TAG,"Inside getCountryListDataFromServer()");
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    Log.i(TAG, response.toString());
                    try{
                        if(response.length() > 0){
                            Log.i(TAG,response.toString());
                            countryList.add("Select");
                            for(int i = 0 ; i < response.length();i++) {
                                countryList.add(new Gson().fromJson(response.get(i).toString(), String.class));
                            }
                            Log.i(TAG,"In showing filter fragment...");
                            showFilterFragment();
                            Log.i(TAG,"In storing country names in database...");
                            DataHelper dataHelper = new DataHelper();
                            dataHelper.execute(STORE_COUNTRY_LIST);
                        } else {
                            // No Users, return an empty list
                            countryList.clear();
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
                        Toast errorMessage = Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT);
                        errorMessage.show();
                    }
                }
            };
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, Url.COUNTRY_NAMES, null, success,failure);
            VolleyQueue.instance(getApplicationContext()).add(jsObjRequest);
        }

        private void getUserCount(){
            Log.i(TAG,"Inside getCountryListDataFromServer()");
            Response.Listener<String> success = new Response.Listener<String>() {
                public void onResponse(String response) {
                    totalUsersCount = Integer.parseInt(response);
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    int status = error.networkResponse.statusCode;
                    if(status == 404 || status == -1){
                        Toast errorMessage = Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT);
                        errorMessage.show();
                    }
                }
            };
            StringRequest jsObjRequest = new StringRequest(Request.Method.GET, Url.COUNT, success,failure);
            VolleyQueue.instance(getApplicationContext()).add(jsObjRequest);
        }

        private void getNextIdFromServer(final String country, final String state,final String year, final int nextIdInDB){

            Response.Listener<String> success = new Response.Listener<String>() {
                public void onResponse(String response) {
                    Log.i(TAG, response);
                    nextId = Integer.parseInt(response);
                    isCheckingForUpdates = false;
                    if(nextId > nextIdInDB){
                        Log.i(TAG,"New data available");
                        getUserDataFromServer(QueryAndUrlHelper.prepareServerAPIUrl(country,state,year) + 0 + " &afterid=" + nextIdInDB);
                        currentUrl = QueryAndUrlHelper.prepareServerAPIUrl(country,state,year);
                        currentSQLQuery = QueryAndUrlHelper.prepareSQLQuery(country,state,year);
                    } else {
                        Log.i(TAG,"No data available");
                        DataHelper userDataRequest = new DataHelper();
                        userDataRequest.execute(RETRIEVE_USER_DATA,country,state,year);
                        currentUrl = QueryAndUrlHelper.prepareServerAPIUrl(country,state,year);
                    }

                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    int status = error.networkResponse.statusCode;
                    if(status == 404 || status == -1){
                        Toast errorMessage = Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT);
                        errorMessage.show();
                    }
                }
            };
            StringRequest jsObjRequest = new StringRequest(Request.Method.GET, Url.NEXT_ID, success,failure);
            VolleyQueue.instance(getApplicationContext()).add(jsObjRequest);
        }
    }




}
