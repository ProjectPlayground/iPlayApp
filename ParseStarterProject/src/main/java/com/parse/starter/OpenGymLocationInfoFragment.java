package com.parse.starter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by chris on 5/14/16.
 */
public class OpenGymLocationInfoFragment extends Fragment {
    ListView peopleAtLocationListView;
    Button checkInButton;
    ImageView locationImageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container!=null){
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.open_gym_info_frag,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
