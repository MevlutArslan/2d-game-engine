package engine.rendering;


import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// https://ogldev.org/www/tutorial29/tutorial29.html
//public class PickingTexture {
//    // TODO : FRAMEBUFFER
//    private int fboId;
//    private int pickingTexture;
//    private int depthTexture;
//
//    public PickingTexture(int width, int height){
//
//    }
//
//    private boolean init(int width, int height){
////        fboId = glGenFramebuffers();
////        glBindFramebuffer(GL_FRAMEBUFFER, fboId);
////
////        // Create the texture object for the primitive information buffer
////        glGenTextures(1, pickingTexture);
////        glBindTexture(GL_TEXTURE_2D, pickingTexture);
////        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height,
////                0, GL_RGB, GL_FLOAT, NULL);
////        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
////                pickingTexture, 0);
////
////        // Create the texture object for the depth buffer
////        glGenTextures(1, depthTexture);
////        glBindTexture(GL_TEXTURE_2D, depthTexture);
////        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height,
////                0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
////        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D,
////                depthTexture, 0);
////
////        // Disable reading to avoid problems with older GPUs
////        glReadBuffer(GL_NONE);
////
////        glDrawBuffer(GL_COLOR_ATTACHMENT0);
////
////        // Verify that the FBO is correct
////        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
////
////        if (status != GL_FRAMEBUFFER_COMPLETE) {
////            System.out.println(("FB error, status: 0x%x\n" + status));
////            return false;
////        }
////
////        // Restore the default framebuffer
////        glBindTexture(GL_TEXTURE_2D, 0);
////        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//
//
//
//        return false;
//    }
//}
