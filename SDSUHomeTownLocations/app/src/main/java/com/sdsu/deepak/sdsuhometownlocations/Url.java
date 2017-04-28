package com.sdsu.deepak.sdsuhometownlocations;

/**
 * Created by Deepak on 3/13/2017.
 */

public class Url {

    // Add new user to the server
    static final String ADD_USER = "http://bismarck.sdsu.edu/hometown/adduser";
    // Next Id of user from the server
    static final String NEXT_ID = "http://bismarck.sdsu.edu/hometown/nextid";
    // List of countries
    static final String COUNTRY_NAMES = "http://bismarck.sdsu.edu/hometown/countries";
    // Users count
    static final String STATE_NAMES = "http://bismarck.sdsu.edu/hometown/states?country=";
    static final String USERS_COUNT = "http://bismarck.sdsu.edu/hometown/count";
    // To retrieve Users information from the server
    static final String USERS = "http://bismarck.sdsu.edu/hometown/users";

    static final String STATE_FILTER = "http://bismarck.sdsu.edu/hometown/users?state=";

    static final String COUNTRY_FILTER = "http://bismarck.sdsu.edu/hometown/users?country=";

    static final String CITY_FILTER = "http://bismarck.sdsu.edu/hometown/users?city=";

    static final String YEAR_FILTER = "http://bismarck.sdsu.edu/hometown/users?year=";

    static final String AFTER_ID = "http://bismarck.sdsu.edu/hometown/users?afterid=";

    static final String BEFORE_ID = "http://bismarck.sdsu.edu/hometown/users?beforeid=";

    static final String REVERSE = "http://bismarck.sdsu.edu/hometown/users?reverse=true";

    static final String PAGE = "http://bismarck.sdsu.edu/hometown/users?page=";

    static final String BASE_URL = "http://bismarck.sdsu.edu/hometown/users?reverse=true";


}
