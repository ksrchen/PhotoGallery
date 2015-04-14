package com.bignerdranch.android.PhotoGallery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ksrchen on 5/18/14.
 */
public class NotificationReciever extends BroadcastReceiver {
    private static final String TAG = "NotificationReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "recieved result" + getResultCode());
        if (getResultCode() != Activity.RESULT_OK){
            return;
        }

        int requestCode = intent.getIntExtra("REQUEST_CODE", 0);
        Notification notification = (Notification)intent.getParcelableExtra("NOTIFICATION");

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestCode, notification);
    }
}
