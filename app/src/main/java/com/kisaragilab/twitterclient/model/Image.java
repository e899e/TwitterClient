package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

public class Image {

    private String url;
    private String width;
    private String height;

    public Image(@NonNull JSONObject json) throws JSONException {
        this.url = json.optString("url");
        this.width = json.optString("width");
        this.height = json.optString("height");
    }

    public String getUrl() {
        return url;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

}
