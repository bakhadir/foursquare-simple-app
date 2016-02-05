package com.bakhadir.locationapp;

import android.app.Activity;
import android.content.SharedPreferences;

import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.constants.FoursquareConstants;
import com.bakhadir.locationapp.criterias.VenuesExploreCriteria;
import com.bakhadir.locationapp.listeners.AccessTokenRequestListener;
import com.bakhadir.locationapp.listeners.CompleteVenueInfoListener;
import com.bakhadir.locationapp.listeners.FoursquareVenuesRequestListener;
import com.bakhadir.locationapp.listeners.ImageRequestListener;
import com.bakhadir.locationapp.listeners.UserInfoRequestListener;
import com.bakhadir.locationapp.listeners.VenuePhotosListener;
import com.bakhadir.locationapp.tasks.VenuePhotosRequest;
import com.bakhadir.locationapp.tasks.users.SelfInfoRequest;
import com.bakhadir.locationapp.tasks.users.UserImageRequest;
import com.bakhadir.locationapp.tasks.venues.CompleteVenueRequest;
import com.bakhadir.locationapp.tasks.venues.FoursquareVenuesNearbyRequest;
import com.bakhadir.locationapp.dialogs.FoursquareDialog;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class FoursquareAsync {

    private Activity mActivity;
    private String mAccessToken = "";
    private FoursquareDialog mDialog;

    public FoursquareAsync(Activity activity) {
        mActivity = activity;
    }

    // Mainly for testing
    public FoursquareAsync() {
    }

    /**
     * Requests the access to API
     */
    public void requestAccess(AccessTokenRequestListener listener) {
        if (!hasAccessToken()) {
            loginDialog(listener);
        } else {
            listener.onAccessGrant(getAccessToken());
        }
    }

    private boolean hasAccessToken() {
        String token = getAccessToken();
        return !token.equals("");
    }

    private String getAccessToken() {
        if (mAccessToken.equals("")) {
            SharedPreferences settings = mActivity.getSharedPreferences(AppConstants.SHARED_PREF_FILE, 0);
            mAccessToken = settings.getString(AppConstants.ACCESS_TOKEN,"");
        }
        return mAccessToken;
    }

    private void loginDialog(AccessTokenRequestListener listener) {
        String url = FoursquareConstants.AUTHENTICATE_STRING
                + FoursquareConstants.CLIENT_ID_STRING + FoursquareConstants.CLIENT_ID
                + FoursquareConstants.RESPONSE_TYPE_STRING + FoursquareConstants.REDIRECT_URI_STRING
                + FoursquareConstants.CALLBACK_URL;

        mDialog = new FoursquareDialog(mActivity, url, listener);
        mDialog.show();
    }

    /**
     * Requests logged user information asynchronously.
     *
     * @param listener As the request is asynchronous, listener used to retrieve the
     *                 User object, containing the information.
     */
    public void getUserInfo(UserInfoRequestListener listener) {
        SelfInfoRequest request = new SelfInfoRequest(mActivity, listener);
        request.execute(getAccessToken());
    }

    /**
     * Requests logged user photo.
     *
     * @param listener Listener to pass bitmap back to MainActivity
     * @param photoUrl User's photo URL
     */
    public void getUserPhotoBitmap(ImageRequestListener listener, String photoUrl) {
        UserImageRequest userImageRequest = new UserImageRequest(listener);
        userImageRequest.execute(photoUrl);
    }

    /**
     * Requests the nearby Venues.
     *
     * @param criteria The criteria to your search request
     * @param listener As the request is asynchronous, listener used to retrieve the
     *                 Venues object, containing the information.
     */
    public void getVenuesNearby(FoursquareVenuesRequestListener listener, VenuesExploreCriteria criteria) {
        FoursquareVenuesNearbyRequest request = new FoursquareVenuesNearbyRequest(listener, criteria);
        request.execute(getAccessToken());
    }

    public void getCompleteVenueInfo(CompleteVenueInfoListener listener, String venueId) {
        CompleteVenueRequest request = new CompleteVenueRequest(listener, venueId);
        request.execute(getAccessToken());

    }

    public void getVenuePhotos(VenuePhotosListener listener, String venueId) {
        VenuePhotosRequest request = new VenuePhotosRequest(listener, venueId);
        request.execute(getAccessToken());
    }
}
