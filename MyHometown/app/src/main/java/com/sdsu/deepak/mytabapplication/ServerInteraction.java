package com.sdsu.deepak.mytabapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak on 4/5/2017.
 * ServerInteraction class is used to interact with the Bismarck server and store the results
 * obtained in the data base.
 */

class ServerInteraction {

    private static final String TAG = "Hometown|SvrInt";
    private ArrayList<User> usersList = new ArrayList<>();
    private ArrayList<String> stateList = new ArrayList<>();
    private ArrayList<String> countryList = new ArrayList<>();
    private String currentUrl, currentQuery;
    private int maxId, minId, nextId;
    private ResponseHandler responseHandler;
    private Context appContext;
    DataBaseInteraction dataBaseInteraction;

     ServerInteraction(Context appContext){
        this.appContext = appContext;
        if (appContext instanceof ServerInteraction.ResponseHandler) {
            responseHandler = (ServerInteraction.ResponseHandler) appContext;
        } else {
            throw new RuntimeException(appContext.toString()
                    + " must implement ResponseHandler");
        }
        dataBaseInteraction = new DataBaseInteraction();
    }


    void getUserData(Bundle filter){
        String country = filter.getString(Keys.COUNTRY);
        String year = filter.getString(Keys.YEAR);
        String state = filter.getString(Keys.STATE);
        int nextId = filter.getInt(Keys.NEXT_ID);
        currentQuery = QueryAndUrlHelper.prepareSQLQueryForMap(country,state,year,nextId);
        currentUrl = QueryAndUrlHelper.prepareServerAPIForMap(country,state,year,nextId);
        checkDataBaseForUserData(currentQuery);
    }

    private void checkDataBaseForUserData(String query){
        DataBaseInteraction dataBaseInteraction = new DataBaseInteraction();
        dataBaseInteraction.execute(Keys.RETRIEVE_USER_DATA,query);

    }

     ArrayList<User> getUserDataFromServer(String url){
            Log.i(TAG,"Query :"+url);
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    Log.i("Main Activity", response.toString());
                    try{
                        if(response.length() > 0){
                            Log.i(TAG,response.toString());
                            for(int i = 0 ; i < response.length();i++) {
                                usersList.add(new Gson().fromJson(response.get(i).toString(), User.class));
                            }
                            Log.i(TAG,"Saving in database");
                            new DataBaseInteraction().execute(Keys.PERSIST_USER_DATA);
                            GeocodeLocator geocodeLocator = new GeocodeLocator();
                            geocodeLocator.execute("Go");
                        } else {
                            // No Users, return an empty list
                            usersList.clear();
                            responseHandler.userDataListener(usersList);
                        }
                    }catch(JSONException e){
                        Log.i("Main Activity",e.toString());
                    }
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    int status = error.networkResponse.statusCode;
                    if(status == 404 || status == -1){
                        Toast errorMessage = Toast.makeText(appContext,"Server Error",Toast.LENGTH_SHORT);
                        errorMessage.show();
                    }
                }
            };
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null, success,failure);
            VolleyQueue.instance(appContext).add(jsObjRequest);
        return usersList;
        }

     void getCountryListDataFromServer(){
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
                    Toast errorMessage = Toast.makeText(appContext,"Server Error",Toast.LENGTH_SHORT);
                    errorMessage.show();
                }
            }
        };
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, Url.COUNTRY_NAMES, null, success,failure);
        VolleyQueue.instance(appContext).add(jsObjRequest);
    }

     void getStateListFromServer(String selectedCountry){
        Log.i(TAG,"Inside getStateListFromServer()");
        if(!selectedCountry.equalsIgnoreCase("Select Country")) {
            stateList.clear();
            stateList.add("All");
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    try{
                        for(int i=0;i<response.length();i++){
                            stateList.add(response.getString(i));
                        }
                        responseHandler.stateListDataListener(stateList);
                    }
                    catch(JSONException e){
                        Log.i(TAG,"EmptyList");
                    }
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                }
            };
            JsonArrayRequest request = new JsonArrayRequest( Url.STATE_NAMES+selectedCountry, success, failure);
            VolleyQueue.instance(appContext).add(request);
        }
    }


    /**
     * Method to get nextId from server
     */
    public void checkDataUpdates(){
        Response.Listener<String> success = new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.i(TAG, response);
                nextId = Integer.parseInt(response);
                Log.i(TAG,"Next id"+nextId);
               new DataBaseInteraction().execute(Keys.RETRIEVE_MAX_ID);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                int status = error.networkResponse.statusCode;
                if(status == 404 || status == -1){
                    Toast errorMessage = Toast.makeText(appContext,"Server Error",Toast.LENGTH_SHORT);
                    errorMessage.show();
                }
            }
        };
        StringRequest jsObjRequest = new StringRequest(Request.Method.GET, Url.NEXT_ID, success,failure);
        VolleyQueue.instance(appContext).add(jsObjRequest);
    }

    private boolean isDatabaseSync(int nextId,int maxId){
        if(maxId==nextId-1){
            return true;
        } else {
            return false;
        }
    }


    // GEO CODER
    private class GeocodeLocator extends AsyncTask<String,User,String> {
        LatLng latLng;
        ArrayList<User> usersWithoutLatLng = new ArrayList<>();
        ArrayList<User> usersWithLatLng = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG,"onPreExecute");
            for (User u : usersList) {
                if (u.getLatitude() == 0 && u.getLongitude() == 0) {
                    usersWithoutLatLng.add(u);
                }
                else{
                    usersWithLatLng.add(u);
                }
            }
            Log.i(TAG,"WL"+usersWithLatLng.size()+" WTL:"+usersWithoutLatLng.size());
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.i(TAG,"GC : "+usersWithLatLng.size());
            for (User user : usersWithoutLatLng) {
                Geocoder locator = new Geocoder(appContext);
                String location = user.getState() + ", " + user.getCountry();
                try {
                    List<Address> possibleAddresses =
                            locator.getFromLocationName(location, 1);
                    for (Address approxLocation : possibleAddresses) {
                        if (approxLocation.hasLatitude())
                            Log.i(TAG, "Lat " + approxLocation.getLatitude());
                        if (approxLocation.hasLongitude())
                            Log.i(TAG, "Long " + approxLocation.getLongitude());
                        latLng = new LatLng(approxLocation.getLatitude(), approxLocation.getLongitude());
                    }
                } catch (Exception error) {
                    Log.i(TAG, "Address lookup Error", error);
                    latLng = new LatLng(0.0, 0.0);
                }
                user.setLatitude(latLng.latitude);
                user.setLongitude(latLng.longitude);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String users) {
            super.onPostExecute(users);
            usersList.clear();
            usersList.addAll(usersWithLatLng);
            usersList.addAll(usersWithoutLatLng);
            DataBaseInteraction dataBaseInteraction = new DataBaseInteraction();
            dataBaseInteraction.execute(Keys.PERSIST_USER_DATA);
            responseHandler.userDataListener(usersList);
        }
    }


    private class DataBaseInteraction extends AsyncTask<String,String,String>{
        String operation, query, result;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(appContext);
        @Override
        protected String doInBackground(String... strings) {
            operation = strings[0];
            if(strings.length > 1){
                query = strings[1];
            }
            switch (operation){
                case Keys.PERSIST_USER_DATA:
                    dataBaseHelper.addUserTable(usersList);
                    result = Keys.SEND_USER_DATA;
                    break;

                case Keys.RETRIEVE_USER_DATA:
                    usersList.clear();
                    usersList = dataBaseHelper.retrieveUserTable(query);
                    if(usersList.isEmpty() || usersList.size() < 25 ){
                        Log.i(TAG,"Less than minimum");
                        result = Keys.GET_DATA_FROM_SERVER;
                    } else {
                        result = Keys.SEND_USER_DATA;
                    }

                    break;

                case Keys.PERSIST_COUNTRY_DATA:
                    dataBaseHelper.setCountryListInDB(countryList);
                    result = Keys.SEND_COUNTRY_DATA;
                    break;

                case Keys.RETRIEVE_COUNTRY_DATA:
                    countryList = dataBaseHelper.getCountryListFromDB();
                    result = Keys.SEND_COUNTRY_DATA;
                    break;

                case Keys.RETRIEVE_MAX_ID:
                    maxId = dataBaseHelper.getMaxIdFromDB();
                    Log.i(TAG,"MAX id : "+maxId);
                    result = Keys.SEND_MAX_ID;
                    break;

            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch(s){
                case Keys.SEND_USER_DATA:
                    responseHandler.userDataListener(usersList);
                    break;
                case Keys.SEND_COUNTRY_DATA:
                    responseHandler.countryListDataListener(countryList);
                    break;
                case Keys.SEND_MAX_ID:
                   responseHandler.sendNextIdAndMaxId(nextId,maxId);
                    break;
                case Keys.GET_DATA_FROM_SERVER:
                    getUserDataFromServer(currentUrl);
                    break;

            }

        }
    }

    interface ResponseHandler {
        // Used to send user data fetched from server
        void userDataListener(ArrayList<User> users);

        // Used to send country list data
        void countryListDataListener(ArrayList<String> countryList);

        // Used to send state list data
        void stateListDataListener(ArrayList<String> stateList);

        // used to send user count
        void sendUsersCount(int count);

        // used to send nextId
        void sendNextIdAndMaxId(int nextId,int maxId);

    }

}
