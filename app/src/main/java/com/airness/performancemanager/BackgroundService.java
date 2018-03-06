package com.airness.performancemanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ${AirNess} on 2018/2/19.
 *
 */

public class BackgroundService extends Service {
    public final static String TAG="BackgroundService";

    @Override
    public void onCreate() {
        super.onCreate();

        if(!isAccessibilityServiceEnabled(this)){
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this,"为了保证系统正常运行，请在设置-辅助功能-服务中找到电池管理，开启此应用的辅助功能。",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{
            Notification.Builder builder=new Notification.Builder(this.getApplicationContext());
            Intent nfIntent=new Intent(this,MainActivity.class);
            builder.setContentIntent(PendingIntent.getActivity(this,0,nfIntent,0))
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.notification_large))
                    .setContentTitle(this.getResources().getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.notification_small)
                    .setContentText("电池能效良好。")
                    .setWhen(System.currentTimeMillis());
            Notification notification=builder.build();
            startForeground(1,notification);


            //截屏任务
            BackgroundService.sendShotAlarm(this);

            //启动上传任务
            //Intent uploadService=new Intent(BackgroundService.this,)

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        flags=Service.START_REDELIVER_INTENT;
        return flags;
    }


    public static void sendShotAlarm(Context context){
        //启动截屏任务
        Intent shotService=new Intent(context,TaskBroadcast.class);
        shotService.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        shotService.setAction(context.getResources().getString(R.string.shot));

        PendingIntent sender=PendingIntent.
                getBroadcast(context,999,shotService,PendingIntent.FLAG_UPDATE_CURRENT);

        long startTime= SystemClock.elapsedRealtime();

        startTime+=12*60*1000;

        AlarmManager am=(AlarmManager) context.getSystemService(ALARM_SERVICE);//一分钟执行一次
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime,
                    sender);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime, sender);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private boolean isAccessibilityServiceEnabled(Context context){
        int accessibilityEnable=0;
        final String service=getPackageName()+"/"+MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnable= Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnable == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
}
