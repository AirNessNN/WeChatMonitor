package com.airness.performancemanager;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Dell on 2018/2/19.
 *
 */

public class TaskBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        BackgroundService.sendShotAlarm(context);


        try {
            MyAccessibilityService.sendMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
