package com.kisaragilab.twitterclient.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmUserSettings extends RealmObject {

    @PrimaryKey private String id;
                private RealmList<RealmUserList> userLists;

    public String getId() {
        return id;
    }

    public RealmList<RealmUserList> getUserLists() {
        return userLists;
    }

    public void setUserLists(RealmList<RealmUserList> userLists) {
        this.userLists = userLists;
    }

}
