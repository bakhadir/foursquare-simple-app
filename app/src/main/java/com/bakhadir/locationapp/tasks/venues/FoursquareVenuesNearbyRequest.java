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
import com.bakhadir.locationapp.listeners.FoursquareVenuesRequestListener;
import com.bakhadir.locationapp.models.Venue;
import com.bakhadir.locationapp.utils.LocationAppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class FoursquareVenuesNearbyRequest {

    public static final String TAG = FoursquareVenuesNearbyRequest.class.getSimpleName();

    private FoursquareVenuesRequestListener mListener;
    private VenuesExploreCriteria mCriteria;
    private Exception error;
    private ArrayList<Venue> compactVenues;

    public FoursquareVenuesNearbyRequest(FoursquareVenuesRequestListener listener, VenuesExploreCriteria criteria) {
        mListener = listener;
        mCriteria = criteria;
    }

    public void execute(String accessToken) {
        String tag = "venues_req_tag";
        // Build request url
        String url = String.format(FoursquareConstants.EXPLORE_VENUES_URL_FORMAT,
                FoursquareConstants.API_DATE_VERSION,
                mCriteria.getLocation().getLatitude(),
                mCriteria.getLocation().getLongitude(),
                mCriteria.getQuantity(),
                mCriteria.getRadius(),
                (mCriteria.getSortByDistance() ? 1 : 0));

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
                compactVenues = processResponse(LocationAppUtils.decodeByteToJSONObj(entry.data));
                handleResult(compactVenues);

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
                            compactVenues = processResponse(response);
                            handleResult(compactVenues);
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

    private void handleResult(ArrayList<Venue> compactVenues) {
        if (mListener != null) {

            if (error != null) {
                mListener.onError(error.getMessage());
                return;
            }
            mListener.onVenuesFetched(compactVenues);
        }
    }

    private ArrayList<Venue> processResponse(JSONObject response) {
        ArrayList<Venue> compactVenues = new ArrayList<>();
        try {
            // Get return code
            int returnCode = Integer.parseInt(response.getJSONObject("meta").getString("code"));
            // 200 = OK
            if (returnCode == 200) {

                // Get "items" JSONArray
                JSONArray groupsJsonArr = response.getJSONObject("response").getJSONArray("groups");
                JSONObject groupsJsonObj = groupsJsonArr.getJSONObject(0);
                JSONArray itemsJsonArr = groupsJsonObj.getJSONArray("items");

                for (int i = 0; i < itemsJsonArr.length(); i++) {
                    Venue compactVenue = Venue.fromJSONObject(itemsJsonArr.getJSONObject(i).getJSONObject("venue"));
                    compactVenues.add(compactVenue);
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
        return compactVenues;
    }
}
