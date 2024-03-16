package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Entity {

    private ArrayList<Cashtag> cashtags;
    private ArrayList<Hashtag> hashtags;
    private ArrayList<Mention> mentions;
    private ArrayList<Url> urls;

    public Entity(@NonNull JSONObject json) throws JSONException {
        JSONArray cashtagArray = json.optJSONArray("cashtags");
        if(cashtagArray != null) {
            cashtags = new ArrayList<>();
            for(int i = 0; i < cashtagArray.length(); i++) {
                cashtags.add(new Cashtag(cashtagArray.getJSONObject(i)));
            }
        }

        JSONArray hashtagArray = json.optJSONArray("hashtags");
        if(hashtagArray != null) {
            hashtags = new ArrayList<>();
            for(int i = 0; i < hashtagArray.length(); i++) {
                hashtags.add(new Hashtag(hashtagArray.getJSONObject(i)));
            }
        }

        JSONArray mentionArray = json.optJSONArray("hashtags");
        if(mentionArray != null) {
            mentions = new ArrayList<>();
            for(int i = 0; i < mentionArray.length(); i++) {
                mentions.add(new Mention(mentionArray.getJSONObject(i)));
            }
        }

        JSONArray urlArray = json.optJSONArray("urls");
        if(urlArray != null) {
            urls = new ArrayList<>();
            for(int i = 0; i < urlArray.length(); i++) {
                urls.add(new Url(urlArray.getJSONObject(i)));
            }
        }
    }

    public ArrayList<Cashtag> getCashtags() {
        return cashtags;
    }

    public ArrayList<Hashtag> getHashtags() {
        return hashtags;
    }

    public ArrayList<Mention> getMentions() {
        return mentions;
    }

    public ArrayList<Url> getUrls() {
        return urls;
    }

}
