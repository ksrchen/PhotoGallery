package com.bignerdranch.android.PhotoGallery;

import android.support.v4.app.Fragment;

/**
 * Created by ksrchen on 5/25/14.
 */
public class PhotoPageActivity extends  SingleFragmentActivity {
    @Override
    protected Fragment CreateFragment() {
        return new PhotoPageFragment();
    }
}
