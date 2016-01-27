package com.bakhadir.locationapp.criterias;

import android.location.Location;

/**
 * Created by sam_ch on 1/20/2016.
 */
public class VenuesExploreCriteria  {

    private int mRadius = 4000;
    private int mQuantity = 50;
    private boolean mSortByDistance = true;
    private Location mLocation;
//    private Location mLocation = new Location(LocationManager.GPS_PROVIDER);

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public boolean getSortByDistance() {
        return mSortByDistance;
    }

    public void setSortByDistance (boolean mSortByDistance) {
        this.mSortByDistance = mSortByDistance;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;
    }
}
