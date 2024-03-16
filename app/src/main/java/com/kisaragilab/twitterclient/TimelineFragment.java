package com.kisaragilab.twitterclient;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.kisaragilab.twitterclient.adapter.TwitterAdapter;
import com.kisaragilab.twitterclient.customView.CustomImageView;
import com.kisaragilab.twitterclient.customView.TimelineHeader;
import com.kisaragilab.twitterclient.db.RealmDBHelper;
import com.kisaragilab.twitterclient.model.ReferencedTweet;
import com.kisaragilab.twitterclient.model.Tweet;
import com.kisaragilab.twitterclient.model.TweetV1;
import com.kisaragilab.twitterclient.model.TwitterList;
import com.kisaragilab.twitterclient.model.User;
import com.kisaragilab.twitterclient.realm.RealmTwitterList;
import com.kisaragilab.twitterclient.realm.RealmUser;
import com.kisaragilab.twitterclient.task.TwitterGetJSONArrayTask;
import com.kisaragilab.twitterclient.task.TwitterGetJSONObjectTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import io.realm.Realm;
import static com.kisaragilab.twitterclient.Constants.SEARCH_REQ;
import static com.kisaragilab.twitterclient.Constants.SEARCH_URL;

public class TimelineFragment extends Fragment implements TimelineHeader.TimelineHeaderDelegate {

    private final Context mContext;
    private final String mTitle;
    private final String mUrl;
    private final CustomImageView.MediaViewerDelegate mCallback;

    private TwitterList mTwitterList;
    private String mListId;
//    private boolean mIncludeReply;
//    private boolean mIncludeRetweet;

    protected TimelineHeader timelineHeader;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;
    private ArrayList<Tweet> mTweetList;
    private TwitterAdapter adapter;

    private String mTopTweetId;
    private String mBottomTweetId;

    private ExecutorService e;

    private boolean isLoadingTweets = false;

    public TimelineFragment(
            Context context,
            String title,
            String url,
            HashMap<String, String> idMap,
            CustomImageView.MediaViewerDelegate callback
    ) {
        this.mContext = context;
        this.mTitle = title;
        this.mUrl = url;
        this.mCallback = callback;
        this.mTweetList = new ArrayList<>();

        Set<String> idSet = idMap.keySet();
        mListId = null;
        if(idSet.contains("user_id")) {
            mListId = idMap.get("user_id");
        } else if(idSet.contains("list_id")) {
            mListId = idMap.get("list_id");
        }
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmTwitterList realmTwitterList = realm.where(RealmTwitterList.class).equalTo("id", mListId).findFirst();
            if(realmTwitterList != null) {
                mTwitterList = new TwitterList(realmTwitterList);
            }
        }

        init(mListId);
    }

    private void init(String id) {
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmTwitterList realmTwitterList = realm.where(RealmTwitterList.class).equalTo("id", id).findFirst();
            if(realmTwitterList != null) {
//                mIncludeReply = realmTwitterList.isIncludeReply();
//                mIncludeRetweet = realmTwitterList.isIncludeRetweet();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline_fragment, container, false);

        connectIds(view);

        setupSwipeUpListener();
        setupSwipeDownListener();

        timelineHeader.init(mContext, this, mTwitterList);

        e = Executors.newFixedThreadPool(1);
        TwitterGetJSONArrayTask task = new TwitterGetJSONArrayTask(mContext, mUrl);
        task.addReqParam("count", "100");
        task.addReqParam("truncated", "false");
        task.addReqParam("tweet_mode", "extended");
        if(mTitle.equals("Home")) {
            task.addReqParam("user_id", mListId);
        } else {
            task.addReqParam("list_id", mListId);
        }
        Future<JSONArray> result = e.submit(task);
        try {
            JSONArray array = result.get();
            if(result.isDone()) {
                if(array.length() > 0) {
                    JSONObject json = array.optJSONObject(0);
                    if(json != null) {
                        mTopTweetId = json.getString("id");
                    }

                    json = array.optJSONObject(array.length() - 1);
                    if(json != null) {
                        mBottomTweetId = json.getString("id");
                    }
                }

                mTweetList = loadTweetFromJson(array);

                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
                adapter = new TwitterAdapter(mContext, mTwitterList, mTweetList);
                adapter.setMediaViewerCallback(mCallback);
                recyclerView.setAdapter(adapter);
                recyclerView.setItemAnimator(null);
            }
        } catch(ExecutionException | InterruptedException | JSONException ex) {
            ex.fillInStackTrace();
        }

        return view;
    }

    private void connectIds(View view) {
        timelineHeader = view.findViewById(R.id.timelineHeader);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        recyclerView = view.findViewById(R.id.recycleView);
    }

    private ArrayList<Tweet> loadTweetFromJson(JSONArray array) {
        try {
            HashMap<String, TweetV1> v1Tweets = new HashMap<>();
            HashMap<String, TweetV1> v1Quotes = new HashMap<>();
            ArrayList<Tweet> tweets = new ArrayList<>();
            StringBuilder mainIds = new StringBuilder();
            StringBuilder quoteIds = new StringBuilder();
            for(int i = 0; i < array.length(); i++) {
                TweetV1 tweet = new Gson().fromJson(array.getJSONObject(i).toString(), TweetV1.class);

                if(!array.getJSONObject(i).optString("in_reply_to_status_id_str").equals("null")) continue;

                mainIds.append(tweet.getId()).append(",");

                if(tweet.isQuoteStatus()) {
                    quoteIds.append(tweet.getQuotedStatusId()).append(",");
                }

                v1Tweets.put(tweet.getId(), tweet);

                if(tweet.getRetweetedStatus() != null) {
                    v1Quotes.put(tweet.getRetweetedStatus().getId(), tweet.getRetweetedStatus());
                }

                if(tweet.getQuotedStatus() != null) {
                    v1Quotes.put(tweet.getQuotedStatus().getId(), tweet.getQuotedStatus());
                }
            }

            if(mainIds.length() == 0) return new ArrayList<>();

            TwitterGetJSONObjectTask task = new TwitterGetJSONObjectTask(mContext, SEARCH_URL);
            task.addReqParam(SEARCH_REQ.replace(":ids", mainIds.toString().substring(0, mainIds.length() - 1)));
            Future<JSONObject> v2Result = e.submit(task);
            JSONObject json = v2Result.get();

            JSONArray data = json.optJSONArray("data");
            HashMap<String, Tweet> mainTweetMap = null;
            if(data != null) {
                mainTweetMap = new HashMap<>();
                for(int i = 0; i < json.optJSONArray("data").length(); i++) {
                    Tweet tweet = new Gson().fromJson(json.optJSONArray("data").optJSONObject(i).toString(), Tweet.class);
                    mainTweetMap.put(tweet.getId(), tweet);
                }
            }

            JSONObject includes = json.optJSONObject("includes");

            JSONArray usersJson = includes.optJSONArray("users");
            HashMap<String, User> users = null;
            if(usersJson != null) {
                users = new HashMap<>();
                for(int i = 0; i < usersJson.length(); i++) {
                    User user = new Gson().fromJson(usersJson.optJSONObject(i).toString(), User.class);
                    users.put(user.getId(), user);
                }
            }

            JSONArray tweetsJson = includes.optJSONArray("tweets");
            HashMap<String, Tweet> subTweets = null;
            if(tweetsJson != null) {
                subTweets = new HashMap<>();
                for(int i = 0; i < tweetsJson.length(); i++) {
                    Tweet tweet = new Gson().fromJson(tweetsJson.optJSONObject(i).toString(), Tweet.class);
                    subTweets.put(tweet.getId(), tweet);
                }
            }

            if(subTweets != null) {
                for(Tweet tweet : subTweets.values()) {
                    if(users != null) {
                        tweet.setUser(users.get(tweet.getAuthorId()));
                    }

                    if(tweet.getAttachments() != null && tweet.getAttachments().getMediaKeys() != null) {
                        TweetV1 tweetV1 = v1Quotes.get(tweet.getId());
                        try{
                            tweet.setMedia(tweetV1.getExtendedEntities().getMedia());
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    TweetV1 tweetV1 = v1Quotes.get(tweet.getId());
                    tweet.updateDataWithV2(tweetV1);
                }
            }

            if(mainTweetMap != null) {
                for(Tweet tweet : mainTweetMap.values()) {
                    if(users != null) {
                        tweet.setUser(users.get(tweet.getAuthorId()));
                    }

                    if(tweet.getAttachments() != null && tweet.getAttachments().getMediaKeys() != null) {
                        TweetV1 tweetV1 = v1Tweets.get(tweet.getId());
                        tweet.setMedia(tweetV1.getExtendedEntities().getMedia());
                    }

                    if(tweet.getReferencedTweets() != null) {
                        for(ReferencedTweet referencedTweet : tweet.getReferencedTweets()) {
                            tweet.setReferencedTweet(subTweets.get(referencedTweet.getId()));
                        }
                    }

                    TweetV1 tweetV1 = v1Tweets.get(tweet.getId());
                    tweet.updateDataWithV2(tweetV1);

                    tweets.add(tweet);
                }
            }

            return tweets;
        } catch(InterruptedException | JSONException | ExecutionException ex) {
            ex.printStackTrace();
            Toast.makeText(mContext, "Hit the API call limit.", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void setupSwipeUpListener() {
        swipeContainer.setOnRefreshListener(() -> {
            ExecutorService e = Executors.newSingleThreadExecutor();
            TwitterGetJSONArrayTask task = new TwitterGetJSONArrayTask(mContext, mUrl);
            task.addReqParam("since_id", mTopTweetId);
            task.addReqParam("truncated", "false");
            task.addReqParam("tweet_mode", "extended");
            if(mTwitterList.getId().equals(RealmUser.getUserId())) {
                task.addReqParam("user_id", RealmUser.getUserId());
            } else {
                task.addReqParam("list_id", mTwitterList.getId());
            }
            Future<JSONArray> result = e.submit(task);
            try {
                JSONArray array = result.get();
                if(result.isDone()) {
                    if(array == null) return;

                    JSONObject json = array.optJSONObject(0);
                    if(json != null) {
                        String id = json.optString("id");
                        if(id != null) {
                            mTopTweetId = id;
                        }
                    }

                    ArrayList<Tweet> tmpTweets = loadTweetFromJson(array);
                    if(tmpTweets != null) {
                        adapter.addDataToTop(tmpTweets);
                    }
                }
            } catch(ExecutionException | InterruptedException ex) {
                ex.fillInStackTrace();
            }

//            updateData();

            Toast.makeText(mContext, "Refreshed.", Toast.LENGTH_LONG).show();

            swipeContainer.setRefreshing(false);
        });
    }

    public void setupSwipeDownListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                recyclerView.post(() -> {
                    if(!recyclerView.canScrollVertically(1)) {
                        if(isLoadingTweets) return;

                        isLoadingTweets = true;
                        ExecutorService e = Executors.newSingleThreadExecutor();
                        TwitterGetJSONArrayTask task = new TwitterGetJSONArrayTask(mContext, mUrl);
                        task.addReqParam("max_id", mBottomTweetId);
                        task.addReqParam("truncated", "false");
                        task.addReqParam("tweet_mode", "extended");
                        if(mTwitterList.getId().equals(RealmUser.getUserId())) {
                            task.addReqParam("user_id", RealmUser.getUserId());
                        } else {
                            task.addReqParam("list_id", mTwitterList.getId());
                        }
                        Future<JSONArray> result = e.submit(task);
                        try {
                            JSONArray array = result.get();
                            if(result.isDone()) {
                                if(array == null) return;

                                JSONObject json = array.optJSONObject(array.length() - 1);
                                if(json != null) {
                                    String id = json.optString("id");
                                    if(!id.isEmpty()) {
                                        mBottomTweetId = id;
                                    }
                                }

                                ArrayList<Tweet> tmpTweets = loadTweetFromJson(array);
                                if(tmpTweets != null && tmpTweets.size() > 0) {
                                    if(tmpTweets.size() != 1) {
                                        adapter.addDataToLast(tmpTweets);
                                    }
                                }
                            }
                        } catch(ExecutionException | InterruptedException ex) {
                            ex.fillInStackTrace();
                        }

                        isLoadingTweets = false;
                    }
                });
            }
        });
    }

    @Override
    public void onRetweetOptionChanged(boolean isChecked) {
        try(Realm realm = Realm.getInstance(RealmDBHelper.config)) {
            realm.executeTransaction(tmpRealm -> {
                RealmTwitterList realmTwitterList = tmpRealm.where(RealmTwitterList.class).equalTo("id", mListId).findFirst();
                if(realmTwitterList != null) {
                    realmTwitterList.setIncludeRetweet(isChecked);
                    tmpRealm.copyToRealmOrUpdate(realmTwitterList);
                }
            });

            if(adapter != null) {
                recyclerView.getLayoutManager().removeAllViews();
                updateData();
            } else {
                Toast.makeText(mContext, "Couldn't read data. It need to fetch tweets first.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMediaOptionsStateChanged(int selectedOption) {
        try(Realm realm = Realm.getInstance(RealmDBHelper.config)) {
            realm.executeTransaction(tmpRealm -> {
                RealmTwitterList realmTwitterList = tmpRealm.where(RealmTwitterList.class).equalTo("id", mListId).findFirst();
                if(realmTwitterList != null) {
                    realmTwitterList.setMediaOption(selectedOption);
                    tmpRealm.copyToRealmOrUpdate(realmTwitterList);
                }
            });

            if(adapter != null) {
                recyclerView.getLayoutManager().removeAllViews();
                updateData();
            } else {
                Toast.makeText(mContext, "Couldn't read data. It need to fetch tweets first.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateData() {
        adapter = new TwitterAdapter(mContext, mTwitterList, mTweetList);
        adapter.setMediaViewerCallback(mCallback);
        recyclerView.setAdapter(adapter);
    }

}
