package com.kisaragilab.twitterclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.kisaragilab.twitterclient.db.RealmDBHelper;
import com.kisaragilab.twitterclient.realm.RealmUser;
import com.kisaragilab.twitterclient.task.ImageDownloadTask;
import com.kisaragilab.twitterclient.task.TwitterGetJSONObjectTask;
import com.kisaragilab.twitterclient.util.SharedPreferenceManager;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import io.realm.Realm;
import static com.kisaragilab.twitterclient.Constants.USER_ICON_URL;

public class ActivityLogin extends Activity {

    private TwitterAuthClient authClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTwitterClients();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authClient.onActivityResult(requestCode, resultCode, data);
    }

    private void initTwitterClients() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.api_key),
                getString(R.string.api_key_secret)
        );

        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);

        authClient = new TwitterAuthClient();
        authClient.authorize(
            this,
            new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

                    try(Realm realm = Realm.getInstance(RealmDBHelper.config)) {
                        realm.executeTransaction(tmpRealm -> tmpRealm.copyToRealmOrUpdate(new RealmUser(String.valueOf(session.getUserId()))));
                    }

                    SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(ActivityLogin.this);
                    sharedPreferenceManager.saveUserId(session.getUserId());
                    sharedPreferenceManager.saveUserAccessToken(session.getAuthToken().token);
                    sharedPreferenceManager.saveUserAccessTokenSecret(session.getAuthToken().secret);

                    getUserIcon(session);

                    Launcher.launchHome(ActivityLogin.this);
                    finish();
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(getApplicationContext(), "Login fail", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        );
    }

    private void getUserIcon(TwitterSession session) {
        String userProfileUrl = "";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        TwitterGetJSONObjectTask task = new TwitterGetJSONObjectTask(this, USER_ICON_URL);
        task.addReqParam("user_id", String.valueOf(session.getId()));
        Future<JSONObject> result = executorService.submit(task);
        try {
            JSONObject json = result.get();
            userProfileUrl = json.optString("profile_image_url");
        } catch(ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        }

        if(userProfileUrl != null && !userProfileUrl.isEmpty()) {
            executorService = Executors.newSingleThreadExecutor();
            ImageDownloadTask imageDownloadTask = new ImageDownloadTask(userProfileUrl);
            Future<Bitmap> resultIcon = executorService.submit(imageDownloadTask);
            try {
                Bitmap icon = resultIcon.get();
                File f = new File(getFilesDir(), Constants.USER_ICON_FILE_NAME);

                if(!f.exists()) {
                    try {
                        FileOutputStream outputStream = openFileOutput(Constants.USER_ICON_FILE_NAME, MODE_PRIVATE);
                        icon.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch(ExecutionException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

}
