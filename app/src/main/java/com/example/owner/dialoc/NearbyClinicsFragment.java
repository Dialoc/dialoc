package com.example.owner.dialoc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Owner on 4/12/2017.
 */

public class NearbyClinicsFragment extends Fragment {

    public NearbyClinicsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nearby_clinics_fragment, container, false);
        return view;
    }
}
