package com.bakhadir.locationapp.listeners;

import android.graphics.Bitmap;

/**
 * Created by sam_ch on 1/20/2016.
 */

public interface ImageRequestListener extends ErrorListener {
    void onImageFetched(Bitmap bmp);
}
