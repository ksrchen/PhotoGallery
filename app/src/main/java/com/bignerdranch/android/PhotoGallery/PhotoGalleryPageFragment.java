package com.bignerdranch.android.PhotoGallery;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ksrchen on 3/23/14.
 */
public class PhotoGalleryPageFragment extends Fragment {
    final static String TAG = "PhotoGalleryPageFragment";
    public static final  String EXTRA_URL = "URL";
    private ImageView mImageView;

    public static  PhotoGalleryPageFragment getInstance(String url){
        Bundle args = new Bundle();
        args.putString(EXTRA_URL, url);

        PhotoGalleryPageFragment imageFragment = new PhotoGalleryPageFragment();
        imageFragment.setArguments(args);
        return  imageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String url = (String)getArguments().getString(EXTRA_URL);
        new ImageFetcher().execute(url);
        return  mImageView;
    }

    private class ImageFetcher extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... params) {
            Activity activity = getActivity();
            if (activity == null){
                return null;
            }
            try {
                byte[] data = new FlickrFetchr().getUrlBytes(params[0]);
                if (data != null) {
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    return bitmap;
                }
                return null;
            }catch (IOException exp){
                Log.e(TAG, "Failed to download image:" + params[0], exp);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }
}