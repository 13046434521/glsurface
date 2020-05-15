package com.jtl.surface.gl;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.surface.render.DepthRender;

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
    private ByteBuffer depthImage;

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
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        if (mDepthRender != null) {
            mDepthRender.onDraw(depthImage);
        }
    }

    @Override
    public void updateImage(ByteBuffer dataBuffer) {
        this.depthImage = dataBuffer;
    }

    @Override
    public void updateImage(ByteBuffer dataBuffer, int width, int height) {
        this.depthImage = dataBuffer;
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
    }
}
