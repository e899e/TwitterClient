package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Video {

    private String contentType;
    private String url;

    public Video(@NonNull JSONObject json) throws JSONException {

        this.contentType = json.getString("content_type");
        this.url = json.getString("url");
    }

    public String getContentType() {
        return contentType;
    }

    public String getUrl() {
        return url;
    }

}
