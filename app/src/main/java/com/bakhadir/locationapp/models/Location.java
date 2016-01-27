package com.bakhadir.locationapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class Location {

    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String cc; // country code
    private double lat;
    private double lng;
    private int distance;
    private String crossStreet;
    private ArrayList<String> formattedAddress;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCrossStreet() {
        return crossStreet;
    }

    public void setCrossStreet(String crossStreet) {
        this.crossStreet = crossStreet;
    }

    public ArrayList<String> getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(ArrayList<String> formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public void addFormattedAddress(String partOfAddress) {
        if(formattedAddress == null) {
            formattedAddress = new ArrayList<>();
        }
        formattedAddress.add(partOfAddress);
    }

    public static Location fromJSONObject(JSONObject locationJsonObj) {
        Location location = new Location();

        try {
            location.setAddress(locationJsonObj.getString("address"));
        }
        catch (JSONException je) {
            location.setAddress("");
        }

        try {
            location.setCity(locationJsonObj.getString("city"));
        }
        catch (JSONException je) {
            location.setCity("");
        }

        try {
            location.setState(locationJsonObj.getString("state"));
        }
        catch (JSONException je) {
            location.setState("");
        }

        try {
            location.setPostalCode(locationJsonObj.getString("postalCode"));
        }
        catch (JSONException je) {
            location.setPostalCode("");
        }

        try {
            location.setCountry(locationJsonObj.getString("country"));
        }
        catch (JSONException je) {
            location.setCountry("");
        }

        try {
            location.setCc(locationJsonObj.getString("cc"));
        }
        catch (JSONException je) {
            location.setCc("");
        }

        try {
            location.setLat(locationJsonObj.getDouble("lat"));
        }
        catch (JSONException je) {
            location.setDistance(-1);
        }

        try {
            location.setLng(locationJsonObj.getDouble("lng"));
        }
        catch (JSONException je) {
            location.setDistance(-1);
        }

        try {
            location.setDistance(locationJsonObj.getInt("distance"));
        }
        catch (JSONException je) {
            location.setDistance(-1);
        }

        try {
            location.setCrossStreet(locationJsonObj.getString("crossStreet"));
        }
        catch (JSONException je) {
            location.setCrossStreet("");
        }

        try {
            JSONArray jsonArray = locationJsonObj.getJSONArray("formattedAddress");
            for(int i=0; i<jsonArray.length(); i++) {
                location.addFormattedAddress((String)jsonArray.get(i));
            }
        }
        catch (JSONException je) {
            location.setFormattedAddress(null);
        }

        return location;
    }

    /*//overriding equals() and hashCode() methods for using the class with containers
    //like HashSet, or HashMap, for removing duplicates, sorting etc.
    @Override
    public boolean equals(Object arg) {
        if(arg == null)
            return false;
        if(this == arg)
            return true;
        if(arg instanceof Location) {
            Location that = (Location) arg;
            if(this.lat == that.lat) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        //using bit-manipulation operator (^) to generate close to unique hash codes
        //here we are using the magic numbers 7 and 11 (along with Object id)
        return 7 * (int)this.lat ^ 11;
    }*/
}
