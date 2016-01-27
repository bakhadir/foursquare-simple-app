package com.bakhadir.locationapp.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.bakhadir.locationapp.R;
import com.bakhadir.locationapp.app.AppController;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/25/2016.
 */
public class FullScreenImageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> venuePhotosUrls;
    private LayoutInflater inflater;
    NetworkImageView image;

    // Constructor
    public FullScreenImageAdapter(Context context, ArrayList<String> venuePhotosUrls) {
        this.venuePhotosUrls = venuePhotosUrls;
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return venuePhotosUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = inflater.inflate(R.layout.fullscreen_image_layout, container, false);

        image = (NetworkImageView) itemView.findViewById(R.id.fullScreenImage);
        String releaseImageUrl = venuePhotosUrls.get(position);
        // TODO:
//        image.setDefaultImageResId(defaultImageResId);
//        image.setErrorImageResId(defaultImageResId);
//        image.setBackgroundResource(R.drawable.image_border_drawable);
//        image.setShouldAnimate(true);
        image.setImageUrl(releaseImageUrl, AppController.getInstance().getImageLoader());

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
