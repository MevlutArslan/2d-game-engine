package engine.ui;

import engine.Window;
import engine.input.MouseListener;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class ViewPortWindow{

    public static void imgui(){
        ImGui.begin("ViewPort", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImVec2 windowSize = getLargestViewportSize();
        ImVec2 windowPos = getWindowCenterPosition(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);

        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();

        // For making sure my 0,0 is topLeft
//        ImVec2 bottomLeft = new ImVec2();
//        bottomLeft.x = topLeft.x - ImGui.getScrollX();;
//        bottomLeft.y -= topLeft.y - windowSize.y - ImGui.getScrollY();

        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        MouseListener.setViewPortPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setViewPortSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    private static ImVec2 getWindowCenterPosition(ImVec2 aspectSize){
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewPortY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewPortY + ImGui.getCursorPosY());
    }

    private static ImVec2 getLargestViewportSize(){
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / (16.0f/9.0f);

        if(aspectHeight > windowSize.y){
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * (16.0f/9.0f);
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

}
