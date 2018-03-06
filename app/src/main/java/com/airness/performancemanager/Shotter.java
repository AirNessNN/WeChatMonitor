package com.airness.performancemanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

/**
 * Created by Dell on 2018/2/19.
 */

public class Shotter {
    private final SoftReference<Context> mReference;
    private ImageReader mImageReader=null;

    private MediaProjection mMediaProjection=null;
    private VirtualDisplay virtualDisplay=null;

    private String url=null;

    private OnShotListener IshotListener=null;
    interface OnShotListener{
        void finish(String url);
    }




    public Shotter(Context context, Intent data)throws Exception{
        mReference=new SoftReference<>(context);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            mMediaProjection= getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK,data);

            mImageReader=ImageReader.newInstance(getWidth(),getHeight(), PixelFormat.RGB_888,1);
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {

        virtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                getWidth(),
                getHeight(),
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }


    public void startScreenShot(OnShotListener onShotListener, String loc_url) {
        url = loc_url;
        startScreenShot(onShotListener);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startScreenShot(OnShotListener onShotListener) {

        this.IshotListener = onShotListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            virtualDisplay();

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Image image = mImageReader.acquireLatestImage();
                                        AsyncTaskCompat.executeParallel(new SaveTask(), image);
                                    }
                                },
                    300);

        }
    }



    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap doInBackground(Image... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                return null;
            }

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();//一定要关闭，不然下次打开会报错
            File fileImage = null;
            if (bitmap != null) {
                try {

                    if (TextUtils.isEmpty(url)) {
                        url = getContext().getExternalFilesDir("screenshot").getAbsoluteFile()
                                +
                                "/"
                                +
                                SystemClock.currentThreadTimeMillis() + ".png";
                    }
                    fileImage = new File(url);

                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(fileImage);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    fileImage = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    fileImage = null;
                }
            }

            if (fileImage != null) {
                return bitmap;
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            if (virtualDisplay != null) {
                virtualDisplay.release();
            }

            if (IshotListener != null) {
                IshotListener.finish(url);
            }

        }
    }




    private MediaProjectionManager getMediaProjectionManager(){
        return (MediaProjectionManager)getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private Context getContext(){
        return mReference.get();
    }

    private int getWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
