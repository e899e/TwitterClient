package com.kisaragilab.twitterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.kisaragilab.twitterclient.adapter.ImagePagerAdapter;
import java.util.ArrayList;

public class ActivityImagePager extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_control);

        Intent intent = getIntent();
        ArrayList<String> urlList = intent.getStringArrayListExtra("URLS");
        int position = intent.getIntExtra("POS", 0);

        ViewPager viewPager = findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, urlList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

}
