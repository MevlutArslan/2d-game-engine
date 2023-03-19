package engine.rendering;

import static org.lwjgl.opengl.GL30.*;

// https://learnopengl.com/Advanced-OpenGL/Framebuffers
public class FrameBuffer {

    private int fboId = 0;
    private Texture texture = null;

    public FrameBuffer(int width, int height){
        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D, this.texture.getTextureId(), 0);

        int rboId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboId);


        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            throw new IllegalCallerException("Error with the framebuffer!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getTextureId(){
        return this.texture.getTextureId();
    }
}
