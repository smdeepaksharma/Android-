package com.sdsu.deepak.campusjobs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.sdsu.deepak.campusjobs.model.JobApplicant;
import java.util.HashMap;

/**
 * Database Handler to carry out DB interactions
 * Created by Deepak on 5/1/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "oncampus.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBContract.CREATE_TABLE_JOB_CART);
        sqLiteDatabase.execSQL(DBContract.CREATE_TABLE_JOBS_APPLIED);
    }

    /**
     * Jobs added to cart by the user will br stores in DB for future reference
     * @param applications: User application details
     */
    public void addJobToCart(HashMap<String, JobApplicant> applications) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String jobId : applications.keySet()) {
            ContentValues values = new ContentValues();
            JobApplicant application = applications.get(jobId);
            values.put(DBContract.JobCartTable.COLUMN_DEPARTMENT, application.department);
            values.put(DBContract.JobCartTable.COLUMN_JOB_ID, application.jobId);
            values.put(DBContract.JobCartTable.COLUMN_JOB_TITLE, application.jobTitle);
            values.put(DBContract.JobCartTable.COLUMN_USER_ID, application.userId);
            try {
                db.insertWithOnConflict(DBContract.JobCartTable.TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_IGNORE);
            } catch (SQLiteConstraintException e) {
                Log.i(TAG,"Existing data");
            }
        }
    }

    /**
     * Storing applied jobs list in DB for future reference
     * @param applications: Applied jobs details
     */
    public void addToAppliedTable(HashMap<String, JobApplicant> applications) {
        Log.i(TAG,"applied table"+applications.size());
        SQLiteDatabase db = this.getWritableDatabase();
        for (String jobId : applications.keySet()) {
            ContentValues values = new ContentValues();
            JobApplicant application = applications.get(jobId);
            values.put(DBContract.JobsAppliedTable.COLUMN_APPLIED_ON, application.appliedOn);
            values.put(DBContract.JobsAppliedTable.COLUMN_JOB_ID, application.jobId);
            values.put(DBContract.JobsAppliedTable.COLUMN_USER_ID, application.userId);
            try {
                db.insert(DBContract.JobsAppliedTable.TABLE_NAME,null,values);
            } catch (SQLiteConstraintException e) {
                Log.i(TAG,"Existing data");
            }
        }
    }

    public HashMap<String, JobApplicant> getAppliedJobs(String query){
        HashMap<String, JobApplicant> appliedJobsList = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result = sqLiteDatabase.rawQuery(query,null);
        Log.i(TAG,query);
        Log.i(TAG,"Result count:"+result.getCount());
        while (result.moveToNext()) {
            JobApplicant job = new JobApplicant();
            job.userId = result.getString(result.getColumnIndex(DBContract.JobsAppliedTable.COLUMN_USER_ID));
            job.jobId = result.getString(result.getColumnIndex(DBContract.JobsAppliedTable.COLUMN_JOB_ID));
            job.appliedOn = result.getString(result.getColumnIndex(DBContract.JobsAppliedTable.COLUMN_APPLIED_ON));
            Log.i(TAG, job.userId + job.userId);
            appliedJobsList.put(job.jobId,job);
        }
        result.close();
        if(appliedJobsList.isEmpty()){Log.i(TAG,"Empty");}
        return appliedJobsList;
    }

    /**
     * This method is invoked to get the jobs in cart
     * @param query:
     * @return a HashMap containing jobs added to cart by the user
     */
    public HashMap<String, JobApplicant> getJobsInCart(String query){
        HashMap<String, JobApplicant> jobsInCart = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result = sqLiteDatabase.rawQuery(query,null);
        Log.i(TAG,"Result count:"+result.getCount());
        while (result.moveToNext()) {
            JobApplicant job = new JobApplicant();
            job.userId = result.getString(result.getColumnIndex(DBContract.JobCartTable.COLUMN_USER_ID));
            job.jobId = result.getString(result.getColumnIndex(DBContract.JobCartTable.COLUMN_JOB_ID));
            job.department = result.getString(result.getColumnIndex(DBContract.JobCartTable.COLUMN_DEPARTMENT));
            job.jobTitle = result.getString(result.getColumnIndex(DBContract.JobCartTable.COLUMN_JOB_TITLE));
            jobsInCart.put(job.jobId,job);
        }
        result.close();
        if(jobsInCart.isEmpty()){Log.i(TAG,"Empty");}
        return jobsInCart;
    }

    /**
     * Method used to get the count of jobs added to cart by the user
     * @param userId: ID of the current user
     * @return the count of jobs
     */
    public int getJobCartCount(String userId){
        int count;
        String query = "SELECT COUNT(" + DBContract.JobCartTable.COLUMN_USER_ID + ") FROM "+
                DBContract.JobCartTable.TABLE_NAME + " WHERE " + DBContract.JobCartTable.COLUMN_USER_ID +" ='" + userId + "'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * Method to delete the job cart items
     * @param jobId: ID
     * @param userId: User ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteJobFromCart(String jobId, String userId){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
         return sqLiteDatabase.delete(DBContract.JobCartTable.TABLE_NAME, DBContract.JobCartTable.COLUMN_JOB_ID + " = ? AND " +
                DBContract.JobCartTable.COLUMN_USER_ID + " = ?", new String[]{jobId, userId}) > 0;

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
