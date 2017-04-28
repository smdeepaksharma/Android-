package com.sdsu.deepak.mytabapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Deepak on 4/15/2017.
 */

public class ChatListAdapter extends ArrayAdapter<FireBaseUsers> {

    private final Activity context;
    private final ArrayList<FireBaseUsers> users;

    public ChatListAdapter(@NonNull Activity context, ArrayList<FireBaseUsers> chatusersList) {
        super(context,R.layout.chat_list,chatusersList);
        this.context = context;
        users = chatusersList;
    }

    public void add(FireBaseUsers list) {
        users.add(list);
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.chat_list, null,true);
        FireBaseUsers user = users.get(position);
        TextView userName = (TextView) rowView.findViewById(R.id.firebase_user_name);
        TextView lastMessage = (TextView) rowView.findViewById(R.id.text_view_last_message);
        userName.setText(user.getName());
        lastMessage.setText(user.getLastMessage());
        return rowView;
    };
}
