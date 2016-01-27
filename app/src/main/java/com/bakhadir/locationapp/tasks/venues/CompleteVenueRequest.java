package com.bakhadir.locationapp.tasks.venues;

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
import com.bakhadir.locationapp.criterias.VenuesExploreCriteria;
import com.bakhadir.locationapp.listeners.CompleteVenueInfoListener;
import com.bakhadir.locationapp.listeners.FoursquareVenuesRequestListener;
import com.bakhadir.locationapp.models.Venue;
import com.bakhadir.locationapp.utils.LocationAppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/22/2016.
 */

public class CompleteVenueRequest {

    public static final String TAG = CompleteVenueRequest.class.getSimpleName();

    private CompleteVenueInfoListener mListener;
    private Exception error;
    private Venue completeVenue;
    private String mVenueId;

    public CompleteVenueRequest(CompleteVenueInfoListener listener, String venueId) {
        mListener = listener;
        mVenueId = venueId;
    }

    public void execute(String accessToken) {
        String tag = "complete_venues_req_tag";
        // Build request url
        String url = String.format(FoursquareConstants.COMPETE_VENUE_URL_FORMAT, mVenueId,
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
                completeVenue = processResponse(LocationAppUtils.decodeByteToJSONObj(entry.data));
                handleResult(completeVenue);

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
                            completeVenue = processResponse(response);
                            handleResult(completeVenue);
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

    private void handleResult(Venue completeVenue) {
        if (mListener != null) {

            if (error != null) {
                mListener.onError(error.getMessage());
                return;
            }
            mListener.onCompleteVenueInfoFetched(completeVenue);
        }
    }

    private Venue processResponse(JSONObject response) {
        Venue completeVenue = new Venue();
        try {
            // Get return code
            int returnCode = Integer.parseInt(response.getJSONObject("meta").getString("code"));
            // 200 = OK
            if (returnCode == 200) {

                // Get "venue" JSONObject
                JSONObject venueJson = response.getJSONObject("response").getJSONObject("venue");
                // build complete venue
                completeVenue = Venue.fromJSONObject(venueJson);
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
        return completeVenue;
    }

}
