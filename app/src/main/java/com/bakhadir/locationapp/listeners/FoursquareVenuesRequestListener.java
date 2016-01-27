package com.bakhadir.locationapp.listeners;

import com.bakhadir.locationapp.models.Venue;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/20/2016.
 */
public interface FoursquareVenuesRequestListener extends ErrorListener {

    void onVenuesFetched(ArrayList<Venue> venues);

}
