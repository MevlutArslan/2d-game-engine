package engine.ui.editor;

import engine.ui.editor.EditorComponent;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Console extends EditorComponent {

    private static LinkedHashMap<String, LocalTime> messages;

    public Console() {
        messages = new LinkedHashMap<>();
    }

    @Override
    public void update(float deltaTime) {
        this.imgui();
    }

    public void imgui() {
        ImGui.begin("Console");

        ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getColorU32(255,0,0,1));

        for (Map.Entry<String, LocalTime> entry : messages.entrySet()) {
            // TODO : ADD Time to the text Similar to other engines
            ImGui.text(entry.getKey());
        }

        ImGui.popStyleColor();

        ImGui.end();
    }

    public static void addMessage(String message) {
        messages.put(message, LocalTime.now());
    }
}
