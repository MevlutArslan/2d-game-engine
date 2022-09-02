package engine;

import engine.input.KeyListener;
import engine.input.MouseListener;
import engine.observers.Event;
import engine.observers.EventSystem;
import engine.observers.Observer;
import engine.rendering.*;
import engine.scene.LevelEditorSceneInitializer;
import engine.scene.LevelSceneInitializer;
import engine.scene.Scene;
import engine.scene.SceneInitializer;
import engine.ui.ImGuiApp;
import engine.utility.AssetPool;
import engine.utility.Constants;
import engine.utility.file_utility.FileDialogManager;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import engine.project_manager.Project;

import static engine.utility.Constants.MONITOR_HEIGHT;
import static engine.utility.Constants.MONITOR_WIDTH;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Window setup according to https://www.lwjgl.org/guide
 **/
public class ToolboxEditor implements Observer {
    // Singleton
    private static ToolboxEditor instance = null;

    private final double FIXED_TIME_STEP = 1.0 / 60.0;

    private int editorWindowHeight;
    private int editorWindowWidth;
    private int projectManagerWindowHeight;
    private int projectManagerWindowWidth;

    private String title = "Toolbox Editor";

    // The window handle
    private long window;
    private long mainWindow;

    // Scene Manager related
    private static Scene currentScene = null;

    private static Project activeProject;

    private ImGuiApp imGuiApp;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;

    private Shader defaultShader;
    private Shader pickingShader;

    private boolean isPlaying = false;

    private long audioContext;
    private long audioDevice;

    private boolean projectIsLoaded = false;

    // Methods
    private ToolboxEditor() {
        this.editorWindowHeight = 720;
        this.editorWindowWidth = 1200;
        this.projectManagerWindowHeight = 550;
        this.projectManagerWindowWidth = 700;

        EventSystem.addObserver(this);
    }

    public static ToolboxEditor get() {
        if (ToolboxEditor.instance == null) {
            ToolboxEditor.instance = new ToolboxEditor();
        }
        return ToolboxEditor.instance;
    }

    // TODO absolutely useless method, restructure it
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

    private void openProject() {
        imGuiApp.dispose();

        configureWindowHints();

        mainWindow = glfwCreateWindow(get().editorWindowWidth, get().editorWindowHeight, get().title, NULL, window);
        if (mainWindow == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwDestroyWindow(window);

        window = mainWindow;

        configureGlfwCallbacks();

        glfwMakeContextCurrent(window);

        glfwShowWindow(window);

        imGuiApp = new ImGuiApp(window, pickingTexture);

        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        glfwSetCursorPosCallback(window, MouseListener::mouseCursorPositionCallback);
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

//        configureWindowHints();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        // Create the window
        window = glfwCreateWindow(700, 550, "Editor", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

//        configureGlfwCallbacks();

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

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

        if (!alCapabilities.OpenAL10) {
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

        loadShaders();

        frameBuffer = new FrameBuffer(MONITOR_WIDTH, MONITOR_HEIGHT);
        pickingTexture = new PickingTexture(MONITOR_WIDTH, MONITOR_HEIGHT);
        glViewport(0, 0, MONITOR_WIDTH, MONITOR_HEIGHT);

        imGuiApp = new ImGuiApp(window, pickingTexture);

        // NOTE : Loading project specific scenes needs to happen here.
        ToolboxEditor.changeScene(new LevelEditorSceneInitializer());
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
            if (projectIsLoaded) {
                drawMousePickingBuffer();

                currentScene.render();

                disableMousePicking();

                // Rendering the Game's real buffer
                DebugDraw.beginFrame();
                frameBuffer.bind();

                glClearColor(1, 1, 1, 1);
                glClear(GL_COLOR_BUFFER_BIT);

                // TODO : fix update method
                if (deltaTime >= 0) {
                    // TODO FIX TWITCHING BUG
                    Renderer.bindShader(defaultShader);
//                    while (lag >= FIXED_TIME_STEP) {
                    if (isPlaying) {
                        currentScene.update(deltaTime);
                    } else {
                        currentScene.onUpdateEditor(deltaTime);
                    }
//                        lag -= FIXED_TIME_STEP;
//                    }

                    currentScene.render();
                    DebugDraw.draw();
                }
                frameBuffer.unbind();

                imGuiApp.update(deltaTime, currentScene);
            } else {
                imGuiApp.update(deltaTime);
            }

            KeyListener.endFrame();
            MouseListener.endFrame();

            glfwSwapBuffers(window); // swap the color buffers
        }
    }

    public void drawMousePickingBuffer() {
        enableMousePicking();

        glViewport(0, 0, MONITOR_WIDTH, MONITOR_HEIGHT);
        glClearColor(0, 0, 0, 0);
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
            ImGui.destroyContext();
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
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);

        glfwSetWindowSizeCallback(window, (window, newWidth, newHeight) -> {
            ToolboxEditor.setEditorWindowWidth(newWidth);
            ToolboxEditor.setEditorWindowHeight(newHeight);
        });

        glfwSetWindowCloseCallback(window, handleWindowClose(window));
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
                this.isPlaying = true;
                currentScene.save();
                // reset the scene
                ToolboxEditor.changeScene(new LevelSceneInitializer());
                getImGuiApp().getPropertiesPanel().clearSelected();
                break;
            case GAME_ENGINE_STOP_PLAY:
                this.isPlaying = false;
                // reset to the last saved state
                ToolboxEditor.changeScene(new LevelEditorSceneInitializer());
                break;
            case SAVE_AS_LEVEL:
                FileDialogManager.saveFile();
                break;
            case SAVE_LEVEL:
                // save to currently loaded level, find a way to store it
                break;
            case LOAD_LEVEL:
                // TODO refactor this it conflicts with other load events because of the path
                String path = FileDialogManager.openFile(Constants.SCENE_FILE_EXTENSION);
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
        return ToolboxEditor.currentScene;
    }

    public static int getHeight() {
        return MONITOR_HEIGHT;
    }

    public static int getWidth() {
        return MONITOR_WIDTH;
    }

    private static void setEditorWindowWidth(int newWidth) {
        get().editorWindowWidth = newWidth;
    }

    private static void setEditorWindowHeight(int newHeight) {
        get().editorWindowHeight = newHeight;
    }

    public int getProjectManagerWindowHeight() {
        return projectManagerWindowHeight;
    }

    public void setProjectManagerWindowHeight(int projectManagerWindowHeight) {
        this.projectManagerWindowHeight = projectManagerWindowHeight;
    }

    public int getProjectManagerWindowWidth() {
        return projectManagerWindowWidth;
    }

    public void setProjectManagerWindowWidth(int projectManagerWindowWidth) {
        this.projectManagerWindowWidth = projectManagerWindowWidth;
    }

    public static FrameBuffer getFramebuffer() {
        return get().frameBuffer;
    }

    public static Project getProject() {
        return activeProject;
    }

    public static void setProject(Project project) {
        activeProject = project;
    }


    public static ImGuiApp getImGuiApp() {
        return get().imGuiApp;
    }

    public static void setScene(Scene scene) {
        currentScene = scene;
    }

    public void loadProject(Project project) {
        projectIsLoaded = true;
        openProject();
    }
}

