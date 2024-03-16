package com.kisaragilab.twitterclient.realm;

import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.model.User;
import org.json.JSONException;
import org.json.JSONObject;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmUser extends RealmObject {

    @PrimaryKey private String id;
                private String name;
                private String screenName;
                private String description;
                private String profileImageUrl;

    public RealmUser() {}

    public RealmUser(@NonNull String id) {
        this.id = id;
    }

    public RealmUser(@NonNull User user) {
        this.id = user.getId();
        this.screenName = user.getScreen_name();
        this.name = user.getName();
        this.description = user.getDescription();
        this.profileImageUrl = user.getProfileImageUrl();
    }

    public RealmUser(@NonNull JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.screenName = json.getString("screen_name");
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.profileImageUrl = json.getString("profile_image_url_https");
    }

    public String getId() {
        return id;
    }

    public static String getUserId() {
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmUser realmUser = realm.where(RealmUser.class).findFirst();
            if(realmUser != null) {
                return realmUser.id;
            }
        }
        return null;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return profileImageUrl;
    }

}
