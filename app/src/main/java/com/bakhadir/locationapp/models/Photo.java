package com.bakhadir.locationapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class Photo {

    private String id;
    private int createdAt;
    private String prefix;
    private String suffix;
    private String visibility;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public static Photo fromJSONObject(JSONObject photoJsonObj) {

        Photo photo = new Photo();

        try {
            photo.setId(photoJsonObj.getString("id"));
        }
        catch (JSONException je) {
            photo.setId("");
        }

        try {
            photo.setCreatedAt(photoJsonObj.getInt("createdAt"));
        }
        catch (JSONException je) {
            photo.setCreatedAt(-1);
        }

        try {
            photo.setPrefix(photoJsonObj.getString("prefix"));
        }
        catch (JSONException je) {
            photo.setPrefix("");
        }

        try {
            photo.setSuffix(photoJsonObj.getString("suffix"));
        }
        catch (JSONException je) {
            photo.setSuffix("");
        }

        try {
            photo.setVisibility(photoJsonObj.getString("visibility"));
        }
        catch (JSONException je) {
            photo.setVisibility("");
        }

        return photo;

    }
}
