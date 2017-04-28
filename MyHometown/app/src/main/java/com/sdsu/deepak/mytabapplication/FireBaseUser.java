package com.sdsu.deepak.mytabapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Deepak on 4/15/2017.
 */

public class FireBaseUser implements Parcelable {
    public String uid;
    public String displayName;

    public FireBaseUser() {
    }

    public FireBaseUser(String uid, String displayname) {
        this.uid = uid;
        this.displayName = displayname;
    }

    @Override
public int describeContents() {
    return 0;
}

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uid);
        out.writeString(displayName);
    }

    public static final Parcelable.Creator<FireBaseUser> CREATOR
            = new Parcelable.Creator<FireBaseUser>() {
        public FireBaseUser createFromParcel(Parcel in) {
            return new FireBaseUser(in);
        }

        public FireBaseUser[] newArray(int size) {
            return new FireBaseUser[size];
        }
    };

    private FireBaseUser(Parcel in) {
        uid = in.readString();
        displayName = in.readString();
    }
}
