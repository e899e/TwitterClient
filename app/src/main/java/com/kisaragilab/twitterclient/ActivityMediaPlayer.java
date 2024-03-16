package com.kisaragilab.twitterclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class ActivityMediaPlayer extends Activity {

    public static String RATIO = "RATIO";
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        Intent intent = getIntent();
        connectIds();
        setupMedia(intent.getStringExtra("RATIO"));
    }

    private void connectIds() {
        videoView = findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(this);

        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(mediaPlayer -> videoView.start());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupMedia(String ratio) {
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.dimensionRatio = ratio;
        lp.bottomToBottom = ConstraintSet.PARENT_ID;
        lp.endToEnd = ConstraintSet.PARENT_ID;
        lp.startToStart = ConstraintSet.PARENT_ID;
        lp.topToTop = ConstraintSet.PARENT_ID;

        videoView.setLayoutParams(lp);
        videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("URL")));
    }

}
