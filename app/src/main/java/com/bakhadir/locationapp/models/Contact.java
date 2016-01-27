package com.bakhadir.locationapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_ch on 1/20/2016.
 *
 * Contact info might be in Parent JSON Object (from Complete Venue call)
 */

public class Contact {
    private String phone;
    private String formattedPhone;
    private String twitter;
    private String facebook; // id, numeric
    private String facebookUsername;
    private String facebookName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFormattedPhone() {
        return formattedPhone;
    }

    public void setFormattedPhone(String formattedPhone) {
        this.formattedPhone = formattedPhone;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getFacebookUsername() {
        return facebookUsername;
    }

    public void setFacebookUsername(String facebookUsername) {
        this.facebookUsername = facebookUsername;
    }

    public String getFacebookName() {
        return facebookName;
    }

    public void setFacebookName(String facebookName) {
        this.facebookName = facebookName;
    }

    public static Contact fromJSONObject(JSONObject contactJsonObj) {

        Contact contact = new Contact();

        try {
            contact.setPhone(contactJsonObj.getString("phone"));
        }
        catch (JSONException je) {
            contact.setPhone("");
        }

        try {
            contact.setFormattedPhone(contactJsonObj.getString("formattedPhone"));
        }
        catch (JSONException je) {
            contact.setFormattedPhone("");
        }

        try {
            contact.setTwitter(contactJsonObj.getString("twitter"));
        }
        catch (JSONException je) {
            contact.setTwitter("");
        }

        try {
            contact.setFacebook(contactJsonObj.getString("facebook"));
        }
        catch (JSONException je) {
            contact.setFacebook("");
        }

        try {
            contact.setFacebookUsername(contactJsonObj.getString("facebookUsername"));
        }
        catch (JSONException je) {
            contact.setFacebookUsername("");
        }

        try {
            contact.setFacebookName(contactJsonObj.getString("facebookName"));
        }
        catch (JSONException je) {
            contact.setFacebookName("");
        }

        return contact;

    }
}
