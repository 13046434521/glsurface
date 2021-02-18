package com.jtl.surface;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.jtl.surface.gl.BaseGLSurface;
import com.jtl.surface.gl.YuvGLSurface;
import com.jtl.surface.render.DepthRender;
import com.jtl.surface.render.RgbRender;
import com.jtl.surface.render.YRender;
import com.jtl.surface.render.YuvRender;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.Permission;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DepthRender depthRender;
    private BaseGLSurface mBaseGLSurface;
    private YuvGLSurface mRgbGLSurface;
    private ExecutorService executorService;
    private RgbRender mRgbRender;
    private YuvRender mYuvRender;
    private YRender mYRender;
    private ByteBuffer mByteBuffer;
    private ByteBuffer mByteBuffer2;
    private int width = 1280;
    private int height = 960;
    private CountDownLatch mCountDownLatch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBaseGLSurface = findViewById(R.id.base);
        mRgbGLSurface = findViewById(R.id.yuv);

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
                        if (mByteBuffer == null) {
                            mByteBuffer = ByteBuffer.allocateDirect(width * height * 3 / 2).order(ByteOrder.nativeOrder());
                            mByteBuffer = FileHelper.getInstance().readLocalFileByteBuffer(name, width * height * 3 / 2, mByteBuffer);
                        }
                        if (mByteBuffer2 == null) {
                            mByteBuffer2 = ByteBuffer.allocateDirect(width * height * 3 / 2).order(ByteOrder.nativeOrder());
                            mByteBuffer2 = FileHelper.getInstance().readLocalFileByteBuffer(name, width * height * 3 / 2, mByteBuffer2);
                        }
                        mBaseGLSurface.updateImage(mByteBuffer);
                        mBaseGLSurface.requestRender();

                        mRgbGLSurface.updateImage(mByteBuffer2);
                        mRgbGLSurface.requestRender();
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
