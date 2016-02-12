package com.bakhadir.locationapp;

import android.location.Location;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bakhadir.locationapp.criterias.VenuesExploreCriteria;
import com.bakhadir.locationapp.listeners.FoursquareVenuesRequestListener;
import com.bakhadir.locationapp.models.Venue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.intent.Checks.checkNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by sam_ch on 2/4/2016.
 */
@RunWith(AndroidJUnit4.class)
public class LocationsActivityGetVenuesNearbyTest {

    private CountingIdlingResource mCountingIdlingResource;
    private LocationsActivity mActivity;

    @Rule
    public ActivityTestRule<LocationsActivity> mLocationsActivityTestRule =
            new ActivityTestRule<>(LocationsActivity.class);

    private class DecoratedLocationsAsyncTasks extends LocationsAsyncTasks {
        private LocationsAsyncTasks realLocationsAsyncTask;
        private CountingIdlingResource foursquareIdlingResource;

        private DecoratedLocationsAsyncTasks(LocationsAsyncTasks realLocationsAsyncTasks, CountingIdlingResource foursquareIdlingResource) {
            this.realLocationsAsyncTask = checkNotNull(realLocationsAsyncTasks);
            this.foursquareIdlingResource = checkNotNull(foursquareIdlingResource);
        }

        @Override
        public void getVenuesNearby(FoursquareVenuesRequestListener listener, VenuesExploreCriteria criteria) {
            // Use CountingIdlingResource to track in-flight calls to getVenuesNearby().
            // Whenever the count goes to zero, Espresso will be notified that this
            // resource is idle and the test will be able to proceed.
            foursquareIdlingResource.increment();
            try {
                realLocationsAsyncTask.getVenuesNearby(listener, criteria);
            } finally {
                foursquareIdlingResource.decrement();
            }
        }
    }

    @Before
    public void initTest() {
        mActivity = mLocationsActivityTestRule.getActivity();
        LocationsAsyncTasks realLocationsAsyncTask = mActivity.getLocationsAsyncTask();
        // Here, we use CountingIdlingResource - a common convenience class - to track the idle state of
        // the LocationsAsyncTasks task.
        mCountingIdlingResource = new CountingIdlingResource("FoursquareGetVenuesNearbyCall");
        mActivity.setLocationsAsyncTask(new DecoratedLocationsAsyncTasks(realLocationsAsyncTask, mCountingIdlingResource));
        assertTrue(registerIdlingResources(mCountingIdlingResource));
    }

    @After
    public void afterTest() throws Exception {
        assertTrue(unregisterIdlingResources(mCountingIdlingResource));
    }

    @Test
    public void testLocationsAsync_GetVenuesNearby() {
        VenuesRequestTestListener listener = new VenuesRequestTestListener();

        Location userLocation = new Location("user_location");
        userLocation.setLatitude(42.08);
        userLocation.setLongitude(-87.86);

        final VenuesExploreCriteria criteria = new VenuesExploreCriteria();
        criteria.setLocation(userLocation);

        mActivity.getLocationsAsyncTask().getVenuesNearby(listener, criteria);
    }

    @Test
    public void testLocationsAsync_NoLocation() {
        VenuesRequestNoLocationTestListener listener = new VenuesRequestNoLocationTestListener();

        Location userLocation = new Location("no_location");
        userLocation.setLatitude(0.0);
        userLocation.setLongitude(0.0);

        final VenuesExploreCriteria criteria = new VenuesExploreCriteria();
        criteria.setLocation(userLocation);

        mActivity.getLocationsAsyncTask().getVenuesNearby(listener, criteria);
    }

    private class VenuesRequestTestListener implements FoursquareVenuesRequestListener {

        @Override
        public void onVenuesFetched(ArrayList<Venue> venues) {
            assertNotNull("venues ArrayList is null", venues);
        }

        @Override
        public void onError(String errorMsg) {
            assertTrue(false);
        }
    }

    private class VenuesRequestNoLocationTestListener implements FoursquareVenuesRequestListener {

        @Override
        public void onVenuesFetched(ArrayList<Venue> venues) {
            assertNull("venues ArrayList is not null", venues);
            assertTrue(false);
        }

        @Override
        public void onError(String errorMsg) {
            assertEquals("com.android.volley.ServerError", errorMsg);
        }
    }
}
