package engine.scene;

import engine.Window;
import engine.input.KeyListener;
import engine.rendering.Shader;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.memFree;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    private Shader defaultShader;

    private int vaoId;
    private int vboId;
    private int eboId;

    private float[] vertices = new float[]{
            0.5f, -0.5f, 0.0f,  // 0
            -0.5f, 0.5f, 0.0f, // 1
            0.5f, 0.5f, 0.0f,  // 2
            -0.5f, -0.5f, 0.0f, // 3
    };

    private int[] elementArray = new int[]{
            2, 1, 0,
            0, 1, 3
    };

    public LevelEditorScene() {
        System.out.println("Inside level editor scene");
    }

    @Override
    public void init() {
        defaultShader = new Shader("src/main/resources/basicShader.vertex", "src/main/resources/basicShader.fragment");
        defaultShader.compile();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        memFree(vertexBuffer);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int positionSize = 3;
        int floatSize = Float.BYTES;
        int totalSize = positionSize * floatSize;


        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, totalSize, 0);
        glEnableVertexAttribArray(0);
    }

    @Override
    public void update(float deltaTime) {
        defaultShader.bind();

        // Bind the VAO that we're using
        glBindVertexArray(vaoId);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
        // Unbind everything
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);


        defaultShader.unbind();
    }
}
