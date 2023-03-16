package engine.input;

import engine.Component;
import engine.Entity;
import engine.ToolboxEditor;
import engine.observers.Event;
import engine.observers.EventSystem;
import engine.observers.EventType;
import engine.ui.ViewPortPanel;
import engine.ui.panels.PropertiesPanel;
import engine.utility.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class EngineKeyShortcuts extends Component {

    @Override
    public void onUpdateEditor(float deltaTime) {
        PropertiesPanel propertiesPanel = ToolboxEditor.getImGuiApp().getPropertiesPanel();
        Entity selectedEntity = propertiesPanel.getSelectedEntity();
        List<Entity> selectedEntities = propertiesPanel.getSelectedEntities();
        boolean ctrlPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) || KeyListener.isKeyPressed(GLFW_KEY_RIGHT_CONTROL);

        if (ViewPortPanel.getWantCaptureMouse()) {
            if (ctrlPressed && KeyListener.keyBeginPress(GLFW_KEY_D) && selectedEntity != null) {
                Entity entity = selectedEntity.copy();
                ToolboxEditor.getScene().addEntityToScene(entity);
                entity.transform.position.add(Constants.GRID_SIZE, 0.0f);
                propertiesPanel.setSelectedEntity(entity);
            } else if (ctrlPressed && KeyListener.keyBeginPress(GLFW_KEY_D) && selectedEntities.size() > 1) {
                List<Entity> entities = new ArrayList<>(selectedEntities);
                propertiesPanel.clearSelected();
                for (Entity entity : entities) {
                    Entity copy = entity.copy();
                    ToolboxEditor.getScene().addEntityToScene(copy);
                    propertiesPanel.addSelectedEntityToEntities(copy);
                }
            } else if (ctrlPressed && KeyListener.isKeyPressed(GLFW_KEY_S)) {
                EventSystem.notify(null, new Event(EventType.SAVE_LEVEL));
            } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) && KeyListener.keyBeginPress(GLFW_KEY_BACKSPACE)) {
                for (Entity entity : selectedEntities) {
                    entity.destroy();
                }
                propertiesPanel.clearSelected();
            }
        }
    }

}
