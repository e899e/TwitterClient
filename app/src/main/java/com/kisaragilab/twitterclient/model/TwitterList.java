package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;

import com.kisaragilab.twitterclient.realm.RealmTwitterList;

import org.json.JSONException;

public class TwitterList {

    private String id;
    private String slug;
    private String listName;
    private User user;
    private int subscriberCount;
    private boolean includeReply;
    private boolean includeRetweet;
    private int mediaOption;
    private boolean isActive;

    public TwitterList(@NonNull RealmTwitterList twitterList) {
        this.id = twitterList.getId();
        this.slug = twitterList.getSlug();
        this.listName = twitterList.getListName();
        this.user = twitterList.getUser() == null ? null : new User(twitterList.getUser());
        this.subscriberCount = twitterList.getSubscriberCount();
        this.includeReply = twitterList.isIncludeReply();
        this.includeRetweet = twitterList.isIncludeRetweet();
        this.mediaOption = twitterList.getMediaOption();
        this.isActive = twitterList.isActive();
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

    public User getUser() {
        return user;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public boolean isIncludeReply() {
        return includeReply;
    }

    public boolean isIncludeRetweet() {
        return includeRetweet;
    }

    public int getMediaOption() {
        return mediaOption;
    }

    public boolean isActive() {
        return isActive;
    }

}
