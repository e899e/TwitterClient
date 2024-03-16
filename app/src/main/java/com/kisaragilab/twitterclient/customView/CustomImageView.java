package com.kisaragilab.twitterclient.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.kisaragilab.twitterclient.R;
import com.kisaragilab.twitterclient.model.Media;
import com.kisaragilab.twitterclient.model.Tweet;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CustomImageView extends LinearLayout {

    private ImageView image01;
    private ImageView playImageView;
    private ImageView image02;
    private ImageView image03;
    private ImageView image04;

    private final Context mContext;
    private CustomImageView.MediaViewerDelegate mCallback;

    public CustomImageView(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void init(CustomImageView.MediaViewerDelegate callback, Tweet tweet) {
        View view = inflate(mContext, R.layout.custom_image_view, this);

        connectIds(view);

        this.mCallback = callback;

        initImageViews();

        view.post(() -> {
            final int width = view.getMeasuredWidth();

            if(width == 0) return;

            List<Media> mediaCollection = tweet.getMedia();
            if(mediaCollection != null) {
                try {
                    String type = mediaCollection.get(0).getType();
                    if(type.equals("video") || type.equals("animated_gif")) {
                        loadVideo(mediaCollection.get(0), width);
                    } else {
                        loadPhotos(mediaCollection, width);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void connectIds(View view) {
        image01 = view.findViewById(R.id.image01);
        playImageView = view.findViewById(R.id.play_image);
        image02 = view.findViewById(R.id.image02);
        image03 = view.findViewById(R.id.image03);
        image04 = view.findViewById(R.id.image04);
    }

    private void initImageViews() {
        image01.setImageDrawable(null);
        image02.setImageDrawable(null);
        image03.setImageDrawable(null);
        image04.setImageDrawable(null);

        playImageView.setVisibility(View.GONE);
    }

    private void loadPhotos(List<Media> mediaCollection, int width) {
        for(Media media : mediaCollection) {
            ImageView imageView;
            int imageWidth = (width - 250)/2;
            int imageHeight = (9 * (width - 250) / 16)/2;
            switch(mediaCollection.indexOf(media)) {
                case 0:
                default:
                    imageView = image01;
                    break;

                case 1:
                    imageView = image02;
                    break;

                case 2:
                    imageView = image03;
                    break;

                case 3:
                    imageView = image04;
                    break;

            }

            try {
                Picasso.with(mContext)
                        .load(media.getMediaUrlHttps())
                        .resize(imageWidth, imageHeight)
                        .centerCrop()
                        .into(imageView);

                imageView.setOnClickListener(view -> mCallback.OnImageClicked(mediaCollection, mediaCollection.indexOf(media)));
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadVideo(Media media, int width) {
        ImageView imageView = image01;
        ImageView playButton = playImageView;
        playButton.setVisibility(View.VISIBLE);

        int imageWidth = (width - 250);
        int imageHeight = (9 * (width - 250) / 16);

        String mediaUrl = media.getMediaUrlHttps();
        Picasso.with(mContext)
            .load(mediaUrl)
            .resize(imageWidth, imageHeight)
            .centerCrop()
            .into(imageView);

        imageView.setOnClickListener(view -> {
            mCallback.OnVideoClicked(media.getVideoUrl(), media.getAspectRatio());
        });
    }

    public interface MediaViewerDelegate {
        void OnImageClicked(List<Media> mediaCollection, int position);
        void OnVideoClicked(String url, String ratio);
    }

}
