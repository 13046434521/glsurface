package com.jtl.surface.render;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;

import com.jtl.surface.helper.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 作者:jtl
 * 日期:Created in 2023/4/13 14:55
 * 描述:渲染点
 * 更改:
 */

public class PointRender implements IBaseRender<Point>{
    private static final String TAG =  PointRender.class.getSimpleName();
    private static final String VERTEX_SHADER_NAME = "shader/PointShader.vert";
    private static final String FRAGMENT_SHADER_NAME = "shader/PointShader.frag";

    private int width;
    private int height;
    private final int COMPONENTS_PER_VERTEX = 2;
    private int a_Position; // 插槽a_Position
    private int a_Color;// 插槽a_Color
    private int a_PointSize;// 插槽a_PointSize
    private float[] pointData = new float[COMPONENTS_PER_VERTEX];//点的位置：就x，y两个分量
    private float[] fixColor = {1.0f, 2.0f, 0.0f, 1.0f};// 点的颜色
    private float pointSize = 10;//点的大小

    // 大小为，点的个数 * 字节数 = x，y两个分量 * float字节数 = 2 * 4
    private FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(pointData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

    private int program;

    @Override
    public void createdGLThread(Context context) {
        program= GLES20.glCreateProgram();

        int frag = ShaderHelper.loadGLShader(TAG,context,GLES20.GL_FRAGMENT_SHADER,FRAGMENT_SHADER_NAME);
        int vertex =ShaderHelper.loadGLShader(TAG,context,GLES20.GL_VERTEX_SHADER,VERTEX_SHADER_NAME);
        ShaderHelper.checkGLError("createGlThread");
        GLES20.glAttachShader(program,frag);
        GLES20.glAttachShader(program,vertex);
        ShaderHelper.checkGLError("createGlThread");
        GLES20.glLinkProgram(program);
        ShaderHelper.checkGLError("createGlThread");
        GLES20.glUseProgram(program);

        ShaderHelper.checkGLError("createGlThread");
        a_Position = GLES20.glGetAttribLocation(program,"a_Position");
        ShaderHelper.checkGLError("createGlThread");
        a_Color = GLES20.glGetAttribLocation(program,"a_Color");

        a_PointSize = GLES20.glGetUniformLocation(program,"a_PointSize");
        ShaderHelper.checkGLError("createGlThread");
        GLES20.glDetachShader(program,frag);
        GLES20.glDetachShader(program,vertex);
        ShaderHelper.checkGLError("createGlThread");
        GLES20.glDeleteShader(frag);
        GLES20.glDeleteShader(vertex);

        GLES20.glUseProgram(0);
        ShaderHelper.checkGLError("createGlThread");
    }

    @Override
    public void onSurfaceChanged(float width, float height) {
        this.width = (int) width;
        this.height = (int) height;
    }

    @Override
    public void onDraw(Point point) {
        onDraw(point,width,height);
    }

    @Override
    public void onDraw(Point point, int width, int height) {
        if (point==null||width<0||height<0){
            return;
        }
        GLES20.glUseProgram(program);

        // 坐标转换，转换成opengl的坐标
        float halfWidth = width / 2;
        float halfHeight = height / 2;


        pointData[0] = (point.x - halfWidth) / halfWidth;
        pointData[1] = -(point.y - halfHeight) / halfHeight;

        vertexBuffer.put(pointData).position(0);

        GLES20.glEnableVertexAttribArray(a_Position);
        GLES20.glEnableVertexAttribArray(a_Color);
        // 传入数据，设置读取数据的规则，每2个为一组数据
        GLES20.glVertexAttribPointer(a_Position,COMPONENTS_PER_VERTEX,GLES20.GL_FLOAT,false,0,vertexBuffer);
        // 设置点的大小
        GLES20.glUniform1f(a_PointSize,pointSize);
        // 设置颜色
        GLES20.glVertexAttrib4fv(a_Color,fixColor,0);
        // 画点
        GLES20.glDrawArrays(GLES20.GL_POINTS,0,1);


        GLES20.glDisableVertexAttribArray(a_Position);
        GLES20.glDisableVertexAttribArray(a_Color);

        GLES20.glUseProgram(0);
        ShaderHelper.checkGLError("onDraw");
    }

    public void setPointColor( float[] fixColor){
        this.fixColor = fixColor;
    }

    public void setPointSize( float pointSize){
        this.pointSize = pointSize;
    }
}
