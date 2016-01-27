package com.bakhadir.locationapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class Menu {

    private String type;
    private String label;
    private String anchor;
    private String url;
    private String mobileUrl;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public static Menu fromJSONObject(JSONObject menuJsonObj) {

        Menu menu = new Menu();

        try {
            menu.setType(menuJsonObj.getString("type"));
        }
        catch (JSONException je) {
            menu.setType("");
        }

        try {
            menu.setLabel(menuJsonObj.getString("label"));
        }
        catch (JSONException je) {
            menu.setLabel("");
        }

        try {
            menu.setAnchor(menuJsonObj.getString("anchor"));
        }
        catch (JSONException je) {
            menu.setAnchor("");
        }

        try {
            menu.setUrl(menuJsonObj.getString("url"));
        }
        catch (JSONException je) {
            menu.setUrl("");
        }

        try {
            menu.setMobileUrl(menuJsonObj.getString("mobileUrl"));
        }
        catch (JSONException je) {
            menu.setMobileUrl("");
        }

        return menu;

    }
}
