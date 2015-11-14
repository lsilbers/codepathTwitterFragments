package com.lsilbers.apps.twitternator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lsilbers.apps.twitternator.R;
import com.lsilbers.apps.twitternator.TwitterApplication;
import com.lsilbers.apps.twitternator.models.User;
import com.lsilbers.apps.twitternator.network.TwitterClient;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by lsilberstein on 11/8/15.
 */
public class TweetCompositionActivity extends AppCompatActivity {
    public static final String TAG = "TCA";
    private ImageView ivMyProfile;
    private TextView tvMyName;
    private TextView tvMyUsername;
    private TextView tvCharRemain;
    private EditText etTweet;
    private Button btnTweet;
    private TwitterClient client;
    private User user;
    private boolean canPopulate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_composition);

        ivMyProfile = (ImageView) findViewById(R.id.ivMyProfile);
        tvMyName = (TextView) findViewById(R.id.tvMyName);
        tvMyUsername = (TextView) findViewById(R.id.tvMyUsername);
        etTweet = (EditText) findViewById(R.id.etTweet);
        tvCharRemain = (TextView) findViewById(R.id.tvCharRemain);
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharRemain.setText(String.valueOf(140-s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnTweet = (Button) findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTweet();
            }
        });
        canPopulate = true;
        populateView();

        client = TwitterApplication.getTwitterClient();
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
    }

    // first check if all the necessary configuration is done then populates the fields if it is
    private void populateView() {
        if (user != null && canPopulate) {
            tvMyUsername.setText(getResources().getString(R.string.at_symbol,user.getScreenName()));
            tvMyName.setText(user.getName());
            Picasso.with(this).load(user.getProfileImageUrl()).into(ivMyProfile);
        }
    }

    private void onTweet() {
        client.postStatus(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "tweet error " + (errorResponse != null ? errorResponse.toString() : "status code " + statusCode));
            }
        }, etTweet.getText().toString());
        Log.d(TAG, etTweet.getText().toString());
        etTweet.setText("");
        finish();
    }
}
