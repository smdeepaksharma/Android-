package com.sdsu.deepak.sdsuhometownlocations;

import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FilterFragment.OnFilterChangeListener, UserListFragment.OnUserSelectionListener, UserInformationFragment.OnFragmentInteractionListener{

    private ArrayList<String> countryList = new ArrayList<>();
    private ArrayList<String> yearList = new ArrayList<>();
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    // Fragment Names, used while adding fragments to back stack
    private static String USER_LIST_FRAGMENT = "User List Fragment";
    private static String USER_INFORMATION_FRAGMENT = "User Information Fragment";
    private static String FILTER_FRAGMENT = "Filter Fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Adding fragment
        countryList.add("Select Country");
        getCountryListFromServer();
        int currentYear = 2017;
        int startYear = 1970;
        yearList.add("Select Year");
        for(int i = startYear;i<=currentYear;i++){
            yearList.add(String.valueOf(i));
        }
        Log.i("MainActivity",yearList.toString());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //*********************** Navigation Drawer ****************************/
        //Initializing Navigation View
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.view_users:
                        showFilterFragment();
                        return true;
                    case R.id.add_user_information:
                        showUserInformationFragment();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    /**
     * The search criteria provided by the user is passed by the fragment to the MainActivity.
     * The query is then used to retrieve users from the user.
     * @param filterOptions
     */
    @Override
    public void onFragmentInteraction(Bundle filterOptions) {
        showUserListFragment(filterOptions.getString("Country"),filterOptions.getString("State"),filterOptions.getString("Year"));
    }

    /**
     * This method is invoked in the beginning to inflate the FilterFragment
     */
    private void showFilterFragment(){
        FilterFragment filterFragment = FilterFragment.newInstance(countryList,yearList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.common_container,filterFragment,FILTER_FRAGMENT);
        transaction.addToBackStack(FILTER_FRAGMENT);
        transaction.commit();
    }

    /**
     * This method is invoked when user clicks on Search button in the Filter Fragment.
     * It is used to inflate UserListFragment to display the list of users retrieved from the server
     * @param country
     * @param state
     * @param year
     */
    private void showUserListFragment(String country,String state,String year){
        UserListFragment userListFragment = UserListFragment.newInstance(country,state,year);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack(USER_INFORMATION_FRAGMENT,1);
        transaction.replace(R.id.common_container,userListFragment,USER_LIST_FRAGMENT);
        transaction.addToBackStack(USER_LIST_FRAGMENT);
        transaction.commit();
    }

    /**
     * This method is used to inflate the UserInformationFragment
     * UserInformation Fragment contains Form to obtain user data.
     */
    private void showUserInformationFragment(){
        UserInformationFragment userInformationFragment = UserInformationFragment.newInstance(countryList,yearList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.common_container,userInformationFragment,USER_INFORMATION_FRAGMENT);
        getSupportFragmentManager().popBackStack(USER_LIST_FRAGMENT,1);
        transaction.addToBackStack(USER_INFORMATION_FRAGMENT);
        transaction.commit();

    }

    /**
     * This method is used to retrieve the list of country names from the server
     */
    private void getCountryListFromServer(){
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                try{
                    for(int i=0;i<response.length();i++){
                        countryList.add(response.getString(i));
                    }
                    showFilterFragment();
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
        JsonArrayRequest request = new JsonArrayRequest( Url.COUNTRY_NAMES, success, failure);
        VolleyQueue.instance(this).add(request);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount()<=0){
            finish();
        }
    }
}
