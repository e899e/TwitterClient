package com.kisaragilab.twitterclient.model;

public class TweetV1 {

    private String id_str;
    private String text;
    private String created_at;
    private Entity entities;
    private ExtendedEntity extended_entities;
    private String place_id;
    private String in_reply_to_status_id;
    private String in_reply_to_user_id;
    private String in_reply_to_screen_name;
    private String quoted_status_id;
    private TweetV1 retweeted_status;
    private TweetV1 quoted_status;
    private String lang;
    private boolean is_quote_status;
    private boolean favorited;
    private boolean retweeted;

    public String getId() {
        return id_str;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public Entity getEntities() {
        return entities;
    }

    public ExtendedEntity getExtendedEntities() {
        return extended_entities;
    }

    public String getPlaceId() {
        return place_id;
    }

    public String getInReplyToStatusId() {
        return in_reply_to_status_id;
    }

    public String getInReplyToUserId() {
        return in_reply_to_user_id;
    }

    public String getInReplyToScreenName() {
        return in_reply_to_screen_name;
    }

    public String getQuotedStatusId() {
        return quoted_status_id;
    }

    public TweetV1 getRetweetedStatus() {
        return retweeted_status;
    }

    public TweetV1 getQuotedStatus() {
        return quoted_status;
    }

    public String getLang() {
        return lang;
    }

    public boolean isQuoteStatus() {
        return is_quote_status;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

}
