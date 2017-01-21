package com.katic.centralisedfoodorder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.katic.centralisedfoodorder.OrderHistoryActivity;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, OrderHistoryActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}