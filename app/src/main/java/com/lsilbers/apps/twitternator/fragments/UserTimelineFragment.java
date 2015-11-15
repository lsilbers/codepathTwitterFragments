package com.lsilbers.apps.twitternator.fragments;

import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lsilbers.apps.twitternator.adapters.TweetAdapter;
import com.lsilbers.apps.twitternator.models.Tweet;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lsilberstein on 11/14/15.
 */
public class UserTimelineFragment extends TweetsListFragment {
    private long oldestId;
    public static final String USER_KEY = "user";

    public static UserTimelineFragment newInstance(String user) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(USER_KEY, user);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    protected void getOlderTweets(final TweetAdapter aTweets, final ArrayList<Tweet> tweets) {
        String user = getArguments().getString(USER_KEY);
        client.getUserTimeline(new JsonHttpResponseHandler() {
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
        }, 10, null, oldestId - 1, user);
    }

    private void updateOldestId(Tweet tweet) {
        if (tweet.getTweetId() < oldestId) {
            oldestId = tweet.getTweetId();
        }
    }

    @Override
    protected void loadNewResults(final TweetAdapter aTweets, final ArrayList<Tweet> tweets, final SwipeRefreshLoadingListener swipeRefreshLoadingListener) {
        String user = getArguments().getString(USER_KEY);
        client.getUserTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "loaded new home timeline");
                tweets.clear();
                // delete the saved tweets
                Tweet.clearSavedTweets();
                tweets.addAll(Tweet.fromJSON(response));
                aTweets.notifyDataSetChanged();
                oldestId = tweets.get(tweets.size() - 1).getTweetId();
                if (swipeRefreshLoadingListener != null) {
                    swipeRefreshLoadingListener.hideLoadSymbol();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (swipeRefreshLoadingListener != null) {
                    swipeRefreshLoadingListener.hideLoadSymbol();
                }
                Log.d(TAG, "initial results error " + (errorResponse != null ? errorResponse.toString() : "status code " + statusCode));
            }
        }, null, null, null, user); // use defaults for initial request
    }

    @Override
    protected void loadInitialResults(TweetAdapter aTweets, ArrayList<Tweet> tweets) {
        loadNewResults(aTweets, tweets, null);
    }
}
