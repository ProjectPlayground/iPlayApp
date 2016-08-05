package com.parse.starter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chris on 5/19/16.
 */
public class SportListAdapter extends ArrayAdapter<SportsListItem> {
    Context context;
    List<SportsListItem> data;
    int rowLayoutId;

    public SportListAdapter(Context context, int resource, List<SportsListItem> objects) {
        super(context, resource, objects);

        this.context = context;
        this.data = objects;
        rowLayoutId = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(rowLayoutId, parent, false);
            drawerHolder.sportNameView = (TextView) view
                    .findViewById(R.id.sportNameTextView);
            drawerHolder.sportImageView = (ImageView) view.findViewById(R.id.sportImageView);

            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }

        SportsListItem dItem = (SportsListItem) this.data.get(position);

        drawerHolder.sportImageView.setImageDrawable(view.getResources().getDrawable(
                dItem.getImageId()));
        drawerHolder.sportNameView.setText(dItem.getSportsName());

        return view;
    }

    private class DrawerItemHolder{
        ImageView sportImageView;
        TextView sportNameView;
    }
}
