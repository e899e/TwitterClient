package com.kisaragilab.twitterclient.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.kisaragilab.twitterclient.R;
import com.kisaragilab.twitterclient.model.TwitterList;

public class TimelineHeader extends LinearLayout {

    private TimelineHeaderDelegate mCallback;

    private TextView listTitleTextView;
    private ImageView arrowImageView;
    private LinearLayout retweetFrame;
    private CheckBox retweetCheckBox;
    private ConstraintLayout mediaOptionsFrame;
    private RadioGroup mediaOptionsRadioGroup;
    private RadioButton includeMediaRadioBtn;
    private RadioButton excludeMediaRadioBtn;
    private RadioButton mediaOnlyRadioBtn;

    private boolean mIsHeaderExpanded;

    public TimelineHeader(Context context) {
        super(context);
    }

    public TimelineHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimelineHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, TimelineHeaderDelegate callback, TwitterList twitterList) {
        mCallback = callback;

        View view = inflate(context, R.layout.timeline_header, this);
        connectIds(view);

        listTitleTextView.setText(twitterList.getListName());
        mIsHeaderExpanded = false;

        retweetCheckBox.setChecked(twitterList.isIncludeRetweet());

        retweetFrame.setVisibility(View.GONE);
        mediaOptionsFrame.setVisibility(View.GONE);

        switch(twitterList.getMediaOption()) {
            default:
            case 0:
                includeMediaRadioBtn.setChecked(true);
                break;

            case 1:
                excludeMediaRadioBtn.setChecked(true);
                break;

            case 2:
                mediaOnlyRadioBtn.setChecked(true);
                break;
        }

        retweetCheckBox.setOnClickListener(this::onRetweetClicked);
        retweetFrame.setOnClickListener(this::onRetweetClicked);
        includeMediaRadioBtn.setOnClickListener(this::onRadioBtnClicked);
        excludeMediaRadioBtn.setOnClickListener(this::onRadioBtnClicked);
        mediaOnlyRadioBtn.setOnClickListener(this::onRadioBtnClicked);
    }

    private void connectIds(View view) {
        listTitleTextView = view.findViewById(R.id.listTitleTextView);
        arrowImageView = view.findViewById(R.id.arrowImageView);
        retweetFrame = view.findViewById(R.id.retweetFrame);
        retweetCheckBox = view.findViewById(R.id.retweetCheckBox);
        mediaOptionsFrame = view.findViewById(R.id.mediaOptionsFrame);
        mediaOptionsRadioGroup = view.findViewById(R.id.radioGroup);
        includeMediaRadioBtn = view.findViewById(R.id.includeMediaRadioBtn);
        excludeMediaRadioBtn = view.findViewById(R.id.excludeMediaRadioBtn);
        mediaOnlyRadioBtn = view.findViewById(R.id.mediaOnlyRadioBtn);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        final int btnWidth = arrowImageView.getWidth();
        final int btnHeight = arrowImageView.getHeight();

        arrowImageView.setOnClickListener(view -> {
            if(mIsHeaderExpanded) {
                retweetFrame.setVisibility(View.GONE);
                mediaOptionsFrame.setVisibility(View.GONE);

                startRotateAnim(180, 0, btnWidth / 2, btnHeight / 2);
            } else {
                retweetFrame.setVisibility(View.VISIBLE);
                mediaOptionsFrame.setVisibility(View.VISIBLE);

                startRotateAnim(0, 180, btnWidth / 2, btnHeight / 2);
            }
            mIsHeaderExpanded = !mIsHeaderExpanded;
        });
    }

    private void startRotateAnim(int fromDegrees, int toDegrees, int pivotX, int pivotY) {
        final int DURATION = 200;
        
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        rotate.setDuration(DURATION);
        rotate.setFillAfter(true);
        arrowImageView.startAnimation(rotate);
    }

    private void onRetweetClicked(View view) {
        boolean flg = retweetCheckBox.isChecked();
        mCallback.onRetweetOptionChanged(flg);
        retweetCheckBox.setChecked(flg);
    }

    @SuppressLint("NonConstantResourceId")
    private void onRadioBtnClicked(View view) {
        int selectedOptionId = mediaOptionsRadioGroup.getCheckedRadioButtonId();
        switch(selectedOptionId) {
            case R.id.includeMediaRadioBtn:
                mCallback.onMediaOptionsStateChanged(0);
                break;

            case R.id.excludeMediaRadioBtn:
                mCallback.onMediaOptionsStateChanged(1);
                break;

            case R.id.mediaOnlyRadioBtn:
                mCallback.onMediaOptionsStateChanged(2);
                break;

        }
    }

    public interface TimelineHeaderDelegate {
        void onRetweetOptionChanged(boolean isChecked);
        void onMediaOptionsStateChanged(int selectedOption);
    }

}
