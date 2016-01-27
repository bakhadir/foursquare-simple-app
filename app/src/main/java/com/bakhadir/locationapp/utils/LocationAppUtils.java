package com.bakhadir.locationapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.Cache;
import com.bakhadir.locationapp.R;
import com.bakhadir.locationapp.app.AppController;
import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.constants.FoursquareConstants;
import com.bakhadir.locationapp.models.Category;
import com.bakhadir.locationapp.models.Photo;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by sam_ch on 1/20/2016.
 */

// TODO: methods descriptions

public class LocationAppUtils {

    private static final String TAG = LocationAppUtils.class.getSimpleName();


    /**
     * Converts meters to miles, rounds result (scale = 2) and returns String representation of
     * the miles
     * @param distanceInMeters
     *              meters to be converted
     * @return {@code String} representation of the conversion
     * */
    public static String getDistanceInMiles(int distanceInMeters) {
        double distanceInMiles = (float)AppConstants.milesInKm*distanceInMeters;
        return String.valueOf(BigDecimal.valueOf(distanceInMiles).setScale(AppConstants.scale, BigDecimal.ROUND_HALF_UP).floatValue());
    }

    /**
     * Builds url for the category icon image
     * @param categories
     *          ArrayList of categories. url is constructed from Primary category obj,
     *          and consists of 3 parts: prefix, size (which depends on device density) and suffix
     * @return {@code String} of the category's icon image url
     * */
    public static String getCategoryIconUrl(ArrayList<Category> categories) {
        String catIconUrl = "";
        if(categories != null && !categories.isEmpty()) {
            // Set default category icon url as the url of the first element from categories list
            // just in case if primary is missing
            catIconUrl = categories.get(0).getIcon().getPrefix() + getIconSize() + categories.get(0).getIcon().getSuffix();
            for(Category category : categories) {
                if(category.isPrimary()) {
                    catIconUrl = category.getIcon().getPrefix() + getIconSize() + category.getIcon().getSuffix();
                }
            }
        }
        return catIconUrl;
    }

    /**
     * Returns BG_XX constant depending on the device screen density.
     * BG_XX constant is the part of the category icon url
     * @return {@code String} constant of the icon's size
     * */
    private static String getIconSize() {
        float density = AppController.getInstance().getResources().getDisplayMetrics().density;
        switch(String.valueOf(density)) {
            case "0.75": //ldpi
                return AppConstants.BG_32;
            case "1.0": //mdpi
                return AppConstants.BG_32;
            case "1.5": //hdpi
                return AppConstants.BG_32;
            case "2.0": //xhdpi
                return AppConstants.BG_44;
            case "3.0": //xxhdpi
                return AppConstants.BG_64;
            case "4.0": //xxxhdpi
                return AppConstants.BG_88;
            default:
                return AppConstants.BG_44;
        }
    }

    /**
     * Returns name of primary category from the list, or the category name of the first element
     * from list.
     * @param categories
     *          ArrayList of categories.
     * @return {@code String} of the category's name
     * */
    public static String getPrimaryCategoryName(ArrayList<Category> categories) {
        String categoryName = "";
        if(categories != null && !categories.isEmpty()) {
            // Set default category name as the name of the first element from categories list
            // just in case if primary is missing
            categoryName = categories.get(0).getName();
            for(Category category : categories) {
                if(category.isPrimary()) {
                    categoryName = category.getName();
                }
            }
        }
        return categoryName;
    }

    /**
     * Builds url for the company's logo by getting url host/domain and concatenating it to the
     * "https://logo.clearbit.com/" - Clearbit's free Logo API
     * @param url
     *          {@code String} of the company's url (usually business website)
     * @return {@code String} of the company's logo (image url)
     * */
    public static String getCompanyLogo(String url) {
        String host = "";

        /*String httpProtocol = "http://";
        // Check if url contains http:// protocol
        if(!url.contains(httpProtocol)) {
            url = httpProtocol.concat(url);
        }*/

        // Some urls does not contain http:// protocol
        url = checkHttpHost(url);

        try {
            URL hostUrl = new URL(url);
            host = hostUrl.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return AppConstants.CLEARBIT_URL + host;
    }

    public static String checkHttpHost(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    /**
     * Builds Foursquare User self info request url
     * @param token
     *      token String
     * @return {@code String} of the user's self info request url
     * */
    public static String buildSelfInfoRequestUrl(String token) {
        return FoursquareConstants.USERS_URL + FoursquareConstants.API_DATE_VERSION
                + FoursquareConstants.OAUTH_TOKEN_STRING + token;
    }

    /**
     * Decodes byte array of data to bitmap
     * @param data
     *      {@code byte[]} representation of the bitmap
     * @return decoded bitmap
     * */
    // TODO: test case
    public static Bitmap decodeByteToBitmap(byte[] data){
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Decodes data byte array into JSONObject
     * @param data
     *      {@code byte[]} representation on JSON string
     * @return decoded JSONObject
     * */
    public static JSONObject decodeByteToJSONObj(byte[] data) throws JSONException {
        String newString = new String(data);
        return new JSONObject(newString);
    }

    /**
     * Converts dp to px.
     * @param dpValue
     *      {@code int} representation of dp needed to convert to px
     * @return converted dp to px
     *
     * */

    public static float convertDpToPx(int dpValue) {
        return dpValue * AppController.getInstance().getResources().getDisplayMetrics().density;
    }

    /**
     * Returns cached bitmap of venue's category (if it's in cache)
     * or default placeholder
     * @param categoryIconUrl
     *          url of the venue's category (cache tag)
     * @return {@code Bitmap} from cache (or default placeholder if requested bitmap not in cache)
     * */
    public static Bitmap getBitmapFromCache(String categoryIconUrl) {
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(categoryIconUrl);
        if(entry != null){
            return LocationAppUtils.decodeByteToBitmap(entry.data);
        } else {
            return BitmapFactory.decodeResource(AppController.getInstance().getResources(), R.drawable.def_cat_icon_placeholder);
        }
    }

    /**
     * Builds venue's photo url from photo prefix, "original" size and photo suffix
     * @param photo
     *      {@code Photo} object
     * @return {@code String} of venue's photo url
     *
     * */
    public static String buildPhotoUrl(Photo photo) {
        return photo.getPrefix() + AppConstants.VENUE_PHOTO_ORIGINAL_SIZE + photo.getSuffix();
    }

    /**
     * Builds url for business's facebook and twitter web page
     *
     * */
    public static String buildSocialUrl(String socialSite, String socialUserName) {
        return socialSite + socialUserName;
    }
}
