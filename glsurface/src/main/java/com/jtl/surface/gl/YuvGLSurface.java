package com.jtl.surface.gl;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.surface.render.YuvRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 作者:jtl
 * 日期:Created in 2019/3/22 11:06
 * 描述: Yuv GLSurfaceView类
 * 更改:
 */
public class YuvGLSurface extends BaseGLSurface {
    private ByteBuffer mYUVBuffer;
    private YuvRender mYuvRender;


    public YuvGLSurface(Context context) {
        super(context);
    }

    public YuvGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        mYUVBuffer = ByteBuffer.allocateDirect(mPreviewWidth * mPreviewHeight * 3 / 2);
        mYUVBuffer.order(ByteOrder.nativeOrder());
        mYUVBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        mYuvRender = new YuvRender();
        mYuvRender.createdGLThread(getContext().getApplicationContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);

        mYuvRender.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (mYuvRender != null) {
            mYuvRender.onDraw(mYUVBuffer, mPreviewWidth, mPreviewHeight);
        }
    }

    @Override
    public void updataImage(ByteBuffer dataBuffer) {
        mYUVBuffer = dataBuffer;
    }

    @Override
    public void updataImage(ByteBuffer dataBuffer, int width, int height) {
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
        mYUVBuffer = dataBuffer;
    }
}
