package engine.project_manager;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public class ProjectManagerPanel {

    private static ProjectManagerPanel instance = null;

    public static ProjectManagerPanel getInstance() {
        if(instance == null){
            instance = new ProjectManagerPanel();
        }
        return instance;
    }

    private int switchTabTo = 0;

    public void imgui() {
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

            switch (switchTabTo) {
                case 0:
                    LoadProjectPanel.getInstance().imgui();
                    break;
                case 1:
                    CreateProjectPanel.getInstance().imgui();
                    break;
            }
        }


        ImGui.end();
    }
}
