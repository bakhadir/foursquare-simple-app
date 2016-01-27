package com.bakhadir.locationapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bakhadir.locationapp.criterias.VenuesExploreCriteria;
import com.bakhadir.locationapp.listeners.FoursquareVenuesRequestListener;
import com.bakhadir.locationapp.models.Venue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class FoursquareLocationsActivity extends AppCompatActivity implements FoursquareVenuesRequestListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = FoursquareLocationsActivity.class.getSimpleName();

    private ArrayList<Venue> mVenues = new ArrayList<>();
    private ViewSwitcher viewSwitcher;
    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    private AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foursquare_locations);
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        setSupportActionBar(appToolbar);

        // Build GoogleApiClient
        if(mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
    }

    private boolean isGPSOn() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void buildGPSAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_is_disabled)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(FoursquareLocationsActivity.this, R.string.please_enable_gps, Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        finish();
                    }
                });
        alert = builder.create();
        alert.show();
    }


    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if(alert != null) {
            alert.dismiss();
        }
        super.onStop();
    }

    public ArrayList<Venue> getCompactVenues() {
        return mVenues;
    }

    @Override
    public void onVenuesFetched(ArrayList<Venue> venues) {
        mVenues = venues;
        viewSwitcher.showNext();
        LocationsListFragment locationsFragment = new LocationsListFragment();
        getFragmentManager().beginTransaction().add(R.id.fragmentContainer, locationsFragment, "location_frag").commit();
    }

    @Override
    public void onError(final String errorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FoursquareLocationsActivity.this, errorMsg, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
//        hideProgressDialog();

        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Location userLocation = new Location("user_location");
                userLocation.setLatitude(mLastLocation.getLatitude());
                userLocation.setLongitude(mLastLocation.getLongitude());

                VenuesExploreCriteria criteria = new VenuesExploreCriteria();
                criteria.setLocation(userLocation);

                FoursquareAsync async = new FoursquareAsync(this);
                async.getVenuesNearby(this, criteria);
            } else {
                // check if GPS is enabled
                if(!isGPSOn()) {
                    buildGPSAlertDialog();
                } else { // GPS is enabled, something else went wrong
                    Toast.makeText(FoursquareLocationsActivity.this, R.string.locations_not_avail, Toast.LENGTH_LONG).show();
                }
            }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        Toast.makeText(FoursquareLocationsActivity.this, i, Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Toast.makeText(FoursquareLocationsActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }
}
