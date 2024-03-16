package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

public class Mention {

    private final String start;
    private final String end;
    private final String tag;

    public Mention(@NonNull JSONObject json) throws JSONException {
        this.start = json.optString("start");
        this.end = json.optString("end");
        this.tag = json.optString("tag");
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getTag() {
        return tag;
    }

}
