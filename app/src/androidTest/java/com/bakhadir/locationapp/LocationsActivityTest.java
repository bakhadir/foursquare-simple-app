package com.bakhadir.locationapp;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ViewSwitcher;

import com.bakhadir.locationapp.criterias.VenuesExploreCriteria;
import com.bakhadir.locationapp.listeners.FoursquareVenuesRequestListener;
import com.bakhadir.locationapp.models.Venue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by sam_ch on 1/12/2016.
 */
public class LocationsActivityTest extends ActivityInstrumentationTestCase2<LocationsActivity> {

    public static final String TAG = LocationsActivityTest.class.getSimpleName();

    private LocationsActivity mActivity;
    private Toolbar mToolbar;
    private ViewSwitcher mViewSwitcher;
    private FrameLayout mFrameLayout;
    private GoogleApiClient mGoogleApiClient;


    public LocationsActivityTest() {
        super(LocationsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        mToolbar = (Toolbar) mActivity.findViewById(R.id.app_toolbar);
        mViewSwitcher = (ViewSwitcher) mActivity.findViewById(R.id.viewSwitcher);
        mFrameLayout = (FrameLayout) mActivity.findViewById(R.id.fragmentContainer);
    }

    public void testPreconditions() {
        assertNotNull("mActivity is null", mActivity);
        assertNotNull("mToolbar is null", mToolbar);
        assertNotNull("mViewSwitcher is null", mViewSwitcher);
        assertNotNull("mFrameLayout is null", mFrameLayout);
    }

    public void testGoogleApiClient() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                        .addConnectionCallbacks(new GoogleApiClientListener(signal))
                        .addOnConnectionFailedListener(new GoogleApiClientListener(signal))
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
        });
        signal.await(5, TimeUnit.SECONDS);
    }

    public void testFoursquareVenuesNearbyRequest() throws Throwable{
        final CountDownLatch signal = new CountDownLatch(1);

        Location userLocation = new Location("user_location");
        userLocation.setLatitude(42.08);
        userLocation.setLongitude(-87.86);

        final VenuesExploreCriteria criteria = new VenuesExploreCriteria();
        criteria.setLocation(userLocation);

        runTestOnUiThread(new Runnable() {
            public void run() {
                FoursquareAsync foursquareAsync = new FoursquareAsync(mActivity);
                foursquareAsync.getVenuesNearby(new FoursquareVenuesRqstListener(signal), criteria);
            }
        });

        signal.await(5, TimeUnit.SECONDS);
    }

    private abstract class CallbackListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FoursquareVenuesRequestListener {
        private CountDownLatch signal;

        CallbackListener(CountDownLatch signal) {
            this.signal = signal;
        }

        @Override
        public void onConnected(Bundle bundle) {
            signal.countDown();
        }

        @Override
        public void onConnectionSuspended(int i) {
            signal.countDown();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            signal.countDown();
        }

        @Override
        public void onVenuesFetched(ArrayList<Venue> venues) {
            signal.countDown();
        }

        @Override
        public void onError(String errorMsg) {
            signal.countDown();
        }
    }

    private class GoogleApiClientListener extends CallbackListener {

        GoogleApiClientListener(CountDownLatch signal) {
            super(signal);
        }

        @Override
        public void onConnected(Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                assertTrue(false);
                return;
            }
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            assertNotNull("mLastLocation is null", mLastLocation);
        }

    }

    private class FoursquareVenuesRqstListener extends CallbackListener {

        FoursquareVenuesRqstListener(CountDownLatch signal) {
            super(signal);
        }

        @Override
        public void onVenuesFetched(ArrayList<Venue> venues) {
            assertNotNull("venues ArrayList is null", venues);

            LocationsListFragment locationsFragment = new LocationsListFragment();
            Fragment frag = startFragment(locationsFragment);
            assertNotNull("locationFragment is null", frag);

            super.onVenuesFetched(venues);
        }

        @Override
        public void onError(String errorMsg) {
            assertTrue(false);
            Log.i(TAG, errorMsg);
            super.onError(errorMsg);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.tearDown();
    }

    private Fragment startFragment(Fragment fragment) {
        mActivity.getFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment, "location_frag").commit();
//        getInstrumentation().waitForIdleSync();
        Fragment frag = mActivity.getFragmentManager().findFragmentByTag("location_frag");
        return frag;
    }

}
