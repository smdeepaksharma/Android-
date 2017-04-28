package com.sdsu.deepak.mytabapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Deepak on 3/31/2017.
 */

public class Chat implements Parcelable {

    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;

    public Chat() {}

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(sender);
        out.writeString(receiver);
        out.writeString(senderUid);
        out.writeString(receiverUid);
        out.writeString(message);
        out.writeLong(timestamp);
    }

    public static final Parcelable.Creator<Chat> CREATOR
            = new Parcelable.Creator<Chat>() {
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    private Chat(Parcel in) {
        sender = in.readString();
        receiver = in.readString();
        senderUid = in.readString();
        receiverUid = in.readString();
        message = in.readString();
        timestamp = in.readLong();
    }
}