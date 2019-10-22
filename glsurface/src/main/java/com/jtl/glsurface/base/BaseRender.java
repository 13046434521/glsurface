package com.jtl.glsurface.base;

import android.content.Context;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/27 15:12
 * 描述: BaseRender接口
 * 更改:
 */
public  interface BaseRender {
     void createdGLThread(Context context);

     void onSurfaceChanged(float width, float height);
}
