package com.bignerdranch.android.PhotoGallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * Created by ksrchen on 3/15/14.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 99;

    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    private Listener<Token> mListener;

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public interface Listener<Token> {
        void onThumbnailDownLoaded(Token token, Bitmap bitmap);
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }
    public void queueThumbnail(Token token, String url){
        Log.i(TAG, "Got an URL: " + url);
        if (url != null)
        {
            requestMap.put(token, url);
            mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
        }
        else{
            Log.e(TAG, "BAD request");
        }
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    Token token = (Token)msg.obj;
//                    if (token != null  && requestMap.get(token) != null){
                    if (requestMap.containsKey((token))){
                        Log.i(TAG, "Got a request for url " + requestMap.get(token));
                        handlerRequest(token);
                    }
                }
            }
        };
    }

    private void handlerRequest(final Token token) {
        try{
            final String url = requestMap.get(token);
            if (url == null){
                return;
            }
            byte[] bitmapBytes =  new FlickrFetchr().getUrlBytes(url);
            if (bitmapBytes != null){
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                Log.i(TAG, "Bitmap created");
                mResponseHandler.post( new Runnable() {
                    @Override
                    public void run() {
                        if (requestMap.get(token) != url ){
                            return;
                        }
                        requestMap.remove(token);
                        mListener.onThumbnailDownLoaded(token, bitmap);
                    }
                });
            }
        } catch (IOException exp){
            Log.e(TAG, "Error downloading image", exp);
        }

    }
    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
