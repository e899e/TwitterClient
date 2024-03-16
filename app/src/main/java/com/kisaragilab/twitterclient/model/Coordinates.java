package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Coordinates {

    private final double[] coordinates;
    private final String type;

    public Coordinates(@NonNull JSONObject json) throws JSONException {
        JSONArray coordinates = json.optJSONArray("coordinates");
        this.coordinates = new double[2];
        this.coordinates[0] = coordinates == null ? 0 : coordinates.getDouble(0);
        this.coordinates[1] = coordinates == null ? 0 : coordinates.getDouble(1);
        this.type = json.optString("type");
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }

}
