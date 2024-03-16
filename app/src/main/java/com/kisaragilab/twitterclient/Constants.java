package com.kisaragilab.twitterclient;

public class Constants {
    public static final String GET_USER             = "https://api.twitter.com/2/users/:id?user.fields=profile_image_url";
    public static final String USER_ICON_FILE_NAME  = "user_icon.jpg";
    public static final String USER_ICON_URL        = "https://api.twitter.com/1.1/users/show.json";
    public static final String LIST_GET             = "https://api.twitter.com/1.1/lists/list.json";
    public static final String TIMELINE_URL         = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    public static final String TIMELINE_URL_V2      = "https://api.twitter.com/2/users/:id/tweets?expansions=author_id,referenced_tweets.id,geo.place_id&user.fields=id,name,username,profile_image_url,entities&tweet.fields=author_id,conversation_id,created_at,entities,geo,id,in_reply_to_user_id,lang,public_metrics,possibly_sensitive,referenced_tweets,source,text&place.fields=id,geo,place_type&max_results=100";
    public static final String TIMELINE_URL_V2_REQ  = "expansions=author_id,referenced_tweets.id,geo.place_id&user.fields=id,name,username,profile_image_url,entities&tweet.fields=author_id,conversation_id,created_at,entities,geo,id,in_reply_to_user_id,lang,public_metrics,possibly_sensitive,referenced_tweets,source,text&place.fields=id,geo,place_type&max_results=100";
    public static final String LIST_TIMELINE_URL    = "https://api.twitter.com/1.1/lists/statuses.json";
    public static final String CONVERSATION_URL     = "https://api.twitter.com/2/tweets/search/recent";
    public static final String CONVERSATION_REQ     = "query=conversation_id::id&expansions=author_id,referenced_tweets.id,in_reply_to_user_id,attachments.media_keys,attachments.poll_ids,geo.place_id,entities.mentions.username,referenced_tweets.id.author_id&media.fields=media_key,type,duration_ms,width,height,preview_image_url,url,public_metrics&poll.fields=id,options,duration_minutes,end_datetime,voting_status&tweet.fields=id,text,attachments,author_id,context_annotations,conversation_id,created_at,entities,geo,in_reply_to_user_id,lang,possibly_sensitive,public_metrics,referenced_tweets,reply_settings,source,withheld&user.fields=id,name,username,profile_image_url,protected&max_results=100";
    public static final String SEARCH_URL_V1        = "https://api.twitter.com/1.1/statuses/lookup.json";
    public static final String SEARCH_URL           = "https://api.twitter.com/2/tweets";
    public static final String SEARCH_REQ           = "ids=:ids&expansions=author_id,referenced_tweets.id,in_reply_to_user_id,attachments.media_keys,attachments.poll_ids,geo.place_id,entities.mentions.username,referenced_tweets.id.author_id&media.fields=media_key,type,duration_ms,width,height,preview_image_url,url,public_metrics&poll.fields=id,options,duration_minutes,end_datetime,voting_status&tweet.fields=id,text,attachments,author_id,context_annotations,conversation_id,created_at,entities,geo,in_reply_to_user_id,lang,possibly_sensitive,public_metrics,referenced_tweets,reply_settings,source,withheld&user.fields=id,name,username,profile_image_url,protected";
}
