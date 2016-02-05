package com.bakhadir.locationapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ViewSwitcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;

import static org.hamcrest.Matchers.not;

/**
 * Created by sam_ch on 2/4/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocationsActivityUITest {
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
        // Test that fragment container is not displayed
        onView(withId(R.id.fragmentContainer)).check(matches(not(isDisplayed())));
        // Test that progress bar is displayed
        onView(withId(R.id.progressBar1)).check(matches(isDisplayed()));

        // Test frame layout width and height are MATCH_PARENT
        final ViewGroup.LayoutParams frameLayoutParams = mFrameLayout.getLayoutParams() ;
        assertNotNull(frameLayoutParams);
        assertEquals(frameLayoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(frameLayoutParams.height, WindowManager.LayoutParams.MATCH_PARENT);
    }
}
