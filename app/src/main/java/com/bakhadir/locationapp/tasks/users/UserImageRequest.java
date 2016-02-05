package com.bakhadir.locationapp.tasks.users;

import android.app.Activity;
import android.graphics.Bitmap;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.bakhadir.locationapp.app.AppController;
import com.bakhadir.locationapp.listeners.ImageRequestListener;
import com.bakhadir.locationapp.utils.LocationAppUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class UserImageRequest {

    private ImageRequestListener mListener;
    private Exception error;

    public UserImageRequest(ImageRequestListener listener) {
        mListener = listener;
    }

    // For testing purposes
//    public UserImageRequest() {
//    }

    // For testing purposes
//    public void setImageRequestListener(ImageRequestListener listener) {
//        mListener = listener;
//    }

    // Getting user photo bitmap using Volley
    public void execute(String userPhotoUrl) {

            AppController.getInstance().getImageLoader().get(userPhotoUrl, new ImageListener() {
                @Override
                public void onResponse(ImageContainer response, boolean isImmediate) {
                    // skip cache failure
                    if (isImmediate && response.getBitmap() == null)
                        return;

                    handleResult(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError vError) {
                    error = new Exception(vError.toString());
                    handleResult(null);
                }
            });
//    }

    }

    private void handleResult(Bitmap bitmap) {
        if (mListener != null) {
            if (error != null) {
                mListener.onError(error.getMessage());
                return;
            }
            mListener.onImageFetched(bitmap);
        }
    }

}
