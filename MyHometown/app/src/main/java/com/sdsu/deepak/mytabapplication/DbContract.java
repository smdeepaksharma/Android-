package com.sdsu.deepak.mytabapplication;

import android.provider.BaseColumns;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import dalvik.system.BaseDexClassLoader;

/**
 * Created by Deepak on 4/1/2017.
 */

class DbContract {

    private DbContract(){}
    // Schema for User Table
     static class UserTable implements BaseColumns {

         static final String TABLE_NAME = "UserInformation";
         static final String COLUMN_NICKNAME = "Nickname";
         static final String COLUMN_COUNTRY = "Country";
         static final String COLUMN_STATE = "State";
         static final String COLUMN_CITY = "City";
         static final String COLUMN_YEAR = "Year";
         static final String COLUMN_LATITUDE = "Latitude";
         static final String COLUMN_LONGITUDE = "Longitude";

    }
    // Schema for Country Table
      static class CountryTable implements BaseColumns {
        static final String TABLE_NAME = "CountryTable";
        static final String COLUMN_COUNTRY_NAME = "CountryName";
    }

    // Schema for fire base reference table
    private static class FireBaseReferenceTable implements BaseColumns{
        static final String TABLE_NAME = "FireBaseRef";
        static final String COLUMN_NICK_NAME = "NickName";
        static final String COLUMN_EMAIL_ID ="EmailId";
        static final String COLUMN_FIRE_BASE_PASSWORD = "FirebasePassword";
    }

    // SQLite query to create user table
     static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + UserTable.TABLE_NAME + " (" +
                    UserTable._ID + " INTEGER PRIMARY KEY," +
                    UserTable.COLUMN_NICKNAME + " TEXT," +
                    UserTable.COLUMN_STATE + " TEXT," +
                    UserTable.COLUMN_CITY + " TEXT," +
                    UserTable.COLUMN_YEAR + " TEXT," +
                    UserTable.COLUMN_LATITUDE + " TEXT," +
                    UserTable.COLUMN_LONGITUDE + " TEXT," +
                    UserTable.COLUMN_COUNTRY + " TEXT)";

    // SQLite query to create fire base reference table
    static final String CREATE_FIRE_BASE_REFERENCE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + FireBaseReferenceTable.TABLE_NAME + " (" +
                    FireBaseReferenceTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FireBaseReferenceTable.COLUMN_NICK_NAME + " Text NOT NULL," +
                    FireBaseReferenceTable.COLUMN_EMAIL_ID + " TEXT NOT NULL," +
                    FireBaseReferenceTable.COLUMN_FIRE_BASE_PASSWORD+ " TEXT NOT NULL)";

    // Country table query
    static final String CREATE_COUNTRY_TABLE =
            "CREATE TABLE IF NOT EXISTS " + CountryTable.TABLE_NAME + " (" +
                    CountryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CountryTable.COLUMN_COUNTRY_NAME + " TEXT NOT NULL)";

    static final String GET_COUNTRY_LIST = "SELECT * FROM " + CountryTable.TABLE_NAME;

    static final String RETRIEVE_USERS = "SELECT * FROM " + UserTable.TABLE_NAME + " LIMIT ?";

    static final String COUNTRY_QUERY = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COLUMN_COUNTRY +" = ?";

    static final String YEAR_QUERY = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COLUMN_YEAR +" ?";

    static final String COUNTRY_STATE_QUERY = "SELECT * FROM "
            + UserTable.TABLE_NAME + " WHERE " +
            UserTable.COLUMN_COUNTRY + " = ? AND "+
            UserTable.COLUMN_STATE+ " = ?";

    static final String COUNTRY_STATE_YEAR_QUERY = "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " +
            UserTable.COLUMN_COUNTRY +" ? AND "+
            UserTable.COLUMN_STATE+ " = ? AND" +
            UserTable.COLUMN_YEAR + " = ?";

    static final String GET_MORE_USER_DATA = "SELECT * FROM " + UserTable.TABLE_NAME + " ORDER BY " + UserTable._ID + " DESC LIMIT 10 OFFSET ";


}
