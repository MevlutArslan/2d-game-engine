package engine.rendering;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private String filepath;
    private int textureId;

    private int height;
    private int width;

    public void init(String filepath){
        this.filepath = filepath;

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // GL_TEXTURE_WRAP_S = X axis
        // GL_TEXTURE_WRAP_S = Y axis
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // https://learnopengl.com/Getting-started/Textures
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // when shrinking
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        // load image
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if(image != null){
            this.width = width.get(0);
            this.height = height.get(0);

            if(channels.get(0) == 3){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }
            else if(channels.get(0) == 4){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }
            else{
                System.err.println("Unknown number of channels !");
            }
        } else {
            System.err.println("Could not load image !");
        }

        stbi_image_free(image);


    }

    public int getHeight(){
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind(){
        glBindTexture(GL_TEXTURE_2D,0);
    }

    public int getTextureId(){
        return this.textureId;
    }
}
