package com.ucd.android.qwikscan.screens;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ucd.android.qwikscan.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A subclass fragment for the profile screen
 */
public class ProfileFragment extends Fragment{

    //an ArrayAdapter to hold user's information
    private ArrayAdapter<String> infoAdapter;

    /**
     * Required Empty controller
     */
    public ProfileFragment() {

    }

    /**
     * onCreate method which, when called saves state
     * and sets the option menu
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    /**
     * This method populates an ArrayAdaptor with user inforamtion and
     * prints it to the screen using a ListView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        Activity activity = getActivity();
        Profile parentActivity = (Profile)activity;
        String name = "Name: "+parentActivity.getName();
        String email = "Email: "+parentActivity.getEmail();
        String map_title = getString(R.string.store_name)+" stores near you";
        String[] informationArray ={name,email,map_title};

        List<String> information = new ArrayList<String>(
                Arrays.asList(informationArray));

        infoAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.profile_layout,// The name of the layout ID.
                        R.id.profile_textview, // The ID of the textview to populate.
                        information);


        ListView listView = (ListView) rootView.findViewById(R.id.user_profile);
        listView.setAdapter(infoAdapter);


        return rootView;

    }



}
