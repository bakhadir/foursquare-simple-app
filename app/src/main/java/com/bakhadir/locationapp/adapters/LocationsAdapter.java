package com.bakhadir.locationapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bakhadir.locationapp.LocationsMapActivity;
import com.bakhadir.locationapp.R;
import com.bakhadir.locationapp.app.AppController;
import com.bakhadir.locationapp.models.Venue;
import com.bakhadir.locationapp.utils.LocationAppUtils;
import com.bakhadir.locationapp.utils.VenuesListSort;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sam_ch on 1/20/2016.
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {
    public static final String TAG = LocationsAdapter.class.getSimpleName();


    private ArrayList<Venue> mDataset = new ArrayList<>();
    private Context context;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public NetworkImageView mBusinessLogo;
        public TextView mVenueName;
        public TextView mDistance;
        public NetworkImageView mCategoryIcon;
        public TextView mCategory;
        public TextView mIsOpen;
        public ViewHolderClickListener mListener;


        public ViewHolder(View v, ViewHolderClickListener listener) {
            super(v);
            mListener = listener;
            mBusinessLogo = (NetworkImageView) v.findViewById(R.id.businessLogo);
            mVenueName = (TextView) v.findViewById(R.id.venueName);
            mDistance = (TextView) v.findViewById(R.id.distance);
            mCategoryIcon = (NetworkImageView) v.findViewById(R.id.categoryIcon);
            mCategory = (TextView) v.findViewById(R.id.category);
            mIsOpen = (TextView) v.findViewById(R.id.openClosed);
            v.setOnClickListener(this);
            Log.i(TAG, String.valueOf(getAdapterPosition()));
        }

        @Override
        public void onClick(View v) {
            mListener.onViewHolderClick(v, getAdapterPosition());
        }

        public interface ViewHolderClickListener {
            void onViewHolderClick(View callerView, int position);
        }
    }

    // Constructor
    public LocationsAdapter(ArrayList<Venue> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
        sortDataset();
    }



    // Create new views (invoked by the layout manager)
    @Override
    public LocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_adapter_layout, parent, false);

        return new ViewHolder(v, new ViewHolder.ViewHolderClickListener() {
            @Override
            public void onViewHolderClick(View callerView, int position) {
                Intent intent = new Intent(context, LocationsMapActivity.class);

                // Use Gson, serialize Venue obj and put it as extra
                // to use Venue in LocationsMapActivity
                Gson gson = new Gson();
                String venueJsonString = gson.toJson(mDataset.get(position));
                intent.putExtra("venue_json_string", venueJsonString);
                context.startActivity(intent);
            }
        });
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from dataset at this position
        // - replace the contents of the view with that element
//        holder.mBusinessLogo.setDefaultImageResId(R.drawable.def_no_business_logo_placeholder);
        holder.mBusinessLogo.setErrorImageResId(R.drawable.def_no_business_logo_placeholder);
        holder.mBusinessLogo.setImageUrl(LocationAppUtils.getCompanyLogo(mDataset.get(position).getUrl()), AppController.getInstance().getImageLoader());

        holder.mVenueName.setText(mDataset.get(position).getName());
        holder.mDistance.setText(context.getString(R.string.distance_in_miles, LocationAppUtils.getDistanceInMiles(mDataset.get(position).getLocation().getDistance())));

//        holder.mCategoryIcon.setDefaultImageResId(R.drawable.def_cat_icon_placeholder);
        holder.mCategoryIcon.setErrorImageResId(R.drawable.def_cat_icon_placeholder);
        holder.mCategoryIcon.setImageUrl(LocationAppUtils.getCategoryIconUrl(mDataset.get(position).getCategories()), AppController.getInstance().getImageLoader());


        holder.mCategory.setText(LocationAppUtils.getPrimaryCategoryName(mDataset.get(position).getCategories()));

        if(mDataset.get(position).getHours() != null) {
            if(mDataset.get(position).getHours().isOpen() != null) {
                holder.mIsOpen.setText((mDataset.get(position).getHours().isOpen()) ? R.string.isOpen : R.string.isClosed);
            } else {
                holder.mIsOpen.setText(R.string.not_avail);
            }
        } else {
            holder.mIsOpen.setText(R.string.not_avail);
        }
    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Clear all elements of the recycler
    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<Venue> list) {
        mDataset.addAll(list);
        notifyDataSetChanged();
    }

    private void sortDataset() {
        Collections.sort(mDataset, VenuesListSort.DISTANCE_ASC);
        notifyDataSetChanged();
    }

}
