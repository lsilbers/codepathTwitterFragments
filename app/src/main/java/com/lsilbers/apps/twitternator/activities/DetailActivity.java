package com.lsilbers.apps.twitternator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lsilbers.apps.twitternator.R;
import com.lsilbers.apps.twitternator.models.Tweet;

public class DetailActivity extends AppCompatActivity {
    public static final String TWEET_TAG = "tweet";
    private static final String TAG = "DA";
    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tweet = getIntent().getParcelableExtra(TWEET_TAG);

        Log.d(TAG, tweet.toString());
    }
}
