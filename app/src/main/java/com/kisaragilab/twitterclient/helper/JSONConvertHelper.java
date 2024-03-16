package com.kisaragilab.twitterclient.helper;

import com.google.gson.Gson;
import com.kisaragilab.twitterclient.model.Tweet;
import org.json.JSONObject;

public class JSONConvertHelper {

    public static Tweet convertTweetJson(JSONObject json) {
        String jsonString = json.toString();
        jsonString = jsonString.replaceAll("author_id", "authorId");
        jsonString = jsonString.replaceAll("conversation_id", "conversationId");
        jsonString = jsonString.replaceAll("conversation_id", "conversationId");
        jsonString = jsonString.replaceAll("created_at", "createdAt");
        jsonString = jsonString.replaceAll("coordinates", "coordinates");
        jsonString = jsonString.replaceAll("place_id", "placeId");
        jsonString = jsonString.replaceAll("in_reply_to_user_id", "inReplyToUserId");
        jsonString = jsonString.replaceAll("public_metrics", "publicMetrics");
        jsonString = jsonString.replaceAll("public_metrics", "publicMetrics");
        jsonString = jsonString.replaceAll("possibly_sensitive", "possiblySensitive");
        jsonString = jsonString.replaceAll("reply_settings", "replySettings");
        jsonString = jsonString.replaceAll("expanded_url", "expandedUrl");
        jsonString = jsonString.replaceAll("display_url", "displayUrl");
        jsonString = jsonString.replaceAll("unwound_url", "unwoundUrl");
        jsonString = jsonString.replaceAll("media_keys", "mediaKeys");

        return new Gson().fromJson(jsonString, Tweet.class);
    }

}
