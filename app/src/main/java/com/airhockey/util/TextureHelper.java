package com.airhockey.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {
    public static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId){
        // 纹理对象
        final int[] textureObjectIds = new int[1];

        glGenTextures(1, textureObjectIds, 0);

        if(textureObjectIds[0] == 0)
        {
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
        }

        // 加载压缩图像
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // 指定原始图片

        // 解码
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if(null == bitmap)
        {
            Log.w(TAG, "Resource ID " + resourceId + " could not be decoded. ");

            glDeleteTextures(1, textureObjectIds, 0);
        }

        // 指定纹理对象作为二维纹理对待
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // 纹理过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR); // 缩小
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); // 放大

        // 加载纹理
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // 生成 mip map
        glGenerateMipmap(GL_TEXTURE_2D);

        // 释放位图
        bitmap.recycle();

        // 接触纹理绑定
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0]; // 返回纹理对象
    }
}
