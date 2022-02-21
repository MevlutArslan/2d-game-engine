package engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    // Singleton
    private static KeyListener instance;

    /**
     * GLFW supports 349 keys
     * https://www.glfw.org/docs/3.3/group__keys.html
     * **/
    private final boolean[] keyPressed = new boolean[350];
    private final boolean[] keyBeginPress = new boolean[350];

    private KeyListener(){

    }

    public static KeyListener get(){
        if(KeyListener.instance == null){
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    /** Defined in the docs
     * void key_callback(GLFWwindow* window, int key, int scancode, int action, int mods)
     * {
     *     if (key == GLFW_KEY_E && action == GLFW_PRESS)
     *         activate_airship();
     * }
     *
     * mods = additional keys pressed with a key like CTRL + W
     * action = GLFW_PRESS, GLFW_REPEAT or GLFW_RELEASE
     *
     *
     * **/
    public static void keyCallback(long window, int key, int scanCode, int action, int mods){
        if(action == GLFW_PRESS){
            get().keyPressed[key] = true;
            get().keyBeginPress[key] = true;
        }
        else if(action == GLFW_RELEASE){
            get().keyPressed[key] = false;
            get().keyBeginPress[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode){
        if(keyCode < get().keyPressed.length){
            return get().keyPressed[keyCode];
        }
        else{
            throw new NullPointerException("This key is not supported!");
        }
    }

    public static boolean keyBeginPress(int keyCode){
        boolean result = get().keyBeginPress[keyCode];
        if(result){
            get().keyBeginPress[keyCode] = false;
        }
        return result;
    }
}
