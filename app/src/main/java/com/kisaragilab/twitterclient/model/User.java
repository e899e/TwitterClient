package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.realm.RealmUser;
import org.json.JSONObject;

public class User {

    private String id;
    private String name;
    private String username;
    private String description;
    private String profile_image_url;

    public User(@NonNull RealmUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getName();
        this.description = user.getDescription();
        this.profile_image_url = user.getUrl();
    }

    public User(@NonNull JSONObject json) {
        this.id = json.optString("id");
        this.name = json.optString("name");
        this.username = json.optString("username");
        this.description = json.optString("description");
        this.profile_image_url = json.optString("profile_image_url");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreen_name() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public String getProfileImageUrl() {
        return profile_image_url;
    }

}
