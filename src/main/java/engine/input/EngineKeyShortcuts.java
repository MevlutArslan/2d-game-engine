package engine.input;

import engine.Component;
import engine.Entity;
import engine.GameWindow;
import engine.observers.Event;
import engine.observers.EventSystem;
import engine.observers.EventType;
import engine.ui.panels.PropertiesPanel;

import static org.lwjgl.glfw.GLFW.*;

public class EngineKeyShortcuts extends Component {

    @Override
    public void onUpdateEditor(float deltaTime){
        PropertiesPanel propertiesPanel = GameWindow.getImGuiApp().getPropertiesPanel();
        Entity selectedEntity = propertiesPanel.getSelectedEntity();
        boolean ctrlPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) || KeyListener.isKeyPressed(GLFW_KEY_RIGHT_CONTROL);

        if(ctrlPressed && KeyListener.isKeyPressed(GLFW_KEY_S)){
            EventSystem.notify(null, new Event(EventType.SAVE_LEVEL));
        }
        else if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D)){
            Entity newEntity = selectedEntity.copy();
            GameWindow.getScene().addEntityToScene(newEntity);
            newEntity.transform.position.add(0.1f,0.1f);
            propertiesPanel.setSelectedEntity(newEntity);

        }
    }
}
