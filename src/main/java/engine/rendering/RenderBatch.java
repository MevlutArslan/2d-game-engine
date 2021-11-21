package engine.rendering;

import components.rendering.SpriteRenderer;
import engine.Window;
import engine.utility.AssetPool;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    // Vertex
    // ------
    // Position                      Color
    // float, float                  float, float, float, float
    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;

    // offsets are in bytes
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numberOfSprites;
    private boolean hasRoom;

    private float[] vertices;
    private int vaoId;
    private int vboId;

    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize){
        this.maxBatchSize = maxBatchSize;
        this.shader =
                AssetPool.getShader(
                new String[]{
                "src/main/resources/basicShader.vertex",
                "src/main/resources/basicShader.fragment"
        });

        this.sprites = new SpriteRenderer[this.maxBatchSize];

        // 4 vertices per tile/quad
        this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numberOfSprites = 0;
        this.hasRoom = true;
    }

    // GPU STUFF IN HERE
    public void start(){
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        // Our vertices can change in the future so
        // GL_DYNAMIC_DRAW tells the GPU I will be changing the vertices in the futures and make sure
        // they are stored in a place where we can efficiently read and write to.
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboId = glGenBuffers();

        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        // we will never change indices so we can use GL_STATIC_DRAW
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

    }


    public void render(){
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        // I will buffer some data into vbo, the vertices starting from 0..
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);


        shader.bind();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());

        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numberOfSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.unbind();
    }

    private void loadVertexPropertiesIndex(int index){
        SpriteRenderer sprite = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for(int i = 0; i < 4; i++){
            if(i == 1){
                yAdd = 0.0f;
            }
            else if(i == 2){
                xAdd = 0.0f;
            }
            else if(i == 3){
                yAdd = 1.0f;
            }

            vertices[offset] = sprite.parent.transform.position.x +
                               (xAdd * sprite.parent.transform.scale.x);
            vertices[offset+1] = sprite.parent.transform.position.y +
                               (yAdd * sprite.parent.transform.scale.y);

            vertices[offset+2] = color.x;
            vertices[offset+3] = color.y;
            vertices[offset+4] = color.z;
            vertices[offset+5] = color.w;

            offset += VERTEX_SIZE;

        }
    }

    public void addSprite(SpriteRenderer sprite){
        int index = this.numberOfSprites;
        this.sprites[index] = sprite;
        this.numberOfSprites ++;

        loadVertexPropertiesIndex(index);

        if(numberOfSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }

    private int[] generateIndices(){
        // 6 indices per quad, 3 indices per triange
        int[] elements = new int[6 * maxBatchSize];
        for(int i = 0; i < maxBatchSize; i++){
            loadElementIndices(elements, i);
        }

        return elements;
    }

    // NO idea how this works...
    private void loadElementIndices(int[] elements, int index){
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex+1] = offset + 2;
        elements[offsetArrayIndex+2] = offset + 0;

        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;

    }


    public boolean hasRoom(){
        return this.hasRoom;
    }
}
