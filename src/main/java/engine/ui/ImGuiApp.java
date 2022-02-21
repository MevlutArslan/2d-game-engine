package engine.ui;

import engine.input.MouseListener;
import engine.rendering.PickingTexture;
import engine.scene.Scene;
import engine.ui.editor.EditorMenu;
import engine.ui.editor.menus.EditMenu;
import engine.ui.editor.menus.FileMenu;
import engine.ui.panels.ContentBrowserPanel;
import engine.ui.panels.PropertiesPanel;
import engine.utility.Constants;
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

    private EditorMenu editorMenu;
    private ImGuiContext context;

    private PropertiesPanel propertiesPanel;

    private ContentBrowserPanel contentBrowser;


    public void init() {
        context = ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        setDarkThemeColors();

        io.setIniFilename("imgui.ini");
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        io.setFontDefault(Constants.defaultFont);

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

    public ImGuiApp(long window, PickingTexture pickingTexture) {
        this.window = window;
        this.propertiesPanel = new PropertiesPanel(pickingTexture);
        this.contentBrowser = new ContentBrowserPanel("src/main/resources");

        this.editorMenu = new EditorMenu();
        this.editorMenu.addEditorMenu(new FileMenu());
        this.editorMenu.addEditorMenu(new EditMenu());

        this.init();
    }


    public void update(float deltaTime, Scene currentScene) {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
        enableDocking();
        ViewPortWindow.imgui();
        currentScene.imgui();

        propertiesPanel.update(deltaTime, currentScene);
        propertiesPanel.imgui();

        contentBrowser.update(deltaTime);
        contentBrowser.imgui();
        editorMenu.update(deltaTime);
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

//        float minWindowSizeX = ImGui.getStyle().getWindowMinSizeX();
        ImGui.getStyle().setWindowMinSize(370, 200);
        ImGui.begin("Docking", new ImBoolean(true), window_flags);

        // Assertion failed: (SizeOfStyleVarStack >= g.StyleVarStack.Size && "PushStyleVar/PopStyleVar Mismatch!")
        ImGui.popStyleVar(2);

        ImGui.dockSpace(ImGui.getID("Docking"));

    }

    private void setDarkThemeColors(){
        float[][] colors = ImGui.getStyle().getColors();

        colors[ImGuiCol.WindowBg] = new float[]{0.1f, 0.105f, 0.11f, 1.0f};

        colors[ImGuiCol.Header] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.HeaderHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.HeaderActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.Button] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.ButtonHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.ButtonActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.FrameBg] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.FrameBgActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.Tab] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TabHovered] = new float[]{0.38f, 0.3805f, 0.381f, 1.0f};
        colors[ImGuiCol.TabActive] = new float[]{0.28f, 0.2805f, 0.281f, 1.0f};
        colors[ImGuiCol.TabUnfocused] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};

        colors[ImGuiCol.TitleBg] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.95f, 0.1505f, 0.951f, 1.0f};

        ImGui.getStyle().setColors(colors);
    }

    public PropertiesPanel getPropertiesPanel() {
        return this.propertiesPanel;
    }
}
