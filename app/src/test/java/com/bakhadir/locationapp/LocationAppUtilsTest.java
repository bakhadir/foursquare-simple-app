package com.bakhadir.locationapp;

import android.util.Log;

import com.bakhadir.locationapp.adapters.LocationsAdapter;
import com.bakhadir.locationapp.constants.AppConstants;
import com.bakhadir.locationapp.models.Category;
import com.bakhadir.locationapp.models.Photo;
import com.bakhadir.locationapp.models.User;
import com.bakhadir.locationapp.utils.LocationAppUtils;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by sam_ch on 1/13/2016.
 */
public class LocationAppUtilsTest {

    @Test
    public void testDistanceInMiles() {
        assertEquals("0.0", LocationAppUtils.getDistanceInMiles(0));
        assertEquals("0.03", LocationAppUtils.getDistanceInMiles(50));
        assertEquals("0.62", LocationAppUtils.getDistanceInMiles(1000));
        assertEquals("-0.62", LocationAppUtils.getDistanceInMiles(-1000));
        assertEquals("-0.03", LocationAppUtils.getDistanceInMiles(-50));
    }

    @Test
    public void testCategoryIconUrl() {
        assertEquals("", LocationAppUtils.getCategoryIconUrl(new ArrayList<Category>())); // TODO create MOCK ArrayList<Category>()
        assertEquals("", LocationAppUtils.getCategoryIconUrl(null));
    }

    @Test
    public void testGetPrimaryCategoryName() {
        assertEquals("", LocationAppUtils.getPrimaryCategoryName(null));
        assertEquals("", LocationAppUtils.getPrimaryCategoryName(new ArrayList<Category>())); // TODO create MOCK ArrayList<Category>()
    }

    @Test
    public void testCompanyLogo() {
        assertEquals("https://logo.clearbit.com/", LocationAppUtils.getCompanyLogo(""));
        // assertEquals("https://logo.clearbit.com/", LocationAppUtils.getCompanyLogo(null)); never will bee null, default always ""
        assertEquals("https://logo.clearbit.com/www.website.com", LocationAppUtils.getCompanyLogo("http://www.website.com"));
        assertEquals("https://logo.clearbit.com/www.website.com", LocationAppUtils.getCompanyLogo("www.website.com"));
        assertEquals("https://logo.clearbit.com/website.com", LocationAppUtils.getCompanyLogo("http://website.com"));
        assertEquals("https://logo.clearbit.com/website.com", LocationAppUtils.getCompanyLogo("http://website.com/default.php"));
        assertEquals("https://logo.clearbit.com/www.website.com", LocationAppUtils.getCompanyLogo("http://www.website.com/home/default/index.php"));
    }

    @Test
    public void testCheckHttpHost() {
        assertEquals("http://", LocationAppUtils.checkHttpHost(""));
        assertEquals("http://www.company.com", LocationAppUtils.checkHttpHost("www.company.com"));
        assertEquals("http://www.company.com", LocationAppUtils.checkHttpHost("http://www.company.com"));
        assertEquals("https://www.company.com", LocationAppUtils.checkHttpHost("https://www.company.com"));
    }

    @Test
    public void testBuildSelfInfoRequestUrl() {
        assertEquals("https://api.foursquare.com/v2/users/self?v=20151201&oauth_token=MY_TOKEN", LocationAppUtils.buildSelfInfoRequestUrl("MY_TOKEN"));
        assertEquals("https://api.foursquare.com/v2/users/self?v=20151201&oauth_token=MY_SECOND_TOKEN", LocationAppUtils.buildSelfInfoRequestUrl("MY_SECOND_TOKEN"));
    }

    @Test
    public void testDecodeByteToBitmap() throws IOException {
        assertNull("bitmap is not null", LocationAppUtils.decodeByteToBitmap(null));
        assertNull("bitmap is not null", LocationAppUtils.decodeByteToBitmap(new byte[]{}));
        assertNull("bitmap is not null", LocationAppUtils.decodeByteToBitmap(new byte[]{0, 1, 5, 10}));
//        assertNotNull("bitmap is null", LocationAppUtils.decodeByteToBitmap(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("data/bitmap_byte_array.test")))); //TODO: ?
//        assertNotNull("bitmap is null", LocationAppUtils.decodeByteToBitmap(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("data/test_image.jpg")))); //TODO: ?
    }

    @Test
    public void testBuildPhotoUrl() {
        assertEquals("nulloriginalnull", LocationAppUtils.buildPhotoUrl(new Photo())); // TODO mock Photo object
    }
}
