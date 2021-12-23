package engine.input;

import components.rendering.SpriteRenderer;
import engine.Component;
import engine.Entity;
import engine.Window;
import engine.rendering.Sprite;
import engine.utility.Constants;

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
            selectedEntity.transform.position.x = (int)(selectedEntity.transform.position.x / Constants.GRID_SIZE) * Constants.GRID_SIZE;
            selectedEntity.transform.position.y = (int)(selectedEntity.transform.position.y / Constants.GRID_SIZE) * Constants.GRID_SIZE;

            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                place();
            }
        }
    }

    public boolean hasSelectedEntity(){
        return this.selectedEntity != null;
    }
}
