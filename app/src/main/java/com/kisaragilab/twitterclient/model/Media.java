package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class Media {

    private final String media_key;
    private String type;
    private String media_url_https;
    private String url;
    private String expanded_url;
    private String display_url;
    private int width;
    private int height;
    private VideoInfo video_info;

    public Media(@NonNull JSONObject json) throws JSONException {
        this.media_key = json.optString("media_key");
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
        return media_key;
    }

    public String getType() {
        return type;
    }

    public String getMediaUrlHttps() {
        return media_url_https;
    }

    public void setMediaUrlHttps(String media_url_https) {
        this.media_url_https = media_url_https;
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public VideoInfo getVideoInfo() {
        return video_info;
    }

    public String getVideoUrl() {
        if(video_info == null) return null;
        video_info.getVariants().sort(new Comparator<Variants>() {
            @Override
            public int compare(Variants o1, Variants o2) {
                if(o1.getBitrate() < o2.getBitrate()) {
                    return 1;
                } else if(o1.getBitrate() > o2.getBitrate()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        return video_info.getVariants().get(0).getUrl();
    }

    public String getAspectRatio() {
        if(video_info == null) {
            if(width == 0 && height == 0) {
                return null;
            } else {
                return width + ":" + height;
            }
        }
        if(video_info.getAspectRatio() == null) return null;

        return video_info.getAspectRatio()[0] + ":" + video_info.getAspectRatio()[1];
    }

}
