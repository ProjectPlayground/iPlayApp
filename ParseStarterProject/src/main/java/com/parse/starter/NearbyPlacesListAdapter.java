package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.vision.text.Text;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by chris on 7/23/16.
 */


/**
 * Created by chris on 5/19/16.
 */
public class NearbyPlacesListAdapter extends ArrayAdapter<Place> {
    Context context;
    List<Place> data;
    int rowLayoutId;

    public NearbyPlacesListAdapter(Context context, int resource, List<Place> objects) {
        super(context, resource, objects);

        this.context = context;
        this.data = objects;
        rowLayoutId = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemHolder drawerHolder;
        View view = convertView;

        //Inflate the layout if necessary
        if (view == null) {
           LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(rowLayoutId,parent,false) ;
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(rowLayoutId, parent, false);
            drawerHolder.placeNameTextView = (TextView) view.findViewById(R.id.placeNameTextView);
            drawerHolder.placeAddressTextView = (TextView) view.findViewById(R.id.placeAddressTextView);
            drawerHolder.placeDistanceFromCurrLocTextView = (TextView) view.findViewById(R.id.distanceFromPlaceTextView);
            drawerHolder.placeNumberOfPeopleTextView = (TextView) view.findViewById(R.id.numberOfPeopleTextView);
            drawerHolder.numberOfPeopleIcon = (ImageView) view.findViewById(R.id.numberOfPeopleImageView);

            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }

        Place dItem = (Place) this.data.get(position);

        drawerHolder.placeNameTextView.setText(dItem.getName()); ;
        drawerHolder.placeAddressTextView.setText(dItem.getAddress()); ;
        drawerHolder.placeDistanceFromCurrLocTextView.setText("0.5mi"); ;
        drawerHolder.placeNumberOfPeopleTextView.setText("5");
       // drawerHolder.numberOfPeopleIcon ;

        //Populate the data for the place

        return view;
    }

    private class DrawerItemHolder{
        TextView placeNameTextView;
        TextView placeAddressTextView;
        TextView placeDistanceFromCurrLocTextView;
        ImageView numberOfPeopleIcon;
        TextView placeNumberOfPeopleTextView;
    }
}
