package com.lsilbers.apps.twitternator.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "aJ2tlNrIKwpqRuwD9xvBd29iN";       // Change this
	public static final String REST_CONSUMER_SECRET = "nLTxNOqZ7O7Nqql3nqC4pkzGmmO9NNbBSsOYD1SF7vp1lMCu9b"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://lsilberstwit"; // Change this (here and in manifest)
	private static final Integer DEFAULT_COUNT = 25;
    private static final String TAG = "TC";

    public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/**
	 * Retrieves the home timeline
	 * @param handler the response handler (should take a jsonArray and convert to tweets)
	 * @param count the number of results to return (defaults to 25)
	 * @param sinceId the id of the youngest tweet you wish to return (defaults to 1)
	 * @param maxId the id of the oldest tweet you wish to return
	 */
	public void getHomeTimeline(AsyncHttpResponseHandler handler, @Nullable Integer count, @Nullable Long sinceId, @Nullable Long maxId) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		if (count == null) {
			count = DEFAULT_COUNT;
		}
		params.put("count", count);
		if (sinceId == null) {
			sinceId = 1l;
		}
		params.put("since_id", sinceId);
		// only use max id if specified
		if (maxId != null) {
			params.put("max_id", maxId);
		}
		client.get(apiUrl, params, handler);
        Log.d(TAG, "GET " + apiUrl + "?" + params.toString());
	}

    /**
     * Gets the details for the currently logged in user
     * @param handler for the response - should expect a json object
     */
    public void getUserAccount(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("skip_status", true);
        client.get(apiUrl, params, handler);
        Log.d(TAG, "GET " + apiUrl + "?" + params.toString());
    }

    /**
     * Posts a new tweet
     * @param handler handles the response
     * @param tweet the status to tweet
     */
    public void postStatus(AsyncHttpResponseHandler handler, String tweet) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        client.post(apiUrl, params, handler);
        Log.d(TAG, "POST " + apiUrl + "?" + params.toString());
    }
}