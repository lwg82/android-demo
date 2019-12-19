package com.example.opengles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.example.androiddemo.R;

public class OpenGLESActivity extends AppCompatActivity {

    private boolean rendererSet;

    private GLSurfaceView glSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_open_gles);

        // tvOpenGL = (TextView) findViewById(R.id.tv_opengles);
        glSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        Log.v("OpenGL ES :", String.format("%04x", configurationInfo.reqGlEsVersion));
        //tvOpenGL.setText("OpenGL ES " + String.format("%04x", configurationInfo.reqGlEsVersion));

        if(configurationInfo.reqGlEsVersion > 0x20000)
        {
            glSurfaceView.setEGLContextClientVersion(2);

            glSurfaceView.setRenderer(new OpenGLESRender());

            rendererSet = true;
        }
        else
        {
            rendererSet = false;
        }

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(rendererSet){
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(rendererSet){
            glSurfaceView.onResume();
        }
    }

}
