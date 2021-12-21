package engine.rendering;


import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// https://ogldev.org/www/tutorial29/tutorial29.html
public class PickingTexture {
    // TODO : FRAMEBUFFER
    private int fboId;
    private int pickingTextureId;
    private int depthTextureId;

    public PickingTexture(int width, int height){
        this.init(width, height);
    }

    private boolean init(int width, int height){
        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        // Create the texture object for the primitive information buffer
        pickingTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, pickingTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height,
                0, GL_RGB, GL_FLOAT, NULL);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                pickingTextureId, 0);

        // Create the texture object for the depth buffer
        depthTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height,
                0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D,
                depthTextureId, 0);

        // Disable reading to avoid problems with older GPUs
        glReadBuffer(GL_NONE);

        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        // Verify that the FBO is correct
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

        if (status != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println(("FB error, status: 0x%x\n" + status));
            return false;
        }

        // Restore the default framebuffer
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return true;
    }

    public void enableWriting(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboId);
    }

    public void disableWriting(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public int readPixel(int x, int y){
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboId);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3];
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

        return (int)(pixels[0] - 1);
    }


}
