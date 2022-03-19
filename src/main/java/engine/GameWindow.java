package engine;

import engine.input.KeyListener;
import engine.input.MouseListener;
import engine.observers.Event;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.physics.Physics2d;
import engine.rendering.*;
import engine.scene.LevelEditorSceneInitializer;
import engine.scene.LevelSceneInitializer;
import engine.scene.Scene;
import engine.scene.SceneInitializer;
import engine.ui.ImGuiApp;
import engine.utility.AssetPool;
import engine.utility.Constants;
import engine.utility.file_utility.FileDialogManager;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import static engine.utility.Constants.MONITOR_HEIGHT;
import static engine.utility.Constants.MONITOR_WIDTH;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Window setup according to https://www.lwjgl.org/guide
 **/
public class GameWindow implements Observer {
    // Singleton
    private static GameWindow instance = null;

    private final double FIXED_TIME_STEP = 1.0 / 60.0;

    private int height;
    private int width;
    private String title;

    // The window handle
    private long window;

    // Scene Manager related
    private static Scene currentScene = null;

    private ImGuiApp imGuiApp;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;

    private Shader defaultShader;
    private Shader pickingShader;

    private boolean isEditorMode = true;

    private long audioContext;
    private long audioDevice;

    // Methods
    private GameWindow() {
        this.height = 720;
        this.width = 1200;
        this.title = "Game Engine";

        EventSystem.addObserver(this);
    }

    public static GameWindow get() {
        if (GameWindow.instance == null) {
            GameWindow.instance = new GameWindow();
        }
        return GameWindow.instance;
    }

    // TODO absolutely uselss method, restructure it
    public static void changeScene(SceneInitializer sceneInitializer) {
        if (currentScene != null) {
            currentScene.destroy();
        }

//        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        // TODO : Change load to loadDefault or use the Overriden method with defaultLevelSrc from project's settings
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }


    public static ImGuiApp getImGuiApp() {
        return get().imGuiApp;
    }

    public static void setScene(Scene scene) {
        currentScene = scene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        update();

        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

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

        // Audio stuff
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if(!alCapabilities.OpenAL10){
            System.err.println("Audio library not supported!");
            System.exit(-1);
        }
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


        frameBuffer = new FrameBuffer(MONITOR_WIDTH, MONITOR_HEIGHT);
        pickingTexture = new PickingTexture(MONITOR_WIDTH, MONITOR_HEIGHT);
        imGuiApp = new ImGuiApp(window, pickingTexture);

        glViewport(0, 0, MONITOR_WIDTH, MONITOR_HEIGHT);

        loadShaders();

        GameWindow.changeScene(new LevelEditorSceneInitializer());
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

            endTime = (float) glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
            lag += deltaTime;


            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            // Rendering the Mouse Picking Buffer
            drawMousePickingBuffer();

            currentScene.render();

            disableMousePicking();

            // Rendering the Game's real buffer
            DebugDraw.beginFrame();
            frameBuffer.bind();

            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);



            if (deltaTime >= 0) {
                // TODO FIX TWITCHING BUG
//                while (lag >= FIXED_TIME_STEP) {
                    if(isEditorMode){
                        currentScene.onUpdateEditor(deltaTime);
                    }else{
                        currentScene.update(deltaTime);
                    }
                    lag -= FIXED_TIME_STEP;
//                }

                Renderer.bindShader(defaultShader);
                currentScene.render();
                DebugDraw.draw();
            }

            frameBuffer.unbind();

            imGuiApp.update(deltaTime, currentScene);

            KeyListener.endFrame();
            MouseListener.endFrame();

            glfwSwapBuffers(window); // swap the color buffers
        }

    }

    public void drawMousePickingBuffer() {
        enableMousePicking();

        glViewport(0, 0, MONITOR_WIDTH, MONITOR_HEIGHT);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Renderer.bindShader(pickingShader);
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
            GameWindow.setWidth(newWidth);
            GameWindow.setHeight(newHeight);
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

    @Override
    public void onNotify(Entity entity, Event event) {
        switch (event.type) {
            case GAME_ENGINE_START_PLAY:
                this.isEditorMode = false;
                currentScene.save();
                // reset the scene
                GameWindow.changeScene(new LevelSceneInitializer());
                getImGuiApp().getPropertiesPanel().clearSelected();
                break;
            case GAME_ENGINE_STOP_PLAY:
                this.isEditorMode = true;
                // reset to the last saved state
                GameWindow.changeScene(new LevelEditorSceneInitializer());
                break;
            case SAVE_AS_LEVEL:
                FileDialogManager.saveFile();
                break;
            case SAVE_LEVEL:
                // save to currently loaded level, find a way to store it
                break;
            case LOAD_LEVEL:
                // TODO refactor this it conflicts with other load events because of the path
                String path = FileDialogManager.openFile(Constants.sceneFileType);
                Scene scene = new Scene(new LevelEditorSceneInitializer());
                scene.load(path);
                currentScene = scene;
                currentScene.init();
                currentScene.start();
                break;
            default:
                break;
        }
    }

    public static Scene getScene() {
        return GameWindow.currentScene;
    }

    public static int getHeight() {
        return MONITOR_HEIGHT;
    }

    public static int getWidth() {
        return MONITOR_WIDTH;
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

    public static Physics2d getPhysics(){
        return currentScene.getPhysics();
    }
}

