package com.lsilbers.apps.twitternator.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsilbers.apps.twitternator.R;
import com.lsilbers.apps.twitternator.TwitterApplication;
import com.lsilbers.apps.twitternator.adapters.TweetAdapter;
import com.lsilbers.apps.twitternator.models.Tweet;
import com.lsilbers.apps.twitternator.network.TwitterClient;
import com.lsilbers.apps.twitternator.utils.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class TweetsListFragment extends Fragment {
    public final String TAG = getClass().getSimpleName();
    protected TwitterClient client;
    private TweetAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;
    private SwipeRefreshLayout srTimeline;

    public TweetsListFragment() {
        // Required empty public constructor
    }

    public interface SwipeRefreshLoadingListener {
        void hideLoadSymbol();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        srTimeline = (SwipeRefreshLayout) v.findViewById(R.id.srTimeline);

        setupRecyclerView();
        srTimeline.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNewResults(aTweets, tweets, new SwipeRefreshLoadingListener() {
                    @Override
                    public void hideLoadSymbol() {
                        if (srTimeline != null) {
                            srTimeline.setRefreshing(false);
                        }
                    }
                });
            }
        });

        loadInitialResults(aTweets, tweets);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweets = new TweetAdapter(tweets);

        //loadSavedTweets();
        client = TwitterApplication.getTwitterClient();
    }

    // this requires further thought
    private void loadSavedTweets(){
        Log.d(TAG, "Loading saved tweets");
        tweets.addAll(Tweet.getAll());
        aTweets.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(aTweets);
        rvTweets.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                getOlderTweets(aTweets, tweets);
            }
        });
    }

    /**
     * Called when the endless scroll listener informs us that more results are required
     * @param aTweets the adapter for the tweet list
     * @param tweets the data set
     */
    protected abstract void getOlderTweets(TweetAdapter aTweets, ArrayList<Tweet> tweets);

    /**
     * Called to retrieve a new set of results
     * @param swipeRefreshLoadingListener used to hide the loading symbol once the results have been retrieved
     * @param aTweets the adapter for the tweet list
     * @param tweets the data set
     */
    protected abstract void loadNewResults(TweetAdapter aTweets, ArrayList<Tweet> tweets, SwipeRefreshLoadingListener swipeRefreshLoadingListener);

    /**
     * Called to load the initial set of results
     * @param aTweets the adapter for the tweet list
     * @param tweets the data set
     */
    protected abstract void loadInitialResults(TweetAdapter aTweets, ArrayList<Tweet> tweets);
}
