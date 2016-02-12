package com.bakhadir.locationapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bakhadir.locationapp.adapters.LocationsAdapter;
import com.bakhadir.locationapp.app.AppController;
import com.bakhadir.locationapp.models.Venue;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class LocationsListFragment extends Fragment {

    public static final String TAG = LocationsListFragment.class.getSimpleName() ;

    private ArrayList<Venue> myDataset = new ArrayList<>();

    public LocationsListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize dataset
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.locations_list_frag_layout, container, false);
        rootView.setTag(TAG);

        SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        // Set RecyclerView's LayoutManager to the one given.
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        LocationsAdapter mAdapter = new LocationsAdapter(myDataset, getActivity());
        // Set LocationsAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    private void refreshList() {
        // clear old items
//        mAdapter.clear();
        // Clear Volley Cache (we want to get new Locations around)
        AppController.getInstance().getRequestQueue().getCache().clear();

        // Restart activity
        Intent intent = new Intent(getActivity(), LocationsActivity.class);
        // Put boolean REQUESTING_LOCATION_UPDATES_KEY = true extra, so LocationsActivity's
        // onResume() will fire up startLocationUpdates();
        intent.putExtra(LocationsActivity.REQUESTING_LOCATION_UPDATES_KEY, true);
        startActivity(intent);
        getActivity().finish();
    }

    private void initDataset() {
        LocationsActivity activity = (LocationsActivity) getActivity();
        myDataset = activity.getCompactVenues();
        Log.i(TAG, "initDataset");
    }
}
