package com.jtl.surface.gl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.jtl.surface.render.RgbRender;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * RgbSurfaceView RGB彩色图渲染
 *
 * @author jtl
 * @date 2019/9/26
 */
public class RgbGLSurface extends BaseGLSurface {
    private static final String TAG = RgbGLSurface.class.getSimpleName();
    private ByteBuffer rgbImage;

    private RgbRender rgbRender;

    public RgbGLSurface(Context context) {
        super(context);
    }

    public RgbGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        rgbRender = new RgbRender();
        rgbRender.createdGLThread(getContext().getApplicationContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        if (rgbImage == null || mPreviewWidth <= 0 || mPreviewHeight <= 0) {
            Log.w(TAG, "参数错误");
            return;
        }
        rgbRender.onDraw(rgbImage, mPreviewWidth, mPreviewHeight);
    }

    public void updateImage(ByteBuffer rgbImage) {
        this.rgbImage = rgbImage;
    }

    @Override
    public void updateImage(ByteBuffer dataBuffer, int width, int height) {
        this.rgbImage = dataBuffer;
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
    }
}
