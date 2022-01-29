package engine.ui.editor;

import engine.Component;
import engine.Entity;
import engine.utility.Constants;
import engine.utility.IAllowForComponentRemoval;
import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

// https://github.com/TheCherno/Hazel/blob/master/Hazelnut/src/Panels/SceneHierarchyPanel.cpp
public class CustomImGuiController {
    private static float defaultColumnWidth = 120.0f;

    private static final float VERTICAL_SPACING = 5.0f;
    private static final float HORIZONTAL_SPACING = 0.001f;

    public static void drawVec3Control(String label, Vector3f values, float resetValue) {
        drawVec3Control(label, values, resetValue, defaultColumnWidth);
    }

    public static void drawVec3Control(String label, Vector3f values, float resetValue, float columnWidth) {
        ImGuiIO io = ImGui.getIO();
        ImFont boldFont = Constants.boldFont;

        ImGui.pushID(label);
        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        float lineHeight = ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        ImVec2 buttonSize = new ImVec2(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushFont(boldFont);

        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##X", new float[]{values.x}, 0.1f, 0.0f, 0.0f, "%.2f");
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushFont(boldFont);

        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##Y", new float[]{values.y}, 0.1f, 0.0f, 0.0f, "%.2f");
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushFont(boldFont);

        if (ImGui.button("Z", buttonSize.x, buttonSize.y)) {
            values.z = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##Z", new float[]{values.z}, 0.1f, 0.0f, 0.0f, "%.2f");
        ImGui.popItemWidth();

        ImGui.popStyleVar();

        ImGui.columns(1);

        ImGui.dummy(0, VERTICAL_SPACING);

        ImGui.popID();
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        drawVec2Control(label, values, resetValue, defaultColumnWidth);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth) {
        ImGuiIO io = ImGui.getIO();
        ImFont boldFont = Constants.boldFont;

        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);

        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        float lineHeight = ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        ImVec2 buttonSize = new ImVec2(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);

        ImGui.pushFont(boldFont);
        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);
//        ImGui.dummy(HORIZONTAL_SPACING, 0);
        ImGui.sameLine();
//        ImVec2 defaultPadding = ImGui.getStyle().getItemSpacing();
        float[] valueX = new float[]{values.x};
        // identifier, value, increment amount
        ImGui.dragFloat("##X", valueX, 0.1f);
        ImGui.popItemWidth();
//        ImGui.getStyle().setItemSpacing(defaultPadding.x, defaultPadding.y);
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushFont(boldFont);

        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        float[] valueY = new float[]{values.y};
        ImGui.dragFloat("##Y", valueY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();
        ImGui.nextColumn();

        values.x = valueX[0];
        values.y = valueY[0];

        ImGui.popStyleVar();
        ImGui.columns(1);

        ImGui.dummy(0, VERTICAL_SPACING);
        ImGui.popID();

    }

    // TODO
    public static void drawColorField(String label, Vector4f values, float resetValue) {
        drawColorField(label, values, resetValue, defaultColumnWidth);
    }

    public static void drawColorField(String label, Vector4f values, float resetValue, float columnWidth) {
        ImGuiIO io = ImGui.getIO();
        ImFont boldFont = Constants.boldFont;

        ImGui.pushID(label);
        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        float lineHeight = ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        ImVec2 buttonSize = new ImVec2(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 4.0f) / 2.0f;

        ImGui.colorPicker4("", new float[]{values.x, values.y, values.z, values.w});

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushFont(boldFont);

        if (ImGui.button("R", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##R", new float[]{values.x}, 0.1f, 0.0f, 0.0f, "%.2f");
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushFont(boldFont);

        if (ImGui.button("G", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##G", new float[]{values.y}, 0.1f, 0.0f, 0.0f, "%.2f");
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);
        ImGui.pushFont(boldFont);

        if (ImGui.button("B", buttonSize.x, buttonSize.y)) {
            values.z = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##B", new float[]{values.z}, 0.1f, 0.0f, 0.0f, "%.2f");
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushFont(boldFont);

        if (ImGui.button("A", buttonSize.x, buttonSize.y)) {
            values.w = resetValue;
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        ImGui.dragFloat("##A", new float[]{values.w}, 0.1f, 0.0f, 0.0f, "%.2f");
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.popStyleVar();

        ImGui.columns(1);

        ImGui.popID();
    }

    public static int dragInt(String label, int value) {
        return dragInt(label, value, defaultColumnWidth);
    }

    public static int dragInt(String label, int value, float columnWidth) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        int[] val = new int[]{value};
        ImGui.dragInt("##int", val, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return val[0];
    }

    public static float dragFloat(String label, float value) {
        return dragFloat(label, value, defaultColumnWidth);
    }

    public static float dragFloat(String label, float value, float columnWidth) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] val = new float[]{value};
        ImGui.dragFloat("##float", val, 0.1f);

        ImGui.columns(1);
        ImGui.popID();
        return val[0];
    }

    public static void drawComponent(Component component, String name, Entity entity, boolean allowForRemoval) {

        final int treeNodeFlags =  ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding;
        ImVec2 contentRegionAvailable = ImGui.getContentRegionAvail();

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4, 4);
        float lineHeight = ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        ImGui.separator();
        boolean open = ImGui.treeNodeEx(component.hashCode(), treeNodeFlags, name);
        ImGui.popStyleVar();
        ImGui.sameLine(contentRegionAvailable.x - lineHeight * 0.5f);
        if (ImGui.button("+", lineHeight, lineHeight)) {
            ImGui.openPopup("Component Settings");
        }


        boolean removeComponent = false;

        if (ImGui.beginPopup("Component Settings")) {
            if (ImGui.menuItem("Remove Component", "", false, allowForRemoval)) {
                removeComponent = true;
            }


            ImGui.endPopup();
        }

        if (open) {
            component.imgui();
            ImGui.treePop();
        }

        if (removeComponent) {
            entity.removeComponent(component.getClass());
        }

    }
}
