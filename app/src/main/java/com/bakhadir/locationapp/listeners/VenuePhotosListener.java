package com.bakhadir.locationapp.listeners;

import com.bakhadir.locationapp.models.Photo;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/25/2016.
 */
public interface VenuePhotosListener extends ErrorListener {

    void onVenuePhotosFetched(ArrayList<Photo> photosList);

}
