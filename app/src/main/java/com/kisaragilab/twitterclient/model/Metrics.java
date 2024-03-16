package com.kisaragilab.twitterclient.model;

public class Metrics {

    private int retweet_count;
    private int reply_count;
    private int like_count;
    private int quote_count;

    public int getRetweetCount() {
        return retweet_count;
    }

    public int getReplyCount() {
        return reply_count;
    }

    public int getLikeCount() {
        return like_count;
    }

    public int getQuoteCount() {
        return quote_count;
    }

    public void setLikeCount(int count) {
        like_count = count;
    }

}
