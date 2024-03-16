package com.kisaragilab.twitterclient.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.kisaragilab.twitterclient.model.Media;
import java.util.List;

public class ImageAdapter extends PagerAdapter {

    private List<Media> medias;

    public ImageAdapter(List<Media> medias) {
        this.medias = medias;
    }

    @Override
    public int getCount() {
        return medias.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }

}
