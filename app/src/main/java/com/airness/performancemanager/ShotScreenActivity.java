package com.airness.performancemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.Toast;

/**
 *
 * Created by ${AirNess} on 2018/2/20.
 */

public class ShotScreenActivity extends Activity {

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);

        requestScreenShot();
    }

    private void requestScreenShot(){
        if (Build.VERSION.SDK_INT >= 21) {
            startActivityForResult(
                    ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_MEDIA_PROJECTION:{
                if(resultCode==-1&&data!=null){
                    /*Shotter shotter=new Shotter(ShotScreenActivity.this,data);
                    shotter.startScreenShot(new Shotter.OnShotListener() {
                        @Override
                        public void finish(String url) {
                            Toast.makeText(ShotScreenActivity.this,url,Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
            }
        }
    }
}
