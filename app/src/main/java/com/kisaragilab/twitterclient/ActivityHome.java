package com.kisaragilab.twitterclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.navigation.NavigationView;
import com.kisaragilab.twitterclient.adapter.TwitterListAdapter;
import com.kisaragilab.twitterclient.adapter.TwitterPagerAdapter;
import com.kisaragilab.twitterclient.customView.CustomImageView;
import com.kisaragilab.twitterclient.db.RealmDBHelper;
import com.kisaragilab.twitterclient.dialog.ListDialog;
import com.kisaragilab.twitterclient.model.Media;
import com.kisaragilab.twitterclient.model.TwitterList;
import com.kisaragilab.twitterclient.realm.RealmTwitterList;
import com.kisaragilab.twitterclient.realm.RealmUser;
import com.kisaragilab.twitterclient.task.TwitterGetJSONArrayTask;
import com.kisaragilab.twitterclient.util.SharedPreferenceManager;
import com.kisaragilab.twitterclient.util.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import io.realm.Realm;
import io.realm.RealmResults;
import static com.kisaragilab.twitterclient.ActivityMediaPlayer.RATIO;

public class ActivityHome
    extends AppCompatActivity
    implements
        NavigationView.OnNavigationItemSelectedListener,
        TwitterListAdapter.ListControlImpl,
        CustomImageView.MediaViewerDelegate
{

    private NavigationView navigationView;
    private ViewPager mViewPager;
    private TwitterPagerAdapter mAdapter;
    private Realm realm;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        realm = Realm.getInstance(RealmDBHelper.config);

        connectIds();

        RealmUser realmUser = realm.where(RealmUser.class).findFirst();
        userId = realmUser.getId();

        navigationView.setNavigationItemSelectedListener(this);

        mAdapter = new TwitterPagerAdapter(
            getSupportFragmentManager(),
            this,
            this
        );

        try(Realm realm = Realm.getInstance(RealmDBHelper.config)) {
            RealmResults<RealmTwitterList> realmTwitterLists = realm.where(RealmTwitterList.class).equalTo("isActive", true).findAll();
            for(RealmTwitterList realmTwitterList : realmTwitterLists) {
                final Menu menu = navigationView.getMenu();
                MenuItem home = menu.findItem(R.id.listHome);
                if(!realmTwitterList.getId().equals(RealmUser.getUserId())) {
                    menu.add(home.getGroupId(), Menu.NONE, home.getOrder(), realmTwitterList.getListName()).setTitle(realmTwitterList.getId()).setIcon(R.drawable.ic_list);
                }
            }
        }

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm != null) {
            realm.close();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuAddTweet:
                startActivity(new Intent(this, ActivityCreateTweet.class));
                return true;

            case R.id.menuAddColumn:
                inflateList();
                return true;

            case R.id.listHome:
                mViewPager.setCurrentItem(0);
                return true;

            case R.id.menuSettings:
                inflateSettingsMenu();
                return true;

            default:
                return true;
        }
    }

    private void connectIds() {
        navigationView = findViewById(R.id.navigationView);
        mViewPager = findViewById(R.id.viewPager);
    }

    private ArrayList<TwitterList> loadListFromOnline() {
        ArrayList<TwitterList> twitterLists = new ArrayList<>();
        realm.executeTransaction(realm -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor();

            TwitterGetJSONArrayTask task = new TwitterGetJSONArrayTask(this, Constants.LIST_GET);
            task.addReqParam("user_id", userId);

            Future<JSONArray> result = executorService.submit(task);
            try {
                JSONArray array = result.get();
                for(int i = 0; i < array.length(); i++) {
                    RealmTwitterList realmTwitterList = new RealmTwitterList(array.getJSONObject(i));
                    RealmTwitterList realmTwitterList_old = realm.where(RealmTwitterList.class).equalTo("id", realmTwitterList.getId()).findFirst();
                    if(realmTwitterList_old != null) {
                        realmTwitterList.setIncludeReply(realmTwitterList_old.isIncludeReply());
                        realmTwitterList.setIncludeRetweet(realmTwitterList_old.isIncludeRetweet());
                        realmTwitterList.setMediaOption(realmTwitterList_old.getMediaOption());
                        realmTwitterList.setActive(realmTwitterList_old.isActive());
                    } else {
                        realmTwitterList.setIncludeReply(true);
                        realmTwitterList.setIncludeRetweet(true);
                        realmTwitterList.setMediaOption(0);
                        realmTwitterList.setActive(false);
                    }
                    realm.copyToRealmOrUpdate(realmTwitterList);

                    twitterLists.add(new TwitterList(realmTwitterList));
                }
            } catch(ExecutionException | InterruptedException | JSONException ex) {
                ex.printStackTrace();
            }
        });

        return twitterLists;
    }

    private void inflateList() {
        ArrayList<TwitterList> twitterLists = loadListFromOnline();
        ArrayList<String> activeListIds = new ArrayList<>();
        try(Realm realm = Realm.getInstance(RealmDBHelper.config)) {
            RealmResults<RealmTwitterList> realmTwitterLists = realm.where(RealmTwitterList.class)
                    .equalTo("isActive", true)
                    .and()
                    .notEqualTo("id", RealmUser.getUserId())
                    .findAll();
            for(RealmTwitterList realmTwitterList : realmTwitterLists) {
                activeListIds.add(realmTwitterList.getId());
            }
        }

        DialogFragment dialogFragment = new ListDialog(this, twitterLists, activeListIds,this);
        dialogFragment.show(getSupportFragmentManager(), "listDialog");
    }

    private void inflateSettingsMenu() {
        final View settingsView = new View(this);
        settingsView.setLayoutParams(new ViewGroup.LayoutParams(1, 1));

        final ViewGroup root = this.getWindow().getDecorView().findViewById(android.R.id.content);
        root.addView(settingsView);

        View menuSettingView = this.findViewById(R.id.menuSettings);
        settingsView.setX(menuSettingView.getX() + navigationView.getWidth());
        settingsView.setY(menuSettingView.getHeight() * (navigationView.getMenu().size() - 1));

        PopupMenu popup = new PopupMenu(this, settingsView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings, popup.getMenu());
        popup.show();
    }

    public void onLogoutClicked(MenuItem item) {
        Utilities.deleteUserIconFile(this);
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        sharedPreferenceManager.clean();
        RealmDBHelper.clean();
        finish();
    }

    @Override
    public void OnCheckBoxStateChanged(boolean isChecked, TwitterList twitterList) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        final Menu menu = navigationView.getMenu();
        MenuItem home = menu.findItem(R.id.listHome);

        realm.executeTransactionAsync(realm -> {
            RealmTwitterList realmTwitterList = new RealmTwitterList(twitterList);
            realmTwitterList.setActive(isChecked);
            realm.copyToRealmOrUpdate(realmTwitterList);
        });

        if(isChecked) {
            mAdapter.addListFragment(twitterList);
            mAdapter.notifyDataSetChanged();
            mViewPager.setOffscreenPageLimit(mAdapter.getCount());

            menu.add(home.getGroupId(), Menu.NONE, home.getOrder(), twitterList.getListName()).setTitle(twitterList.getId()).setIcon(R.drawable.ic_list);
        } else {
            mAdapter.removeFragment(twitterList);
            mAdapter.notifyDataSetChanged();
            mViewPager.setOffscreenPageLimit(mAdapter.getCount());

            ArrayList<MenuItem> menuItems = new ArrayList<>();
            for(int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                if(menuItem.getGroupId() == home.getGroupId()) {
                    menuItems.add(menuItem);
                }
            }

            for(MenuItem menuItem : menuItems) {
                if(menuItem.getTitle().equals(twitterList.getId())) {
                    menu.removeItem(menuItem.getItemId());
                    break;
                }
            }
        }

        ft.commit();
    }

    @Override
    public void OnImageClicked(List<Media> mediaCollection, int position) {
        ArrayList<String> urls = new ArrayList<>();
        for(Media media : mediaCollection) {
            urls.add(media.getUrl());
        }

        startActivity(new Intent(this, ActivityImagePager.class).putStringArrayListExtra("URLS", urls).putExtra("POS", position));
    }

    @Override
    public void OnVideoClicked(String url, String ratio) {
        startActivity(new Intent(this, ActivityMediaPlayer.class).putExtra("URL", url).putExtra(RATIO, ratio));
    }

}
