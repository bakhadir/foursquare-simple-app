package com.bakhadir.locationapp.models;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class Venue {

    private String id;
    private String name;
    private Contact contact;
    private Location location;
    private ArrayList<Category> categories;
    private boolean isVerified;
    private Stats stats;
    private String url; //*
    private Hours hours; //*
    private Menu menu; //* menu url etc
    private Price price; //* price object
    private double rating; //* 0-10 (included only in "explore" venues not "search")
    private String ratingColor; //*

    private String description; //* complete venue only
    private ArrayList<Photo> photos; //* complete venues
    private Photo bestPhoto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        if(this.categories == null) {
            this.categories = new ArrayList<>();
        }
        this.categories.add(category);
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Hours getHours() {
        return hours;
    }

    public void setHours(Hours hours) {
        this.hours = hours;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRatingColor() {
        return ratingColor;
    }

    public void setRatingColor(String ratingColor) {
        this.ratingColor = ratingColor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {
        if(this.photos == null) {
            this.photos = new ArrayList<>();
        }
        this.photos.add(photo);
    }

    public Photo getBestPhoto() {
        return bestPhoto;
    }

    public void setBestPhoto(Photo photo) {
        this.bestPhoto = photo;
    }

    public static Venue fromJSONObject(JSONObject venueJsonObj) {

        Venue venue = new Venue();

        try {
            venue.setId(venueJsonObj.getString("id"));
        }
        catch (JSONException je) {
            venue.setId("");
        }

        try {
            venue.setName(venueJsonObj.getString("name"));
        }
        catch (JSONException je) {
            venue.setName("");
        }

        try {
            venue.setContact(Contact.fromJSONObject(venueJsonObj.getJSONObject("contact")));
        }
        catch (JSONException je) {
            // Contact obj is null, try to get Contact from venue's "parent" JSON obj
            try {
                venue.setContact(Contact.fromJSONObject((venueJsonObj.getJSONObject("parent").getJSONObject("contact"))));
            } catch (JSONException jsonE) {
                venue.setContact(null);
            }
        }

        try {
            venue.setLocation(Location.fromJSONObject(venueJsonObj.getJSONObject("location")));
        }
        catch (JSONException je) {
            // Location obj is null, try to get Location from "parent" JSON obj
            try {
                venue.setLocation(Location.fromJSONObject((venueJsonObj.getJSONObject("parent").getJSONObject("location"))));
            } catch (JSONException e) {
                venue.setLocation(null);
            }

        }

        try {
            JSONArray categories = venueJsonObj.getJSONArray("categories");
            for (int i = 0; i < categories.length(); i++) {
                venue.addCategory(Category.fromJSONObject(categories.getJSONObject(i)));
            }
        }
        catch (JSONException je) {
            // Categories array is empty/null, get Categories from "parent" JSON obj
            try {
                JSONArray parentCategories = venueJsonObj.getJSONObject("parent").getJSONArray("categories");
                for (int i=0; i < parentCategories.hashCode(); i++) {
                    venue.addCategory(Category.fromJSONObject(parentCategories.getJSONObject(i)));
                }
            } catch(JSONException e) {
                venue.setCategories(null);
            }
        }

        try {
            venue.setIsVerified(venueJsonObj.getBoolean("verified"));
        }
        catch (JSONException je) {
            venue.setIsVerified(false);
        }

        try {
            venue.setStats(Stats.fromJSONObject(venueJsonObj.getJSONObject("stats")));
        }
        catch (JSONException je) {
            venue.setStats(null);
        }

        try {
            venue.setUrl(venueJsonObj.getString("url"));
        }
        catch (JSONException je) {
            // url String is empty or null, get url from "parent" obj
            try {
                venue.setUrl(venueJsonObj.getJSONObject("parent").getString("url"));
            } catch (JSONException e) {
                venue.setUrl("");
            }
        }

        try {
            venue.setHours(Hours.fromJSONObject(venueJsonObj.getJSONObject("hours")));
        } catch (JSONException je) {
            venue.setHours(null);
        }

        try {
            venue.setMenu(Menu.fromJSONObject(venueJsonObj.getJSONObject("menu")));
        } catch (JSONException je) {
            venue.setMenu(null);
        }

        try {
            venue.setPrice(Price.fromJSONObject(venueJsonObj.getJSONObject("price")));
        } catch (JSONException je) {
            venue.setPrice(null);
        }

        try {
            venue.setRating((double)venueJsonObj.get("rating"));
        } catch (JSONException je) {
            venue.setRating(-1.0);
        }

        try {
            venue.setRatingColor(venueJsonObj.getString("ratingColor"));
        } catch (JSONException je) {
            venue.setRatingColor("");
        }

        try {
            venue.setDescription(venueJsonObj.getString("description"));
        } catch (JSONException je) {
            venue.setDescription("");
        }

        try {
            venue.setBestPhoto(Photo.fromJSONObject(venueJsonObj.getJSONObject("bestPhoto")));
        } catch (JSONException je) {
            venue.setBestPhoto(null);
        }

        try {
            JSONObject groups = (JSONObject)venueJsonObj.getJSONObject("photos").getJSONArray("groups").get(0); // getting photos only from 0th element
            JSONArray photos = groups.getJSONArray("items");
            for (int i = 0; i < photos.length(); i++) {
                venue.addPhoto(Photo.fromJSONObject(photos.getJSONObject(i)));
            }
        }
        catch (JSONException je) {
            venue.setPhotos(null);
        }

        return venue;
    }

    //overriding equals() and hashCode() methods for using the class with containers
    //like HashSet, or HashMap, for removing duplicates, sorting etc.
    @Override
    public boolean equals(Object arg) {
        if(arg == null)
            return false;
        if(this == arg)
            return true;
        if(arg instanceof Venue) {
            Venue that = (Venue) arg;
            if(this.id.equals(that.id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        //using bit-manipulation operator (^) to generate close to unique hash codes
        //here we are using the magic numbers 7 and 11 (along with random integer)
        return 7 * ((int)(Math.random()*1000)) ^ 11;
    }
}
