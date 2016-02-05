package com.bakhadir.locationapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.test.ViewAsserts;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by sam_ch on 1/29/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityUITest {

    private ImageView mUserImage;
    private ViewSwitcher mViewSwitcher;
    private TextView mUserName;
    private Button mLocationsButton;
    private Toolbar mAppToolbar;

    private String mMainActivityLocationsButtonText;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initMainActivityUIComponents() {
        mUserImage = (ImageView) mMainActivityRule.getActivity().findViewById(R.id.userImage);
        mViewSwitcher = (ViewSwitcher) mMainActivityRule.getActivity().findViewById(R.id.viewSwitcher1);
        mUserName = (TextView) mMainActivityRule.getActivity().findViewById(R.id.userName);
        mLocationsButton = (Button) mMainActivityRule.getActivity().findViewById(R.id.locationButton);
        mAppToolbar = (Toolbar) mMainActivityRule.getActivity().findViewById(R.id.app_toolbar);

        mMainActivityLocationsButtonText = "LOCATIONS";
    }

    @Test
    public void testPreconditions() {
        assertNotNull("mUserImage is null", mUserImage);
        assertNotNull("mViewSwitcher is null", mViewSwitcher);
        assertNotNull("mUserName is null", mUserName);
        assertNotNull("mLocationsButton is null", mLocationsButton);
        assertNotNull("mAppToolbar is null", mAppToolbar);
    }

    @Test
    public void testMainActivityLocationsButton_labelText() {
        onView(withId(R.id.locationButton)).check(matches(withText(mMainActivityLocationsButtonText)));
    }

    // Test LocationsButton Visibility and Layout Parameters
    @Test
    public void testLocationsButton_layout() {
        final View decorView = mMainActivityRule.getActivity().getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mLocationsButton);
        assertTrue(View.VISIBLE == mLocationsButton.getVisibility());

        assertEquals(mMainActivityRule.getActivity().getString(R.string.locations_button_text), mLocationsButton.getText().toString());

        final ViewGroup.LayoutParams layoutParams = mLocationsButton.getLayoutParams() ;
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
