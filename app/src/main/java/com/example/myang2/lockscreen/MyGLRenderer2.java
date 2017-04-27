package com.example.myang2.lockscreen;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class MyGLRenderer2 implements GLSurfaceView.Renderer {
    private static final float CUBE_ROTATION_INCREMENT = 0.6f;

    private static final int REFRESH_RATE_FPS = 60;

    private static final float FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;

    private Pyramid mPyramid;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private int[] faces = null;
    private float[] vertices = null;
    private float[] colors = null;
    private int curIndex;
    private float xMax;
    private float xMin;
    private float yMax;
    private float yMin;
    private float zMax;
    private float zMin;

    public MyGLRenderer2() {
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }
        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
            // Set the background frame color
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glClearDepthf(1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glDepthFunc(GLES20.GL_LEQUAL);
            mPyramid = new Pyramid();
        }

        public void onDrawFrame(GL10 unused) {
            // Redraw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            // Set the camera position (View matrix)
            //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -4, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // Apply the rotation.
            //Matrix.setRotateM(mRotationMatrix, 0, mCubeRotation, 1.0f, 1.0f, 1.0f);
            // Combine the rotation matrix with the projection and camera view
            //Matrix.multiplyMM(mFinalMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);

            // Calculate the projection and view transformation
            //Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            mPyramid.setFaces(faces);
            mPyramid.setColors(colors);
            mPyramid.setVertices(vertices);
            mPyramid.draw(mMVPMatrix);
        }

        public void onSurfaceChanged(GL10 unused, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;

            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
//            if (curIndex == 0) {
//                Matrix.orthoM(mProjectionMatrix, 0, -ratio * 2, ratio / 2, -1.0f, 1.0f, 3, 10);
//            }
//            if (curIndex == 1) {
//                Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3, 7);
//            }
            //Matrix.translateM(mProjectionMatrix, 0, -(xMax + xMin) / 2, -(yMax + yMin) / 2, -(zMax + zMin) / 2);
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1, 10);
            //Matrix.scaleM(mProjectionMatrix, 0, 0.8f, 0.8f, 0.8f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        }

        public static int loadShader(int type, String shaderCode){

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type);

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            return shader;
        }

    public void setFaces(int[] faces) {
        this.faces = faces;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public void setColors(int length) {
        int colorSize = 4 * length / 3;
        colors = new float[colorSize];
        for (int i = 0; i < colorSize;) {
            colors[i] = 1.0f;
            colors[i + 1] = 0.0f;
            colors[i + 2] = 0.0f;
            colors[i + 3] = 1.0f;
            i += 4;
        }
    }

    public void setFile(int index) {
        this.curIndex = index;
    }

    public void setMaxMin(float[] maxMin) {
        xMax = maxMin[0];
        xMin = maxMin[1];
        yMax = maxMin[2];
        yMin = maxMin[3];
        zMax = maxMin[4];
        zMin = maxMin[5];
    }
}
