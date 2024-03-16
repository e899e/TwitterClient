package com.kisaragilab.twitterclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.R;

public class SharedPreferenceManager {

    private final SharedPreferences sharedPreferences;
    private final String userIdKey;
    private final String userAccessTokenKey;
    private final String userAccessTokenSecretKey;

    public SharedPreferenceManager(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
        userIdKey = context.getString(R.string.twitter_user_id);
        userAccessTokenKey = context.getString(R.string.twitter_user_access_token);
        userAccessTokenSecretKey = context.getString(R.string.twitter_user_access_token_secret);
    }

    public void saveUserId(long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(userIdKey, value);
        editor.apply();
    }

    public long loadUserId() {
        return sharedPreferences.getLong(userIdKey, 0);
    }

    public void saveUserAccessToken(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userAccessTokenKey, value);
        editor.apply();
    }

    public String loadUserAccessToken() {
        return sharedPreferences.getString(userAccessTokenKey, null);
    }

    public void saveUserAccessTokenSecret(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userAccessTokenSecretKey, value);
        editor.apply();
    }

    public String loadUserAccessTokenSecret() {
        return sharedPreferences.getString(userAccessTokenSecretKey, null);
    }

    public void clean() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(userIdKey);
        editor.remove(userAccessTokenKey);
        editor.remove(userAccessTokenSecretKey);
        editor.apply();
    }

}
