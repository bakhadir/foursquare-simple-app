package com.bakhadir.locationapp.tasks;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bakhadir.locationapp.app.AppController;
import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.constants.FoursquareConstants;
import com.bakhadir.locationapp.listeners.VenuePhotosListener;
import com.bakhadir.locationapp.models.Photo;
import com.bakhadir.locationapp.models.Venue;
import com.bakhadir.locationapp.utils.LocationAppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/25/2016.
 */
public class VenuePhotosRequest {

    public static final String TAG = VenuePhotosRequest.class.getSimpleName();

    private VenuePhotosListener mListener;
    private Exception error;
    private ArrayList<Photo> photosList;
    private String mVenueId;

    public VenuePhotosRequest(VenuePhotosListener listener, String venueId) {
        mListener = listener;
        mVenueId = venueId;
    }

    public void execute(String accessToken) {
        String tag = "venue_photos_req_tag";
        // Build request url
        String url = String.format(FoursquareConstants.VENUE_PHOTOS_URL_FORMAT, mVenueId,
                FoursquareConstants.API_DATE_VERSION);

        // Check if we have accessToken
        if (!accessToken.equals("")) {
            url = url + AppConstants.OAUTH_TOKEN_STRING + accessToken;
        } else {
            url = url + AppConstants.CLIENT_ID_STRING + FoursquareConstants.CLIENT_ID
                    + AppConstants.CLIENT_SECRET_STRING + FoursquareConstants.CLIENT_SECRET;
        }

        // Check if we have cached response
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if(entry != null){
            try {
                // Decode byte[] to JSONObject
                photosList = processResponse(LocationAppUtils.decodeByteToJSONObj(entry.data));
                handleResult(photosList);

            } catch (JSONException e) {
                error = new Exception(e.toString());
                handleResult(null);
            }
        } else {
            // Cached response doesn't exists. Make volley json request
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            photosList = processResponse(response);
                            handleResult(photosList);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError vError) {
                    VolleyLog.d(TAG, "Error: " + vError.getMessage());
                    error = new Exception(vError.toString());
                    handleResult(null);
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag);
        }
    }

    private void handleResult(ArrayList<Photo> photosList) {
        if (mListener != null) {

            if (error != null) {
                mListener.onError(error.getMessage());
                return;
            }
            mListener.onVenuePhotosFetched(photosList);
        }
    }

    private ArrayList<Photo> processResponse(JSONObject response) {
        ArrayList<Photo> venuePhotos = new ArrayList<>();
        try {
            // Get return code
            int returnCode = Integer.parseInt(response.getJSONObject("meta").getString("code"));
            // 200 = OK
            if (returnCode == 200) {

                // Get "photos" JSONObject
                JSONObject photosJson = response.getJSONObject("response").getJSONObject("photos");
                JSONArray itemsJsonArr = photosJson.getJSONArray("items");

                for (int i = 0; i < itemsJsonArr.length(); i++) {
                    Photo venuePhoto = Photo.fromJSONObject(itemsJsonArr.getJSONObject(i));
                    venuePhotos.add(venuePhoto);
                }
            } else {
                if (mListener != null) {
                    error = new Exception(response.getJSONObject("meta").getString("errorDetail"));
                    return null;
                }
            }
        } catch (JSONException e) {
            if (mListener != null) {
                error = e;
                return null;
            }
            e.printStackTrace();
        }
        return venuePhotos;
    }

}
