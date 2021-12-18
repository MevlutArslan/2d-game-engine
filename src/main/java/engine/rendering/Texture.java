package engine.rendering;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private String filepath;
    private transient int textureId;

    private int height;
    private int width;

    // https://www.youtube.com/watch?v=lALvR4j6RCM&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=28
    /*
        Because we are creating a new constructor for the framebuffer that takes in a width and height
        we need to have a default one that does nothing and will give an error if anyone tries to use it.
     */
    public Texture(){
        textureId = -1;
        width = -1;
        height = -1;
    }

    // SHOULD ONLY USE FOR THE FRAMEBUFFER!
    public Texture(int width, int height){
        this.filepath = "Generated!";

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

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

            stbi_image_free(image);
        } else {
            System.err.println("Could not load image !");
        }
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

    public String getFilepath(){
        return this.filepath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        return texture.getWidth() == this.width && texture.getHeight() == this.height &&
                texture.getTextureId() == this.getTextureId() &&
                texture.getFilepath().equals(this.filepath);
    }

}
