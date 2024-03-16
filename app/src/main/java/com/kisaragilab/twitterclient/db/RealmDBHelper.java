package com.kisaragilab.twitterclient.db;

import android.content.Context;
import androidx.annotation.NonNull;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmDBHelper {

    public static RealmConfiguration config;

    public static void initRealm(@NonNull Context context) {
        Realm.init(context);
        config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .name("KisaragiLab.realm")
                .build();

        Realm.setDefaultConfiguration(config);
    }

    public static void clean() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(tmpRealm -> {
                realm.deleteAll();
            });
        }
    }

}
