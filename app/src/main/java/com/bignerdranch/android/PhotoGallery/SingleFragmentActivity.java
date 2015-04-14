package com.bignerdranch.android.PhotoGallery;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;

/**
 * Created by ksrchen on 12/14/13.
 */
public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment CreateFragment();
    protected int getLayoutResId() {
        return R.layout.activity_fragement;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer) ;
        if (fragment == null){
            fragment = CreateFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

    }
}
