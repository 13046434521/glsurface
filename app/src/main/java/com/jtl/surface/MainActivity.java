package com.jtl.surface;

import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;

import com.jtl.surface.gl.BaseGLSurface;
import com.jtl.surface.gl.RgbGLSurface;
import com.jtl.surface.gl.YuvGLSurface;
import com.jtl.surface.render.DepthRender;
import com.jtl.surface.render.RgbRender;
import com.jtl.surface.render.YRender;
import com.jtl.surface.render.YuvRender;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DepthRender depthRender;
    private BaseGLSurface mBaseGLSurface;
    private YuvGLSurface mYuvGLSurface;

    private RgbGLSurface mRgbGLSurface;
    private ExecutorService executorService;
    private RgbRender mRgbRender;
    private YuvRender mYuvRender;
    private YRender mYRender;
    private ByteBuffer mByteBuffer;
    private ByteBuffer mByteBuffer2;

    private ByteBuffer mByteBuffer3;
    private int width = 1280;
    private int height = 960;
    private CountDownLatch mCountDownLatch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBaseGLSurface = findViewById(R.id.base);
        mYuvGLSurface = findViewById(R.id.yuv);
        mRgbGLSurface = findViewById(R.id.rgb);

        mRgbRender = new RgbRender();
        depthRender = new DepthRender();
        mYuvRender = new YuvRender();
        mYRender = new YRender();
        mBaseGLSurface.setRender(mYuvRender);
        executorService = Executors.newFixedThreadPool(2);
        if(PermissionHelper.hasStoragePermission(this)){
            init();
        }else{
            PermissionHelper.requestStoragePermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
            init();
        }else{
            PermissionHelper.requestStoragePermission(MainActivity.this);
        }
    }

    private void init(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mCountDownLatch.await();
                    while (true) {
                        String name = "/sdcard/GLSurface/GL_Image/1280_960_262.yuv";
                        String rgbName = "/sdcard/GLSurface/GL_Image/640_480_rgb.raw";
                        if (mByteBuffer == null) {
                            mByteBuffer = ByteBuffer.allocateDirect(width * height * 3 / 2).order(ByteOrder.nativeOrder());
                            mByteBuffer = FileHelper.getInstance().readLocalFileByteBuffer(name, width * height * 3 / 2, mByteBuffer);
                        }
                        if (mByteBuffer2 == null) {
                            mByteBuffer2 = ByteBuffer.allocateDirect(640 * 480 * 3).order(ByteOrder.nativeOrder());
                            mByteBuffer2 = FileHelper.getInstance().readLocalFileByteBuffer(rgbName, 640 * 480 * 3 , mByteBuffer2);
                        }
                        if (mByteBuffer3 == null) {
                            mByteBuffer3 = ByteBuffer.allocateDirect(width * height * 3 / 2).order(ByteOrder.nativeOrder());
                            mByteBuffer3 = FileHelper.getInstance().readLocalFileByteBuffer(name, width * height * 3 / 2, mByteBuffer3);
                        }
                        mBaseGLSurface.updataImage(mByteBuffer);
                        mBaseGLSurface.requestRender();

                        mRgbGLSurface.updataImage(mByteBuffer2);
                        mRgbGLSurface.updatePoint(new Point(640/2,480/2));
                        mRgbGLSurface.requestRender();

                        mYuvGLSurface.updataImage(mByteBuffer3);
                        mYuvGLSurface.requestRender();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] paths = getAssets().list("image");
                    for (String path : paths) {
                        FileHelper.getInstance().copy(getAssets().open("image/" + path), FileHelper.getInstance().getFaceImageFolderPath() + path, false);
                    }
                    mCountDownLatch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
