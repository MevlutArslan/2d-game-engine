package engine.rendering;

import engine.Window;
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

// https://github.com/codingminecraft/MarioYoutube/blob/6a7acf801b6831bfd6e6b462b274d091937cebc1/src/main/java/renderer/DebugDraw.java
public class DebugDraw {

    private static final int MAX_LINES = 500;
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

        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, lines.size()  * 6 * 2);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.unbind();
    }

    public static void addLine2d(Vector2f from, Vector2f to){
        addLine2d(from, to, new Vector3f(1,1,1), 1);
    }

    public static void addLine2d(Vector2f from, Vector2f to,
                                 Vector3f color){
        addLine2d(from, to, color, 1);
    }

    public static void addLine2d(Vector2f from, Vector2f to,
                                 Vector3f color, int lifeTime){
        if(lines.size() >= MAX_LINES)
            return;

        DebugDraw.lines.add(new Line2d(from, to, color, lifeTime));
    }
}
