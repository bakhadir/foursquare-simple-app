package com.bakhadir.locationapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class  Category {

    private String id;
    private String name;
    private String pluralName;
    private String shortName;
    private Icon icon;
    private boolean primary;

//    private String[] parents; TODO

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

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /*public String[] getParents() {
        return parents;
    }

    public void setParents(String[] parents) {
        this.parents = parents;
    }*/

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public static Category fromJSONObject(JSONObject categoryJsonObj) {

        Category category = new Category();

        try {
            category.setId(categoryJsonObj.getString("id"));
        }
        catch (JSONException je) {
            category.setId("");
        }

        try {
            category.setName(categoryJsonObj.getString("name"));
        }
        catch (JSONException je) {
            category.setName("");
        }

        try {
            category.setPluralName(categoryJsonObj.getString("pluralName"));
        }
        catch (JSONException je) {
            category.setPluralName("");
        }

        try {
            category.setShortName(categoryJsonObj.getString("shortName"));
        }
        catch (JSONException je) {
            category.setShortName("");
        }

        try {
            category.setIcon(Icon.fromJSONObject(categoryJsonObj.getJSONObject("icon")));
        } catch (JSONException je) {
            category.setIcon(null);
        }

        try {
            category.setPrimary(categoryJsonObj.getBoolean("primary"));
        }
        catch (JSONException je) {
            category.setPrimary(false);
        }

        return category;
    }

}
