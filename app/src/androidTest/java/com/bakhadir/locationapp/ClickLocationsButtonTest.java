package com.bakhadir.locationapp;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

/**
 * Created by sam_ch on 1/12/2016.
 */
public class ClickLocationsButtonTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    private Button mLocationsButton;

    public ClickLocationsButtonTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        mMainActivity = getActivity();
        mLocationsButton = (Button) mMainActivity.findViewById(R.id.locationButton);

    }

    // Test LocationsButton behaviour
    @MediumTest
    public void testLocationsButton_clickButtonAndExpectNewActivityLaunch() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(LocationsActivity.class.getName(), null, false);
        TouchUtils.clickView(this, mLocationsButton);
        LocationsActivity targetActivity = (LocationsActivity) activityMonitor.waitForActivity();
        assertNotNull("LocationsActivity is not launched", targetActivity);
    }

}
