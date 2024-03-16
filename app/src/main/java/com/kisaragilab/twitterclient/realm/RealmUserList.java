package com.kisaragilab.twitterclient.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmUserList extends RealmObject {

    @PrimaryKey private String id;
                private int internalId;
                private boolean isActive;


    public String getId() {
        return id;
    }

    public int getInternalId() {
        return internalId;
    }

    public void setInternalId(int internalId) {
        this.internalId = internalId;
    }

}
