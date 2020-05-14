package com.jtl.glsurface;

import android.os.Bundle;

import com.jtl.glsurface.gl.BaseGLSurface;
import com.jtl.glsurface.gl.RgbGLSurface;
import com.jtl.glsurface.render.DepthRender;
import com.jtl.glsurface.render.RgbRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    DepthRender depthRender;
    private BaseGLSurface mBaseGLSurface;
    private RgbGLSurface mRgbGLSurface;
    private ExecutorService executorService;
    private RgbRender mRgbRender;
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

        mBaseGLSurface.setRender(depthRender);

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    mByteBuffer = ByteBuffer.allocateDirect(640 * 480 * 2).order(ByteOrder.nativeOrder());
                    mByteBuffer = FileHelper.getInstance().readLocalFileByteBuffer(FileHelper.getInstance().getFaceImageFolderPath() + "二维码丢帧0depth.raw", 640 * 480 * 2, mByteBuffer);

                    mBaseGLSurface.updateImage(mByteBuffer, 640, 480);
                    mBaseGLSurface.requestRender();
                    mRgbGLSurface.updateImage(mByteBuffer, 640, 480);
                }
            }
        });
    }
}
