package com.bakhadir.locationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.bakhadir.locationapp.adapters.FullScreenImageAdapter;
import com.bakhadir.locationapp.utils.DepthPageTransformer;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/25/2016.
 */

public class VenuePhotosFullScreenActivity extends AppCompatActivity {

    FullScreenImageAdapter adapter;
    ViewPager viewPager;
    ArrayList<String> venuePhotosUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_photo_fullscreen);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent intent = getIntent();
        int position = intent.getIntExtra("image_position", 0);
        venuePhotosUrls = intent.getStringArrayListExtra("venue_photos_urls");

        adapter = new FullScreenImageAdapter(this, venuePhotosUrls);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(position);
    }

}
