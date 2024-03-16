package com.kisaragilab.twitterclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.kisaragilab.twitterclient.db.RealmDBHelper;
import com.kisaragilab.twitterclient.model.Test;
import com.kisaragilab.twitterclient.model.Tweet;
import com.kisaragilab.twitterclient.model.TweetV1;
import com.kisaragilab.twitterclient.util.SharedPreferenceManager;

public class Launcher extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmDBHelper.initRealm(this);

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        long userId = sharedPreferenceManager.loadUserId();
        if(userId == 0) {
            Intent intent = new Intent(this, ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0,  0);
        } else {
            launchHome(this);
        }

    }

    public static void launchHome(@NonNull Context context) {
        Intent intent = new Intent(context, ActivityHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
