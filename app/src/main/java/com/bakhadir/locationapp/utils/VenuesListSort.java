package com.bakhadir.locationapp.utils;

import com.bakhadir.locationapp.models.Venue;

import java.util.Comparator;

/**
 * Created by sam_ch on 1/25/2016.
 */

public class VenuesListSort {

    public static final Comparator<Venue> DISTANCE_ASC = new Comparator<Venue>() {
        @Override
        public int compare(Venue holder1, Venue holder2) {
            return holder1.getLocation().getDistance() < holder2.getLocation().getDistance() ? -1
                    : holder1.getLocation().getDistance() > holder2.getLocation().getDistance() ? 1
                    : 0;
        }
    };

    public static final Comparator<Venue> DISTANCE_DESC = new Comparator<Venue>() {
        @Override
        public int compare(Venue holder1, Venue holder2) {
            return holder2.getLocation().getDistance() < holder1.getLocation().getDistance() ? -1
                    : holder2.getLocation().getDistance() > holder1.getLocation().getDistance() ? 1
                    : 0;
        }
    };

}
