package com.sdsu.deepak.mytabapplication;

import android.util.Log;

import java.net.URLEncoder;

/**
 * Created by Deepak on 4/8/2017.
 */

public class QueryAndUrlHelper {

    private static final String TAG = "Hometown | QueryAndUrl";
    private static final String SELECT ="Select";
    private static final String ALL = "All";


    /**
     * This method is invoked to obtain the appropriate data base query according to user selected filter options
     * @param countryName : CountryName selected by the user
     * @param stateName : StateName selected by the user
     * @param year : Year selected by the user
     * @return : SQLite database query
     */
    public static String prepareSQLQuery(String countryName,String stateName,String year){
        String query;

        // Setting up url based on values selected by the user
        if(!stateName.equals(ALL) && !countryName.equals(SELECT) && year.equals(SELECT)){
            // Country and State
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_COUNTRY + " = '" + countryName + "' AND " +
                    DbContract.UserTable.COLUMN_STATE + " = '" + stateName + "' ORDER BY "+ DbContract.UserTable._ID +
                    " DESC LIMIT 25 ";
        }
        else if(stateName.equals(ALL) && !countryName.equals(SELECT) && !year.equals(SELECT)) {
            // Country and Year
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_COUNTRY + " = '" + countryName + "' AND " +
                    DbContract.UserTable.COLUMN_YEAR + " = " + year +" LIMIT 25 ";
        }
        else if(stateName.equals(ALL) && !countryName.equals(SELECT) && year.equals(SELECT)){
            // Only Country
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_COUNTRY + " = '" + countryName + "' LIMIT 25 ";
        }
        else if(stateName.equals(ALL) && countryName.equals("Select Country") && !year.equals(SELECT)){
            // Only year
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_YEAR + " = " + year + " LIMIT 25 ";
        }
        else if(!stateName.equals(ALL) && !countryName.equals(SELECT) && !year.equals(SELECT)){
            // Country, State and  Year
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_COUNTRY + " = '" + countryName + "' AND " +
                    DbContract.UserTable.COLUMN_STATE + " = '" + stateName + "' AND " +
                    DbContract.UserTable.COLUMN_YEAR + " = " + year + " LIMIT 25 ";
        }
        else{
            // All users
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME +  " LIMIT 25";
        }
        Log.i(TAG,"SQL : "+query);
        return query;
    }


    public static String prepareServerAPIUrl(String countryName ,String stateName,String year){
        String url;
        // Initial condition for evaluating the search query
        if(!stateName.equalsIgnoreCase(ALL)) {
            try {
                stateName = URLEncoder.encode(stateName, "utf-8");
            } catch (Exception e) {
                Log.i(TAG, "Unable to encode state name");
            }
        }

        // Setting up url based on values selected by the user
        if(!stateName.equals(ALL) && !countryName.equals(SELECT) && year.equals(SELECT)){
            // Country and State
            url = Url.COUNTRY_FILTER + countryName + "&state=" + stateName + "&reverse=true" + "&page=";
        }
        else if(stateName.equals(ALL) && !countryName.equals(SELECT) && !year.equals(SELECT)) {
            // Country and Year
            url = Url.COUNTRY_FILTER+countryName+"&year="+year + "&reverse=true" + "&page=";
        }
        else if(stateName.equals(ALL) && !countryName.equals(SELECT) && year.equals(SELECT)){
            // Only Country
            url = Url.COUNTRY_FILTER + countryName + "&reverse=true" + "&page=";
        }
        else if(stateName.equals(ALL) && countryName.equals(SELECT) && !year.equals(SELECT)){
            // Only year
            url = Url.YEAR_FILTER + year + "&reverse=true" + "&page=";
        }
        else if(!stateName.equals(ALL) && !countryName.equals(SELECT) && !year.equals(SELECT)){
            // Country, State and  Year
            url = Url.COUNTRY_FILTER + countryName +"&state=" + stateName + "&year=" + year + "&reverse=true" + "&page=";
        }
        else{
            // All users
            url = Url.PAGE_REVERSE;
        }
        Log.i(TAG,"URL : "+url);
        return url;
    }

    public static String prepareServerAPIForMap(String countryName ,String stateName,String year,int nextId){
        String url;
        // Initial condition for evaluating the search query
        if(!stateName.equalsIgnoreCase(ALL)) {
            try {
                stateName = URLEncoder.encode(stateName, "utf-8");
            } catch (Exception e) {
                Log.i(TAG, "Unable to encode state name");
            }
        }


        // Setting up url based on values selected by the user
        if(!stateName.equals(ALL) && !countryName.equals(SELECT) && year.equals(SELECT)){
            // Country and State
            url = Url.COUNTRY_FILTER + countryName + "&state=" + stateName + "&reverse=true" + "&page=0&pagesize=100&beforeid="+ nextId;
        }
        else if(stateName.equals(ALL) && !countryName.equals(SELECT) && !year.equals(SELECT)) {
            // Country and Year
            url = Url.COUNTRY_FILTER+countryName+"&year="+year + "&reverse=true" + "&page=0&pagesize=100&beforeid="+ nextId;
        }
        else if(stateName.equals(ALL) && !countryName.equals(SELECT) && year.equals(SELECT)){
            // Only Country
            url = Url.COUNTRY_FILTER + countryName + "&reverse=true" + "&page=0&pagesize=100&beforeid="+ nextId;
        }
        else if(stateName.equals(ALL) && countryName.equals(SELECT) && !year.equals(SELECT)){
            // Only year
            url = Url.YEAR_FILTER + year + "&reverse=true" + "&page=0&pagesize=100&beforeid="+ nextId;
        }
        else if(!stateName.equals(ALL) && !countryName.equals(SELECT) && !year.equals(SELECT)){
            // Country, State and  Year
            url = Url.COUNTRY_FILTER + countryName +"&state=" + stateName + "&year=" + year + "&reverse=true" +"&page=0&pagesize=100&beforeid="+ nextId;
        }
        else{
            // All users
            url = Url.BASE_200_URL + "&page=0&pagesize=100&beforeid="+ nextId;
        }
        Log.i(TAG,"URL : "+url);
        return url;
    }
    public static String prepareSQLQueryForMap(String countryName,String stateName,String year,int startAt){
        String query;
        int endAt = startAt - 100;
        // Setting up url based on values selected by the user
        if(!stateName.equals(ALL) && !countryName.equals(SELECT) && year.equals(SELECT)){
            // Country and State
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_COUNTRY + " = '" + countryName + "' AND " +
                    DbContract.UserTable.COLUMN_STATE + " = '" + stateName + "' AND "
                    + DbContract.UserTable._ID + " > " + endAt + " ORDER BY " + DbContract.UserTable._ID + " DESC";

        }
        else if(stateName.equals(ALL) && !countryName.equals(SELECT) && !year.equals(SELECT)) {
            // Country and Year
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_COUNTRY + " = '" + countryName + "' AND " +
                    DbContract.UserTable.COLUMN_YEAR + " = " + year + " AND " +
                    DbContract.UserTable._ID + " < " + startAt + " AND " +
                    DbContract.UserTable._ID + " > " + endAt + " ORDER BY " +
                    DbContract.UserTable._ID + " DESC";
        }
        else if(stateName.equals(ALL) && !countryName.equals(SELECT) && year.equals(SELECT)){
            // Only Country
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_COUNTRY + " = '" + countryName + "' AND "+
                    DbContract.UserTable._ID + " < " + startAt + " AND "
                    + DbContract.UserTable._ID + " > " + endAt + " ORDER BY " + DbContract.UserTable._ID + " DESC";
        }
        else if(stateName.equals(ALL) && countryName.equals("Select Country") && !year.equals(SELECT)){
            // Only year
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_YEAR + " = " + year  + " AND " +
                    DbContract.UserTable._ID + " < " + startAt + " AND " +
                    DbContract.UserTable._ID + " > " + endAt + " ORDER BY " +
                    DbContract.UserTable._ID + " DESC";
        }
        else if(!stateName.equals(ALL) && !countryName.equals(SELECT) && !year.equals(SELECT)){
            // Country, State and  Year
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME + " WHERE " +
                    DbContract.UserTable.COLUMN_COUNTRY + " = '" + countryName + "' AND " +
                    DbContract.UserTable.COLUMN_STATE + " = '" + stateName + "' AND " +
                    DbContract.UserTable.COLUMN_YEAR + " = " + year + " AND "
                    + DbContract.UserTable._ID + " < " + startAt + " AND "
                    + DbContract.UserTable._ID + " > " + endAt + " ORDER BY " + DbContract.UserTable._ID + " DESC";;
        }
        else{
            // All users
            query = "SELECT * FROM " + DbContract.UserTable.TABLE_NAME +  " WHERE "
                    + DbContract.UserTable._ID + " < " + startAt + " AND "
                    + DbContract.UserTable._ID + " > " + endAt + " ORDER BY " + DbContract.UserTable._ID + " DESC";
        }
        Log.i(TAG,"SQL : "+query);
        return query;
    }



}
