package com.jtl.surface.render;

import android.content.Context;

import java.nio.ByteBuffer;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/27 15:12
 * 描述: BaseRender接口
 * 更改:
 */
public interface  IBaseRender<T> {

    void createdGLThread(Context context);

    void onSurfaceChanged(float width, float height);

    void onDraw(T buffer);

    void onDraw(T buffer, int width, int height);
}
