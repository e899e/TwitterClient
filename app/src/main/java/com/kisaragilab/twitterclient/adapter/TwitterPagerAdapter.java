package com.kisaragilab.twitterclient.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.kisaragilab.twitterclient.Constants;
import com.kisaragilab.twitterclient.TimelineFragment;
import com.kisaragilab.twitterclient.customView.CustomImageView;
import com.kisaragilab.twitterclient.model.TwitterList;
import com.kisaragilab.twitterclient.realm.RealmTwitterList;
import com.kisaragilab.twitterclient.realm.RealmUser;
import java.util.ArrayList;
import java.util.HashMap;
import io.realm.Realm;
import io.realm.RealmResults;

public class TwitterPagerAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private final CustomImageView.MediaViewerDelegate mCallback;
    private ArrayList<String> mTitles;
    private ArrayList<HashMap<String, String>> mTimelineIds;

    public TwitterPagerAdapter(@NonNull FragmentManager fm,
                               @NonNull Context context,
                               @NonNull CustomImageView.MediaViewerDelegate callback) {
        super(fm);
        this.mContext = context;
        this.mCallback = callback;
        this.mTitles = new ArrayList<>();
        this.mTimelineIds = new ArrayList<>();

        init();
    }
    
    private void init() {
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmTwitterList home = realm.where(RealmTwitterList.class).equalTo("id", RealmUser.getUserId()).findFirst();
            if(home == null) {
                realm.executeTransaction(tmpRealm -> {
                    tmpRealm.copyToRealmOrUpdate(new RealmTwitterList(
                        RealmUser.getUserId(),
                        "Home",
                        true,
                        true,
                        false,
                        0,
                        true
                    ));
                });
            }

            RealmResults<RealmTwitterList> realmTwitterLists = realm.where(RealmTwitterList.class).equalTo("isActive", true).findAll();
            for(RealmTwitterList realmTwitterList : realmTwitterLists) {
                addListFragment(new TwitterList(realmTwitterList));
            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        String title = mTitles.get(position);
        HashMap<String, String> req = mTimelineIds.get(position);

        if(title.equals("Home")) {
            return new TimelineFragment(mContext, title, Constants.TIMELINE_URL, req, mCallback);
        } else {
            return new TimelineFragment(mContext, title, Constants.LIST_TIMELINE_URL, req, mCallback);
        }
    }

    @Override
    public int getCount() {
        return mTimelineIds.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        int index = mTimelineIds.indexOf(object);

        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    public void addListFragment(TwitterList twitterList) {
        if(twitterList.getId().equals(RealmUser.getUserId())) {
            String userId = RealmUser.getUserId();
            HashMap<String, String> req = new HashMap<>();
            req.put("user_id", userId);
            mTimelineIds.add(req);
            mTitles.add("Home");
        } else {
            HashMap<String, String> req = new HashMap<>();
            req.put("list_id", twitterList.getId());
            mTimelineIds.add(req);
            mTitles.add(twitterList.getListName());
        }
    }

    public void removeFragment(TwitterList twitterList) {
        int index = 0;
        boolean foundFragment = false;
        for(HashMap<String, String> tmpReqParam : mTimelineIds) {
            if(tmpReqParam.get("list_id") != null) {
                if(tmpReqParam.get("list_id").equals(twitterList.getId())) {
                    index = mTimelineIds.indexOf(tmpReqParam);
                    foundFragment = true;
                    break;
                }
            }
        }

        if(foundFragment) {
            mTimelineIds.remove(index);
        }
    }

}
