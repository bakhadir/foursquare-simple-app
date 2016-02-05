package com.bakhadir.locationapp.tasks.users;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bakhadir.locationapp.app.AppController;
import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.listeners.UserInfoRequestListener;
import com.bakhadir.locationapp.models.User;
import com.bakhadir.locationapp.utils.LocationAppUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class SelfInfoRequest {

    public static final String TAG = SelfInfoRequest.class.getSimpleName();

    private Activity mActivity;
    private ProgressDialog mProgressDialog; // TODO Replace with progress bar
    private UserInfoRequestListener mListener;
    private Exception error;
    private User user;

    public SelfInfoRequest(Activity activity, UserInfoRequestListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    public void execute(String accessToken) {
        String tag = "self_req_tag";
        user = null;
        Gson gson = new Gson();
        // Show Progress Dialog
        showProgressDialog();
        // Check if there is a parameter "user_info"
        if (accessToken != null && retrieveUserInfo().equals("")) {
            // Build url
            String url = LocationAppUtils.buildSelfInfoRequestUrl(accessToken);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            // Hide Progress Dialog
                            hideProgressDialog();
                            user = processResponse(response);
                            handleResult(user);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError vError) {
                    VolleyLog.d(TAG, "Error: " + vError.getMessage());
                    // Hide Progress Dialog
                    hideProgressDialog();
                    error = new Exception(vError.toString());
                    handleResult(null);
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

        } else {
            hideProgressDialog();
            user = gson.fromJson(retrieveUserInfo(), User.class);
            handleResult(user);
        }
    }

    private User processResponse(JSONObject response) {
        User user = null;
        Gson gson = new Gson();
        try {
            // Get return code
            int returnCode = response.getJSONObject("meta").getInt("code");
            // 200 = OK
            if (returnCode == 200) {
                String json = response.getJSONObject("response").getJSONObject("user").toString();
                saveUserInfo(json);
                user = gson.fromJson(json, User.class);
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

        return user;
    }

    private void handleResult(User user) {
        if (mListener != null) {

            if (error != null) {
                mListener.onError(error.getMessage());
                return;
            }

            // Check if user image bitmap in cache
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(user.getPhoto());
            if(entry != null){
                // Decode byte array (entry.data) to bitmap
                user.setBitmapPhoto(LocationAppUtils.decodeByteToBitmap(entry.data));
            }

            mListener.onUserInfoFetched(user);
        }
    }

    private String retrieveUserInfo() {
        SharedPreferences settings = mActivity.getSharedPreferences(AppConstants.SHARED_PREF_FILE, 0);
        return settings.getString(AppConstants.USER_INFO, "");
    }

    private void saveUserInfo(String userJson) {
        SharedPreferences settings = mActivity.getSharedPreferences(AppConstants.SHARED_PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AppConstants.USER_INFO, userJson);
        editor.apply();
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Getting user info ...");
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
