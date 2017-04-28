package com.sdsu.deepak.sdsuhometownlocations;

import android.app.Activity;
import android.graphics.Color;
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
 * Created by Deepak on 3/15/2017.
 */

public class CustomListAdapter extends ArrayAdapter<User> {

    private final Activity context;
    private final ArrayList<User> users;
    private final ArrayList<Integer> colorPalette = new ArrayList<>();

    public CustomListAdapter(Activity context, ArrayList<User> userInformation ) {
        super(context, R.layout.users_list,userInformation);
        setUpColorPalette();
        this.context=context;
        this.users=userInformation;
    }

    @Override
    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.users_list, null,true);
        User user = users.get(position);
        Random rand = new Random();
        TextDrawable icon = TextDrawable.builder().buildRound("N", colorPalette.get(rand.nextInt(5)));
        try {
            String firstLetter = user.getNickname().substring(0, 1);
            icon = TextDrawable.builder().buildRound(firstLetter, colorPalette.get(rand.nextInt(5)));
        }catch (StringIndexOutOfBoundsException e){
            Log.i("Custom Adpater List","No Name");
        }
        TextView userName = (TextView) rowView.findViewById(R.id.user_name);
        TextView userLocation = (TextView) rowView.findViewById(R.id.user_location_details);
        TextView year = (TextView) rowView.findViewById(R.id.year_details);
        ImageView image = (ImageView) rowView.findViewById(R.id.icon);
        image.setImageDrawable(icon);
        userName.setText(user.getNickname());
        userLocation.setText(" "+user.getCountry()+", "+user.getState()+", "+user.getCity());
        year.setText(" "+String.valueOf(user.getYear()));
        return rowView;
    };

    private void setUpColorPalette(){
        colorPalette.add(Color.rgb(178,102,255));
        colorPalette.add(Color.rgb(51,153,255));
        colorPalette.add(Color.rgb(0,153,76));
        colorPalette.add(Color.rgb(255,128,0));
        colorPalette.add(Color.LTGRAY);
    }
}