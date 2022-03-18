package com.jtl.surface.gl;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.jtl.surface.render.DepthRender;
import com.jtl.surface.render.RectRender;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * DepthGLSurface
 *
 * @author jtl
 * @date 2019/9/26
 */
public class DepthGLSurface extends BaseGLSurface {
    private DepthRender mDepthRender;
    private RectRender rectRender;
    private ByteBuffer depthImage;
    private Rect mRect;

    public DepthGLSurface(Context context) {
        super(context);
    }

    public DepthGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mDepthRender = new DepthRender();
        mDepthRender.createdGLThread(getContext().getApplicationContext());

        rectRender = new RectRender();
        rectRender.createdGLThread(getContext().getApplicationContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        if (mDepthRender != null) {
            mDepthRender.onDraw(depthImage, mPreviewWidth, mPreviewHeight);
        }


        if (mRect != null) {
            rectRender.onDraw(mRect, mPreviewWidth, mPreviewHeight);
        }
    }

    @Override
    public void updataImage(ByteBuffer dataBuffer) {
        this.depthImage = dataBuffer;
    }

    @Override
    public void updataImage(ByteBuffer dataBuffer, int width, int height) {
        this.depthImage = dataBuffer;
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
    }

    public void updateRect(Rect rect) {
        this.mRect = rect;
    }

    public void updateRect(Rect rect, int width, int height) {
        this.mRect = rect;
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
    }

    public void updataImage(ByteBuffer rgbImage, Rect rect) {
        this.depthImage = rgbImage;
        this.mRect = rect;
    }

    public void updataImage(ByteBuffer dataBuffer, Rect rect, int width, int height) {
        this.depthImage = dataBuffer;
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
        this.mRect = rect;
    }
}
