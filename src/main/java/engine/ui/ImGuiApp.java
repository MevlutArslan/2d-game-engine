package engine.ui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiContext;

public class ImGuiApp {
    private static ImGuiApp instance = null;
    private long window;

    private ImGuiContext context;

    private ImGuiImplGlfw glfwBinding;
    private ImGuiImplGl3 glBinding;

    private ImGuiApp(long window){
        this.window = window;

        this.init();
    }

    public static ImGuiApp get(long window){
        if(instance == null){
            ImGuiApp.instance = new ImGuiApp(window);
            return ImGuiApp.instance;
        }
        return ImGuiApp.instance;
    }

    public void init(){
        context = ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        // Setup Platform/Renderer bindings
        glfwBinding = new ImGuiImplGlfw();
        glfwBinding.init(window,true);
        glBinding = new ImGuiImplGl3();
        glBinding.init("#version 330");
        // Setup Dear ImGui style
        ImGui.styleColorsDark();
    }

    public void update(float deltaTime){
        glfwBinding.newFrame();
        ImGui.newFrame();

        ImGui.begin("window");
        ImGui.button("Button");
        ImGui.end();

        ImGui.render();
        glBinding.renderDrawData(ImGui.getDrawData());

    }

    public void dispose(){
        glBinding.dispose();
        glfwBinding.dispose();
        ImGui.destroyContext();

    }
}
