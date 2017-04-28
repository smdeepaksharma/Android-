package com.sdsu.deepak.mytabapplication;

/**
 * Created by Deepak on 4/15/2017.
 */

public class FireBaseUsers {

    private String userid;
    private String name;
    private String lastMessage;

    public FireBaseUsers() {
        // required empty constructor
    }

    public FireBaseUsers(String id, String name,String lastMsg){
        this.userid = id;
        this.name = name;
        this.lastMessage = lastMsg;
    }
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
