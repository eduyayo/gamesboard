package com.pigdroid.gameboard.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, RestServiceImpl.class);
        context.startService(service);
    }

}