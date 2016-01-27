package com.bakhadir.locationapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class Price {

    private int tier;
    private String message;
    private String currency;


    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static Price fromJSONObject(JSONObject priceJsonObj) {

        Price price  = new Price();

        try {
            price.setTier(priceJsonObj.getInt("tier"));
        }
        catch (JSONException je) {
            price.setTier(-1);
        }

        try {
            price.setMessage(priceJsonObj.getString("message"));
        }
        catch (JSONException je) {
            price.setMessage("");
        }

        try {
            price.setCurrency(priceJsonObj.getString("currency"));
        }
        catch (JSONException je) {
            price.setCurrency("");
        }

        return price;

    }
}
