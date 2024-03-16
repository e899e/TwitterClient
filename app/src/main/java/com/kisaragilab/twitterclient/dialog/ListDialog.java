package com.kisaragilab.twitterclient.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kisaragilab.twitterclient.R;
import com.kisaragilab.twitterclient.adapter.TwitterListAdapter;
import com.kisaragilab.twitterclient.model.TwitterList;
import com.kisaragilab.twitterclient.realm.RealmTwitterList;
import com.kisaragilab.twitterclient.realm.RealmUser;
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class ListDialog extends DialogFragment implements SearchView.OnQueryTextListener {

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<TwitterList> list;
    private final ArrayList<String> activeListIds;
    private final TwitterListAdapter.ListControlImpl callback;

    private RecyclerView recyclerView;
    private TwitterListAdapter adapter;
    private SearchView searchView;
    private TextView messageWhenEmpty;
    private ConstraintLayout loadingFrame;

    public ListDialog(@NonNull Context context, List<TwitterList> list, ArrayList<String> activeListIds, TwitterListAdapter.ListControlImpl callback) {
        this.context = context;
        this.list = list;
        this.activeListIds = activeListIds;
        this.callback = callback;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate( R.layout.list_dialog_fragment, null );
        connectIds(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TwitterListAdapter(context, getTwitterList(), callback);
//        adapter = new TwitterListAdapter(context, list, activeListIds, callback);
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(this);

        return new AlertDialog.Builder(context)
                .setView(view)
                .create();
    }

    private void connectIds(View view) {
        recyclerView = view.findViewById(R.id.recycleView);
        searchView = view.findViewById(R.id.searchView);
        messageWhenEmpty = view.findViewById(R.id.messageWhenEmpty);
//        loadingFrame = view.findViewById(R.id.loadingFrame);
    }

    private ArrayList<TwitterList> getTwitterList() {
        ArrayList<TwitterList> list = new ArrayList<>();

        try(Realm realm = Realm.getDefaultInstance()) {
            RealmResults<RealmTwitterList> realmTwitterLists = realm.where(RealmTwitterList.class)
                    .notEqualTo("id", RealmUser.getUserId())
                    .findAll();

            if(realmTwitterLists != null) {
                for(RealmTwitterList realmTwitterList : realmTwitterLists) {
                    list.add(new TwitterList(realmTwitterList));
                }
            }

            return list;
        } catch(RealmException ex) {
            ex.printStackTrace();
            return list;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<TwitterList> tmpList = new ArrayList<>();
        for(TwitterList twitterList : list) {
            if(twitterList.getListName().contains(newText)) {
                tmpList.add(twitterList);
            }
        }

        if(tmpList.size() == 0) {
            messageWhenEmpty.setVisibility(View.VISIBLE);
        } else {
            messageWhenEmpty.setVisibility(View.GONE);
        }

        adapter.updateData(tmpList);

        return false;
    }
}
