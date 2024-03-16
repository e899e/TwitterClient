package com.kisaragilab.twitterclient.customView;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.kisaragilab.twitterclient.R;
import com.kisaragilab.twitterclient.helper.CircleTransform;
import com.kisaragilab.twitterclient.model.Metrics;
import com.kisaragilab.twitterclient.model.Tweet;
import com.kisaragilab.twitterclient.util.Utilities;
import com.squareup.picasso.Picasso;

public class CustomRetweetView extends ConstraintLayout {

    private final Context context;
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
    private CustomImageView customImageView;

    public CustomRetweetView(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomRetweetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomRetweetView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(CustomImageView.MediaViewerDelegate callback, Tweet tweet) {
        View view = inflate(context, R.layout.custom_retweet_view, this);

        connectIds(view);

        String url = tweet.getUser().getProfileImageUrl();
        if(url.contains("_normal")) {
            url = url.replace("_normal", "");
        }
        Picasso.with(context).load(url).transform(new CircleTransform()).into(userProfileImage);
        userId.setText(tweet.getUser().getName());

        String username = "@" + tweet.getUser().getScreen_name();
        screenName.setText(username);
        tweetBody.setText(Utilities.formatTweetBody(tweet.getText()));

        container.post(() -> {
            if(tweet.getMedia() == null) return;
            customImageView = new CustomImageView(context);
            customImageView.init(callback, tweet);
            tweetBodyLayout.addView(customImageView);
        });

        Metrics metrics = tweet.getPublicMetrics();
        if(metrics.getRetweetCount() > 0) {
            retweetCounter.setText(Utilities.formatCount(metrics.getRetweetCount()));
        } else {
            retweetCounter.setText(null);
        }
        if(metrics.getReplyCount() > 0) {
            replyCounter.setText(Utilities.formatCount(metrics.getReplyCount()));
        } else {
            replyCounter.setText(null);
        }
        if(metrics.getLikeCount() > 0) {
            favoriteCounter.setText(Utilities.formatCount(metrics.getLikeCount()));
        } else {
            favoriteCounter.setText(null);
        }

        ImageView likeImage = favoriteImage;
        if(tweet.isLiked()) {
            likeImage.setColorFilter(context.getColor(R.color.colorFavorite), PorterDuff.Mode.SRC_IN);
        } else {
            likeImage.clearColorFilter();
        }
    }

    private void connectIds(View view) {
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

}
