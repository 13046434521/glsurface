package com.jtl.surface;

import android.os.Bundle;

import com.jtl.surface.gl.BaseGLSurface;
import com.jtl.surface.gl.YuvGLSurface;
import com.jtl.surface.render.DepthRender;
import com.jtl.surface.render.RgbRender;
import com.jtl.surface.render.YRender;
import com.jtl.surface.render.YuvRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    DepthRender depthRender;
    private BaseGLSurface mBaseGLSurface;
    private YuvGLSurface mRgbGLSurface;
    private ExecutorService executorService;
    private RgbRender mRgbRender;
    private YuvRender mYuvRender;
    private YRender mYRender;
    private ByteBuffer mByteBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBaseGLSurface = findViewById(R.id.base);
        mRgbGLSurface = findViewById(R.id.rgb);
        PermissionHelper.requestStoragePermission(this);
        mRgbRender = new RgbRender();
        depthRender = new DepthRender();
        mYuvRender = new YuvRender();
        mYRender = new YRender();
        mBaseGLSurface.setRender(mYRender);

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String name = "/sdcard/IMIFace/FaceImage/1280_960_262.yuv";
                    if (mByteBuffer == null) {
                        mByteBuffer = ByteBuffer.allocateDirect(1280 * 960 * 3 / 2).order(ByteOrder.nativeOrder());
                        mByteBuffer = FileHelper.getInstance().readLocalFileByteBuffer(FileHelper.getInstance().getFaceImageFolderPath() + "1280_960_262.yuv", 960 * 1280 * 3 / 2, mByteBuffer);
                    }

                    mBaseGLSurface.updateImage(mByteBuffer, 1280, 960);
                    mBaseGLSurface.requestRender();
//
                    mRgbGLSurface.updateImage(mByteBuffer, 1280, 960);
                    mRgbGLSurface.requestRender();
                }
            }
        });
    }
}
