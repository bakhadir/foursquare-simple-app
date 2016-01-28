package com.bakhadir.locationapp;

import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.constants.FoursquareConstants;
import com.bakhadir.locationapp.listeners.AccessTokenRequestListener;
import com.bakhadir.locationapp.listeners.UserInfoRequestListener;
import com.bakhadir.locationapp.models.User;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by sam_ch on 1/12/2016.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public static final String TAG = MainActivityTest.class.getSimpleName();

    private MainActivity mMainActivity;
    private ImageView mUserImage;
    private ViewSwitcher mViewSwitcher;
    private TextView mUserName;
    private Button mLocationsButton;
    private Toolbar mAppToolbar;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMainActivity = getActivity();
        mUserImage = (ImageView) mMainActivity.findViewById(R.id.userImage);
        mViewSwitcher = (ViewSwitcher) mMainActivity.findViewById(R.id.viewSwitcher1);
        mUserName = (TextView) mMainActivity.findViewById(R.id.userName);
        mLocationsButton = (Button) mMainActivity.findViewById(R.id.locationButton);
        mAppToolbar = (Toolbar) mMainActivity.findViewById(R.id.app_toolbar);
    }

    public void testPreconditions() {
        assertNotNull("mMainActivity is null", mMainActivity);
        assertNotNull("mUserImage is null", mUserImage);
        assertNotNull("mViewSwitcher is null", mViewSwitcher);
        assertNotNull("mUserName is null", mUserName);
        assertNotNull("mLocationsButton is null", mLocationsButton);
        assertNotNull("mAppToolbar is null", mAppToolbar);
    }

    public void testMainActivityLocationsButton_labelText() {
        final String expected = mMainActivity.getString(R.string.locations_button_text);
        final String actual = mLocationsButton.getText().toString();
        assertEquals(expected, actual);
    }

    // Test LocationsButton Visibility and Layout Parameters
    @MediumTest
    public void testLocationsButton_layout() {
        final View decorView = mMainActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mLocationsButton);
        assertTrue(View.VISIBLE == mLocationsButton.getVisibility());

        assertEquals(mMainActivity.getString(R.string.locations_button_text), mLocationsButton.getText().toString());

        final ViewGroup.LayoutParams layoutParams = mLocationsButton.getLayoutParams() ;
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testFoursquareAsyncRequestAccess() throws Throwable{
        final CountDownLatch signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            public void run() {
                FoursquareAsync foursquareAsync = new FoursquareAsync(getActivity());
                foursquareAsync.requestAccess(new AccessTokenListener(signal));
            }
        });

        signal.await(5, TimeUnit.SECONDS);

    }

    public void testFoursquareAsyncGetUserInfo() throws Throwable{
        final CountDownLatch signal = new CountDownLatch(1);

        runTestOnUiThread(new Runnable() {
            public void run() {
                FoursquareAsync foursquareAsync = new FoursquareAsync(getActivity());
                foursquareAsync.getUserInfo(new SelfInfoListener(signal));
            }
        });

        signal.await(5, TimeUnit.SECONDS);

    }

    private abstract class CallbackListener implements UserInfoRequestListener, AccessTokenRequestListener {
        private CountDownLatch signal;

        CallbackListener(CountDownLatch signal){
            this.signal = signal;
        }

        @Override
        public void onAccessGrant(String accessToken) {
            signal.countDown();
        };

        @Override
        public void onUserInfoFetched(User user) {
            signal.countDown();
        }

        @Override
        public void onError(String errorMsg) {
            signal.countDown();
        }

    }

    private class SelfInfoListener extends CallbackListener {

        SelfInfoListener(CountDownLatch signal) {
            super(signal);
        }

        @Override
        public void onUserInfoFetched(User user) {
            String expected = "Sam Sameev";
            String actual = user.getFirstName() + " " + user.getLastName();
            assertEquals(expected, actual);
            assertNotNull("UserImage is null", user.getBitmapPhoto()); // TODO: fails
            super.onUserInfoFetched(user);
        }

        @Override
        public void onError(String errorMsg) {
            assertTrue(false);
            Log.i(TAG, errorMsg);
            super.onError(errorMsg);
        }
    }

    private class AccessTokenListener extends CallbackListener {

        AccessTokenListener(CountDownLatch signal) {
            super(signal);
        }

        @Override
        public void onAccessGrant(String accessToken) {
            SharedPreferences settings = getActivity().getSharedPreferences(AppConstants.SHARED_PREF_FILE, 0);
            String expected  = settings.getString(AppConstants.ACCESS_TOKEN, "");
            assertEquals(expected, accessToken);
            super.onAccessGrant(accessToken);
        }

        @Override
        public void onError(String errorMsg) {
            SharedPreferences settings = getActivity().getSharedPreferences(AppConstants.SHARED_PREF_FILE, 0);
            String expected  = settings.getString(AppConstants.ACCESS_TOKEN, "");
            assertEquals(expected, "");
            Log.i(TAG, errorMsg);
            super.onError(errorMsg);
        }
    }
}
