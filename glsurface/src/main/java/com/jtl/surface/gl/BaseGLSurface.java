package com.jtl.surface.gl;

import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.jtl.surface.R;
import com.jtl.surface.render.IBaseRender;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/27 14:51
 * 描述: 自定义GLSurfaceView 的基础类
 * 更改:
 */
public class BaseGLSurface extends GLSurfaceView implements GLSurfaceView.Renderer {
    private int mRenderMode = 0;
    public int width;
    public int height;
    private IBaseRender mRender;
    private ByteBuffer mDataBuffer;
    public BaseGLSurface(Context context) {
        super(context);
    }

    public BaseGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BaseGLSurface);
            mRenderMode = array.getInteger(R.styleable.BaseGLSurface_renderMode, 0);
        }
        init();
    }

    private void init() {
        this.setPreserveEGLContextOnPause(true); // GLSurfaceView  onPause和onResume切换时，是否保留EGLContext上下文
        this.setEGLContextClientVersion(2); //OpenGL ES 的版本
        this.setEGLConfigChooser(8, 8, 8, 8, 24, 0); //深度位数，在setRender之前调用

        this.setRenderer(this);
        this.setRenderMode(mRenderMode);//RENDERMODE_CONTINUOUSLY 或者 RENDERMODE_WHEN_DIRTY
    }

    public void setRender(IBaseRender render) {
        this.mRender = render;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1, 1, 1, 0);
        if (mRender != null) {
            mRender.createdGLThread(this.getContext());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        if (mRender != null) {
            mRender.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (mRender != null && mDataBuffer != null) {
            mRender.onDraw(mDataBuffer, width, height);
        }
    }

    public void updateImage(ByteBuffer dataBuffer) {
        this.mDataBuffer = dataBuffer;
    }

    public void updateImage(ByteBuffer dataBuffer, int width, int height) {
        this.mDataBuffer = dataBuffer;
        this.width = width;
        this.height = height;
    }
}
