package com.jtl.glsurface.gl;

import android.content.Context;
import android.util.AttributeSet;

import com.jtl.glsurface.Constant;
import com.jtl.glsurface.render.YRender;
import com.jtl.glsurface.render.YuvRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.jtl.glsurface.Constant.YUV420P_NV12;
import static com.jtl.glsurface.Constant.YUV_Y;

/**
 * 作者:jtl
 * 日期:Created in 2019/3/22 11:06
 * 描述: Yuv GLSurfaceView类
 * 更改:
 */
public class YuvGLSurface extends BaseGLSurface {
    private byte[] mYData;
    private byte[] mUVData;
    private int width = Constant.WIDTH;
    private int height = Constant.HEIGHT;
    private ByteBuffer mYBuffer;
    private ByteBuffer mUVBuffer;
    private YuvRender mYuvRender;
    private YRender mYRender;
    private @Constant.CameraData
    int mCameraType = YUV420P_NV12;

    public YuvGLSurface(Context context) {
        super(context);
    }

    public YuvGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        mYData = new byte[width * height];
        mYBuffer = ByteBuffer.allocateDirect(width * height);
        mYBuffer.order(ByteOrder.nativeOrder());
        mYBuffer.position(0);

        mUVData = new byte[width * height / 2];
        mUVBuffer = ByteBuffer.allocateDirect(width * height / 2);
        mUVBuffer.order(ByteOrder.nativeOrder());
        mUVBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        mYuvRender = new YuvRender();
        mYuvRender.createdGLThread(getContext().getApplicationContext());

        mYRender = new YRender();
        mYRender.createdGLThread(getContext().getApplicationContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);

        mYRender.onSurfaceChanged(this.width, this.height);
        mYuvRender.onSurfaceChanged(this.width, this.height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (mCameraType == YUV420P_NV12) {
            mYuvRender.onDraw(mYBuffer, mUVBuffer);
        } else if (mCameraType == YUV_Y) {
            mYRender.onDraw(mYBuffer);
        }
    }

    @Override
    public void updateImage(ByteBuffer dataBuffer) {

    }

    @Override
    public void updateImage(ByteBuffer dataBuffer, int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setCameraData(byte[] data) {
        if (data != null && data.length > 0) {
            System.arraycopy(data, 0, mYData, 0, mYData.length);
            System.arraycopy(data, mYData.length, mUVData, 0, mUVData.length);

            mYBuffer.put(mYData).position(0);
            mUVBuffer.put(mUVData).position(0);
        }
    }
}
