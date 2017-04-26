package com.example.myang2.lockscreen;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Float.MAX_VALUE;
import static java.lang.Float.MIN_VALUE;

class PlyReader {
    private BufferedReader bufferedReader;
    private final int NO_INDEX = 100;
    private int vertexIndex = NO_INDEX;
    private int colorIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private boolean inHeader = true;
    private int currentElement = 0;
    private int currentFace = 0;
    /* data fields to store points, colors, faces information read from PLY file */
    private float[] vertices = null;
    private float[] colors = null;
    private float[] normals = null;
    private int[] faces = null;
    // Size of an individual element, in floats
    private int vertexSize = 0;
    private int colorSize = 0;
    private int normalSize = 0;
    private int faceSize = 3;
    // Normalizing constants
    private float vertexMax = MIN_VALUE;
    private float vertexXMax = MIN_VALUE;
    private float vertexXMin = MAX_VALUE;
    private float vertexYMax = MIN_VALUE;
    private float vertexYMin = MAX_VALUE;
    private float vertexZMax = MIN_VALUE;
    private float vertexZMin = MAX_VALUE;
    private float colorMax = 0;
    // Number of elements in the entire PLY
    private int vertexCount = 0;
    private int faceCount = 0;
    // Counter for header
    private int elementCount = 0;

    PlyReader(InputStream plyFile) {
        bufferedReader = new BufferedReader(new InputStreamReader(plyFile));
    }

    void ParsePly() throws IOException {
        // Check if this is even a PLY file.
        String line = bufferedReader.readLine();
        if(!line.equals("ply")) {
            Log.e("ReadHeader", "File is not a PLY! Leave us.");
        }

        // Check for ASCII format
        line = bufferedReader.readLine();
        String words[] = line.split(" ");
        if(!words[1].equals("ascii")) {
            Log.e("ReadHeader", "File is not ASCII format! Cannot read.");
        }

        // Read the header
        line = bufferedReader.readLine();
        while (line != null && inHeader) {
            ReadHeader(line);
            if (inHeader) {
                line = bufferedReader.readLine();
            }
        }

        // Populate the data
        if (vertexSize != 3) {
            Log.e("ParsePly", "Incorrect count of vertices! Expected 3.");
        }
        vertices = new float[vertexCount * vertexSize];
        faces = new int[faceCount * faceSize];
        if (colorSize != 0) { colors = new float [vertexCount * colorSize]; }
        if (normalSize != 0) { normals = new float [vertexCount * normalSize]; }
        line = bufferedReader.readLine();
        while (line != null) {
            ReadData(line);
            line = bufferedReader.readLine();
        }
        ScaleData();
    }

    private void ReadHeader(String line) {
        // Make into a list of words, yo.
        String words[] = line.split(" ");
        if(words[0].equals("comment")) { return; }
        // Check if element or property
        if (words[0].equals("element")) {
            if (words[1].equals("vertex")) {
                vertexCount = Integer.parseInt(words[2]);
            } else if (words[1].equals("face")) {
                faceCount = Integer.parseInt(words[2]);
            }
        }
        if (words[0].equals("property")) {
            switch (words[2]) {
                case "x":
                case "y":
                case "z":
                    if (vertexIndex > elementCount) {
                        vertexIndex = elementCount;
                    }
                    vertexSize++;
                    break;
                case "nx":
                case "ny":
                case "nz":
                    if (normalIndex > elementCount) {
                        normalIndex = elementCount;
                    }
                    normalSize++;
                    break;
                case "red":
                case "green":
                case "blue":
                case "alpha":
                    if (colorIndex > elementCount) {
                        colorIndex = elementCount;
                    }
                    colorSize++;
                    break;
            }
            elementCount++;
        }

        if (words[0].equals("end_header")) {
            inHeader = false;
        }
    }

    private void ReadData(String line) {
        String words[] = line.split(" ");
        // Compensate for extra line read with (vertexCount - 1)
        if (currentElement < vertexCount) {
            for (int i = 0; i < vertexSize; i++) {
                vertices[currentElement * vertexSize + i] = Float.parseFloat(words[vertexIndex + i]);
                if (vertexMax < Math.abs(vertices[currentElement * vertexSize + i])) {
                    vertexMax = Math.abs(vertices[currentElement * vertexSize + i]);
                }
            }
            for (int i = 0; i < colorSize; i++) {
                colors[currentElement * colorSize + i] = Float.parseFloat(words[colorIndex + i]);
                if (colorMax < colors[currentElement * colorSize + i]) {
                    colorMax = colors[currentElement * colorSize + i];
                }
            }
            for (int i = 0; i < normalSize; i++) {
                normals[currentElement * normalSize + i] = Float.parseFloat(words[normalIndex + i]);
            }
            currentElement++;
        } else if (currentFace < faceCount) {
            for (int i = 0; i < 3; i++) {
                faces[currentFace * faceSize + i] = Integer.parseInt(words[i + 1]);
            }
            currentFace++;
        }
    }

    private void ScaleData() {
        for (int i = 0; i < vertexCount * vertexSize; i++) {
            if (i % 3 == 0 && vertices[i] > vertexXMax) {
                vertexXMax = vertices[i];
            }
            if (i % 3 == 0 && vertices[i] < vertexXMin) {
                vertexXMin = vertices[i];
            }
            if (i % 3 == 1 && vertices[i] > vertexYMax) {
                vertexYMax = vertices[i];
            }
            if (i % 3 == 1 && vertices[i] < vertexYMin) {
                vertexYMin = vertices[i];
            }
            if (i % 3 == 2 && vertices[i] > vertexZMax) {
                vertexZMax = vertices[i];
            }
            if (i % 3 == 2 && vertices[i] < vertexZMin) {
                vertexZMin = vertices[i];
            }
        }
        float midX = (vertexXMin + vertexXMax) / 2.0f;
        float midY = (vertexYMax + vertexYMin) / 2.0f;
        float midZ = (vertexZMax + vertexZMin) / 2.0f;
        float width = vertexXMax - vertexXMin;
        float height = vertexYMax - vertexYMin;
        float depth = vertexZMax - vertexZMin;
        float ratio = Math.max(width, Math.max(height, depth)) / 2.9f;

        //vertexMax *= 1.5f;
        for (int i = 0; i < vertexCount * vertexSize; i++) {
            if (i % 3 == 0) {
                vertices[i] -= midX;
                vertices[i] /= ratio;
            }
            if (i % 3 == 1) {
                vertices[i] -= midY;
                vertices[i] /= ratio;
            }
            if (i % 3 == 2) {
                vertices[i] -= midZ;
                vertices[i] /= ratio;
            }
        }

        for (int i = 0; i < vertexCount * colorSize; i++) {
            colors[i] /= colorMax;
        }
    }

    // Getters
    float[] getVertices() {
//        float[] coord = new float[faces.length * 3];
//        for (int i = 0; i < faces.length;) {
//            coord[i * 3] = vertices[faces[i] * 3];
//            coord[i * 3 + 1] = vertices[faces[i] * 3 + 1];
//            coord[i * 3 + 2] = vertices[faces[i] * 3 + 2];
//        }
//        return coord;
        return vertices;
    }
    int[] getFaces() {
        return faces;
    }
    float[] getMaxMin() {
        float[] maxMin = new float[6];
        maxMin[0] = vertexXMax;
        maxMin[1] = vertexXMin;
        maxMin[2] = vertexYMax;
        maxMin[3] = vertexYMin;
        maxMin[4] = vertexZMax;
        maxMin[5] = vertexZMin;
        return maxMin;
    }
}
