package com.jtl.surface.gl;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.surface.Constant;
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
    private int width = Constant.WIDTH;
    private int height = Constant.HEIGHT;
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
        mYUVBuffer = ByteBuffer.allocateDirect(width * height * 3 / 2);
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

        mYuvRender.onSurfaceChanged(this.width, this.height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (mYuvRender != null) {
            mYuvRender.onDraw(mYUVBuffer, width, height);
        }
    }

    @Override
    public void updateImage(ByteBuffer dataBuffer) {

    }

    @Override
    public void updateImage(ByteBuffer dataBuffer, int width, int height) {
        this.width = width;
        this.height = height;
        mYUVBuffer = dataBuffer;
    }
}
