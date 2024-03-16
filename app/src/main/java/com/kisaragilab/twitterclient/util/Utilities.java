package com.kisaragilab.twitterclient.util;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.Constants;
import com.kisaragilab.twitterclient.model.Media;
import com.kisaragilab.twitterclient.model.Tweet;
import com.kisaragilab.twitterclient.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Utilities {

    public static Date strToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy", Locale.getDefault());
        try {
            return simpleDateFormat.parse(date);
        } catch(ParseException e) {
            return null;
        }
    }

    public static File getUserIconFile(@NonNull Context context) {
        File file = new File(context.getFilesDir(), Constants.USER_ICON_FILE_NAME);
        if(file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    public static void deleteUserIconFile(@NonNull Context context) {
        File file = new File(context.getFilesDir(), Constants.USER_ICON_FILE_NAME);
        if(file.exists()) {
            if(!file.delete()) {
                Toast.makeText(context, "Icon file deletion failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String formatCount(int count) {
        String countStr = String.valueOf(count);
        if(count > 999) {
            String left = countStr.substring(0, countStr.length() - 3);
            String right = countStr.substring(left.length(), left.length() + 1);
            return left + "." + right + "k";
        }
        return String.valueOf(count);
    }

    public static Tweet getRetweetStatus(JSONObject json) {
        try {
            return new Tweet(json.getJSONObject("retweeted_status"));
        } catch(Exception ex) {
            return null;
        }
    }

    public static Tweet getQuotedTweetStatus(JSONObject json) {
        try {
            return new Tweet(json.getJSONObject("quoted_status"));
        } catch(Exception ex) {
            return null;
        }
    }

    public static String formatTweetBody(String text) {
        int urlIndex = -1;
        if(text.contains("\n\nhttps://t.co")) {
            urlIndex = text.indexOf("\n\nhttps://t.co");
        } else if(text.contains("\nhttps://t.co")) {
            urlIndex = text.indexOf("\nhttps://t.co");
        } else if(text.contains("https://t.co")) {
            urlIndex = text.indexOf("https://t.co");
        }

        if(urlIndex == -1) {
            return text;
        } else {
            String urlPart = text.substring(urlIndex);
            return text.replace(urlPart, "");
        }
    }

    public static void loadUsersFromJson(HashMap<String, User> userMap, JSONArray array) throws JSONException {
        if(array == null) return;
        if(userMap == null) {
            userMap = new HashMap<>();
        }

        for(int i = 0; i < array.length(); i++) {
            User user = new User(array.getJSONObject(i));
            userMap.put(user.getId(), user);
        }
    }

    public static void loadMediaFromJson(HashMap<String, Media> mediaMap, JSONArray array) throws JSONException {
        if(array == null) return;
        if(mediaMap == null) {
            mediaMap = new HashMap<>();
        }

        for(int i = 0; i < array.length(); i++) {
            Media media = new Media(array.getJSONObject(i));
            mediaMap.put(media.getMediaKey(), media);
        }
    }

    public static void getGifTweetId(HashMap<String, String> gifUrlMap, JSONArray array) throws JSONException {
        if(gifUrlMap == null) {
            gifUrlMap = new HashMap<>();
        }
        if(array == null) {
            return;
        }

        for(int i = 0; i < array.length(); i++) {
            JSONObject main = array.optJSONObject(i);
            JSONObject entities = main.optJSONObject("extended_entities");

            JSONArray mediaArray = null;
            if(entities != null) {
                mediaArray = entities.getJSONArray("media");
            }

            if (mediaArray != null) {
                for(int j = 0; j < mediaArray.length(); j++) {
                    JSONObject mediaJson = mediaArray.getJSONObject(j);
                    if(mediaJson.optString("type").equals("animated_gif")) {
                        JSONObject videoInfo = mediaJson.optJSONObject("video_info");
                        if(videoInfo != null) {
                            JSONArray variants = videoInfo.optJSONArray("variants");
                            if(variants != null) {
                                JSONObject json = variants.optJSONObject(0);
                                if(json != null) {
                                    gifUrlMap.put(main.getString("id_str"), json.getString("url"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
