package com.lsilbers.apps.twitternator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lsilbers.apps.twitternator.R;
import com.lsilbers.apps.twitternator.TwitterApplication;
import com.lsilbers.apps.twitternator.fragments.UserTimelineFragment;
import com.lsilbers.apps.twitternator.models.User;
import com.lsilbers.apps.twitternator.network.TwitterClient;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String USER = "user";
    private User user;
    private ImageView ivAvatar;
    private TextView tvFullName;
    private TextView tvScreenName;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private TextView tvTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = getIntent().getParcelableExtra(USER);
        if (user == null) {
            TwitterClient client = TwitterApplication.getTwitterClient();
            client.getUserAccount(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, response.toString());
                    user = User.fromJSON(response);
                    populateView();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "get user account " + (errorResponse != null ? errorResponse.toString() : "status code " + statusCode));
                }
            });
        } else {
            populateView();
        }

        if (savedInstanceState == null) {
            String screenName = null;
            if(user != null) {
                screenName = user.getScreenName();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flProfileTweets, UserTimelineFragment.newInstance(screenName))
                    .commit();
        }
    }

    private void populateView() {
        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        tvTagline = (TextView) findViewById(R.id.tvTagline);
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivAvatar);
        tvFullName.setText(user.getName());
        tvScreenName.setText(getResources().getString(R.string.at_symbol, user.getScreenName()));
        tvFollowers.setText(getResources().getString(R.string.followers,user.getFollowersCount()));
        tvFollowing.setText(getResources().getString(R.string.following,user.getFollowingsCount()));
        tvTagline.setText(user.getTagline());
    }
}
