package com.bignerdranch.android.PhotoGallery;

import android.net.Uri;
import android.util.Log;
import org.apache.http.HttpConnection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ksrchen on 3/1/14.
 */
public class FlickrFetchr {
    public static  final  String TAG = "FlickerFetchr";
    public static final String PRE_SEARCH_QUERY = "searchQuery";
    public static final String PRE_LAST_RESULT_ID = "LastResultId";

    public static final String ENDPOINT = "https://api.flickr.com/services/rest";
    public static final String API_KEY = "535f42ff0285af6fc3851acb93c95cbb";
    public static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    public static final String METHOD_SEARCH = "flickr.photos.search";
    public static final String PARAM_EXTRA = "extras";
    public static final String PARAM_TEXT = "text";
    public static final String EXTRA_SMALL_URL = "url_s";
    public static final String EXTRA_ORIGINAL_URL = "url_l";
    private static final String XML_PHOTO = "photo";

    public byte[] getUrlBytes (String urlSpec) throws IOException{
        URL url =  new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            connection.disconnect();
        }

    }
    public  String getUrl(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }
    public ArrayList<GalleryItem>  fetchItems() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRA, EXTRA_SMALL_URL + "," + EXTRA_ORIGINAL_URL)
                .build().toString();

        return downloadGalleryItems(url);
    }
    public ArrayList<GalleryItem>  searchItems(String query) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRA, EXTRA_SMALL_URL + "," + EXTRA_ORIGINAL_URL)
                .appendQueryParameter(PARAM_TEXT, query)
                .build().toString();

        return downloadGalleryItems(url);
    }
    private ArrayList<GalleryItem> downloadGalleryItems(String url){
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        try {
            String xmlString = getUrl(url);
            Log.i(TAG, "recieved xml: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            parseItems(items, parser);
        }
        catch (Exception exp){
            Log.e(TAG, "Failed to fetch items", exp);
        }
        return items;

    }
    void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException{
        int eventType = parser.next();
        while (eventType != XmlPullParser.END_DOCUMENT){
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals(XML_PHOTO) ){
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String url = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                String originalUrl = parser.getAttributeValue(null, EXTRA_ORIGINAL_URL);
                String owner = parser.getAttributeValue(null, "owner");
                if (url != null){
                    GalleryItem item = new GalleryItem();
                    item.setCaption(caption);
                    item.setId(id);
                    item.setUrl(url);
                    item.setOriginalUrl(originalUrl);
                    item.setOwner(owner);
                    items.add(item);
                }

            }
            eventType = parser.next();
        }
    }
}
