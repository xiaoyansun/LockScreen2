package com.example.myang2.lockscreen;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class Pyramid {
    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  _vColor = vColor;" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  gl_FragColor = _vColor;" +
                    "}";

    private final int mProgram;
    private FloatBuffer vertexBuffer;  // Buffer for vertex-array
    private IntBuffer indexBuffer;    // Buffer for index-array
    private FloatBuffer colorBuffer;
    private static final int COORS_PER_VERTEX = 3;
    private static final int VALUES_PER_COLOR = 4;
    private int[] curFaces;
    private float[] curColors;

    // Constructor - Set up the buffers
    Pyramid() {
        mProgram = GLES20.glCreateProgram();
    }

    void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        int vertexStride = COORS_PER_VERTEX * 4;
        GLES20.glVertexAttribPointer(mPositionHandle, COORS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        int colorStride = VALUES_PER_COLOR * 4;
        GLES20.glVertexAttribPointer(
                mColorHandle, VALUES_PER_COLOR, GLES20.GL_FLOAT, false, colorStride, colorBuffer);

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, curColors, 0);

        // Draw the triangle
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        //GLES20.glDisableVertexAttribArray(mPositionHandle);

        // get handle to shape's transformation matrix
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        for (int i = 0; i < curFaces.length;) {
            //GLES20.glDrawElements(GLES20.GL_LINE_LOOP, 3, GLES20.GL_UNSIGNED_INT, indexBuffer);
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, i, 3);
            i += 3;
            //vertexBuffer.position(i);
        }

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, curFaces.length * 3);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mMVPMatrixHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }

    void setFaces(int[] faces) {
        curFaces = faces;
    }

    void setColors(float[] colors) {
        curColors = colors;
    }

    void setVertices(float[] vertices) {
        // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(vertices);         // Copy data into buffer
        vertexBuffer.position(0);           // Rewind

        // Setup color-array buffer. Colors in float. An float has 4 bytes
        ByteBuffer cbb = ByteBuffer.allocateDirect(curColors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(curColors);
        colorBuffer.position(0);

        // Setup index-array buffer. Indices in int.
        ByteBuffer ibb = ByteBuffer.allocateDirect(curFaces.length * 4);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asIntBuffer();
        indexBuffer.put(curFaces);
        indexBuffer.position(0);

        int vertexShader = MyGLRenderer2.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer2.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }
}
