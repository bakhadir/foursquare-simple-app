package com.bakhadir.locationapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bakhadir.locationapp.adapters.VenuePhotosAdapter;
import com.bakhadir.locationapp.app.AppController;
import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.listeners.CompleteVenueInfoListener;
import com.bakhadir.locationapp.listeners.VenuePhotosListener;
import com.bakhadir.locationapp.models.Photo;
import com.bakhadir.locationapp.models.Venue;
import com.bakhadir.locationapp.utils.LocationAppUtils;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.OnSheetDismissedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class LocationsMapActivity extends FragmentActivity implements OnMapReadyCallback, CompleteVenueInfoListener/*, VenuePhotosListener*/ {

    public static final String TAG = LocationsMapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private double lat;
    private double lng;
    private String locationName;
    private Venue venue;
    private BottomSheetLayout bottomSheet;
    private View convertView;
    private Venue completeVenue;

    private NetworkImageView mVenuePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_map);

        bottomSheet = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        // Add listener to recenter camera when bottomsheet dismissed
        bottomSheet.addOnSheetDismissedListener(new OnSheetDismissedListener() {
            @Override
            public void onDismissed(BottomSheetLayout bottomSheetLayout) {
                recenterCamera();
            }
        });


        // Get json String extra
        Gson gson = new Gson();
        venue = gson.fromJson(getIntent().getStringExtra("venue_json_string"), Venue.class);

        lat = venue.getLocation().getLat();
        lng = venue.getLocation().getLng();
        locationName = venue.getName();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locationMap);
        mapFragment.getMapAsync(this);

        buildBottomSheet();

        FoursquareAsync async = new FoursquareAsync(this);
        async.getCompleteVenueInfo(this, venue.getId());

        // TODO get Venue photos async
//         FoursquareAsync async = new FoursquareAsync(this);
//        async.getVenuePhotos(this, venue.getId());
    }

    private void buildBottomSheet() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.bottom_sheet_layout, bottomSheet, false);

        mVenuePhoto = (NetworkImageView) convertView.findViewById(R.id.venuePhoto);

        TextView venueName = (TextView) convertView.findViewById(R.id.venueName);
        venueName.setText(venue.getName());

        TextView venueAddress = (TextView) convertView.findViewById(R.id.venueAddress);
        venueAddress.setText(venue.getLocation().getFormattedAddress().get(0));

        TextView venuePhone = (TextView) convertView.findViewById(R.id.venuePhone);
        venuePhone.setText(venue.getContact().getFormattedPhone());

        TextView venueOpenNow = (TextView) convertView.findViewById(R.id.isOpen);
        if (venue.getHours() != null) {
            // Check for Status first
            if (!venue.getHours().getStatus().equals("")) {
                venueOpenNow.setText(venue.getHours().getStatus());
            } else if (venue.getHours().isOpen() != null) {
                venueOpenNow.setText((venue.getHours().isOpen()) ? R.string.isOpen : R.string.isClosed);
            }
        }

        TextView venueRating = (TextView) convertView.findViewById(R.id.venueRating);
        View venueRatingColor = convertView.findViewById(R.id.ratingColorImage);

        if (venue.getRating() != -1.0) {
            venueRating.setText(String.valueOf(venue.getRating()));
        } else {
            venueRating.setText(R.string.not_avail);
        }

        if (venue.getRatingColor() != null && !venue.getRatingColor().equals("")) {
            venueRatingColor.setBackgroundColor(Color.parseColor("#" + venue.getRatingColor()));
        }


        final LinearLayout venueWebLinks = (LinearLayout) convertView.findViewById(R.id.venueWebLinks);
        OnIconClickListener onIconClickListener = new OnIconClickListener();

        // Create call Icon
        if (venue.getContact() != null && !venue.getContact().getPhone().equals("")) {
            ImageView venuePhoneIcon = createIcon(R.drawable.ic_phone_grey600_36dp, R.string.tag_phone_icon, venue.getContact().getPhone(), onIconClickListener);
            venueWebLinks.addView(venuePhoneIcon);
        }

        // Create website icon
        if (!venue.getUrl().equals("")) {
            ImageView venueWebsiteIcon = createIcon(R.drawable.ic_web_grey600_36dp, R.string.tag_website_icon, venue.getUrl(), onIconClickListener);
            venueWebLinks.addView(venueWebsiteIcon);
        }

        // Create twitter icon
        if (venue.getContact() != null && !venue.getContact().getTwitter().equals("")) {
            ImageView venueTwitterIcon = createIcon(R.drawable.ic_twitter_box_grey600_36dp, R.string.tag_twitter_icon,
                    LocationAppUtils.buildSocialUrl(AppConstants.TWITTER_URL, venue.getContact().getTwitter()), onIconClickListener);
            venueWebLinks.addView(venueTwitterIcon);
        }

        // Create facebook icon
        if(venue.getContact() != null) {
            String fbTag = "";
            // Check if Contact contains facebook or facebookUsername
            if(!venue.getContact().getFacebookUsername().equals("")) {
                fbTag = LocationAppUtils.buildSocialUrl(AppConstants.FACEBOOK_URL, venue.getContact().getFacebookUsername());
            } else if(!venue.getContact().getFacebook().equals("")) {
                fbTag = LocationAppUtils.buildSocialUrl(AppConstants.FACEBOOK_URL, venue.getContact().getFacebook());
            }

            // We have facebook url
            if(!fbTag.equals("")) {
                ImageView venueFacebookIcon = createIcon(R.drawable.ic_facebook_box_grey600_36dp, R.string.tag_facebook_icon,
                        fbTag, onIconClickListener);
                venueWebLinks.addView(venueFacebookIcon);
            }
        }

        if (venue.getMenu() != null && !venue.getMenu().getMobileUrl().equals("")) {
            ImageView venueMenuIcon = createIcon(R.drawable.ic_menu_grey600_36dp, R.string.tag_menu_icon, venue.getMenu().getMobileUrl(), onIconClickListener);
            venueWebLinks.addView(venueMenuIcon);
        }

        // Set venueWebLinks layout GONE if it has no child views
        if(venueWebLinks.getChildCount() == 0) {
            venueWebLinks.setVisibility(View.GONE);
        }
    }

    private ImageView createIcon(int bitmapResource, int iconTagKey, String iconTagValue, OnIconClickListener listener) {

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), bitmapResource));
        imageView.setPadding((int) LocationAppUtils.convertDpToPx(AppConstants.ICON_PADDING), 0,
                (int) LocationAppUtils.convertDpToPx(AppConstants.ICON_PADDING), 0);
        imageView.setClickable(true);
        // This tag is used for switch statement in OnIconClickListener
        imageView.setTag(iconTagKey);
        imageView.setTag(iconTagKey, iconTagValue);
        imageView.setOnClickListener(listener);

        // Set imageView layout parameters (setting layout weight to 1 for each image view)
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        imageView.setLayoutParams(param);

        return imageView;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in venue location and move the camera
        LatLng locationLatLng = new LatLng(lat, lng);
        //Build camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(locationLatLng)
                .zoom(15).build();

        //Zoom in and animate the camera.
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500, null);

        // Add marker
        mMap.addMarker(new MarkerOptions()
                .position(locationLatLng)
                .title(locationName)
                .icon(BitmapDescriptorFactory.fromBitmap(LocationAppUtils.getBitmapFromCache(LocationAppUtils.getCategoryIconUrl(venue.getCategories()))))
                .snippet(LocationAppUtils.getPrimaryCategoryName(venue.getCategories())))
                .showInfoWindow();

        // Move camera after 1 second delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.scrollBy(0.0f, LocationAppUtils.convertDpToPx(AppConstants.BOTTOM_SHEET_IMAGE_DP) / 2));
                bottomSheet.setPeekSheetTranslation(LocationAppUtils.convertDpToPx(AppConstants.BOTTOM_SHEET_IMAGE_DP));
                bottomSheet.showWithSheetView(convertView);
            }
        }, 1000);
    }

    private void recenterCamera() {
        mMap.animateCamera(CameraUpdateFactory.scrollBy(0.0f, -(LocationAppUtils.convertDpToPx(AppConstants.BOTTOM_SHEET_IMAGE_DP) / 2)));
    }

    @Override
    public void onCompleteVenueInfoFetched(Venue venue) {
        completeVenue = venue;
        mVenuePhoto.setErrorImageResId(R.drawable.ic_image_grey600_36dp);
        // Get venue's best photo url
        mVenuePhoto.setImageUrl(LocationAppUtils.buildPhotoUrl(completeVenue.getBestPhoto()), AppController.getInstance().getImageLoader());
        if(completeVenue.getPhotos() != null && !completeVenue.getPhotos().isEmpty()) {
            buildVenuePhotosGrid();
        }
    }

    @Override
    public void onError(String errorMsg) {
        // TODO: TOAST
    }

    private void buildVenuePhotosGrid() {
        RecyclerView venuePhotosRecyclerView = (RecyclerView) convertView.findViewById(R.id.venuePhotosRecyclerView);
        ArrayList<String> venuePhotosUrls = new ArrayList<>();
        venuePhotosRecyclerView.setVisibility(View.VISIBLE);
        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        CustomGridLayoutManager customLinearLayoutManager = new CustomGridLayoutManager(this, 3, LinearLayoutManager.VERTICAL,false);
        // Set RecyclerView's LayoutManager to the one given.
        venuePhotosRecyclerView.setLayoutManager(customLinearLayoutManager);

        // Get venue photos urls
        for(Photo photo : completeVenue.getPhotos()) {
            venuePhotosUrls.add(LocationAppUtils.buildPhotoUrl(photo));
        }

        // specify an adapter
        VenuePhotosAdapter mAdapter = new VenuePhotosAdapter(venuePhotosUrls, this);
        // Set VenuePhotosAdapter as the adapter for RecyclerView.
        venuePhotosRecyclerView.setAdapter(mAdapter);
        venuePhotosRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Add "Load More" bitmap as last image in grid view
        mAdapter.addLoadMoreImage();

    }

    // Listener for web links and phone icon clicks
    private class OnIconClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int viewKeyTag = (int)v.getTag();
            String viewTagValue = (String) v.getTag(viewKeyTag);

            // Check for http
            if(viewKeyTag != R.string.tag_phone_icon) {
                viewTagValue = LocationAppUtils.checkHttpHost(viewTagValue);
            }

            switch (viewKeyTag) {
                case R.string.tag_phone_icon:
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + viewTagValue));
                    startActivity(callIntent);
                    break;
                case R.string.tag_website_icon:
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    webIntent.setData(Uri.parse(viewTagValue));
                    startActivity(webIntent);
                    break;
                case R.string.tag_twitter_icon:
                    Intent twitterIntent = new Intent(Intent.ACTION_VIEW);
                    twitterIntent.setData(Uri.parse(viewTagValue));
                    startActivity(twitterIntent);
                    break;
                case R.string.tag_facebook_icon:
                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                    facebookIntent.setData(Uri.parse(viewTagValue));
                    startActivity(facebookIntent);
                    break;
                case R.string.tag_menu_icon:
                    Intent menuIntent = new Intent(Intent.ACTION_VIEW);
                    menuIntent.setData(Uri.parse(viewTagValue));
                    startActivity(menuIntent);
                    break;
                default:
                    break;
            }
        }
    }

    // CustomLinearLayoutManager with vertical scroll disabled - used for RecyclerView
    private class CustomGridLayoutManager extends GridLayoutManager {
        public CustomGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        // it will always pass false to RecyclerView when calling "canScrollVertically()" method.
        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }
}
