package com.parse.starter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 5/14/16.
 */
public class MainPageFragment extends ListFragment {
    //Create List for ArrayAdapter
    List<SportsListItem> sportsListData;
    SportListAdapter listAdapter;
    TextView sportsTitleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.main_page_fragment, container, false);    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sportsListData = new ArrayList<SportsListItem>();
        sportsTitleTextView = (TextView) getView().findViewById(R.id.selectSportTextView);
        sportsTitleTextView.setText(R.string.main_page_title_name);
        testListView();
        listAdapter = new SportListAdapter(getActivity(),R.layout.sports_list_row_layout,sportsListData);
        setListAdapter(listAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Instantiate SearchMainFragment
//               MapSearchFragment searchMainFrag = new MapSearchFragment();
                //Send the MapSearchFragment what sport was selected
                Bundle bundle = new Bundle();
                bundle.putString("sportSelected", sportsListData.get(position).getSportsName());
//              searchMainFrag.setArguments(bundle);
                // Add the fragment to the 'fragment_container' FrameLayout
//                Note: When you remove or replace a fragment and add the transaction to the back stack, the fragment that is removed is stopped (not destroyed).
//                If the user navigates back to restore the fragment, it restarts. If you do not add the transaction to the
//                back stack, then the fragment is destroyed when removed or replaced.
//              getActivity().getSupportFragmentManager().beginTransaction()
//                      .replace(R.id.fragment_container, searchMainFrag).addToBackStack(null).commit();
//
                Intent mapsIntent = new Intent(getContext(),MapsActivity.class);
                mapsIntent.putExtras(bundle);
                startActivity(mapsIntent);




            }
        });
    }
    private void testListView(){
        sportsListData.add(new SportsListItem("Baseball",R.drawable.base_ball));
        sportsListData.add(new SportsListItem("Basketball",R.drawable.basket_ball));
        sportsListData.add(new SportsListItem("Football",R.drawable.foot_ball));
        sportsListData.add(new SportsListItem("Hockey",R.drawable.hockey_ball));
        sportsListData.add(new SportsListItem("Rugby",R.drawable.ruby_ball));
        sportsListData.add(new SportsListItem("Soccer",R.drawable.soccer));
        sportsListData.add(new SportsListItem("Tennis",R.drawable.tennis_ball));
        sportsListData.add(new SportsListItem("Volleyball",R.drawable.volley_ball));

        Log.e("ListViewTest", "Size of ListView" + sportsListData.size());


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }





}
