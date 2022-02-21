package engine.ui.panels;

import components.NonPickable;
import engine.Entity;
import engine.input.MouseListener;
import engine.physics.components.Box2dCollider;
import engine.physics.components.CircleCollider;
import engine.physics.components.RigidBody2d;
import engine.rendering.PickingTexture;
import engine.scene.Scene;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesPanel {
    private Entity selectedEntity = null;
    private PickingTexture pickingTexture;

    public PropertiesPanel(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float deltaTime, Scene currentScene) {

    }

    public void imgui() {
        ImGui.begin("Properties");
        if (selectedEntity != null) {
            selectedEntity.imgui();

            if (ImGui.button("Add Component")) {
                ImGui.openPopup("AddComponent");
            }

            if (ImGui.beginPopup("AddComponent")) {
                if (ImGui.menuItem("Add Rigidbody")) {
                    if (selectedEntity.getComponent(RigidBody2d.class) == null) {
                        selectedEntity.addComponent(new RigidBody2d());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (selectedEntity.getComponent(Box2dCollider.class) == null &&
                            selectedEntity.getComponent(CircleCollider.class) == null) {
                        selectedEntity.addComponent(new Box2dCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (selectedEntity.getComponent(CircleCollider.class) == null &&
                            selectedEntity.getComponent(Box2dCollider.class) == null) {
                        selectedEntity.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }
        }
        ImGui.end();
    }

    public Entity getSelectedEntity() {
        return this.selectedEntity;
    }

    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}
