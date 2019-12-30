package com.airhockey.android;

import android.graphics.Shader;
import android.opengl.GLSurfaceView;
import android.content.Context;

import com.airhockey.util.LoggerConfig;
import com.airhockey.util.MatrixHelper;
import com.airhockey.util.ShaderHelper;
import com.airhockey.util.TextResourceReader;
import com.example.androiddemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private int program;

    private final Context context;

    private static final int BYTES_PER_FLOAT = 4;

    private final FloatBuffer vertexData;

    private final float[] projectionMatrix = new float[16]; // 正交矩阵

    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;


    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;


    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aColorLocation;


    private final float[] modelMatrix = new float[16];


    public AirHockeyRender(Context ctx) {
        /*
        float[] tableVerticesWithTriangles = {
                // 三角形1
                0f, 0f,
                9f, 14f,
                0f, 14f,
                // 三角形2
                0f, 0f,
                9f, 0f,
                9f, 14f,
                // line1
                0f, 7f,
                9f, 7f,

                //
                4.5f, 2f,
                4.5f, 12f
        };
        */
/*
        float[] tableVerticesWithTriangles = {
                // Triangle 1
                -0.5f, -0.5f,
                0.5f,  0.5f,
                -0.5f,  0.5f,

                // Triangle 2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f,  0.5f,

                // Line 1
                -0.5f, 0f,
                0.5f, 0f,

                // Mallets
                0f, -0.25f,
                0f,  0.25f
        };
*/
/*
        float[] tableVerticesWithTriangles = {
                   0f,    0f,
                -0.5f, -0.5f,
                 0.5f, -0.5f,
                 0.5f,  0.5f,
                -0.5f,  0.5f,
                -0.5f, -0.5f,

                // Line 1
                -0.5f, 0f,
                 0.5f, 0f,

                // Mallets
                0f, -0.25f,
                0f,  0.25f
        };

 */
        // 代颜色属性
        /*
        float[] tableVerticesWithTriangles = {
                // Triangle Fan
                0f,    0f,   1f,   1f,   1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // Mallets
                0f, -0.4f, 0f, 0f, 1f,
                0f,  0.4f, 1f, 0f, 0f
        };
         */

        float[] tableVerticesWithTriangles = {
                // Order of coordinates: X, Y, Z, W, R, G, B

                // Triangle Fan
                0f, 0f, 0f, 1.5f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
                0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

                // Mallets
                0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
                0f, 0.4f, 0f, 1.75f, 1f, 0f, 0f
        };

        // 分配本地内存
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        //
        vertexData.put(tableVerticesWithTriangles);

        this.context = ctx;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkPrograme(vertexShader, fragmentShader);

        //if(LoggerConfig.ON){

        //}

        if (!ShaderHelper.validateProgram(program)) {
            // 失败
            return;
        }

        glUseProgram(program);

        // u_Matrix
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        // 查询 varying
        //aColorLocation = glGetUniformLocation(program, A_COLOR);
        aColorLocation = glGetAttribLocation(program, A_COLOR);

        // 查询 属性
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        // 给 opengl 赋值
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);


        // 颜色
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        /*
        final float aspectRation = width > height ? (float)width/(float)height: (float)height/(float)width;

        if(width> height) {
            orthoM(projectionMatrix, 0, -aspectRation, aspectRation, -1f, 1f, -1f, 1f);
        }
        else
        {
            orthoM(projectionMatrix, 0, -1f, 1f,-aspectRation, aspectRation,  -1f, 1f);
        }
         */

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);

        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);

        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // 绘制桌面
        //glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        //glDrawArrays(GL_TRIANGLES, 0, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        // 绘制中心线
        //glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        //
        //glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        //glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}

