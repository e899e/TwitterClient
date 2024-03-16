package com.kisaragilab.twitterclient.customView;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import com.kisaragilab.twitterclient.ActivityImagePager;
import com.kisaragilab.twitterclient.ActivityMediaPlayer;
import com.kisaragilab.twitterclient.ActivityTweetDetails;
import com.kisaragilab.twitterclient.R;
import com.kisaragilab.twitterclient.helper.CircleTransform;
import com.kisaragilab.twitterclient.model.Media;
import com.kisaragilab.twitterclient.model.Metrics;
import com.kisaragilab.twitterclient.model.Tweet;
import com.kisaragilab.twitterclient.task.TwitterPostTask;
import com.kisaragilab.twitterclient.util.Utilities;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static com.kisaragilab.twitterclient.ActivityMediaPlayer.RATIO;
import static com.kisaragilab.twitterclient.ActivityTweetDetails.TWEET_ID_TAG;

public class TweetCellView extends ConstraintLayout implements CustomImageView.MediaViewerDelegate {

    private Context context;

    private ConstraintLayout retweetTagContainer;
    private TextView retweetUsernameTextView;
    private ConstraintLayout container;
    private ImageView userProfileImage;
//    private LinearLayout tweetContainer;
    private TextView screenName;
    private TextView userId;
    private LinearLayout tweetBodyLayout;
    private TextView tweetBody;
    private TextView retweetCounter;
    private TextView replyCounter;
    private TextView favoriteCounter;
//    private ImageView replyImage;
//    private ImageView retweetImage;
    private ImageView favoriteImage;
//    private ImageView othersImage;

    public TweetCellView(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public TweetCellView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public TweetCellView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(Tweet tweet, @LayoutRes int resource, boolean isDetails) {
        View view = inflate(context, resource, this);
        connectIds(view);

        boolean applyOriginalData = tweet.isRT() && !tweet.isQuoted();

        container.setOnClickListener(tmpView -> {
            if(!isDetails) {
                Intent intent = new Intent(context, ActivityTweetDetails.class);
                if(applyOriginalData) {
                    intent.putExtra(TWEET_ID_TAG, new Gson().toJson(tweet.getReferencedTweet()));
                } else {
                    intent.putExtra(TWEET_ID_TAG, new Gson().toJson(tweet));
                }
                context.startActivity(intent);
            }
        });

        if(tweet.isRT()) {
            retweetTagContainer.setVisibility(View.VISIBLE);
            retweetUsernameTextView.setText(tweet.getUser().getName());
        } else {
            retweetTagContainer.setVisibility(View.GONE);
        }

        String url = applyOriginalData ? tweet.getReferencedTweet().getUser().getProfileImageUrl() : tweet.getUser().getProfileImageUrl();;
        if(!url.isEmpty()) {
            Picasso.with(context).load(url).transform(new CircleTransform()).into(userProfileImage);
            userId.setText(applyOriginalData ? tweet.getReferencedTweet().getUser().getName() : tweet.getUser().getName());
        }

        String username = "@" + (applyOriginalData ? tweet.getReferencedTweet().getUser().getScreen_name() : tweet.getUser().getScreen_name());
        screenName.setText(username);
        tweetBody.setText(Utilities.formatTweetBody(applyOriginalData ? tweet.getReferencedTweet().getText() : tweet.getText()));

        View childView = tweetBodyLayout.getChildAt(1);
        if(childView != null) {
            tweetBodyLayout.removeView(childView);
        }

        if(tweet.isQuoted()) {
            CustomRetweetView customRetweetView = new CustomRetweetView(context);
            customRetweetView.init(this, tweet.getReferencedTweet());
            tweetBodyLayout.addView(customRetweetView);
        } else {
            if (tweet.getMedia() != null) {
                CustomImageView mediaViewer = new CustomImageView(context);
                mediaViewer.init(this, tweet);
                this.tweetBodyLayout.addView(mediaViewer);
            }
        }

        Metrics metrics;
        if(applyOriginalData) {
            metrics = tweet.getReferencedTweet().getPublicMetrics();
        } else {
            metrics = tweet.getPublicMetrics();
        }

        if (metrics.getRetweetCount() > 0) {
            retweetCounter.setText(Utilities.formatCount(metrics.getRetweetCount()));
        } else {
            retweetCounter.setText(null);
        }
        if (metrics.getReplyCount() > 0) {
            replyCounter.setText(Utilities.formatCount(metrics.getReplyCount()));
        } else {
            replyCounter.setText(null);
        }
        if (metrics.getLikeCount() > 0) {
            favoriteCounter.setText(Utilities.formatCount(metrics.getLikeCount()));
        } else {
            favoriteCounter.setText(null);
        }

        ImageView likeImage = favoriteImage;
        if (tweet.isLiked()) {
            likeImage.setColorFilter(context.getColor(R.color.colorFavorite), PorterDuff.Mode.SRC_IN);
        } else {
            likeImage.clearColorFilter();
        }
        likeImage.setOnClickListener(v -> {
            if (!tweet.isLiked()) {
                ExecutorService e = Executors.newSingleThreadExecutor();
                HashMap<String, String> reqParams = new HashMap<>();
                reqParams.put("id", tweet.getId());
                TwitterPostTask task = new TwitterPostTask(context, "https://api.twitter.com/1.1/favorites/create.json", reqParams);
                Future<Exception> result = e.submit(task);
                try {
                    if (result.get() == null) {
                        if (result.isDone()) {
                            tweet.setLiked(true);
                            metrics.setLikeCount(metrics.getLikeCount() + 1);
                            likeImage.setColorFilter(context.getColor(R.color.colorFavorite), PorterDuff.Mode.SRC_IN);
                        }
                    }
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else {
                ExecutorService e = Executors.newSingleThreadExecutor();
                HashMap<String, String> reqParams = new HashMap<>();
                reqParams.put("id", tweet.getId());
                TwitterPostTask task = new TwitterPostTask(context, "https://api.twitter.com/1.1/favorites/destroy.json", reqParams);
                Future<Exception> result = e.submit(task);
                try {
                    if (result.get() == null) {
                        if (result.isDone()) {
                            tweet.setLiked(false);
                            metrics.setLikeCount(metrics.getLikeCount() - 1);
                            likeImage.clearColorFilter();
                        }
                    }
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void connectIds(View view) {
        retweetTagContainer = view.findViewById(R.id.RetweetTagContainer);
        retweetUsernameTextView = view.findViewById(R.id.retweetUsernameTextView);
        container = view.findViewById(R.id.container);
        userProfileImage = view.findViewById(R.id.userProfileImage);
//        tweetContainer = view.findViewById(R.id.tweet_container);
        screenName = view.findViewById(R.id.screenName);
        userId = view.findViewById(R.id.userId);
        tweetBodyLayout = view.findViewById(R.id.tweetBodyLayout);
        tweetBody = view.findViewById(R.id.tweetBody);
//        retweetImage = view.findViewById(R.id.retweetImage);
        retweetCounter = view.findViewById(R.id.retweetCounter);
//        replyImage = view.findViewById(R.id.replyImage);
        replyCounter = view.findViewById(R.id.replyCounter);
        favoriteImage = view.findViewById(R.id.favoriteImage);
        favoriteCounter = view.findViewById(R.id.favoriteCounter);
//        othersImage = view.findViewById(R.id.othersImage);
    }

    @Override
    public void OnImageClicked(List<Media> mediaCollection, int position) {
        ArrayList<String> urls = new ArrayList<>();
        for(Media media : mediaCollection) {
            urls.add(media.getMediaUrlHttps());
        }

        context.startActivity(new Intent(context, ActivityImagePager.class).putStringArrayListExtra("URLS", urls).putExtra("POS", position));
    }

    @Override
    public void OnVideoClicked(String url, String ratio) {
        context.startActivity(new Intent(context, ActivityMediaPlayer.class).putExtra("URL", url).putExtra(RATIO, ratio));
    }

}
