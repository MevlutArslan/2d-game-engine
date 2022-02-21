package engine.input;

import engine.GameWindow;
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


    public static float getWorldCoordsX() {
        return (float) get().worldX;
    }

    // https://stackoverflow.com/questions/7692988/opengl-math-projecting-screen-space-to-world-space-coords
    private static void calcWorldCoordsX(){
        // subtracting the viewportPos.x from x gives us the start point for the viewport's accurate position
        float currentX = getX() - get().viewPortPos.x;
        currentX = (currentX / get().viewPortSize.x) * 2.0f - 1.0f;

        Matrix4f viewProjection = new Matrix4f();
        GameWindow.getScene().getCamera().getInverseViewMatrix()
                .mul(GameWindow.getScene().getCamera().getInverseProjectionMatrix(),viewProjection);

        Vector4f vector = new Vector4f(currentX, 0, 0, 1);
        vector.mul(viewProjection);

        get().worldX = vector.x;
    }

    // I don't need to modify the above method as described by the answer on stack overflow as
    // I dont want to invert it and am working in 2D space not 3D
    public static float getWorldCoordsY() {
        return (float) get().worldY;
    }

    private static void calcWorldCoordsY(){
        float currentY =  getY() - get().viewPortPos.y;
        currentY = -((currentY / get().viewPortSize.y) * 2.0f - 1.0f);

        Matrix4f viewProjection = new Matrix4f();
        GameWindow.getScene().getCamera().getInverseViewMatrix().mul(GameWindow.getScene().getCamera().getInverseProjectionMatrix(), viewProjection);

        Vector4f vector = new Vector4f(0, currentY, 0, 1);
        vector.mul(viewProjection);

        get().worldY = vector.y;
    }

    public static float getScreenX(){
        float currentX = getX() - get().viewPortPos.x;
        currentX = (currentX / get().viewPortSize.x) * 2560;
        return currentX;
    }

    public static float getScreenY(){
        float currentY =  getY() - get().viewPortPos.y;
        currentY = 1600 -((currentY / get().viewPortSize.y) * 1600);
        return currentY;
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
        calcWorldCoordsX();
        calcWorldCoordsY();


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

    public static float getWorldDx(){
        return (float) (get().lastWorldX - get().worldX);
    }

    public static float getWorldDy(){
        return (float) (get().lastWorldY - get().worldY);
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
