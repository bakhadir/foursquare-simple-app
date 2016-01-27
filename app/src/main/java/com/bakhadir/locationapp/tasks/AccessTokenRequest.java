package com.bakhadir.locationapp.tasks;

import android.os.AsyncTask;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.constants.FoursquareConstants;
import com.bakhadir.locationapp.listeners.AccessTokenRequestListener;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class AccessTokenRequest extends AsyncTask<String, Integer, String> {
    private Activity mActivity;
    private ProgressDialog mProgress;
    private AccessTokenRequestListener mListener;

    public AccessTokenRequest(Activity activity, AccessTokenRequestListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(mActivity);
        mProgress.setCancelable(false);
        mProgress.setMessage("Getting access token ...");
        mProgress.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String code = params[0];
        String token = "";
        // Check if there is a parameter called "code"
        if (code != null) {
            try {
                // Call Foursquare again to get the access token
                JSONObject tokenJson = executeHttpGet(FoursquareConstants.ACCESS_TOKEN_STRING
                        + FoursquareConstants.CLIENT_ID_STRING + FoursquareConstants.CLIENT_ID
                        + FoursquareConstants.CLIENT_SECRET_STRING + FoursquareConstants.CLIENT_SECRET
                        + FoursquareConstants.GRANT_TYPE_STRING
                        + FoursquareConstants.REDIRECT_URI_STRING + FoursquareConstants.CALLBACK_URL
                        + FoursquareConstants.CODE_STRING + code);

                token = tokenJson.getString("access_token");

                //saving token
                Log.i("Access Token", token);
                SharedPreferences settings = mActivity.getSharedPreferences(AppConstants.SHARED_PREF_FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(AppConstants.ACCESS_TOKEN, token);
                // Commit the edits!
                editor.apply();

            } catch (Exception exp) {
                Log.e("LoginTest", "Getting Access token failed", exp);
                mListener.onError(exp.toString());
            }
        } else {
            Toast.makeText(mActivity, "Unknown login error", Toast.LENGTH_SHORT)
                    .show();
            mListener.onError("Unknown login error");
        }
        return token;
    }

    @Override
    protected void onPostExecute(String access_token) {
        mProgress.dismiss();
        mListener.onAccessGrant(access_token);
        super.onPostExecute(access_token);
    }

    // Calls a URI and returns the answer as a JSON object
    private JSONObject executeHttpGet(String uri) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(uri)
                .build();
        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }
}
