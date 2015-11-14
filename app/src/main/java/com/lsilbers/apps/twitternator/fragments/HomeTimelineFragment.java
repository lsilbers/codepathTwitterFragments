package com.lsilbers.apps.twitternator.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsilbers.apps.twitternator.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTimelineFragment extends Fragment {


    public HomeTimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_timeline, container, false);
    }


}
