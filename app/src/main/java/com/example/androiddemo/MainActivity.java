package com.example.androiddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airhockey.android.AirHockeyActivity;
import com.example.opengles.OpenGLESActivity;
import com.example.util.Utils;

/********************************
 *
 *   快捷键
 *   重写方法 Ctrl + O
 *   代码格式化  Ctrl + Alt + L
 *
 *   Android 第一行代码  P56
 *
 ********************************/

public class MainActivity extends AppCompatActivity {

    private TextView tvText;

    private ActivityManager activityManager;
    private ConfigurationInfo configurationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = (TextView)findViewById(R.id.tv_text);
        findViewById(R.id.button_test1).setOnClickListener(new OnClickListener());
        findViewById(R.id.button_test2).setOnClickListener(new OnClickListener());
        findViewById(R.id.button_test_destroy_activity).setOnClickListener(new OnClickListener());
        findViewById(R.id.button_test_explict_intent).setOnClickListener(new OnClickListener());
        findViewById(R.id.button_test_implict_intent).setOnClickListener(new OnClickListener());
        findViewById(R.id.button_test_opengl).setOnClickListener(new OnClickListener());
        findViewById(R.id.button_air_hockey).setOnClickListener(new OnClickListener());

        // 获取 OpenGLES 版本
        activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        configurationInfo = activityManager.getDeviceConfigurationInfo();

        StringBuilder  sb = new StringBuilder();

        sb.append(Utils.getScreenWidth(MainActivity.this));
        sb.append("x");
        sb.append(Utils.getScreenHeight(MainActivity.this));
        sb.append(" Density:");
        sb.append(Utils.getScreenDensity(MainActivity.this));

        sb.append("\n");
        sb.append("OpenGL ES Version:");
        sb.append(String.format("%04x", configurationInfo.reqGlEsVersion));
        sb.append("\n");

        tvText.setText(sb.toString());
    }


    class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button_test1) {
                Toast.makeText(MainActivity.this, "测试 short", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.button_test2) {
                Toast.makeText(MainActivity.this, "测试 long", Toast.LENGTH_LONG).show();
                Log.v("测试日志1", "测试日志2");
            }
            else if (v.getId() == R.id.button_test_destroy_activity) {
                finish(); // 销毁活动
            }
            else if (v.getId() == R.id.button_test_explict_intent) {
                // explicit intent
                Intent intent = new Intent(MainActivity.this, TestIntentActivity.class);
                startActivity(intent);
            }
            else if (v.getId() == R.id.button_test_implict_intent) {
                Intent intent = new Intent("com.example.androiddemo.ACTION_START");
                intent.addCategory("com.example.androiddemo.TEST_CATEGORY");
                startActivity(intent);
            }
            else if (v.getId() == R.id.button_test_opengl) {
                Intent intent = new Intent(MainActivity.this, OpenGLESActivity.class);
                startActivity(intent);
            }
            else if (v.getId() == R.id.button_air_hockey) {
                Intent intent = new Intent(MainActivity.this, AirHockeyActivity.class);
                startActivity(intent);
            }
        }

    }

}
