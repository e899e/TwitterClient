package com.kisaragilab.twitterclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kisaragilab.twitterclient.R;
import com.kisaragilab.twitterclient.customView.CustomImageView;
import com.kisaragilab.twitterclient.customView.TweetCellView;
import com.kisaragilab.twitterclient.model.Metrics;
import com.kisaragilab.twitterclient.model.Tweet;
import com.kisaragilab.twitterclient.model.TwitterList;
import com.kisaragilab.twitterclient.realm.RealmTwitterList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import io.realm.Realm;

public class TwitterAdapter extends RecyclerView.Adapter<TwitterAdapter.TweetViewHolder> {

    private final Context mContext;
    private TwitterList twitterList;
    private List<Tweet> allTweets;
    private List<Tweet> viewableTweets;
    private CustomImageView.MediaViewerDelegate mCallback;
    private boolean mIsIncludeRetweet;
    private int mMediaOptions;

    public TwitterAdapter(@NonNull Context context, TwitterList twitterList, ArrayList<Tweet> data) {
        this.mContext = context;
        this.twitterList = twitterList;
        this.allTweets = new ArrayList<>(data);
        this.viewableTweets = new ArrayList<>();
        setHasStableIds(true);

        if(twitterList != null) {
            try(Realm realm = Realm.getDefaultInstance()) {
                RealmTwitterList realmTwitterList = realm.where(RealmTwitterList.class).equalTo("id", twitterList.getId()).findFirst();
                if(realmTwitterList != null) {
                    mIsIncludeRetweet = realmTwitterList.isIncludeRetweet();
                    mMediaOptions = realmTwitterList.getMediaOption();
                }
            }
        } else {
            mIsIncludeRetweet = true;
            mMediaOptions = 0;
        }

        updateUI();
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_cell_for_adapter, parent,false);
        updateUI();
        return new TweetViewHolder(view);
    }

    public void setMediaViewerCallback(CustomImageView.MediaViewerDelegate callback) {
        this.mCallback = callback;
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        Tweet tweet = viewableTweets.get(position);

        if(holder.container.getChildAt(0) != null) {
            holder.container.removeAllViews();
        }

        TweetCellView view = new TweetCellView(mContext);
        view.init(tweet, R.layout.tweet_cell, false);
        holder.container.addView(view);
    }

    @Override
    public int getItemCount() {
        return viewableTweets.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addSingleToLast(Tweet data) {
        allTweets.add(data);
        allTweets.sort(new Comparator<Tweet>() {
            @Override
            public int compare(Tweet o1, Tweet o2) {
                Metrics o1Metrics = o1.getPublicMetrics();
                Metrics o2Metrics = o2.getPublicMetrics();
                if(o1Metrics.getLikeCount() < o2Metrics.getLikeCount()) {
                    return 1;
                } else if(o1Metrics.getLikeCount() > o2Metrics.getLikeCount()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        updateUI();
        notifyDataSetChanged();
    }

    public void addDataToTop(ArrayList<Tweet> data) {
        if(allTweets != null) {
            allTweets.addAll(0, data);
            updateUI();
            notifyDataSetChanged();
        }
    }

    public void addDataToLast(ArrayList<Tweet> data) {
        if(allTweets != null && allTweets.size() > 0) {
            allTweets.remove(allTweets.size() - 1);
        }
        allTweets.addAll(data);
        updateUI();
        notifyDataSetChanged();
    }

    public void updateUI() {
        allTweets.sort(new Comparator<Tweet>() {
            @Override
            public int compare(Tweet o1, Tweet o2) {
                if(o1.getCreatedAt().before(o2.getCreatedAt())) {
                    return -1;
                } else if(!o1.getCreatedAt().before(o2.getCreatedAt())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        ArrayList<Tweet> rtList;
        if(mIsIncludeRetweet) {
            rtList = new ArrayList<>(allTweets);
        } else {
            rtList = new ArrayList<>();
            for(Tweet tweet : allTweets) {
                if(!tweet.isRT() && !tweet.isQuoted()) {
                    rtList.add(tweet);
                }
            }
        }

        switch(mMediaOptions) {
            case 0:
                viewableTweets = new ArrayList<>(rtList);
                break;

            case 1:
                viewableTweets = new ArrayList<>();
                for(Tweet tweet : rtList) {
                    if(tweet.getMedia() == null) {
                        viewableTweets.add(tweet);
                    }
                }
                break;

            case 2:
                viewableTweets = new ArrayList<>();
                for(Tweet tweet : rtList) {
                    if(tweet.getMedia() == null) continue;
                    viewableTweets.add(tweet);
                }
                break;
        }

        viewableTweets.sort(new Comparator<Tweet>() {
            @Override
            public int compare(Tweet o1, Tweet o2) {
                if(o1.getCreatedAt().before(o2.getCreatedAt())) {
                    return 1;
                } else if(o1.getCreatedAt().after(o2.getCreatedAt())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    protected static class TweetViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout container;
//        public TweetCellView tweetCellView;

        public TweetViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.container);
//            tweetCellView = view.findViewById(R.id.tweetCellView);
        }

    }

}
