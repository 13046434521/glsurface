package com.jtl.surface.gl;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.jtl.surface.render.RectRender;
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
    private RgbRender rgbRender;
    private RectRender rectRender;
    private ByteBuffer rgbImage;

    private Rect mRect;

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

        rectRender = new RectRender();
        rectRender.createGlThread(getContext().getApplicationContext());
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

        if (mRect!=null){
            rectRender.draw(mRect,mPreviewWidth,mPreviewHeight);
        }
    }
    public void updateRect(Rect rect) {
        this.mRect=rect;
    }

    public void updateRect(Rect rect, int width, int height) {
        this.mRect=rect;
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
    }

    public void updateImage(ByteBuffer rgbImage) {
        this.rgbImage = rgbImage;
    }

    public void updateImage(ByteBuffer rgbImage,Rect rect) {
        this.rgbImage = rgbImage;
        this.mRect=rect;
    }

    @Override
    public void updateImage(ByteBuffer dataBuffer, int width, int height) {
        this.rgbImage = dataBuffer;
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
    }

    public void updateImage(ByteBuffer dataBuffer, Rect rect,int width, int height) {
        this.rgbImage = dataBuffer;
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
        this.mRect=rect;
    }
}
