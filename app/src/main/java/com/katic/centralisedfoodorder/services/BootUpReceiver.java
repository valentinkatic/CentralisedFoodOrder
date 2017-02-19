package com.katic.centralisedfoodorder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.katic.centralisedfoodorder.OrderHistoryActivity;

public class BootUpReceiver extends BroadcastReceiver {

    private static final String TAG = "BootUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "BootUpReceiver started");

        /*Intent i = new Intent(context, OrderHistoryActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
    }

}