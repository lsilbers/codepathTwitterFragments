package com.lsilbers.apps.twitternator.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "Users")
public class User extends Model implements Parcelable {

//        "name": "OAuth Dancer",
    @Column(name = "name")
    private String name;
//        "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
    @Column(name = "profile_image_url")
    private String profileImageUrl;
//        "id_str": "119476949",
    @Column(name = "id_str")
    private String idStr;
//        "followers_count": 28,
    @Column(name = "followers_count")
    private int followersCount;
//        "description": "",
    @Column(name = "tagline")
    private String tagline;
//        "friends_count": 14,
    @Column(name = "followings_count")
    private int followingsCount;
//        "screen_name": "oauth_dancer"
    @Column(name = "screen_name")
    private String screenName;

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getIdStr() {
        return idStr;
    }

    public String getScreenName() {
        return screenName;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public String getTagline() {
        return tagline;
    }

    public int getFollowingsCount() {
        return followingsCount;
    }

    public User(){
        super();
    }

    @Override
    public String toString() {
        return "user:{"+name+","+screenName+","+idStr+","+profileImageUrl+", "+followersCount+", "+tagline+", "+ followingsCount +"}";
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.idStr = jsonObject.getString("id_str");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.screenName = jsonObject.getString("screen_name");
            user.followingsCount = jsonObject.getInt("friends_count");
            user.followersCount = jsonObject.getInt("followers_count");
            user.tagline = jsonObject.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        user.save();
        return user;
    }

    public static void clearSavedUsers(){
        new Delete().from(User.class).execute();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.idStr);
        dest.writeInt(this.followersCount);
        dest.writeString(this.tagline);
        dest.writeInt(this.followingsCount);
        dest.writeString(this.screenName);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.profileImageUrl = in.readString();
        this.idStr = in.readString();
        this.followersCount = in.readInt();
        this.tagline = in.readString();
        this.followingsCount = in.readInt();
        this.screenName = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
