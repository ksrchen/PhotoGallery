package com.bignerdranch.android.PhotoGallery;

/**
 * Created by ksrchen on 3/2/14.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOriginalUrl;

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    private String mOwner;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public String toString() {
        return mCaption;
    }

    public String getOriginalUrl() {
        return mOriginalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        mOriginalUrl = originalUrl;
    }
    public String getPhotoPageUrl () {
        return "http://www.flickr.com/photos/" + mOwner + "/" + mId;
    }
}
