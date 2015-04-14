package com.bignerdranch.android.PhotoGallery;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;

/**
 * Created by ksrchen on 3/1/14.
 */
public class PhotoGalleryFragment extends VisibleFragment {
    private GridView mGridView;
    private static ArrayList<GalleryItem> mItems;
    private ThumbnailDownloader<ImageView> mThumbnailLoader;
    private static final String TAG = "PhotoGalleryFragement";
    private MenuItem mSearchItem = null;

    public static ArrayList<GalleryItem> getItems() {
        return mItems;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mSearchItem = menu.findItem(R.id.menu_item_search);
            SearchView searchView = (SearchView)mSearchItem.getActionView();
            SearchManager searchManager = (SearchManager)getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(
                    getActivity().getComponentName());
            searchView.setSearchableInfo(searchableInfo);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                if (mSearchItem != null){
                    SearchView searchView = (SearchView)mSearchItem.getActionView();
                    if (!searchView.isIconified()){
                        searchView.setQuery(null, false);
                        searchView.setIconified(true);
                    }
                }
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(FlickrFetchr.PRE_SEARCH_QUERY, null)
                        .commit();
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlaram(getActivity(), shouldStartAlarm);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())){
            toggleItem.setTitle(R.string.stop_polling);
        }
        else{
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

       // PollService.setServiceAlaram(getActivity(), true);

        mThumbnailLoader = new ThumbnailDownloader<ImageView>(new Handler());
        mThumbnailLoader.setListener( new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownLoaded(ImageView imageView, Bitmap bitmap) {
                if (isVisible()){
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
        mThumbnailLoader.start();
        mThumbnailLoader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    public void updateItems() {
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.photo_gallery_fragment, container, false);
        mGridView = (GridView)view.findViewById(R.id.gridview);
        setupAdapter();

        mGridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem item = mItems.get((position));
                Uri photoPageUri = Uri.parse(item.getPhotoPageUrl());

                Intent i = new Intent(getActivity(), PhotoPageActivity.class);
                i.setData(photoPageUri);

                startActivity(i);
            }
        });
        return view;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            Activity activity = getActivity();
            if (activity == null){
                return new ArrayList<GalleryItem>();
            }

            String query = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(FlickrFetchr.PRE_SEARCH_QUERY, null);


            return query == null? new FlickrFetchr().fetchItems():
                    new FlickrFetchr().searchItems(query);
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }

    private void setupAdapter() {
        if (getActivity() == null || mGridView == null){
            return;
        }
        if (mItems != null){
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        }
        else {
            mGridView.setAdapter(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailLoader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailLoader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
        public GalleryItemAdapter (ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }
            ImageView imageView = (ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            //imageView.setImageResource(R.drawable.brian_up_close);
            final GalleryItem item = getItem(position);
            if (item != null && item.getUrl() != null){
                mThumbnailLoader.queueThumbnail(imageView, item.getUrl());
            }
//            imageView.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i(TAG, "Fetching " + item.getOriginalUrl());
//                    Intent intent = new Intent(getActivity(), PhotoGalleryPageActivity.class);
//                    intent.putExtra(PhotoGalleryPageFragment.EXTRA_URL, item.getOriginalUrl());
//                    startActivity(intent);
//
//                }
//            });
            return convertView;
        }
    }
}
