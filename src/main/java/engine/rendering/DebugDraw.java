package engine.rendering;

import engine.GameWindow;
import engine.camera.Camera;
import engine.utility.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import engine.utility.JMath;

// https://github.com/codingminecraft/MarioYoutube/blob/6a7acf801b6831bfd6e6b462b274d091937cebc1/src/main/java/renderer/DebugDraw.java
public class DebugDraw {

    private static final int MAX_LINES = 5000;
    public static List<Line2d> lines = new ArrayList<>();

    // 2 floats for coords, 3 floats for color , 1 int for lifetime = 6
    // 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader(new String[]{"src/main/resources/shaders/debugShaders/debug.vertex", "src/main/resources/shaders/debugShaders/debug.fragment"});

    private static int vaoId;
    private static int vboId;

    private static boolean started = false;

    public static void start() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if (lines.size() <= 0)
            return;

        int index = 0;
        for (Line2d line : lines) {
            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0,
                Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        shader.compile();
        shader.bind();

        shader.uploadMat4f("uProjection", GameWindow.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", GameWindow.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, lines.size());

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.unbind();
    }

    public static void addLine2d(Vector2f from, Vector2f to) {
        addLine2d(from, to, new Vector3f(1, 1, 1), 1);
    }

    public static void addLine2d(Vector2f from, Vector2f to,
                                 Vector3f color) {
        addLine2d(from, to, color, 1);
    }

    public static void addLine2d(Vector2f from, Vector2f to, Vector3f color, int lifeTime) {
        Camera camera = GameWindow.getScene().getCamera();
        Vector2f cameraLeft = new Vector2f(camera.cameraPosition).add(new Vector2f(-2.0f,-2.0f));
        Vector2f cameraRight = new Vector2f(camera.cameraPosition).add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoomLevel())).add(new Vector2f(4.0f, 4.0f));
        boolean lineInView = ((from.x >= cameraLeft.x && from.x <= cameraRight.x) && (from.y >= cameraLeft.y && from.y <= cameraRight.y)) ||
                        ((to.x >= cameraLeft.x && to.x <= cameraRight.x) && (to.y >= cameraLeft.y && to.y <= cameraRight.y));

        if (lines.size() >= MAX_LINES || !lineInView) {
            return;
        }

        DebugDraw.lines.add(new Line2d(from, to, color, lifeTime));
    }

    public static void drawSquare(Vector2f pos, Vector2f dimensions, float rotation){
        drawSquare(pos, dimensions, rotation, new Vector3f(0, 1, 0), 1);
    }
    // https://www.youtube.com/watch?v=mUVVcCxf9wQ&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=27
    public static void drawSquare(Vector2f pos, Vector2f dimensions, float rotation, Vector3f color, int lifetime) {
        // if I dont put it in a new Vector2f it will modify the existing one which we definetly
        // dont want to modify the origin of the square
        Vector2f bottomLeft = new Vector2f(pos).sub(new Vector2f(dimensions).mul(0.5f));
        Vector2f topRight = new Vector2f(pos).add(new Vector2f(dimensions).mul(0.5f));

        Vector2f[] vertices = {
                // Bottom Left
                new Vector2f(bottomLeft.x, bottomLeft.y),
                // Bottom Right
                new Vector2f(topRight.x, bottomLeft.y),
                // Top Right
                new Vector2f(topRight.x, topRight.y),
                // Top Left
                new Vector2f(bottomLeft.x, topRight.y)
        };

        // if there is a rotation
        if (rotation != 0) {
            for (Vector2f vertex : vertices) {
                JMath.rotate(vertex, rotation, pos);
            }
        }

        DebugDraw.addLine2d(vertices[0], vertices[1], color, lifetime);
        DebugDraw.addLine2d(vertices[1], vertices[2], color, lifetime);
        DebugDraw.addLine2d(vertices[2], vertices[3], color, lifetime);
        DebugDraw.addLine2d(vertices[3], vertices[0], color, lifetime);
    }

    public static void drawCircle(Vector2f pos, float radius){
        drawCircle(pos, radius, new Vector3f(0,1,0),1);
    }
    
    // https://www.youtube.com/watch?v=mUVVcCxf9wQ&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=27
    public static void drawCircle(Vector2f pos, float radius, Vector3f color, int lifetime) {
        final int NUMBER_OF_VERTICES = 12;
        Vector2f[] vertices = new Vector2f[NUMBER_OF_VERTICES];

        // The angle to increase by is 360/ number of vertices we want
        int increaseAngle = 360 / NUMBER_OF_VERTICES;

        int currentAngle = 0;

        for(int i = 0; i < NUMBER_OF_VERTICES; i++){
            // the first point is radius(0 + radius = radius) , 0
            Vector2f point = new Vector2f(0,radius);

            JMath.rotate(point, currentAngle, new Vector2f());
            vertices[i] = new Vector2f(point).add(pos);

            if(i > 0){
                addLine2d(vertices[i-1], vertices[i], color, lifetime);
            }

            currentAngle += increaseAngle;
        }

        // to draw the last line from last - 1 to first vertex
        addLine2d(vertices[NUMBER_OF_VERTICES - 1], vertices[0], color, lifetime);


    }
}
