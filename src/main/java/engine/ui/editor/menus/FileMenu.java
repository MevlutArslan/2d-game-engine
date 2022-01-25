package engine.ui.editor.menus;

import engine.GameWindow;
import engine.observers.Event;
import engine.observers.EventSystem;
import engine.observers.EventType;
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
                EventSystem.notify(null, new Event(EventType.SAVE_LEVEL));
            }
            if (ImGui.menuItem("Open", "Ctrl+O")) {
                // Load
            }
            ImGui.endMenu();
        }
    }
}
