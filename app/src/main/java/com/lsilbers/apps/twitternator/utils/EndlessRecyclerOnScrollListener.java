package com.lsilbers.apps.twitternator.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * code adapted from https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int lastVisibleItem;
    private int totalItemCount;
    int[] lastVisibleItemPositions;

    private LinearLayoutManager layoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        totalItemCount = layoutManager.getItemCount();

        // last visible item on the screen is the highest of the values in the spans
        lastVisibleItem = layoutManager.findLastVisibleItemPosition();

        Log.d(TAG, "total count = " + totalItemCount + ", lastVisible = " + lastVisibleItem);

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        // if the last item on screen is the last item in the list we need to load more
        if (!loading && totalItemCount
                <= (lastVisibleItem + 5)) {
            // End has been reached
            Log.d(TAG, "load more!");
            onLoadMore();
            loading = true;
        }
    }

    // resets the listener for the next query
    public void reset(){
        lastVisibleItem = 0;
        totalItemCount = 0;
        previousTotal = 0;
        lastVisibleItemPositions = null;
    }

    public abstract void onLoadMore();
}