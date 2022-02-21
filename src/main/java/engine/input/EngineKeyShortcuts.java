package engine.input;

import engine.Component;
import engine.Entity;
import engine.GameWindow;
import engine.observers.Event;
import engine.observers.EventSystem;
import engine.observers.EventType;
import engine.ui.panels.PropertiesPanel;
import engine.utility.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class EngineKeyShortcuts extends Component {

    @Override
    public void onUpdateEditor(float deltaTime) {
        PropertiesPanel propertiesPanel = GameWindow.getImGuiApp().getPropertiesPanel();
        Entity selectedEntity = propertiesPanel.getSelectedEntity();
        List<Entity> selectedEntities = propertiesPanel.getSelectedEntities();
        boolean ctrlPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) || KeyListener.isKeyPressed(GLFW_KEY_RIGHT_CONTROL);

        if (ctrlPressed && KeyListener.keyBeginPress(GLFW_KEY_D) && selectedEntity != null) {
            Entity entity = selectedEntity.copy();
            GameWindow.getScene().addEntityToScene(entity);
            entity.transform.position.add(Constants.GRID_SIZE, 0.0f);
            propertiesPanel.setSelectedEntity(entity);
        } else if (ctrlPressed && KeyListener.keyBeginPress(GLFW_KEY_D) && selectedEntities.size() > 1) {
            List<Entity> entities = new ArrayList<>(selectedEntities);
            propertiesPanel.clearSelected();
            for (Entity entity : entities) {
                Entity copy = entity.copy();
                GameWindow.getScene().addEntityToScene(copy);
                propertiesPanel.addSelectedEntityToEntities(copy);
            }
        } else if (ctrlPressed && KeyListener.isKeyPressed(GLFW_KEY_S)) {
            EventSystem.notify(null, new Event(EventType.SAVE_LEVEL));
        } else if (KeyListener.keyBeginPress(GLFW_KEY_BACKSPACE)) {
            for (Entity entity : selectedEntities) {
                entity.destroy();
            }
            propertiesPanel.clearSelected();
        }
    }

}
