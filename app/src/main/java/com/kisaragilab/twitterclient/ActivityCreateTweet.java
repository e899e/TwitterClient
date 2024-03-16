package com.kisaragilab.twitterclient;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import com.kisaragilab.twitterclient.helper.CircleTransform;
import com.kisaragilab.twitterclient.task.TwitterPostTask;
import com.kisaragilab.twitterclient.util.Utilities;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActivityCreateTweet extends AppCompatActivity {

    private InputMethodManager inputMethodManager;

    private ConstraintLayout rootLayout;
    private ImageView userProfileImg;
    private EditText tweetBodyTxt;
    private TextView textLengthResponseTxt;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tweet);
        setTitle("Create Tweet");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        connectIds();

        File userIcon = Utilities.getUserIconFile(this);
        if(userIcon != null) {
            Picasso.with(this).load(Utilities.getUserIconFile(this)).transform(new CircleTransform()).into(userProfileImg);
        }

        tweetBodyTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = editable.length();
                if(textLength > 140) {
                    sendButton.getBackground().setTint(Color.GRAY);
                    sendButton.setAlpha(0.5f);
                } else {
                    sendButton.getBackground().setTint(getColor(R.color.colorMainButton));
                    sendButton.setAlpha(1f);
                }

                if(textLength > 130) {
                    textLengthResponseTxt.setVisibility(View.VISIBLE);
                    String text = String.valueOf(140 - textLength);
                    textLengthResponseTxt.setText(text);
                } else {
                    textLengthResponseTxt.setVisibility(View.INVISIBLE);
                }
            }
        });

        sendButton.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(rootLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            rootLayout.requestFocus();

            int textLength = tweetBodyTxt.length();
            if(textLength == 0) {
                Snackbar.make(rootLayout, getString(R.string.tweet_send_error_0), Snackbar.LENGTH_LONG).show();
            } else if(textLength > 140) {
                Snackbar.make(rootLayout, getString(R.string.tweet_send_error_140), Snackbar.LENGTH_LONG).show();
            } else {
                ExecutorService e = Executors.newSingleThreadExecutor();
                HashMap<String, String> reqParams = new HashMap<>();
                reqParams.put("status", tweetBodyTxt.getText().toString());
                TwitterPostTask task = new TwitterPostTask(this, "https://api.twitter.com/1.1/statuses/update.json", reqParams);
                Future<Exception> result = e.submit(task);

                try {
                    if(result.get() == null) {
                        finish();
                    } else {
                        Snackbar.make(rootLayout, getString(R.string.tweet_send_error), Snackbar.LENGTH_LONG).show();
                    }
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
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
        rootLayout = findViewById(R.id.root_layout);
        userProfileImg = findViewById(R.id.userIcon);
        tweetBodyTxt = findViewById(R.id.tweetBody);
        textLengthResponseTxt = findViewById(R.id.textLengthResponse);
        sendButton = findViewById(R.id.sendButton);
    }

}