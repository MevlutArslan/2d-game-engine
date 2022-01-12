package engine.ui.editor.menus;

import engine.ui.editor.EditorComponent;
import imgui.ImGui;

public class EditMenu extends EditorComponent {

    @Override
    public void update(float deltaTime) {
        if (ImGui.beginMenu("Edit")) {
            if (ImGui.menuItem("Undo", "Ctrl+Z")) {
                // UNDO
            }
            if (ImGui.menuItem("Redo", "Ctrl+Y")) {
                // REDO
            }
            ImGui.endMenu();
        }
    }
}
