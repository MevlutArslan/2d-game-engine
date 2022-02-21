package engine.input;

import components.NonPickable;
import components.rendering.SpriteRenderer;
import engine.Component;
import engine.Entity;
import engine.GameWindow;
import engine.rendering.PickingTexture;
import engine.scene.Scene;
import engine.utility.Constants;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControl extends Component {

    private Entity selectedEntity;

    private float debounceTime = 0.05f;
    private float debounce = debounceTime;

    public void pickUpEntity(Entity entity){
        if(this.selectedEntity != null){
            this.selectedEntity.destroy();
        }
        selectedEntity = entity;
        this.selectedEntity.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f,0.8f,0.8f,0.5f));
        this.selectedEntity.addComponent(new NonPickable());
        GameWindow.getScene().addEntityToScene(selectedEntity);
    }

    public void place(){
        Entity entity = this.selectedEntity.copy();
        entity.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,1));
        entity.removeComponent(NonPickable.class);
        GameWindow.getScene().addEntityToScene(entity);
    }

    @Override
    public void onUpdateEditor(float deltaTime) {
        debounce -= deltaTime;
        if(selectedEntity != null && debounce <= 0){
            selectedEntity.transform.position.x = MouseListener.getWorldCoordinateX();
            selectedEntity.transform.position.y = MouseListener.getWorldCoordinateY();
            selectedEntity.transform.position.x = ((int)Math.floor(selectedEntity.transform.position.x / Constants.GRID_SIZE) * Constants.GRID_SIZE) + Constants.GRID_SIZE / 2.0f;
            selectedEntity.transform.position.y = ((int)Math.floor(selectedEntity.transform.position.y / Constants.GRID_SIZE) * Constants.GRID_SIZE) + Constants.GRID_SIZE / 2.0f;

            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                place();
                debounce = debounceTime;
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                selectedEntity.destroy();
                selectedEntity = null;
            }
        }
        debounce -= deltaTime;

        PickingTexture pickingTexture = GameWindow.getImGuiApp().getPropertiesPanel().getPickingTexture();
        Scene currentScene = GameWindow.getScene();

        if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            System.err.println("Selected Entity ID : " + pickingTexture.readPixel(x,y));
            Entity pickedEntity = currentScene.getEntityById(pickingTexture.readPixel(x, y));

            // TODO : Fix overlapping gizmo picks
            if (pickedEntity != null && pickedEntity.getComponent(NonPickable.class) == null) {
                GameWindow.getImGuiApp().getPropertiesPanel().setSelectedEntity(pickedEntity);
            } else if (pickedEntity == null) {
                GameWindow.getImGuiApp().getPropertiesPanel().setSelectedEntity(null);
            }
            this.debounce = 0.2f;
        }
    }

    public boolean hasSelectedEntity(){
        return this.selectedEntity != null;
    }
}
