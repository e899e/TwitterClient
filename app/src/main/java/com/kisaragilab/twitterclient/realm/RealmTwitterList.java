package com.kisaragilab.twitterclient.realm;

import androidx.annotation.NonNull;

import com.kisaragilab.twitterclient.model.TwitterList;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmTwitterList extends RealmObject {

    @PrimaryKey private String id;
                private String slug;
                private String listName;
                private RealmUser user;
                private int subscriberCount;
                private boolean includeReply;
                private boolean includeRetweet;
                private int mediaOption;
                private boolean isActive;

    public RealmTwitterList() {}

    public RealmTwitterList(String id, String name, boolean isIncludeReply, boolean isIncludeRetweet, boolean isMediaOnly, int mediaOption, boolean isActive) {
        this.id = id;
        this.listName = name;
        this.includeReply = isIncludeReply;
        this.includeRetweet = isIncludeRetweet;
        this.mediaOption = mediaOption;
        this.isActive = isActive;
    }

    public RealmTwitterList(@NonNull TwitterList twitterList) {
        this.id = twitterList.getId();
        this.slug = twitterList.getSlug();
        this.listName = twitterList.getListName();
        this.user = new RealmUser(twitterList.getUser());
        this.subscriberCount = twitterList.getSubscriberCount();
        this.includeReply = twitterList.isIncludeReply();
        this.includeRetweet = twitterList.isIncludeRetweet();
        this.mediaOption = twitterList.getMediaOption();
        this.isActive = twitterList.isActive();
    }

    public RealmTwitterList(@NonNull JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.slug = json.getString("slug");
        this.listName = json.getString("name");
        this.user = new RealmUser(json.getJSONObject("user"));
        this.subscriberCount = json.getInt("subscriber_count");
    }

    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getListName() {
        return listName;
    }

    public RealmUser getUser() {
        return user;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public boolean isIncludeReply() {
        return includeReply;
    }

    public void setIncludeReply(boolean includeReply) {
        this.includeReply = includeReply;
    }

    public boolean isIncludeRetweet() {
        return includeRetweet;
    }

    public void setIncludeRetweet(boolean includeRetweet) {
        this.includeRetweet = includeRetweet;
    }

    public int getMediaOption() {
        return mediaOption;
    }

    public void setMediaOption(int mediaOption) {
        this.mediaOption = mediaOption;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
