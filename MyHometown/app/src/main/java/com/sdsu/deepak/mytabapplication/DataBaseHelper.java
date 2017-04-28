package com.sdsu.deepak.mytabapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Deepak on 4/1/2017.
 */

    // TODO : ALl database operations should go into this class
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hometown.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "Hometown | DBHelper";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DbContract.CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(DbContract.CREATE_COUNTRY_TABLE);
        sqLiteDatabase.execSQL(DbContract.CREATE_FIRE_BASE_REFERENCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void displayDatabaseInfo(){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i(TAG,this.getDatabaseName());
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                Log.i(TAG,"Table Name=> "+c.getString(0));
                c.moveToNext();
            }
        }
    }

    public void addUserTable(ArrayList<User> saveUsers) {
        SQLiteDatabase db = this.getWritableDatabase();


        for (User user : saveUsers) {
            ContentValues values = new ContentValues();
            // Specify column name and its corresponding value
            values.put(DbContract.UserTable._ID, user.getId());
            values.put(DbContract.UserTable.COLUMN_NICKNAME, user.getNickname());
            values.put(DbContract.UserTable.COLUMN_COUNTRY, user.getCountry());
            values.put(DbContract.UserTable.COLUMN_STATE, user.getState());
            values.put(DbContract.UserTable.COLUMN_CITY, user.getCity());
            values.put(DbContract.UserTable.COLUMN_LATITUDE, user.getLatitude());
            values.put(DbContract.UserTable.COLUMN_LONGITUDE, user.getLongitude());
            // Inserting user data into UserTable Table
            try {
                long newRowId = db.insertWithOnConflict(DbContract.UserTable.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_IGNORE);
                Log.i(TAG, "Inserting Row id"+ newRowId + "uid: "+user.getId() + " : "+user.getNickname());
            } catch (SQLiteConstraintException e) {
                Log.i(TAG,"Existing data");
            }
        }
    }


    public ArrayList<User> retrieveUserTable(String query) {
        Log.i(TAG,"Inside retrieve user table");
        ArrayList<User> usersList = new ArrayList<>();
        Log.i(TAG,"Qry:"+query);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result = sqLiteDatabase.rawQuery(query,null);
        Log.i(TAG,"Result count:"+result.getCount());
        while (result.moveToNext()) {
            // adding user information to new user object
            User user = new User();
            user.setId(result.getInt(result.getColumnIndex(DbContract.UserTable._ID)));
            user.setNickname(result.getString(result.getColumnIndex(DbContract.UserTable.COLUMN_NICKNAME)));
            user.setCountry(result.getString(result.getColumnIndex(DbContract.UserTable.COLUMN_COUNTRY)));
            user.setState(result.getString(result.getColumnIndex(DbContract.UserTable.COLUMN_STATE)));
            user.setCity(result.getString(result.getColumnIndex(DbContract.UserTable.COLUMN_CITY)));
            user.setLatitude(Double.parseDouble(result.getString(result.getColumnIndex(DbContract.UserTable.COLUMN_LATITUDE))));
            user.setLongitude(Double.parseDouble(result.getString(result.getColumnIndex(DbContract.UserTable.COLUMN_LONGITUDE))));
            user.setYear(result.getInt(result.getColumnIndex(DbContract.UserTable.COLUMN_YEAR)));
            Log.i(TAG,"Retrieving" + user.getId()+":"+user.getNickname());
            // Add the user object to the userList
            usersList.add(user);
        }
        result.close();
        if(usersList.isEmpty()){Log.i(TAG,"Empty");}
        return usersList;
    }

    public ArrayList<String> getCountryListFromDB() {
        Log.i(TAG,"Inside Get Country List");
        ArrayList<String> countryList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result = sqLiteDatabase.rawQuery(DbContract.GET_COUNTRY_LIST,null);
        Log.i(TAG,"Country List Result count:"+result.getCount());
        while (result.moveToNext()) {
            countryList.add(result.getString(result.getColumnIndex(DbContract.CountryTable.COLUMN_COUNTRY_NAME)));
        }
        result.close();
        if(countryList.isEmpty()){Log.i(TAG,"Empty Country");}
        return countryList;
    }

    public void setCountryListInDB(ArrayList<String> countryListFromServer) {
        Log.i(TAG,"Inside Set Country List");
        SQLiteDatabase db = this.getWritableDatabase();
        for (String countryName : countryListFromServer) {
            ContentValues country = new ContentValues();
            country.put(DbContract.CountryTable.COLUMN_COUNTRY_NAME, countryName);
            long newRowId = db.insert(DbContract.CountryTable.TABLE_NAME, null, country);
            Log.i(TAG,"Country Row ID : "+newRowId + "  "+country);
        }
    }

    public int getMaxIdFromDB(){
        int maxId = 0;
        Log.i(TAG,"Fetching Max id");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT max("+ DbContract.UserTable._ID + ") from " + DbContract.UserTable.TABLE_NAME,null);
        if (cursor.moveToFirst())
        {
            do
            {
                maxId = cursor.getInt(0);
            } while(cursor.moveToNext());
        }
        Log.i(TAG,"Max ID : "+maxId);
        cursor.close();
        return maxId;
    }

}
