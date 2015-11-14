package com.lsilbers.apps.twitternator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lsilbers.apps.twitternator.R;
import com.lsilbers.apps.twitternator.TwitterApplication;
import com.lsilbers.apps.twitternator.adapters.TweetAdapter;
import com.lsilbers.apps.twitternator.models.Tweet;
import com.lsilbers.apps.twitternator.network.TwitterClient;
import com.lsilbers.apps.twitternator.utils.EndlessRecyclerOnScrollListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeTimelineActivity extends AppCompatActivity {

    private static final String TAG = "HT";
    private TwitterClient client;
    private TweetAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;
    private SwipeRefreshLayout srTimeline;
    private long oldestId;
    private long newestTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tweets = new ArrayList<>();
        aTweets = new TweetAdapter(tweets);
        aTweets.setItemClickListener(new TweetAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Tweet result = tweets.get(position);
                Intent intent = new Intent(HomeTimelineActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.TWEET_TAG, result);
                startActivity(intent);
            }
        });
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        srTimeline = (SwipeRefreshLayout) findViewById(R.id.srTimeline);

        setupRecyclerView();
        srTimeline.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retriveInitialResults();
            }
        });

        loadSavedTweets();
        client = TwitterApplication.getTwitterClient();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // new activity to create a tweet
                Intent intent = new Intent(HomeTimelineActivity.this, TweetCompositionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadSavedTweets(){
        Log.d(TAG, "Loading saved tweets");
        tweets.addAll(Tweet.getAll());
        aTweets.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(aTweets);
        rvTweets.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                getOlderTweets();
            }
        });
    }

    private void getOlderTweets() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "got home timeline for older tweets");
                int count = aTweets.getItemCount();
                ArrayList<Tweet> newTweets = Tweet.fromJSON(response);
                for (Tweet tweet : newTweets) {
                    tweets.add(tweet);
                    aTweets.notifyItemInserted(count);
                    count++;
                    updateOldestId(tweet);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "old tweets error " + (errorResponse != null ? errorResponse.toString() : "status code " + statusCode));
            }
        }, 10, null, oldestId - 1);
    }

    @Override
    protected void onResume() {
        //getNewerTweets();
        retriveInitialResults();
        super.onResume();
    }

    // at the moment we choose not to use this method - I think it is a little trick to work out how
    // to decide how many results we want etc so we just
    private void getNewerTweets() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "Got home timeline for newer tweets");
                int index = 0;
                ArrayList<Tweet> newTweets = Tweet.fromJSON(response);
                for (Tweet tweet : newTweets) {
                    tweets.add(index, tweet);
                    index++;
                    updateNewestId(tweet);
                }
                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "new tweets error " + (errorResponse != null ? errorResponse.toString() : "status code " + statusCode));
            }
        }, 50, newestTweet, null);
    }

    private void updateNewestId(Tweet tweet) {
        if (tweet.getTweetId() > newestTweet) {
            newestTweet = tweet.getTweetId();
        }
    }

    private void updateOldestId(Tweet tweet) {
        if (tweet.getTweetId() < oldestId) {
            oldestId = tweet.getTweetId();
        }
    }


    // gets the basic set of results and notifies the adapter
    private void retriveInitialResults() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "loaded initial home timeline");
                tweets.clear();
                // delete the saved tweets
                Tweet.clearSavedTweets();
                tweets.addAll(Tweet.fromJSON(response));
                aTweets.notifyDataSetChanged();
                newestTweet = tweets.get(0).getTweetId();
                oldestId = tweets.get(tweets.size()-1).getTweetId();
                if(srTimeline != null) {
                    srTimeline.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(srTimeline != null) {
                    srTimeline.setRefreshing(false);
                }
                Log.d(TAG, "initial results error " + (errorResponse != null ? errorResponse.toString() : "status code " + statusCode));
            }
        }, null, null, null); // use defaults for initial request
    }
}
