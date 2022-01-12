package engine.ui.editor.menus;

import engine.Window;
import engine.ui.editor.EditorComponent;
import imgui.ImGui;

public class FileMenu extends EditorComponent {

    public FileMenu(){
        System.out.println("Created FILE MENU");
    }

    @Override
    public void update(float deltaTime) {
        if(ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "Ctrl+S")) {
                Window.getScene().save();
            }
            if (ImGui.menuItem("Open", "Ctrl+O")) {
                // Load
            }
            ImGui.endMenu();
        }
    }
}
