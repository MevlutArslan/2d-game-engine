package engine.input;

import engine.Component;
import engine.Entity;
import engine.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControl extends Component {

    private Entity selectedEntity;

    public void pickUpEntity(Entity entity){
        selectedEntity = entity;
        Window.getScene().addEntityToScene(selectedEntity);
    }

    public void place(){
        selectedEntity = null;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(float deltaTime) {
        if(selectedEntity != null){
            selectedEntity.transform.position.x = MouseListener.getWorldCoordsX() - 16;
            selectedEntity.transform.position.y = MouseListener.getWorldCoordsY() - 16;

            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                place();
            }
        }
    }
}
