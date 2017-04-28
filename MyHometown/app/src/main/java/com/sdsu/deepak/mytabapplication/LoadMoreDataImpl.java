package com.sdsu.deepak.mytabapplication;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by Deepak on 4/8/2017.
 */

public class LoadMoreDataImpl extends AsyncTask<String,String,String> {

    private ArrayList<User> moreUserListData;
    private Context applicationContext;
    private DataBaseHelper dataBaseHelper;
    private FragmentCommunicator fragmentCommunicator;
    private UserListFragment userListFragment;

    LoadMoreDataImpl(UserListFragment userListFragment, Context appContext){
        this.applicationContext = appContext;
        this.userListFragment = userListFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dataBaseHelper = new DataBaseHelper(applicationContext);
        moreUserListData = new ArrayList<>();
        fragmentCommunicator = userListFragment;
    }

    @Override
        protected String doInBackground(String... commands) {
            String sql = commands[0] + " OFFSET " + commands[1];
            moreUserListData = dataBaseHelper.retrieveUserTable(sql);
            return "Done";
        }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        fragmentCommunicator.sendMoreDataToFragment(moreUserListData);
    }

    interface FragmentCommunicator {
        void sendMoreDataToFragment(ArrayList<User> moreUserData);
        }
    }





