package com.lsilbers.apps.twitternator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lsilbers.apps.twitternator.R;
import com.lsilbers.apps.twitternator.fragments.HomeTimelineFragment;

public class HomeTimelineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HomeTimelineFragment fragment = new HomeTimelineFragment();
        FragmentTransaction ftr = getSupportFragmentManager().beginTransaction();
        ftr.replace(R.id.flFragmentContainer,fragment, fragment.TAG);
        ftr.commit();


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
}
