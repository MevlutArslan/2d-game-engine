package engine.input;

import engine.Window;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    public static MouseListener mouseListener = null;

    // Apparently some mice can scroll horizontally... So I am adding a scrollX variable
    private double scrollX;
    private double scrollY;
    private double xPos;
    private double yPos;
    private double lastX;
    private double lastY;

    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private MouseListener() {
        //Important to initialize values if we are using them
        //as first frame can create problems with undefined variables
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;

        this.scrollY = 0.0;

    }

    public static MouseListener get() {
        if (MouseListener.mouseListener == null) {
            MouseListener.mouseListener = new MouseListener();
        }

        return MouseListener.mouseListener;
    }

    // https://stackoverflow.com/questions/7692988/opengl-math-projecting-screen-space-to-world-space-coords
    public static float getWorldCoordsX() {
        float currentX = getX();
        currentX = (currentX / (float) Window.getWidth()) * 2.0f - 1.0f;

//        Does not work due to the inplace modification done by mul() method
//        Matrix4f inverseMat = Window.getScene().getCamera().getInverseProjectionMatrix().mul(
//                Window.getScene().getCamera().getInverseViewMatrix()
//        );

        Vector4f vector = new Vector4f(currentX, 0, 0, 1);
        vector.mul(Window.getScene().getCamera().getInverseProjectionMatrix()).mul(Window.getScene().getCamera().getInverseViewMatrix());
        currentX = vector.x;

        return currentX;
    }

    // I don't need to modify the above method as described by the answer on stack overflow as
    // I dont want to invert it and am working in 2D space not 3D
    public static float getWorldCoordsY() {
        float currentY = getY();
        currentY = (currentY / (float) Window.getHeight()) * 2.0f - 1.0f;

        Vector4f vector = new Vector4f(0, currentY, 0, 1);
        vector.mul(Window.getScene().getCamera().getInverseProjectionMatrix()).mul(Window.getScene().getCamera().getInverseViewMatrix());
        currentY = vector.y;

        return currentY;
    }

    public static void mouseCursorPositionCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;

        // if the mouse is moving and any of the mouse buttons are down than it is dragging.
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    /**
     * void mouse_button_callback(GLFWwindow* window, int button, int action, int mods)
     **/
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;

        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float) (get().lastY - get().yPos);
    }

    public static float scrollX() {
        return (float) get().scrollX;
    }

    public static float scrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int buttonKey) {
        if (buttonKey < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[buttonKey];
        } else {
            return false;
        }
    }

}
