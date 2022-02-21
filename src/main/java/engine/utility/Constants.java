package engine.utility;

import imgui.ImFont;
import imgui.ImGui;

public class Constants {
    public static final float GRID_SIZE = 0.25f;

    public static final float GRID_WIDTH = 0.25f;
    public static final float GRID_HEIGHT = 0.25f;

    public static final int MONITOR_WIDTH = 2560;
    public static final int MONITOR_HEIGHT = 1600;

    public static final int BACKGROUND_LAYER = -1;
    public static final int GAME_LAYER = 1;
    public static final int LEVEL_EDITOR_UI_LAYER = 2;
    public static final int UI_LAYER = 3;

    public static ImFont boldFont = ImGui.getIO().getFonts().addFontFromFileTTF("src/main/resources/fonts/roboto/Roboto-Bold.ttf", 18.0f);
    public static ImFont defaultFont = ImGui.getIO().getFonts().addFontFromFileTTF("src/main/resources/fonts/roboto/Roboto-Regular.ttf", 18.0f);

    public static final String sceneFileType = "scene";
}
