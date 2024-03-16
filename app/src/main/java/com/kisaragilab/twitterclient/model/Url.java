package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Url {

    private final String start;
    private final String end;
    private final String url;
    private final String expanded_url;
    private final String display_url;
    private ArrayList<Image> images;
    private final String status;
    private final String title;
    private final String description;
    private final String unwoundUrl;

    public Url(@NonNull JSONObject json) throws JSONException {
        this.start = json.optString("start");
        this.end = json.optString("end");
        this.url = json.optString("url");
        this.expanded_url = json.optString("expanded_url");
        this.display_url = json.optString("display_url");

        JSONArray images = json.optJSONArray("images");
        if(images != null) {
            this.images = new ArrayList<>();
            for(int i = 0; i < images.length(); i++) {
                this.images.add(new Image(images.getJSONObject(i)));
            }
        }

        this.status = json.optString("status");
        this.title = json.optString("title");
        this.description = json.optString("description");
        this.unwoundUrl = json.optString("unwound_url");
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getUrl() {
        return url;
    }

    public String getExpandedUrl() {
        return expanded_url;
    }

    public String getDisplayUrl() {
        return display_url;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUnwoundUrl() {
        return unwoundUrl;
    }

}
