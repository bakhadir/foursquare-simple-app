package com.bakhadir.locationapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bakhadir.locationapp.R;

import com.android.volley.toolbox.NetworkImageView;
import com.bakhadir.locationapp.LocationsMapActivity;
import com.bakhadir.locationapp.VenuePhotosFullScreenActivity;
import com.bakhadir.locationapp.app.AppController;
import com.bakhadir.locationapp.utils.LocationAppUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/25/2016.
 */
public class VenuePhotosAdapter extends RecyclerView.Adapter<VenuePhotosAdapter.ViewHolder> {

    public static final String TAG = VenuePhotosAdapter.class.getSimpleName();


    private ArrayList<String> mDataset = new ArrayList<>();
    private Context context;

    // Constructor
    public VenuePhotosAdapter(ArrayList<String> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    public void addLoadMoreImage() {
        mDataset.add(mDataset.size(), "");
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public NetworkImageView mVenuePhoto;
        public ViewHolderClickListener mListener;


        public ViewHolder(View v, ViewHolderClickListener listener) {
            super(v);
            mListener = listener;
            mVenuePhoto = (NetworkImageView) v.findViewById(R.id.venuePhoto);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onViewHolderClick(v, getAdapterPosition());
        }

        public interface ViewHolderClickListener {
            void onViewHolderClick(View callerView, int position);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public VenuePhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_list, parent, false);

        return new ViewHolder(v, new ViewHolder.ViewHolderClickListener() {
            @Override
            public void onViewHolderClick(View callerView, int position) {
                if(position == (mDataset.size()-1)) {
//                    Intent intent = new Intent(context, VenuePhotosGridActivity.class);
                    Log.i(TAG, "VENUE PHOTOS GRID ACTIVITY HERE");
                } else {
                    // TODO: fire up fullscreen image view
                    // Create new dataset without last element for Fullscreen activity
                    ArrayList<String> intentDataset = new ArrayList<>();
                    intentDataset.addAll(mDataset);
                    intentDataset.remove(mDataset.size()-1);
                    Intent intent = new Intent(context, VenuePhotosFullScreenActivity.class);
                    intent.putExtra("image_position", position);
                    intent.putStringArrayListExtra("venue_photos_urls", intentDataset);
                    context.startActivity(intent);
                }
            }
        });
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from dataset at this position
        // - replace the contents of the view with that element
        // TODO: testing (setting up last "Load More" image view)
        if(position == (mDataset.size()-1)) {
            holder.mVenuePhoto.setDefaultImageResId(R.drawable.ic_load_more_image_grey600_36dp);
//            holder.mVenuePhoto.setErrorImageResId(R.drawable.ic_facebook_box_grey600_36dp);
//            holder.mVenuePhoto.setImageUrl("", AppController.getInstance().getImageLoader());
            holder.mVenuePhoto.setScaleType(ImageView.ScaleType.CENTER);
        } else {
            holder.mVenuePhoto.setDefaultImageResId(R.drawable.ic_image_grey600_36dp);
            holder.mVenuePhoto.setErrorImageResId(R.drawable.ic_image_grey600_36dp);
            holder.mVenuePhoto.setImageUrl(mDataset.get(position), AppController.getInstance().getImageLoader());
        }

    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
