package com.parse.starter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chris on 5/18/16.
 */
public class SearchLocationListCustomAdapter extends ArrayAdapter<SearchLocationListItem> {
    Context context;
    List<SearchLocationListItem> dataList;
    int listItemLayout;

    public SearchLocationListCustomAdapter(Context context2,int layoutResid, List<SearchLocationListItem> dataList2 ){
        super(context2,layoutResid,dataList2);

        context = context2;
        dataList = dataList2;
        listItemLayout = layoutResid;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Reference Drawer Holder
        DrawerItemHolder drawerItemHolder;
        View view = convertView;

        if(view==null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerItemHolder = new DrawerItemHolder();


            view = inflater.inflate(listItemLayout,parent,false);
//            drawerItemHolder.locationTitleTextView = (TextView) view.findViewById(R.id.locationInfoView);
//            drawerItemHolder.setLocationButton = (Button) view.findViewById(R.id.setLocationButton);

        }
        else{
            drawerItemHolder = (DrawerItemHolder) view.getTag();
        }

        SearchLocationListItem item = (SearchLocationListItem) dataList.get(position);
        drawerItemHolder.locationTitleTextView.setText(item.getParkName());

        return view;
    }

    private static class DrawerItemHolder{
        TextView locationTitleTextView;
        Button setLocationButton;
    }
}
