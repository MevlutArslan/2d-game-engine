package engine.scene;

import engine.Window;
import engine.camera.Camera;
import engine.input.KeyListener;
import engine.rendering.Shader;
import engine.rendering.Texture;
import org.joml.Vector2f;
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
            // Vertex locations         // UV coordinates
            100.5f, 0.5f, 0.0f,         1, 1,           // 0  Bottom Right
            0.5f, 100.5f, 0.0f,         0, 0,           // 1  Top Left
            100.5f, 100.5f, 0.0f,       1, 0,           // 2  Top Right
            0.5f, 0.5f, 0.0f,           0, 1            // 3  Bottom Left
    };

    private int[] elementArray = new int[]{
            2, 1, 0,
            0, 1, 3
    };

    private Texture texture;

    public LevelEditorScene() {
        this.camera = new Camera(new Vector2f(-100,0));
    }

    @Override
    public void init() {
        defaultShader = new Shader("src/main/resources/basicShader.vertex", "src/main/resources/basicShader.fragment");
        defaultShader.compile();

        this.texture = new Texture("src/main/resources/textures/Wraith_01_Idle_000.png");


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
        int uvSize = 2;
        int totalSize = (positionSize + uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, totalSize, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, uvSize, GL_FLOAT, false, totalSize, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float deltaTime) {
        camera.cameraPosition.x += 10.0f * deltaTime;

        defaultShader.bind();

        defaultShader.uploadMat4f("uProjection",camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());

        //** Upload Texture to GPU **//
        // Upload slot 0 to Shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        // Activated slot 0
        glActiveTexture(GL_TEXTURE0);
        // put our texture to slot 0
        texture.bind();

        // Bind the VAO that we're using
        glBindVertexArray(vaoId);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1 );

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);


        defaultShader.unbind();
    }
}
