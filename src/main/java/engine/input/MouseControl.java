package engine.input;

import components.NonPickable;
import engine.Component;
import engine.Entity;
import engine.GameWindow;
import engine.rendering.PickingTexture;
import engine.scene.Scene;
import engine.utility.Constants;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControl extends Component {

    private Entity selectedEntity;

    private float debounce = 0.2f;

    public void pickUpEntity(Entity entity){
        selectedEntity = entity;
        GameWindow.getScene().addEntityToScene(selectedEntity);
    }

    public void place(){
//        GameWindow.getImGuiApp().getPropertiesPanel().setSelectedEntity(selectedEntity);
        selectedEntity = null;
    }

    @Override
    public void onUpdateEditor(float deltaTime) {
        if(selectedEntity != null){
            selectedEntity.transform.position.x = MouseListener.getWorldCoordsX();
            selectedEntity.transform.position.y = MouseListener.getWorldCoordsY();
            selectedEntity.transform.position.x = (int)(selectedEntity.transform.position.x / Constants.GRID_SIZE) * Constants.GRID_SIZE;
            selectedEntity.transform.position.y = (int)(selectedEntity.transform.position.y / Constants.GRID_SIZE) * Constants.GRID_SIZE;

            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                place();
            }
        }
        debounce -= deltaTime;

        PickingTexture pickingTexture = GameWindow.getImGuiApp().getPropertiesPanel().getPickingTexture();
        Scene currentScene = GameWindow.getScene();

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            System.err.println("Selected Entity ID : " + pickingTexture.readPixel(x,y));
            Entity pickedEntity = currentScene.getEntityById(pickingTexture.readPixel(x, y));

            // TODO : Fix overlapping gizmo picks
            if (pickedEntity != null && pickedEntity.getComponent(NonPickable.class) == null && !MouseListener.isDragging()) {
                GameWindow.getImGuiApp().getPropertiesPanel().setSelectedEntity(pickedEntity);
            } else if (pickedEntity == null && !MouseListener.isDragging()) {
                GameWindow.getImGuiApp().getPropertiesPanel().setSelectedEntity(null);
            }
            this.debounce = 0.2f;
        }
    }

    public boolean hasSelectedEntity(){
        return this.selectedEntity != null;
    }
}
