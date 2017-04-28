package com.sdsu.deepak.mytabapplication;

/**
 * Created by Deepak on 3/31/2017.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Constants class contains globally used constant values
 */
class Constants {

    static String USER_LIST_FRAGMENT = "User List Fragment";
    static String USER_INFORMATION_FRAGMENT = "User Information Fragment";
    static String FILTER_FRAGMENT = "Filter Fragment";
    static ArrayList<String> countryList = new ArrayList<>();
    static ArrayList<String> yearList = new ArrayList<>();
    public static final String ARG_USERS = "users";
    public static final String ARG_RECEIVER = "receiver";
    public static final String ARG_RECEIVER_UID = "receiver_uid";
    public static final String ARG_CHAT_ROOMS = "chat_rooms";
    public static final String ARG_FIREBASE_TOKEN = "firebaseToken";
    public static final String ARG_FRIENDS = "friends";
    public static final String ARG_UID = "uid";
    public static final String ARG_CHAT_LIST = "ChatList";

     static boolean setUpConstantValues(Context appContext) {
         boolean ready = false;
        return ready;
    }
}