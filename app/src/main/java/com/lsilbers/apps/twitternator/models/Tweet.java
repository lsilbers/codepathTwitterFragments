package com.lsilbers.apps.twitternator.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsilberstein on 11/5/15.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable {

    // tweetId
    @Column(name = "tweet_id")
    private long tweetId;

    // created_at
    @Column(name = "created_at")
    private String createdAt;

    // text
    @Column(name = "text")
    private String text;

    // user
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    public long getTweetId() {
        return tweetId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "tweet:{"+tweetId+","+createdAt+","+user+","+text+"}";
    }

    public Tweet(){
        super();
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.tweetId = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.text = jsonObject.getString("text");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        tweet.save();
        return tweet;
    }

    public static ArrayList<Tweet> fromJSON(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                tweets.add(fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tweets;
    }

    /**
     * @return retrieves all saved tweets from the database
     */
    public static List<Tweet> getAll(){
        return new Select()
                .from(Tweet.class)
                .orderBy("tweet_id desc")
                .execute();
    }

    /**
     * Delete all saved tweets and users
     */
    public static void clearSavedTweets() {
        new Delete().from(Tweet.class).execute();
        User.clearSavedUsers();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.tweetId);
        dest.writeString(this.createdAt);
        dest.writeString(this.text);
        dest.writeParcelable(this.user, flags);
    }

    protected Tweet(Parcel in) {
        this.tweetId = in.readLong();
        this.createdAt = in.readString();
        this.text = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
