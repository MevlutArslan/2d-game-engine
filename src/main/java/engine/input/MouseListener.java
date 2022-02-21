package engine.input;

import engine.GameWindow;
import engine.camera.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
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
    private double worldX;
    private double worldY;
    private double lastWorldX;
    private double lastWorldY;

    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private int mouseButtonsDown = 0;

    private Vector2f viewPortPos = new Vector2f();
    private Vector2f viewPortSize = new Vector2f();

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

    // https://www.youtube.com/watch?v=b_sNaZXQoG0&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=45
    // https://stackoverflow.com/questions/7692988/opengl-math-projecting-screen-space-to-world-space-coords
    public static Vector2f getWorldCoordinates(){
        float currentX = getX() - get().viewPortPos.x;
        float currentY =  getY() - get().viewPortPos.y;

        currentX = (currentX / get().viewPortSize.x) * 2.0f - 1.0f;
        currentY = -((currentY / get().viewPortSize.y) * 2.0f - 1.0f);

        Vector4f temp = new Vector4f(currentX, currentY, 0, 1);
        Camera camera = GameWindow.getScene().getCamera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());

        temp.mul(inverseView.mul(inverseProjection));

        return new Vector2f(temp.x, temp.y);
    }

    public static float getWorldCoordinateX(){
        return getWorldCoordinates().x;
    }

    public static float getWorldCoordinateY(){
        return getWorldCoordinates().y;
    }

    public static Vector2f getScreenCoordinates(){
        float currentX = getX() - get().viewPortPos.x;
        float currentY =  getY() - get().viewPortPos.y;

        currentX = (currentX / get().viewPortSize.x) * 2560;
        currentY = 1600 -((currentY / get().viewPortSize.y) * 1600);

        return new Vector2f(currentX, currentY);
    }

    public static float getScreenX(){
        return getScreenCoordinates().x;
    }

    public static float getScreenY(){
        return getScreenCoordinates().y;
    }


    public static void mouseCursorPositionCallback(long window, double xPos, double yPos) {
        if(get().mouseButtonsDown > 0){
            get().isDragging = true;
        }
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        get().xPos = xPos;
        get().yPos = yPos;
    }

    /**
     * void mouse_button_callback(GLFWwindow* window, int button, int action, int mods)
     **/
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().mouseButtonsDown++;
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            get().mouseButtonsDown--;
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

        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
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

    public static void setViewPortPos(Vector2f viewPortPos) {
        get().viewPortPos.set(viewPortPos);
    }

    public static void setViewPortSize(Vector2f viewPortSize) {
        get().viewPortSize.set(viewPortSize);
    }
}
