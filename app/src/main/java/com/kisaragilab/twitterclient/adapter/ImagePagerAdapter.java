package com.kisaragilab.twitterclient.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mUrls;

    public ImagePagerAdapter(Context mContext, ArrayList<String> urls) {
        this.mContext = mContext;
        mUrls = urls;
    }

    public void add(String item) {
        mUrls.add(item);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String url = mUrls.get(position);

        ImageView view = new ImageView(mContext);
        Picasso.with(mContext).load(url).into(view);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ImageView) object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
