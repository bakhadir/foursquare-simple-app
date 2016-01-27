package com.bakhadir.locationapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class Hours {

    private String status;
    private Boolean isOpen;
    private Boolean isLocalHoliday;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isLocalHoliday() {
        return isLocalHoliday;
    }

    public void setIsLocalHoliday(Boolean isLocalHoliday) {
        this.isLocalHoliday = isLocalHoliday;
    }

    public static Hours fromJSONObject(JSONObject hoursJsonObj) {
        Hours hours = new Hours();

        try{
            hours.setStatus(hoursJsonObj.getString("status"));
        } catch (JSONException je) {
            hours.setStatus("");
        }

        try{
            hours.setIsOpen(hoursJsonObj.getBoolean("isOpen"));
        } catch (JSONException je) {
            hours.setIsOpen(null); //default is NULL (n/a)
        }

        try{
            hours.setIsLocalHoliday(hoursJsonObj.getBoolean("isLocalHoliday"));
        } catch (JSONException je) {
            hours.setIsLocalHoliday(null);
        }

        return hours;
    }
}
