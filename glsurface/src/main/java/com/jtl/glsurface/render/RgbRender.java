package com.jtl.glsurface.render;

import android.content.Context;
import android.opengl.GLES20;

import com.jtl.glsurface.helper.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import androidx.annotation.Nullable;

/**
 * RgbRender
 *
 * @author jtl
 * @date 2019/9/26
 */
public class RgbRender implements IBaseRender {
    private static final String TAG = RgbRender.class.getSimpleName();
    /**
     * 顶点着色器(彩色图)
     */
    private static final String VERTEX_SHADER_NAME = "shader/RgbShader.vert";
    /**
     * 片元着色器(彩色图)
     */
    private static final String FRAGMENT_SHADER_NAME = "shader/RgbShader.frag";

    /**
     * 顶点坐标分量数
     */
    private static final int COMPONENTS_PER_VERTEX = 3;
    /**
     * 纹理坐标分量数
     */
    private static final int COMPONENTS_PER_TEXCOORDS = 2;
    /**
     * float类型字节数
     */
    private static final int BYTES_PER_FLOAT = 4;

    private FloatBuffer screenVertexBuffer;
    private FloatBuffer screenTexCoordBuffer;

    private int textureId = -1;
    private int mProgram;

    private int vertexHandle;
    private int texCoordHandle;
    private int samplerHandle;
    private int mWidth;
    private int mHeight;

    /**
     * 顶点坐标
     */
    private static final float[] SCREEN_VERTEX = new float[]{
            -1.0f, -1.0f, 0.0f, -1.0f, +1.0f, 0.0f, +1.0f, -1.0f, 0.0f, +1.0f, +1.0f, 0.0f,
    };

    /**
     * 纹理坐标
     */
    private static final float[] SCREEN_TEXCOORDS_0 = new float[]{
            // 原始方向坐标
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    @Override
    public void createdGLThread(Context context) {
        // 生成纹理id
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];

        // 纹理单元参数
        int textureTarget = GLES20.GL_TEXTURE_2D;
        GLES20.glBindTexture(textureTarget, textureId);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 图像顶点坐标数组
        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(SCREEN_VERTEX.length * BYTES_PER_FLOAT);
        vertexBuffer.order(ByteOrder.nativeOrder());
        screenVertexBuffer = vertexBuffer.asFloatBuffer();
        screenVertexBuffer.put(SCREEN_VERTEX);
        screenVertexBuffer.position(0);

        // 纹理坐标数组
        float[] screenTexcoords = SCREEN_TEXCOORDS_0;

        ByteBuffer textureBuffer = ByteBuffer.allocateDirect(screenTexcoords.length * BYTES_PER_FLOAT);
        textureBuffer.order(ByteOrder.nativeOrder());
        screenTexCoordBuffer = textureBuffer.asFloatBuffer();
        screenTexCoordBuffer.put(screenTexcoords);
        screenTexCoordBuffer.position(0);

        mProgram = GLES20.glCreateProgram();
        int vertexShader = ShaderHelper.loadGLShader(TAG, context, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_NAME);
        int fragmentShader =  ShaderHelper.loadGLShader(TAG, context, GLES20.GL_FRAGMENT_SHADER,FRAGMENT_SHADER_NAME);

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);


        vertexHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        texCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");
        samplerHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");

        ShaderHelper.checkGLError("createdGLThread");
    }

    @Override
    public void onSurfaceChanged(float width, float height) {
        ShaderHelper.checkGLError("onSurfaceChanged");
    }

    public void onDraw(ByteBuffer rgbData){
        onDraw(rgbData, mWidth, mHeight);
    }

    public void onDraw(@Nullable ByteBuffer rgbData, @Nullable int width, @Nullable int height) {
        mWidth = width;
        mHeight = height;

        GLES20.glUseProgram(mProgram);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // Set the vertex positions.
        GLES20.glVertexAttribPointer(vertexHandle, COMPONENTS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, screenVertexBuffer);

        // Set the texture coordinates.
        GLES20.glVertexAttribPointer(
                texCoordHandle,
                COMPONENTS_PER_TEXCOORDS,
                GLES20.GL_FLOAT,
                false,
                0,
                screenTexCoordBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniform1i(samplerHandle, 0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, mWidth, mHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, rgbData);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);

        ShaderHelper.checkGLError("onSurfaceChanged");
    }
}
