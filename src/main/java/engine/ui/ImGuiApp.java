package engine.ui;

import engine.input.MouseListener;
import engine.scene.Scene;
import engine.ui.editor.EditorMenu;
import engine.ui.editor.menus.EditMenu;
import engine.ui.editor.menus.FileMenu;
import imgui.*;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiContext;
import imgui.type.ImBoolean;


import static org.lwjgl.glfw.GLFW.*;

//https://blog.conan.io/2019/06/26/An-introduction-to-the-Dear-ImGui-library.html
public class ImGuiApp {

    private long window = 0;
    private final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();

    private static ImGuiApp instance = null;
    private EditorMenu editorMenu;
    private ImGuiContext context;

    public void init() {
        context = ImGui.createContext();
        ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini");
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        glfwSetScrollCallback(window, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);

            MouseListener.mouseScrollCallback(window, xOffset, yOffset);
        });


        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }

            if (!io.getWantCaptureMouse() || ViewPortWindow.getWantCaptureMouse()) {
                MouseListener.mouseButtonCallback(w, button, action, mods);
            }
        });

        // Setup Platform/Renderer bindings
        imGuiImplGlfw.init(window, true);
        imGuiImplGl3.init("#version 330");
    }

    private ImGuiApp(long window) {
        this.window = window;
        this.editorMenu = new EditorMenu();
        this.editorMenu.addEditorMenu(new FileMenu());
        this.editorMenu.addEditorMenu(new EditMenu());

        this.init();
    }

    public static ImGuiApp get(long window) {
        if (instance == null) {
            ImGuiApp.instance = new ImGuiApp(window);
            return ImGuiApp.instance;
        }
        return ImGuiApp.instance;
    }

    public void update(float deltaTime, Scene currentScene) {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
        enableDocking();
        ViewPortWindow.imgui();
        currentScene.imgui();
//        if (currentScene.getClass() == LevelEditorScene.class) {
            editorMenu.update(deltaTime);
//        }
        ImGui.end();
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }

    public void dispose() {
        imGuiImplGlfw.dispose();
        imGuiImplGl3.dispose();
        ImGui.destroyContext();
    }

    // https://skia.googlesource.com/external/github.com/ocornut/imgui/+/refs/heads/docking/imgui_demo.cpp
    private void enableDocking() {
        int window_flags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGuiViewport viewport = ImGui.getMainViewport();

        ImGui.setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
        ImGui.setNextWindowSize(viewport.getWorkSizeX(), viewport.getWorkSizeY());
        ImGui.setNextWindowViewport(viewport.getID());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);

        window_flags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Docking", new ImBoolean(true), window_flags);

        // Assertion failed: (SizeOfStyleVarStack >= g.StyleVarStack.Size && "PushStyleVar/PopStyleVar Mismatch!")
        ImGui.popStyleVar(2);

        ImGui.dockSpace(ImGui.getID("Docking"));

    }
}
