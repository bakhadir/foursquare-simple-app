package com.bakhadir.locationapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;

import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.listeners.AccessTokenRequestListener;
import com.bakhadir.locationapp.listeners.ImageRequestListener;
import com.bakhadir.locationapp.listeners.UserInfoRequestListener;
import com.bakhadir.locationapp.models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Checks.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * Created by sam_ch on 2/2/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityAsyncTasksTest {
    private static final String TEST_USER_IMAGE_URL = "https://irs2.4sqi.net/img/user/300x300/149093430-2G0TXFDJFW5NAXWW";
    private static final String TEST_USER_IMAGE_URL_SERVER_ERROR = "https://irs2.4sqi.net/img/user/300x300/149093430-2G0TXFDJFW5NAXWW_BAM!";
    private static final String SERVER_ERROR_MSG = "com.android.volley.ServerError";
    private static final String TEST_USER_IMAGE_BAD_URL = "";
    private static final String BAD_URL_MSG = "com.android.volley.VolleyError: java.lang.RuntimeException: Bad URL ";

    private CountingIdlingResource mCountingIdlingResource;
    private MainActivity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private class DecoratedFoursquareAsync extends FoursquareAsync {
        private FoursquareAsync realFoursquareAsync;
        private CountingIdlingResource foursquareIdlingResource;

        private DecoratedFoursquareAsync(FoursquareAsync realFoursquareAsync, CountingIdlingResource foursquareIdlingResource) {
            this.realFoursquareAsync = checkNotNull(realFoursquareAsync);
            this.foursquareIdlingResource = checkNotNull(foursquareIdlingResource);
        }

        @Override
        public void requestAccess(AccessTokenRequestListener listener) {
            // Use CountingIdlingResource to track in-flight calls to requestAccess().
            // Whenever the count goes to zero, Espresso will be notified that this
            // resource is idle and the test will be able to proceed.
            foursquareIdlingResource.increment();
            try {
                realFoursquareAsync.requestAccess(listener);
            } finally {
                foursquareIdlingResource.decrement();
            }
        }

        @Override
        public void getUserInfo(UserInfoRequestListener listener) {
            // Use CountingIdlingResource to track in-flight calls to getUserInfo().
            // Whenever the count goes to zero, Espresso will be notified that this
            // resource is idle and the test will be able to proceed.
            foursquareIdlingResource.increment();
            try {
                realFoursquareAsync.getUserInfo(listener);
            } finally {
                foursquareIdlingResource.decrement();
            }
        }

        @Override
        public void getUserPhotoBitmap(ImageRequestListener listener, String userPhotoUrl) {
            // Use CountingIdlingResource to track in-flight calls to getUserPhotoBitmap().
            // Whenever the count goes to zero, Espresso will be notified that this
            // resource is idle and the test will be able to proceed.
            foursquareIdlingResource.increment();
            try {
                realFoursquareAsync.getUserPhotoBitmap(listener, userPhotoUrl);
            } finally {
                foursquareIdlingResource.decrement();
            }
        }
    }

    @Before
    public void initTest() {
        mActivity = mMainActivityTestRule.getActivity();
        FoursquareAsync realFoursquareAsync = mActivity.getFoursquareAsync();
        // Here, we use CountingIdlingResource - a common convenience class - to track the idle state of
        // the FoursquareAsync task.
        mCountingIdlingResource = new CountingIdlingResource("FoursquareAsyncCall");
        mActivity.setFoursquareAsync(new DecoratedFoursquareAsync(realFoursquareAsync, mCountingIdlingResource));
        assertTrue(registerIdlingResources(mCountingIdlingResource));
    }

    @After
    public void afterTest() throws Exception {
        assertTrue(unregisterIdlingResources(mCountingIdlingResource));
    }


    @Test
    public void testFoursquareAsyncRequestAccess() {
        AccessTokenRequestTestListener listener = new AccessTokenRequestTestListener();
        mActivity.getFoursquareAsync().requestAccess(listener);
    }

    @Test
    public void testFoursquareAsyncGetUserInfo() {
        final UserInfoRequestTestListener listener = new UserInfoRequestTestListener();
        // Have to use getInstrumentation().runOnMainSync() to avoid
        // java.lang.RuntimeException:
        //      "Can't create handler inside thread that has not called Looper.prepare()"
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.getFoursquareAsync().getUserInfo(listener);
            }
        });
    }

    @Test
    public void testUserImageRequest() {
        final ImageRequestTestListener listener = new ImageRequestTestListener();
        // Use getInstrumentation().runOnMainSync()
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.getFoursquareAsync().getUserPhotoBitmap(listener, TEST_USER_IMAGE_URL);
            }
        });
    }

    @Test
    public void testUserImageRequest_ServerError() {
        final ImageRequestServerErrorTestListener listener = new ImageRequestServerErrorTestListener();
        // Use getInstrumentation().runOnMainSync()
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.getFoursquareAsync().getUserPhotoBitmap(listener, TEST_USER_IMAGE_URL_SERVER_ERROR);
            }
        });
    }

    @Test
    public void testUserImageRequest_BadUrl() {
        final ImageRequestBadUrlTestListener listener = new ImageRequestBadUrlTestListener();
        // Use getInstrumentation().runOnMainSync()
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.getFoursquareAsync().getUserPhotoBitmap(listener, TEST_USER_IMAGE_BAD_URL);
            }
        });
    }

    private class AccessTokenRequestTestListener implements AccessTokenRequestListener {

        @Override
        public void onAccessGrant(String accessToken) {
            SharedPreferences settings = mActivity.getSharedPreferences(AppConstants.SHARED_PREF_FILE, 0);
            String expected  = settings.getString(AppConstants.ACCESS_TOKEN, "");
            assertEquals(expected, accessToken);
        }

        @Override
        public void onError(String errorMsg) {
            SharedPreferences settings = mActivity.getSharedPreferences(AppConstants.SHARED_PREF_FILE, 0);
            String expected  = settings.getString(AppConstants.ACCESS_TOKEN, "");
            assertEquals(expected, "");
        }
    }

    private class UserInfoRequestTestListener implements UserInfoRequestListener {

        @Override
        public void onUserInfoFetched(User user) {
            String expected = "Sam Sameev";
            String actual = user.getFirstName() + " " + user.getLastName();
            assertEquals(expected, actual);
            assertNotNull("UserImage is null", user.getBitmapPhoto());
        }

        @Override
        public void onError(String errorMsg) {
            assertTrue(false);
        }
    }

    private class ImageRequestTestListener implements ImageRequestListener {

        @Override
        public void onImageFetched(Bitmap bitmap) {
            assertNotNull("Bitmap is null", bitmap);
        }

        @Override
        public void onError(String errorMsg) {
            assertTrue(false);
        }
    }

    private class ImageRequestServerErrorTestListener implements ImageRequestListener {

        @Override
        public void onImageFetched(Bitmap bitmap) {
            assertNull("Bitmap is not null", bitmap);
            assertTrue(false);
        }

        @Override
        public void onError(String errorMsg) {
            assertEquals(SERVER_ERROR_MSG, errorMsg);
        }
    }

    private class ImageRequestBadUrlTestListener implements ImageRequestListener {

        @Override
        public void onImageFetched(Bitmap bitmap) {
            assertNull("Bitmap is not null", bitmap);
            assertTrue(false);
        }

        @Override
        public void onError(String errorMsg) {
            assertEquals(BAD_URL_MSG, errorMsg);
        }
    }
}
