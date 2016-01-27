package com.bakhadir.locationapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class Stats {

    private int checkinsCount;
    private int usersCount;
    private int tipCount;

    public int getCheckinsCount() {
        return checkinsCount;
    }

    public void setCheckinsCount(int checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public int getTipCount() {
        return tipCount;
    }

    public void setTipCount(int tipCount) {
        this.tipCount = tipCount;
    }

    public static Stats fromJSONObject(JSONObject statsJsonObj) {

        Stats stats = new Stats();

        try {
            stats.setCheckinsCount(statsJsonObj.getInt("checkinsCount"));
        }
        catch (JSONException je) {
            stats.setCheckinsCount(-1);
        }

        try {
            stats.setUsersCount(statsJsonObj.getInt("usersCount"));
        }
        catch (JSONException je) {
            stats.setUsersCount(-1);
        }

        try {
            stats.setTipCount(statsJsonObj.getInt("tipCount"));
        }
        catch (JSONException je) {
            stats.setTipCount(-1);
        }

        return stats;

    }

}
