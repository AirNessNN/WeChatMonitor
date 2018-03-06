package com.airness.performancemanager;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Dell on 2018/2/20.
 * 截图服务，由背景服务启动执行
 */


public class ShotService extends IntentService {

    private Shotter shotter=null;


    public ShotService() {
        super("ShotService");
        //Log.i("ShotService","实例化");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            Log.i("ShotService","截图");
            if(shotter==null){
                if(MainActivity.data!=null){
                    shotter=new Shotter(ShotService.this,MainActivity.data);
                }
            }
            shotter.startScreenShot(new Shotter.OnShotListener() {
                @Override
                public void finish(String url) {
                    Log.e("截屏任务","截屏路径为"+url);
                }
            });
        }catch (Exception e){
            Log.e("ShotService",e.toString());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // Log.i("ShotService","销毁");
    }


}
