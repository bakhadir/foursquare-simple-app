package com.bakhadir.locationapp;

import android.location.Location;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ViewSwitcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.bakhadir.locationapp.OrientationChangeAction.orientationLandscape;
import static com.bakhadir.locationapp.OrientationChangeAction.orientationPortrait;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import static org.hamcrest.Matchers.not;

/**
 * Created by sam_ch on 2/4/2016.
 */

@RunWith(AndroidJUnit4.class)
public class LocationsActivityUITest {
    protected final static long MOCK_UPDATE_TIME = 1455226382401L;

    private Toolbar mToolbar;
    private ViewSwitcher mViewSwitcher;
    private FrameLayout mFrameLayout;
    private LocationsActivity mActivity;

    @Rule
    public ActivityTestRule<LocationsActivity> mLocationsActivityRule =
            new ActivityTestRule<>(LocationsActivity.class);

    @Before
    public void initLocationsActivityUIComponents() {
        mActivity = mLocationsActivityRule.getActivity();
        mToolbar = (Toolbar) mActivity.findViewById(R.id.app_toolbar);
        mViewSwitcher = (ViewSwitcher) mActivity.findViewById(R.id.viewSwitcher);
        mFrameLayout = (FrameLayout) mActivity.findViewById(R.id.fragmentContainer);
    }


    @Test
    public void testPreconditions() {
        assertNotNull("mActivity is null", mActivity);
        assertNotNull("mToolbar is null", mToolbar);
        assertNotNull("mViewSwitcher is null", mViewSwitcher);
        assertNotNull("mFrameLayout is null", mFrameLayout);
    }

    // Test fragment container visibility
    @Test
    public void testLocationsActivity_layout() {
        // Test frame layout width and height are MATCH_PARENT
        final ViewGroup.LayoutParams frameLayoutParams = mFrameLayout.getLayoutParams() ;
        assertNotNull(frameLayoutParams);
        assertEquals(frameLayoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(frameLayoutParams.height, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Test
    public void testLocationsActivity_viewSwitcher() {
        // Use getInstrumentation().runOnMainSync()
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                // Check if progress bar is shown (index=0)
                if (mViewSwitcher.getChildAt(0).isShown()) {
                    // Switch from progress bar to fragment container
                    mViewSwitcher.showNext();
                }
            }
        });

        // Test that fragment container is displayed
        onView(withId(R.id.fragmentContainer)).check(matches(isDisplayed()));
        // Test that progress bar is not displayed
        onView(withId(R.id.progressBar1)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testLocationActivity_orientationChange() {
        Location mockLocation = new Location("user_location");
        mockLocation.setLatitude(42.08);
        mockLocation.setLongitude(-87.86);

        String mockLastUpdateTime = DateFormat.getTimeInstance().format(System.currentTimeMillis());

        mActivity.setRequestingLocationUpdates(true);
        mActivity.setCurrentLocation(mockLocation);
        mActivity.setLastUpdateTime(mockLastUpdateTime);
        mActivity.setResolvingError(false);

        onView(isRoot()).perform(orientationLandscape());

        // TODO: asserts
        assertNotNull(mActivity.getRequestingLocationUpdates());
        assertEquals(true, mActivity.getRequestingLocationUpdates());
        assertNotNull(mActivity.getCurrentLocation());
        assertEquals(42.08, mActivity.getCurrentLocation().getLatitude());
        assertEquals(-87.86, mActivity.getCurrentLocation().getLongitude());
        assertNotNull(mActivity.getLastUpdateTime());
        assertEquals(mockLastUpdateTime, mActivity.getLastUpdateTime());
        assertNotNull(mActivity.getResolvingError());
        assertEquals(false, mActivity.getResolvingError());
    }

    @Test
    public void testLocationsActivity_Fragment() {
        assertNotNull("location fragment is null", startFragment());
    }

    private LocationsListFragment startFragment() {

        mActivity.getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, new LocationsListFragment(), "location_frag_test")
                .commit();
        getInstrumentation().waitForIdleSync();
        return (LocationsListFragment) mActivity.getFragmentManager().findFragmentByTag("location_frag_test");
    }
}
