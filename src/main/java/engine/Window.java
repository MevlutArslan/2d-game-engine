package engine;

import engine.input.KeyListener;
import engine.input.MouseListener;
import engine.scene.LevelEditorScene;
import engine.scene.LevelScene;
import engine.scene.Scene;
import engine.utility.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/** Window setup according to https://www.lwjgl.org/guide **/
public class Window {
    // Singleton
    private static Window instance = null;

    private final double FIXED_TIME_STEP = 1.0/60.0;

    private int height;
    private int width;
    private String title;

    // The window handle
    private long window;

    // Scene Manager related
    private static Scene currentScene = null;

    // testing things
    public int r,g,b;

    // Methods
    private Window() {
        this.height = 1080;
        this.width = 1920;
        this.title = "Game Engine";

        r = 1;
        g = 1;
        b = 1;
    }

    public static Window get() {
        if (Window.instance == null) {
            Window.instance = new Window();
        }
        return Window.instance;
    }

    public static void changeScene(int newScene){
        switch (newScene) {
            case 0 -> {
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
            }
            case 1 -> {
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
            }
            default -> System.err.println("Unknown Scene");
        }
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        update();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init(){
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(get().width, get().height, get().title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, KeyListener::keyCallback);

        // call the function whenever there is a mouse event
        // the '::' syntax is shorthand for () -> {}
        glfwSetCursorPosCallback(window, MouseListener::mouseCursorPositionCallback);
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);

        glfwSetWindowCloseCallback(window, handleWindowClose(window));

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void update() {
        float beginTime = (float)glfwGetTime();
        float endTime;
        float lag = 0.0f;

        float deltaTime = -1.0f;
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            System.out.println("FPS : " + (1.0f/deltaTime));

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            glClearColor(0, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT);


            endTime = (float)glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
            lag += deltaTime;

            while(lag >= FIXED_TIME_STEP){
                lag -= FIXED_TIME_STEP;
            }

            if(deltaTime >= 0){
                currentScene.update(deltaTime);
            }

            glfwSwapBuffers(window); // swap the color buffers

        }
    }

    public GLFWWindowCloseCallbackI handleWindowClose(long glfwWindow){
        if(glfwWindowShouldClose(glfwWindow)){
            glfwTerminate();
        }

        return null;
    }
}

