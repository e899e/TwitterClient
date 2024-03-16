package com.kisaragilab.twitterclient.realm;

import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.model.Media;
import org.json.JSONException;
import org.json.JSONObject;
import io.realm.annotations.PrimaryKey;

public class RealmMedia {

    @PrimaryKey private String mediaKey;
                private String type;
                private String url;
                private String videoUrl;
                private int width;
                private int height;

    public RealmMedia(@NonNull Media media) {
        this.mediaKey = media.getMediaKey();
        this.type = media.getType();
        this.url = media.getUrl();
        this.videoUrl = media.getUrl();
//        this.videoUrl = media.getVideoUrl();
        this.width = media.getWidth();
        this.height = media.getHeight();
    }

    public RealmMedia(@NonNull JSONObject json) throws JSONException {
        this.mediaKey = json.optString("media_key");
        this.type = json.optString("type");
        if(this.type.equals("photo")) {
            this.url = json.optString("url");
        } else {
            this.url = json.optString("preview_image_url");
        }
        this.width = json.optInt("width");
        this.height = json.optInt("height");
    }

    public String getMediaKey() {
        return mediaKey;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
