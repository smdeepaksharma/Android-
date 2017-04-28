package com.sdsu.deepak.sdsuhometownlocations;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Deepak on 3/15/2017.
 */

public class User implements Parcelable{

    private String nickname;
    private String country;
    private String state;
    private String password;
    private String city;
    private double latitude;
    private double longitude;
    private int year;

    public User(){

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(nickname);
        out.writeString(password);
        out.writeString(country);
        out.writeString(state);
        out.writeString(city);
        out.writeInt(year);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        nickname = in.readString();
        password = in.readString();
        country = in.readString();
        state = in.readString();
        city = in.readString();
        year = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

}
