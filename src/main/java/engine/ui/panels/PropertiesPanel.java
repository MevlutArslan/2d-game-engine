package engine.ui.panels;

import components.NonPickable;
import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.input.MouseListener;
import engine.rendering.PickingTexture;
import engine.rendering.Sprite;
import engine.scene.Scene;
import imgui.ImGui;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesPanel {
    private Entity selectedEntity = null;
    private List<Entity> selectedEntities;
    private List<Vector4f> selectedEntititiesOriginalColor;
    private PickingTexture pickingTexture;

    public PropertiesPanel(PickingTexture pickingTexture) {
        this.selectedEntities = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.selectedEntititiesOriginalColor = new ArrayList<>();
    }

    public void update(float deltaTime, Scene currentScene) {

    }

    public void imgui() {
        // TODO allow for collective editing of selected entities
        if (selectedEntities.size() == 1 && selectedEntities.get(0) != null) {
            selectedEntity = selectedEntities.get(0);

            ImGui.begin("Properties");

            if (selectedEntity != null) {
                selectedEntity.imgui();
            }

            ImGui.end();
        }

    }


    public Entity getSelectedEntity() {
        return selectedEntities.size() == 1 ? this.selectedEntities.get(0) : null;
    }

    public List<Entity> getSelectedEntities() {
        return this.selectedEntities;
    }

    public void clearSelected() {
        if (selectedEntititiesOriginalColor.size() > 0) {
            int i = 0;
            for (Entity entity : selectedEntities) {
                SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
                if (spriteRenderer != null) {
                    spriteRenderer.setColor(selectedEntititiesOriginalColor.get(i));
                }
                i++;
            }
        }
        this.selectedEntities.clear();
        this.selectedEntititiesOriginalColor.clear();
    }

    public void addSelectedEntityToEntities(Entity entity) {
        SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            this.selectedEntititiesOriginalColor.add(new Vector4f(spriteRenderer.getColor()));
            spriteRenderer.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.selectedEntititiesOriginalColor.add(new Vector4f());
        }
        this.selectedEntities.add(entity);
    }

    public void setSelectedEntity(Entity selectedEntity) {
        if (selectedEntity != null) {
            clearSelected();
            this.selectedEntities.add(selectedEntity);
        }
        this.selectedEntity = selectedEntity;
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}
