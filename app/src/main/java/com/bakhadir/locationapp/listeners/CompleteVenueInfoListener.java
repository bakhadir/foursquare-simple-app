package com.bakhadir.locationapp.listeners;

import com.bakhadir.locationapp.models.Venue;

/**
 * Created by sam_ch on 1/22/2016.
 */
public interface CompleteVenueInfoListener extends ErrorListener {

    void onCompleteVenueInfoFetched(Venue venue);

}
