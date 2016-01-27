package com.bakhadir.locationapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class Icon {

    private String prefix;
    private String suffix;

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

    public static Icon fromJSONObject(JSONObject iconJsonObj) {

        Icon icon = new Icon();

        try {
            icon.setPrefix(iconJsonObj.getString("prefix"));
        } catch (JSONException je) {
            icon.setPrefix("");
        }

        try {
            icon.setSuffix(iconJsonObj.getString("suffix"));
        } catch (JSONException je) {
            icon.setSuffix("");
        }

        return icon;
    }

}
