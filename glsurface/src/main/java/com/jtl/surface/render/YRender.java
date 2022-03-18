package com.jtl.surface.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.jtl.surface.helper.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/9 19:45
 * 描述:渲染YUV的Y分量
 * 更改:
 */
public class YRender implements IBaseRender<ByteBuffer> {
    private static final String TAG = YRender.class.getSimpleName();
    private static final String VERTEX_SHADER_NAME = "shader/yuv_y_vert.glsl";
    private static final String FRAGMENT_SHADER_NAME = "shader/yuv_y_frag.glsl";
    private int rotate = -90;
    //纹理坐标
    private float[] textureCoord = new float[]{
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    //顶点坐标
    private float[] vertexCoord = new float[]{
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
    };
    private int[] texture = new int[3];
    private int mProgram;
    private int a_Position;
    private int a_TexCoord;
    private int y_TextureUnit;
    private int a_MvpMatrix;

    private int width;
    private int height;
    private float[] mMVPMatrix = new float[16];

    private FloatBuffer mTextureCoord;
    private FloatBuffer mVertexCoord;

    private byte[] mYData;
    private ByteBuffer mYBuffer;

    @Override
    public void createdGLThread(Context context) {
        initProgram(context);
        initData();
        initTexture();
    }

    private void initProgram(Context context) {
        mProgram = GLES20.glCreateProgram();
        int vertexShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NAME);
        int fragmentShader =  ShaderHelper.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER,FRAGMENT_SHADER_NAME);

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        a_Position = GLES20.glGetAttribLocation(mProgram, "a_Position");
        a_TexCoord = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");
        y_TextureUnit = GLES20.glGetUniformLocation(mProgram, "y_TextureUnit");
        a_MvpMatrix = GLES20.glGetUniformLocation(mProgram, "a_MvpMatrix");
        GLES20.glDetachShader(mProgram, vertexShader);
        GLES20.glDetachShader(mProgram, fragmentShader);

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        ShaderHelper.checkGLError("initProgram");
    }

    private void initData() {
        ByteBuffer textureBuffer = ByteBuffer.allocateDirect(textureCoord.length * 4);
        textureBuffer.order(ByteOrder.nativeOrder());
        mTextureCoord = textureBuffer.asFloatBuffer();
        mTextureCoord.put(textureCoord).position(0);

        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexCoord.length * 4);
        vertexBuffer.order(ByteOrder.nativeOrder());
        mVertexCoord = vertexBuffer.asFloatBuffer();
        mVertexCoord.put(vertexCoord).position(0);
    }

    private void initTexture() {
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glUniform1i(y_TextureUnit, 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        ShaderHelper.checkGLError("initTexture");
    }

    @Override
    public void onSurfaceChanged(float width, float height) {
        this.width = (int) width;
        this.height = (int) height;

        setRotate(rotate);
    }

    @Override
    public void onDraw(ByteBuffer buffer) {
        onDraw(buffer, width, height);
    }

    @Override
    public void onDraw(ByteBuffer yData, int width, int height) {
        if (yData == null) {
            return;
        }
        if (mYBuffer == null) {
            mYData = new byte[width * height];
            mYBuffer = ByteBuffer.allocateDirect(width * height);
            mYBuffer.order(ByteOrder.nativeOrder()).position(0);
        }
        //放进数据中
        yData.get(mYData, 0, mYData.length).position(0);
        //从数组中获取数据
        mYBuffer.put(mYData, 0, mYData.length).position(0);

        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, mYBuffer);

        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glEnableVertexAttribArray(a_TexCoord);
        GLES20.glUniformMatrix4fv(a_MvpMatrix, 1, false, mMVPMatrix, 0);
        GLES20.glVertexAttribPointer(a_Position, 2, GLES20.GL_FLOAT, false, 0, mVertexCoord);
        GLES20.glVertexAttribPointer(a_TexCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureCoord);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(a_Position);
        GLES20.glDisableVertexAttribArray(a_TexCoord);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);

        ShaderHelper.checkGLError("onDraw");
    }


    public void setRotate(int rotate) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setRotateM(mMVPMatrix, 0, rotate, 0, 0, 1);
    }
}
