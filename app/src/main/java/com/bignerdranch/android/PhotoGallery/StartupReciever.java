package com.bignerdranch.android.PhotoGallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.prefs.PreferenceChangeEvent;

/**
 * Created by ksrchen on 5/18/14.
 */
public class StartupReciever extends BroadcastReceiver {
    private static final String TAG = "StartupReciever";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Recieved broadcast intent" + intent.getAction());

        boolean isOn = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                 PollService.PREF_IS_ALARM_ON, false);
        PollService.setServiceAlaram(context, isOn);
    }
}
