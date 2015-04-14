package com.bignerdranch.android.PhotoGallery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.UUID;

/**
 * Created by ksrchen on 3/23/14.
 */
public class PhotoGalleryPageActivity extends FragmentActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()){
            @Override
            public Fragment getItem(int position) {
                GalleryItem item = PhotoGalleryFragment.getItems().get(position);
                return PhotoGalleryPageFragment.getInstance(item.getOriginalUrl());
            }

            @Override
            public int getCount() {
                return PhotoGalleryFragment.getItems().size();
            }
        });

        String url = getIntent().getStringExtra(PhotoGalleryPageFragment.EXTRA_URL);

        for (int i=0;i<PhotoGalleryFragment.getItems().size(); i++){
            String originalUrl = PhotoGalleryFragment.getItems().get(i).getOriginalUrl();
            if (originalUrl != null && originalUrl.equals(url)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        setContentView(mViewPager);
    }
}
