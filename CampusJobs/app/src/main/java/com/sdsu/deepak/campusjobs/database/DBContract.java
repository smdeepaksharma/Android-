package com.sdsu.deepak.campusjobs.database;

import android.provider.BaseColumns;

/**
 * Contains database schema information and queries
 * Created by Deepak on 5/1/2017.
 */

public class DBContract {
    // Schema for Job Cart Table
     static class JobCartTable implements BaseColumns {
        static final String TABLE_NAME = "JobCart";
        static final String COLUMN_JOB_ID = "JobId";
        static final String COLUMN_JOB_TITLE = "JobTitle";
        static final String COLUMN_DEPARTMENT = "Department";
        static final String COLUMN_USER_ID = "UserId";
    }
    // Schema for Jobs Applied Table
     static class JobsAppliedTable implements BaseColumns {
        static final String TABLE_NAME = "JobsApplied";
        static final String COLUMN_JOB_ID = "JobId";
        static final String COLUMN_USER_ID = "UserId";
        static final String COLUMN_APPLIED_ON = "AppliedOn";
    }

    // SQLite query to create job cart table
    static final String CREATE_TABLE_JOB_CART =
            "CREATE TABLE IF NOT EXISTS " +
                    JobCartTable.TABLE_NAME + " (" +
                    JobCartTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    JobCartTable.COLUMN_JOB_ID + " TEXT," +
                    JobCartTable.COLUMN_JOB_TITLE + " TEXT," +
                    JobCartTable.COLUMN_USER_ID + " TEXT," +
                    JobCartTable.COLUMN_DEPARTMENT + " TEXT)";

    // SQLite query to create job applied table
    static final String CREATE_TABLE_JOBS_APPLIED =
            "CREATE TABLE IF NOT EXISTS " +
                    JobsAppliedTable.TABLE_NAME + " (" +
                    JobsAppliedTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    JobsAppliedTable.COLUMN_USER_ID + " TEXT," +
                    JobsAppliedTable.COLUMN_APPLIED_ON + " TEXT," +
                    JobsAppliedTable.COLUMN_JOB_ID + " TEXT)";


    public static final String GET_JOBS_IN_CART = "SELECT * FROM "+
            JobCartTable.TABLE_NAME + " WHERE " + JobCartTable.COLUMN_USER_ID +" = ";

    public static final String GET_APPLIED_JOBS = "SELECT * FROM "+
            JobsAppliedTable.TABLE_NAME + " WHERE " + JobsAppliedTable.COLUMN_USER_ID +" = ";
}
