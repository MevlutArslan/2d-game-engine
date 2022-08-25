package project_manager;

import engine.input.MouseListener;
import engine.ui.ViewPortPanel;
import engine.utility.Constants;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiContext;
import imgui.type.ImBoolean;

import static org.lwjgl.glfw.GLFW.*;

public class ProjectManagerImGui {


    private long window = 0;
    private final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private ImGuiContext context;
    private int switchTabTo = 0;
    float lineHeight;

    private LoadProjectPanel loadProjectPanel;

    public ProjectManagerImGui(long window) {
        this.window = window;
        this.loadProjectPanel = LoadProjectPanel.getInstance();

        this.init();
    }

    public void init() {
        // Setup Dear ImGui style
        context = ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        setDarkThemeColors();

        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);

//        io.setIniFilename("imgui.ini");
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
        });

        glfwSetScrollCallback(window, (w, xOffset, yOffset) -> {
            if (io.getWantCaptureMouse()) {
                MouseListener.mouseScrollCallback(w, xOffset, yOffset);
            } else {
                MouseListener.clear();
            }
        });

        // Setup Platform/Renderer bindings
        imGuiImplGlfw.init(window, true);
        imGuiImplGl3.init("#version 330");

//        lineHeight= ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
    }

    public void update() {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
        enableDocking();

        showProjectManagerWindow();

        ImGui.end();
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }


    public void dispose() {
        imGuiImplGlfw.dispose();
        imGuiImplGl3.dispose();
        ImGui.destroyContext();
    }

    private void setDarkThemeColors() {
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

    private void showProjectManagerWindow() {
        boolean use_work_area = true;
        int flags = ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings;

        // We demonstrate using the full viewport area or the work area (without menu-bars, task-bars etc.)
        // Based on your use case you may want one of the other.
        final ImGuiViewport viewport = ImGui.getMainViewport();
        if (use_work_area) {
            ImGui.setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
            ImGui.setNextWindowSize(viewport.getWorkSizeX(), viewport.getWorkSizeY());
        } else {
            ImGui.setNextWindowPos(viewport.getPosX(), viewport.getWorkPosY());
            ImGui.setNextWindowSize(viewport.getSizeX(), viewport.getSizeY());
        }

        if (ImGui.begin("Project Manager", flags)) {

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 15, 10);

            if (ImGui.button("Load Project", 335, 30)) {
                switchTabTo = 0;
            }
            ImGui.sameLine();

            if (ImGui.button("Create project", 335, 30)) {
                switchTabTo = 1;
            }

            ImGui.popStyleVar();

            switch(switchTabTo){
                case 0:
                    showLoadProject();
                    break;
                case 1:
                    showCreateProject();
                    break;
            }
        }

        ImGui.end();
    }

    private void showLoadProject() {
        loadProjectPanel.imgui();
    }

    private void showCreateProject() {

    }

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
}
