package com.airness.performancemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent localIntent=new Intent(context,BackgroundService.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(localIntent);
    }
}
