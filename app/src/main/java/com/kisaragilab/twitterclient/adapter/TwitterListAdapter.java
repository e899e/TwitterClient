package com.kisaragilab.twitterclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.kisaragilab.twitterclient.R;
import com.kisaragilab.twitterclient.helper.CircleTransform;
import com.kisaragilab.twitterclient.model.TwitterList;
import com.kisaragilab.twitterclient.model.User;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class TwitterListAdapter extends RecyclerView.Adapter<TwitterListAdapter.TwitterListViewHolder> {

    private Context context;
    private List<TwitterList> twitterLists;
    private ArrayList<String> mActiveListIds;
    private ListControlImpl mCallback;

    public TwitterListAdapter(@NonNull Context context, List<TwitterList> data, ArrayList<String> activeListIds, ListControlImpl callback) {
        this.context = context;
        this.twitterLists = new ArrayList<>(data);
        this.mActiveListIds = activeListIds;
        this.mCallback = callback;
    }

    public TwitterListAdapter(@NonNull Context context, List<TwitterList> data, ListControlImpl callback) {
        this.context = context;
        this.twitterLists = new ArrayList<>(data);
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public TwitterListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.twitter_list_cell, parent,false);
        return new TwitterListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TwitterListViewHolder holder, int position) {
        TwitterList list = twitterLists.get(position);
        User user = list.getUser();

        holder.container.setOnClickListener(view -> {
            holder.isActiveCheckBox.performClick();
        });
        holder.listName.setText(list.getListName());
        holder.listOwnerName.setText(user.getName());
        holder.numberOfMembers.setText(String.valueOf(list.getSubscriberCount()));
        Picasso.with(context).load(user.getProfileImageUrl()).transform(new CircleTransform()).into(holder.listOwnerProfileImage);

        holder.isActiveCheckBox.setChecked(list.isActive());
        holder.isActiveCheckBox.setOnClickListener(view -> {
            boolean bool = holder.isActiveCheckBox.isChecked();
            mCallback.OnCheckBoxStateChanged(bool, list);
        });
    }

    @Override
    public int getItemCount() {
        return twitterLists.size();
    }

    public void updateData(List<TwitterList> data) {
        twitterLists.clear();
        twitterLists.addAll(data);
        notifyDataSetChanged();
    }

    protected static class TwitterListViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout container;
        public TextView listName;
        public TextView listOwnerName;
        public TextView numberOfMembers;
        public ImageView listOwnerProfileImage;
        public CheckBox isActiveCheckBox;

        public TwitterListViewHolder(@NonNull View view) {
            super(view);
            container = view.findViewById(R.id.container);
            listName = itemView.findViewById(R.id.listName);
            listOwnerName = itemView.findViewById(R.id.listOwnerName);
            numberOfMembers = itemView.findViewById(R.id.numberOfMembers);
            listOwnerProfileImage = itemView.findViewById(R.id.listOwnerProfileImage);
            isActiveCheckBox = itemView.findViewById(R.id.isActiveCheckBox);
        }

    }

    public interface ListControlImpl {
        void OnCheckBoxStateChanged(boolean isActive, TwitterList twitterList);
    }

}
