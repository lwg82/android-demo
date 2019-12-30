package com.airhockey.util;

import android.content.Context;
import android.util.Log;

import static android.opengl.GLES20.glGenTextures;

public class TextureHelper {
    public static final String TAG = "TextureHelper";

    public static void loadTexture(Context context, int resourceId){
        // 纹理对象
        final int[] textureObjectIds = new int[1];

        glGenTextures(1, textureObjectIds, 0);

        if(textureObjectIds[0] == 0)
        {
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
        }
    }
}
