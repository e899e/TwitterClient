package com.kisaragilab.twitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.kisaragilab.twitterclient.adapter.TwitterAdapter;
import com.kisaragilab.twitterclient.customView.CustomImageView;
import com.kisaragilab.twitterclient.customView.TweetCellView;
import com.kisaragilab.twitterclient.model.Image;
import com.kisaragilab.twitterclient.model.Media;
import com.kisaragilab.twitterclient.model.Metrics;
import com.kisaragilab.twitterclient.model.ReferencedTweet;
import com.kisaragilab.twitterclient.model.Tweet;
import com.kisaragilab.twitterclient.model.TweetV1;
import com.kisaragilab.twitterclient.model.User;
import com.kisaragilab.twitterclient.model.Video;
import com.kisaragilab.twitterclient.task.TwitterGetJSONArrayTask;
import com.kisaragilab.twitterclient.task.TwitterGetJSONObjectTask;
import com.kisaragilab.twitterclient.util.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static com.kisaragilab.twitterclient.ActivityMediaPlayer.RATIO;
import static com.kisaragilab.twitterclient.Constants.CONVERSATION_REQ;
import static com.kisaragilab.twitterclient.Constants.CONVERSATION_URL;
import static com.kisaragilab.twitterclient.Constants.SEARCH_REQ;
import static com.kisaragilab.twitterclient.Constants.SEARCH_URL;
import static com.kisaragilab.twitterclient.Constants.SEARCH_URL_V1;

public class ActivityTweetDetails extends AppCompatActivity implements CustomImageView.MediaViewerDelegate {

    public static final String TWEET_ID_TAG = "com.kisaragilab.twitterclient.ActivityTweetDetails.TWEET_ID_TAG";

    private Tweet tweet;
    private ArrayList<Tweet> allTweets = new ArrayList<>();
    private ArrayList<Tweet> firstLayerTweets = new ArrayList<>();
    private TweetCellView tweetCellView;
    private RecyclerView recyclerView;
    private TwitterAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        setTitle("Tweet");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        connectIds();
        handleIntent();

        tweetCellView.init(tweet, R.layout.tweet_cell_for_details, true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new TwitterAdapter(this, null, firstLayerTweets);
        adapter.setMediaViewerCallback(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        ExecutorService s = Executors.newSingleThreadExecutor();
        s.execute(() -> {
            progressBar.setVisibility(View.VISIBLE);
            fetchReplies();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectIds() {
        tweetCellView = findViewById(R.id.tweetCellView);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if(intent != null) {
            tweet = new Gson().fromJson(intent.getStringExtra(TWEET_ID_TAG), Tweet.class);
        }
    }

    private void fetchReplies() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        TwitterGetJSONObjectTask task = new TwitterGetJSONObjectTask(this, CONVERSATION_URL);
        task.addReqParam(CONVERSATION_REQ.replace(":id", tweet.getConversationId()));
        Future<JSONObject> result = service.submit(task);
        try {
            JSONObject json = result.get();

            JSONArray data = json.optJSONArray("data");
            HashMap<String, Tweet> mainTweetMap = null;
            if(data == null) {
                return;
            }

            mainTweetMap = new HashMap<>();
            for(int i = 0; i < json.optJSONArray("data").length(); i++) {
                Tweet tweet = new Gson().fromJson(json.optJSONArray("data").optJSONObject(i).toString(), Tweet.class);
                mainTweetMap.put(tweet.getId(), tweet);
            }

            JSONObject includes = json.optJSONObject("includes");

            HashMap<String, User> users = null;
            HashMap<String, Media> mediaMap = null;
            HashMap<String, Tweet> subTweetMap = null;
            if(includes != null) {
                JSONArray usersJson = includes.optJSONArray("users");
                if(usersJson != null) {
                    users = new HashMap<>();
                    for(int i = 0; i < usersJson.length(); i++) {
                        User user = new Gson().fromJson(usersJson.optJSONObject(i).toString(), User.class);
                        users.put(user.getId(), user);
                    }
                }

                JSONArray mediaJson = includes.optJSONArray("media");
                if(mediaJson != null) {
                    mediaMap = new HashMap<>();
                    for(int i = 0; i < mediaJson.length(); i++) {
                        Media media = new Gson().fromJson(mediaJson.optJSONObject(i).toString(), Media.class);
                        mediaMap.put(media.getMediaKey(), media);
                    }
                }

                JSONArray tweetsJson = includes.optJSONArray("tweets");
                if(tweetsJson != null) {
                    subTweetMap = new HashMap<>();
                    for(int i = 0; i < tweetsJson.length(); i++) {
                        Tweet tweet = new Gson().fromJson(tweetsJson.optJSONObject(i).toString(), Tweet.class);
                        subTweetMap.put(tweet.getId(), tweet);
                    }
                }
            }

            StringBuilder ids = new StringBuilder();
            for(Tweet tweet : mainTweetMap.values()) {
                ids.append(tweet.getId());
                ids.append(",");
            }
            for(Tweet tweet : subTweetMap.values()) {
                ids.append(tweet.getId());
                ids.append(",");
            }
            TwitterGetJSONArrayTask arrayTask = new TwitterGetJSONArrayTask(this, SEARCH_URL_V1);
            arrayTask.addReqParam("id", ids.toString().substring(0, ids.length() - 1));
            task.addReqParam("count", "100");
            task.addReqParam("tweet_mode", "extended");
            Future<JSONArray> arrayResult = service.submit(arrayTask);
            JSONArray v1Result = arrayResult.get();

            HashMap<String, TweetV1> v1Quotes = new HashMap<>();
            for(int i = 0; i < v1Result.length(); i++) {
                TweetV1 tweet = new Gson().fromJson(v1Result.getJSONObject(i).toString(), TweetV1.class);
                v1Quotes.put(tweet.getId(), tweet);
            }

            for(Tweet tweet : subTweetMap.values()) {
                if(users != null) {
                    tweet.setUser(users.get(tweet.getAuthorId()));
                }

                if(tweet.getAttachments() != null && tweet.getAttachments().getMediaKeys() != null) {
                    TweetV1 tweetV1 = v1Quotes.get(tweet.getId());
                    tweet.setMedia(tweetV1.getExtendedEntities().getMedia());
                }

                TweetV1 tweetV1 = v1Quotes.get(tweet.getId());
                tweet.updateDataWithV2(tweetV1);
            }

            if(mainTweetMap != null) {
                for(Tweet tweet : mainTweetMap.values()) {
                    if(users != null) {
                        tweet.setUser(users.get(tweet.getAuthorId()));
                    }

                    if(tweet.getAttachments() != null && tweet.getAttachments().getMediaKeys() != null) {
                        ArrayList<Media> media = new ArrayList<>();
                        for(String key : tweet.getAttachments().getMediaKeys()) {
                            Media tmpMedia = mediaMap.get(key);
                            tmpMedia.setMediaUrlHttps(tmpMedia.getUrl());
                            media.add(tmpMedia);
                        }
                        tweet.setMedia(media);
                    }

                    allTweets.add(tweet);
                }
            }

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
            });
            for(Tweet tweet : allTweets) {
                if(tweet.getReferencedTweets() != null) {
                    for(ReferencedTweet referencedTweet : tweet.getReferencedTweets()) {
                        if(referencedTweet.getId().equals(this.tweet.getId())) {
                            runOnUiThread(() -> {
                                adapter.addSingleToLast(tweet);
                            });
                        }
                    }
                }
            }
        } catch(ExecutionException | InterruptedException | JSONException ex) {
            ex.fillInStackTrace();
        }
    }

    @Override
    public void OnImageClicked(List<Media> mediaCollection, int position) {
        ArrayList<String> urls = new ArrayList<>();
        for(Media media : mediaCollection) {
            urls.add(media.getUrl());
        }

        startActivity(new Intent(this, ActivityImagePager.class).putStringArrayListExtra("URLS", urls).putExtra("POS", position));
    }

    @Override
    public void OnVideoClicked(String url, String ratio) {
        startActivity(new Intent(this, ActivityMediaPlayer.class).putExtra("URL", url).putExtra(RATIO, ratio));
    }

}
