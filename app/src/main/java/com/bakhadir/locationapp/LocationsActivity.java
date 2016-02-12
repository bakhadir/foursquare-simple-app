package com.bakhadir.locationapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bakhadir.locationapp.criterias.VenuesExploreCriteria;
import com.bakhadir.locationapp.listeners.FoursquareVenuesRequestListener;
import com.bakhadir.locationapp.models.Venue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class LocationsActivity extends AppCompatActivity implements FoursquareVenuesRequestListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = LocationsActivity.class.getSimpleName();

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error.
    private boolean mResolvingError = false;


    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    // Key for saving state of mResolvingError if activity restarts.
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    // Stores parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;
    // Current device location
    private Location mCurrentLocation;
    // Tracks the status of the location updates request.
    private boolean mRequestingLocationUpdates;
    // Time when the location was updated represented as a String.
    protected String mLastUpdateTime;

    private ArrayList<Venue> mVenues = new ArrayList<>();
    private ViewSwitcher viewSwitcher;
    // Provides the entry point to Google Play services.
    private GoogleApiClient mGoogleApiClient;

    private AlertDialog mAlert;

    private LocationsAsyncTasks mAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foursquare_locations);

        // Locate the UI widgets
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        appToolbar.setTitle(R.string.locations_activity_title);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        setSupportActionBar(appToolbar);

        // May contain mRequestingLocationUpdates boolean (passed from LocationsListFragment
        // on swipe-to-refresh). If true, as soon as GoogleApiClient is connected we call
        // startLocationUpdates().
        mRequestingLocationUpdates = getIntent().getBooleanExtra(REQUESTING_LOCATION_UPDATES_KEY, false);
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mResolvingError && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if(mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        if(mAlert != null) {
            mAlert.dismiss();
        }
        super.onStop();
    }

    /**
     * Stores activity data in the Bundle.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        outState.putParcelable(LOCATION_KEY, mCurrentLocation);
        outState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
        super.onSaveInstanceState(outState);
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and TODO!!!
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }

            // Update mResolvingError boolean
            mResolvingError = savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

            getVenuesWithCurrentLocation();
        }
    }

    // Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle bundle) {
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

        if(mRequestingLocationUpdates) {
            // Check if LocationListFragment exist
            if(getFragmentManager().findFragmentByTag("location_frag") != null) {
                // If List is displayed, switch back to ProgressBar
                if(viewSwitcher.getDisplayedChild() == 1) {
                    viewSwitcher.showNext();
                }
                // Remove fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentByTag("location_frag"))
                        .commit();
            }
            startLocationUpdates();
        } else {

            // If the initial location was never previously requested, we use
            // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
            // its value in the Bundle and check for it in onCreate().
            //
            // Because we cache the value of the initial location in the Bundle, it means that if the
            // user launches the activity,
            // moves to a new location, and then changes the device orientation, the original location
            // is displayed as the activity is re-created.

            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Check that {@code getLastLocation()} is not null
            if(mCurrentLocation == null) {
                mRequestingLocationUpdates = true;
                startLocationUpdates();
            } else {
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                getVenuesWithCurrentLocation();
                Log.i(TAG, "onConnected(), mCurrentLocation != null");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason.
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        Toast.makeText(LocationsActivity.this, i, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        if(mResolvingError) {
            // Already attemting to resolve the error
            return;
        } else if(connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch(IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }

        Toast.makeText(LocationsActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }

    // Creates a dialog for an error message
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((LocationsActivity) getActivity()).onDialogDismissed();
        }
    }

    // Called from ErrorDialogFragment when the dialog is dismissed.
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    // connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR) in onConnectionFailed()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    /**
     * Sets up the location request.
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if(isGPSOn()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } catch(SecurityException e) {
                Log.i(TAG, e.getMessage());
                // TODO
            }
        } else {
            buildGPSAlertDialog();
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mRequestingLocationUpdates) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            mRequestingLocationUpdates = false;
            getVenuesWithCurrentLocation();
        }
    }

    // Checks if device's GPS is on
    private boolean isGPSOn() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Builds AlertDialog prompting to user to turn on GPS
    private void buildGPSAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_is_disabled)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Toast.makeText(LocationsActivity.this, R.string.please_enable_gps, Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        // TODO: introduce "No Connection" view
                        finish();
                    }
                });
        mAlert = builder.create();
        mAlert.show();
    }

    private void getVenuesWithCurrentLocation() {
        Location userLocation = new Location("user_location");
        userLocation.setLatitude(mCurrentLocation.getLatitude());
        userLocation.setLongitude(mCurrentLocation.getLongitude());

        VenuesExploreCriteria criteria = new VenuesExploreCriteria();
        criteria.setLocation(userLocation);

        getLocationsAsyncTask().getVenuesNearby(this, criteria);
    }

    // Result from FoursquareVenuesNearbyRequest on successful response (FoursquareVenuesRequestListener)
    @Override
    public void onVenuesFetched(ArrayList<Venue> venues) {
        mVenues = new ArrayList<>();
        mVenues.addAll(venues);
        // onBackPress (returning from LocationsListFragment) calls this method.
        // To avoid switching back to ProgressBar check if displayed View in fact
        // is ProgressBar (index=0) and switch it to FrameLayout
        if(viewSwitcher.getDisplayedChild() == 0) {
            viewSwitcher.showNext();
        }

        // Make sure that LocationListFragment is not null.
        // This needed to avoid recreating the fragment when onBackPress() from LocationListFragment
        if(getFragmentManager().findFragmentByTag("location_frag") == null) {
            LocationsListFragment locationsFragment = new LocationsListFragment();
            getFragmentManager().beginTransaction().add(R.id.fragmentContainer, locationsFragment, "location_frag").commit();
        }
    }

    // From FoursquareVenuesNearbyRequest if response is unsuccessful
    @Override
    public void onError(final String errorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LocationsActivity.this, errorMsg, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public ArrayList<Venue> getCompactVenues() {
        return mVenues;
    }

    public LocationsAsyncTasks getLocationsAsyncTask() {
        if(mAsync == null) {
            mAsync = new LocationsAsyncTasks(this);
        }
        return mAsync;
    }

    public void setLocationsAsyncTask(LocationsAsyncTasks locationsAsyncTasks) {
        this.mAsync = locationsAsyncTasks;
    }

    public void setRequestingLocationUpdates(boolean requestingLocationUpdates) {
        mRequestingLocationUpdates = requestingLocationUpdates;
    }

    public void setCurrentLocation(Location currentLocation) {
        mCurrentLocation = currentLocation;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        mLastUpdateTime = lastUpdateTime;
    }

    public void setResolvingError(boolean resolvingError) {
        mResolvingError = resolvingError;
    }

    public boolean getRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public String getLastUpdateTime() {
        return mLastUpdateTime;
    }

    public boolean getResolvingError() {
        return mResolvingError;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.locations_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;

            /*case R.id.action_login:
                return true;*/

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
