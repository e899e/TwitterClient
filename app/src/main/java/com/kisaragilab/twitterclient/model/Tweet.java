package com.kisaragilab.twitterclient.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kisaragilab.twitterclient.util.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;

public class Tweet {

    private final String id;
    private String text;
    private String author_id;
    private String conversation_id;
    // TODO: ContextAnnotation
    private Date created_at;
    private Entity entity;
    private ArrayList<Media> media;
    private Coordinates coordinates;
    private String place_id;
    private String in_reply_to_user_id;
    private String lang;
    private Metrics public_metrics;
    private boolean possibly_sensitive;
    private ArrayList<ReferencedTweet> referenced_tweets;
    private String referencedTweetId;
    private String referencedTweetType;
    private Tweet referencedTweet;
    private String quotedTweetId;
    private String reply_settings;
    private boolean isRetweeted;
    private boolean isQuoted;
    private String source;
    private User user;

    private Attachments attachments;
    private String tmpVideoUrl;

    private boolean retweeted;
    private boolean liked;

    public Tweet(String id) {
        this.id = id;
    }

    public Tweet(@NonNull JSONObject json) throws JSONException {
        this.id = json.optString("id");
        this.quotedTweetId = json.optString("quoted_status_id_str");
        this.isRetweeted = json.optString("in_reply_to_status_id_str") != null;
        this.isQuoted = json.optBoolean("is_quote_status");
        this.retweeted = json.optBoolean("retweeted");
        this.liked = json.optBoolean("favorited");

        JSONObject entities = json.optJSONObject("extended_entities");
        if(entities == null) return;

        JSONArray media = entities.optJSONArray("media");
        if(media == null) return;

        JSONObject videoInfo = media.optJSONObject(0).optJSONObject("video_info");
        if(videoInfo == null) return;

        JSONArray variants = videoInfo.optJSONArray("variants");
        if(variants != null) {
            int bitrate = 0;
            for(int i = 0; i < variants.length(); i++) {
                JSONObject variant = variants.getJSONObject(i);
                if(variant.optInt("bitrate") > bitrate) {
                    bitrate = variant.optInt("bitrate");
                    this.tmpVideoUrl = variant.optString("url");
                }
            }
        }
    }

    public void updateDataWithV2(@Nullable TweetV1 tweetV1) {
        if(tweetV1 == null) return;
        this.retweeted = tweetV1.isRetweeted();
        this.liked = tweetV1.isFavorited();
    }

    public Attachments getAttachments() {
        return attachments;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthorId() {
        return author_id;
    }

    public Metrics getPublicMetrics() {
        return public_metrics;
    }

    public String getConversationId() {
        return conversation_id;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public Entity getEntity() {
        return entity;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getPlaceId() {
        return place_id;
    }

    public String getInReplyToUserId() {
        return in_reply_to_user_id;
    }

    public String getLang() {
        return lang;
    }

    public ArrayList<ReferencedTweet> getReferencedTweets() {
        return referenced_tweets;
    }

    public boolean isPossiblySensitive() {
        return possibly_sensitive;
    }

    public String getReferencedTweetId() {
        return referencedTweetId;
    }

    public String getReferencedTweetType() {
        return referencedTweetType;
    }

    public Tweet getReferencedTweet() {
        return referencedTweet;
    }

    public void setReferencedTweet(Tweet referencedTweet) {
        this.referencedTweet = referencedTweet;
    }

    public String getQuotedTweetId() {
        return quotedTweetId;
    }

    public boolean isRT() {
        if(referenced_tweets == null) {
            return false;
        }

        for(ReferencedTweet referencedTweet : referenced_tweets) {
            if(referencedTweet.getType().equals("retweeted")) {
                return true;
            }
        }

        return false;
    }

    public boolean isQuoted() {
        if(referenced_tweets == null) {
            return false;
        }

        for(ReferencedTweet referencedTweet : referenced_tweets) {
            if(referencedTweet.getType().equals("quoted")) {
                return true;
            }
        }

        return false;
    }

    public String getReplySettings() {
        return reply_settings;
    }

    public String getSource() {
        return source;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Media> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }

    public String getTmpVideoUrl() {
        return tmpVideoUrl;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

}
