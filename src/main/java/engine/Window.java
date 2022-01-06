package engine;

import components.NonPickable;
import engine.input.KeyListener;
import engine.input.MouseListener;
import engine.rendering.*;
import engine.scene.LevelEditorScene;
import engine.scene.LevelScene;
import engine.scene.Scene;
import engine.ui.ImGuiApp;
import engine.utility.AssetPool;
import engine.utility.Constants;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.opengl.GL;

import static engine.utility.Constants.MONITOR_HEIGHT;
import static engine.utility.Constants.MONITOR_WIDTH;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Window setup according to https://www.lwjgl.org/guide
 **/
public class Window {
    // Singleton
    private static Window instance = null;

    private final double FIXED_TIME_STEP = 1.0 / 60.0;

    private int height;
    private int width;
    private String title;

    // The window handle
    private long window;

    // Scene Manager related
    private static Scene currentScene = null;

    // testing things
    public int r, g, b;

    private ImGuiApp imGuiApp;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;

    private Shader defaultShader;
    private Shader pickingShader;

    private float debounce = 0.2f;

    // Methods
    private Window() {
        this.height = 720;
        this.width = 1200;
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

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0 -> {
                currentScene = new LevelEditorScene();
                currentScene.load();
                currentScene.init();
                currentScene.start();
            }
            case 1 -> {
                currentScene = new LevelScene();
                currentScene.load();
                currentScene.init();
                currentScene.start();
            }
            default -> System.err.println("Unknown Scene");
        }
    }

    public static Scene getScene() {
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

    private void init() {
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        configureWindowHints();

        // Create the window
        window = glfwCreateWindow(get().width, get().height, get().title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        configureGlfwCallbacks();

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
        // https://learnopengl.com/Advanced-OpenGL/Blending
        glEnable(GL_BLEND);
        /* FORMULA : Cresult=Csource∗Fsource+Cdestination∗Fdestination
         *  C¯source: the source color vector. This is the color output of the fragment shader.
         *  C¯destination: the destination color vector. This is the color vector that is currently stored in the color buffer.
         *  Fsource: the source factor value. Sets the impact of the alpha value on the source color.
         *  Fdestination: the destination factor value. Sets the impact of the alpha value on the destination color.
         */
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        imGuiApp = ImGuiApp.get(window);
        frameBuffer = new FrameBuffer(MONITOR_WIDTH, MONITOR_HEIGHT);
        pickingTexture = new PickingTexture(MONITOR_WIDTH, MONITOR_HEIGHT);
        glViewport(0, 0, MONITOR_WIDTH, MONITOR_HEIGHT);

        loadShaders();

        Window.changeScene(0);
    }

    public void update() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float lag = 0.0f;

        float deltaTime = -1.0f;
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            System.out.println("FPS : " + (1.0f / deltaTime));

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            // Rendering the Mouse Picking Buffer
            drawMousePickingBuffer();


            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
                int x = (int) MouseListener.getScreenX();
                int y = (int) MouseListener.getScreenY();
                Entity pickedEntity = currentScene.getEntityById(pickingTexture.readPixel(x, y));
                // TODO : Fix overlapping gizmo picks
                if (pickedEntity != null && pickedEntity.getComponent(NonPickable.class) == null && !MouseListener.isDragging()) {
                    currentScene.selectEntity(pickedEntity);
                } else if (pickedEntity == null && !MouseListener.isDragging()) {
                    currentScene.selectEntity(null);
                }


                this.debounce = 0.2f;
            }

            disableMousePicking();

            // Rendering the Game's real buffer
            DebugDraw.beginFrame();
            frameBuffer.bind();

            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            endTime = (float) glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
            lag += deltaTime;

            if (deltaTime >= 0) {
                DebugDraw.draw();
                currentScene.update(deltaTime);

                Renderer.bindShader(defaultShader);
                currentScene.render();
                while (lag >= FIXED_TIME_STEP) {

                    lag -= FIXED_TIME_STEP;
                }
            }

            frameBuffer.unbind();

            imGuiApp.update(deltaTime, currentScene);

            debounce -= deltaTime;

            glfwSwapBuffers(window); // swap the color buffers

            MouseListener.endFrame();
        }

        currentScene.save();
    }

    public void drawMousePickingBuffer() {
        enableMousePicking();

        glViewport(0, 0, MONITOR_WIDTH, MONITOR_HEIGHT);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Renderer.bindShader(pickingShader);
        currentScene.render();
    }

    private void enableMousePicking() {
        glDisable(GL_BLEND);
        pickingTexture.enableWriting();
    }

    private void disableMousePicking() {
        glEnable(GL_BLEND);
        pickingTexture.disableWriting();
    }

    public GLFWWindowCloseCallbackI handleWindowClose(long glfwWindow) {
        if (glfwWindowShouldClose(glfwWindow)) {

            imGuiApp.dispose();
            glfwTerminate();
        }

        return null;
    }

    public static int getHeight() {
        return get().height;
    }

    public static int getWidth() {
        return get().width;
    }

    private static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    private static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static FrameBuffer getFramebuffer() {
        return get().frameBuffer;
    }

    private void configureWindowHints() {
        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

    }

    private void configureGlfwCallbacks() {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, KeyListener::keyCallback);
        // call the function whenever there is a mouse event
        // the '::' syntax is shorthand for () -> {}
        glfwSetCursorPosCallback(window, MouseListener::mouseCursorPositionCallback);
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);

//        glfwSetWindowCloseCallback(window, handleWindowClose(window));
        glfwSetWindowSizeCallback(window, (window, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

    }

    private void loadShaders() {
        defaultShader = AssetPool.getShader(new String[]{
                "src/main/resources/basicShader.vertex",
                "src/main/resources/basicShader.fragment"
        });

        pickingShader = AssetPool.getShader(new String[]{
                "src/main/resources/shaders/mousePickingShaders/mousePicking.vertex",
                "src/main/resources/shaders/mousePickingShaders/mousePicking.fragment"
        });
    }
}

