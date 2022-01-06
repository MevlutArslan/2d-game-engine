package engine.rendering;

import components.rendering.SpriteRenderer;
import engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex
    // ------
    // Position                      Color                           TexCoords        TexId
    // float, float                  float, float, float, float      float, float     float
    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXTURE_COORDS_SIZE = 2;
    private final int TEXTURE_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    // offsets are in bytes
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDS_OFFSET + TEXTURE_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEXTURE_ID_OFFSET + TEXTURE_ID_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numberOfSprites;
    private boolean hasRoom;

    private float[] vertices;
    private int vaoId;
    private int vboId;

    private int maxBatchSize;


    private int zIndex;

    private List<Texture> textures;

    private int[] textureSlots = {
            0, 1, 2, 3, 4, 5, 6, 7
    };

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.zIndex = zIndex;
        this.maxBatchSize = maxBatchSize;

        this.sprites = new SpriteRenderer[this.maxBatchSize];

        // 4 vertices per tile/quad
        this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numberOfSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    // GPU STUFF IN HERE
    public void start() {
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

        glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);

    }


    public void render() {
        boolean rebufferNeeded = false;
        for (int i = 0; i < numberOfSprites; i++) {
            if (sprites[i].hasChanged()) {
                loadVertexPropertiesIndex(i);
                sprites[i].setHasChangedToFalse();
                rebufferNeeded = true;

            }
        }
        if (rebufferNeeded) {
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            // I will buffer some data into vbo, the vertices starting from 0..
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        Shader shader = Renderer.getCurrentShader();
        shader.bind();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());

        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        // bind textures
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoId);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glDrawElements(GL_TRIANGLES, this.numberOfSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }

        shader.unbind();
    }

    private void loadVertexPropertiesIndex(int index) {
        SpriteRenderer sprite = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTextureCoords();

        int texId = 0;
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(sprite.getTexture())) {
                    texId = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = sprite.parent.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(sprite.parent.transform.position.x, sprite.parent.transform.position.y, 0);
            // rotate on z axis to get a 2d rotation
            transformMatrix.rotate(
                    (float) Math.toRadians(sprite.parent.transform.rotation), 0, 0, 1
            );

            transformMatrix.scale(
                    sprite.parent.transform.scale.x, sprite.parent.transform.scale.y, 1
            );
        }
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            Vector4f currentPos = new Vector4f(
                    sprite.parent.transform.position.x +
                    (xAdd * sprite.parent.transform.scale.x),
                    sprite.parent.transform.position.y +
                    (yAdd * sprite.parent.transform.scale.y),
                    0, 1);

            if(isRotated){
                currentPos = new Vector4f(xAdd, yAdd, 0, 1 ).mul(transformMatrix);
            }
            // load position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            // load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // load texture
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // load textureId
            vertices[offset + 8] = texId;

            // load entity id
            vertices[offset + 9] = sprite.parent.getEntityId() + 1;

            offset += VERTEX_SIZE;
        }
    }

    public void addSprite(SpriteRenderer sprite) {
        int index = this.numberOfSprites;
        this.sprites[index] = sprite;
        this.numberOfSprites++;

        if (sprite.getTexture() != null) {
            if (!this.textures.contains(sprite.getTexture())) {
                textures.add(sprite.getTexture());
            }
        }

        loadVertexPropertiesIndex(index);

        if (numberOfSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad, 3 indices per triange
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    // NO idea how this works...
    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;

    }

    public boolean hasTexture(Texture texture) {
        return this.textures.contains(texture);
    }

    public boolean hasTextureRoom() {
        // 8 being the maximum number of textures we have on the gpu
        return this.textures.size() < 8;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public int getzIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
