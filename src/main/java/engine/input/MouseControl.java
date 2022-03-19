package engine.input;

import components.NonPickable;
import components.rendering.SpriteRenderer;
import engine.Component;
import engine.Entity;
import engine.GameWindow;
import engine.rendering.DebugDraw;
import engine.rendering.PickingTexture;
import engine.scene.Scene;
import engine.ui.panels.PropertiesPanel;
import engine.utility.Constants;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControl extends Component {

    private Entity selectedEntity;

    private float debounceTime = 0.2f;
    private float debounce = debounceTime;
    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    private final int multiSelectMouseButton = GLFW_MOUSE_BUTTON_RIGHT;

    public void pickUpEntity(Entity entity) {
        if (this.selectedEntity != null) {
            this.selectedEntity.destroy();
        }
        selectedEntity = entity;
        this.selectedEntity.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        this.selectedEntity.addComponent(new NonPickable());
        GameWindow.getScene().addEntityToScene(selectedEntity);
    }

    public void place() {
        Entity entity = this.selectedEntity.copy();
        entity.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        entity.removeComponent(NonPickable.class);
        GameWindow.getScene().addEntityToScene(entity);
    }

    @Override
    public void onUpdateEditor(float deltaTime) {
        debounce -= deltaTime;
        PickingTexture pickingTexture = GameWindow.getImGuiApp().getPropertiesPanel().getPickingTexture();
        Scene currentScene = GameWindow.getScene();

        if (selectedEntity != null) {
            float x = MouseListener.getWorldCoordinateX();
            float y = MouseListener.getWorldCoordinateY();
            selectedEntity.transform.position.x = ((int) Math.floor(x / Constants.GRID_SIZE) * Constants.GRID_SIZE) + Constants.GRID_SIZE / 2.0f;
            selectedEntity.transform.position.y = ((int) Math.floor(y / Constants.GRID_SIZE) * Constants.GRID_SIZE) + Constants.GRID_SIZE / 2.0f;

            // TODO fix gizmo's not functioning
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth = Constants.GRID_WIDTH / 2.0f;
                float halfHeight = Constants.GRID_HEIGHT / 2.0f;

                if (MouseListener.isDragging() &&
                        !blockExistsInSquare(selectedEntity.transform.position.x - halfWidth,
                                selectedEntity.transform.position.y - halfHeight)) {
                    place();
                } else if (!MouseListener.isDragging() && debounce < 0) {
                    place();
                    debounce = debounceTime;
                }
            }
            if (KeyListener.isKeyPressed(GLFW_KEY_Q)) {
                selectedEntity.destroy();
                selectedEntity = null;
            }

        } else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            Entity pickedEntity = currentScene.getEntityById(pickingTexture.readPixel(x, y));

            // TODO : Fix overlapping gizmo picks
            if (pickedEntity != null && pickedEntity.getComponent(NonPickable.class) == null) {
                GameWindow.getImGuiApp().getPropertiesPanel().setSelectedEntity(pickedEntity);
            } else if (pickedEntity == null && !MouseListener.isDragging()) {
                GameWindow.getImGuiApp().getPropertiesPanel().setSelectedEntity(null);
            }
            this.debounce = 0.2f;

        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            if (!boxSelectSet) {
                GameWindow.getImGuiApp().getPropertiesPanel().clearSelected();
                boxSelectStart = MouseListener.getScreenCoordinates();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreenCoordinates();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.drawSquare((new Vector2f(boxSelectStartWorld)).add(halfSize), new Vector2f(halfSize).mul(2.0f), 0);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int) boxSelectStart.x;
            int screenStartY = (int) boxSelectStart.y;
            int screenEndX = (int) boxSelectEnd.x;
            int screenEndY = (int) boxSelectEnd.y;

            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int temp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = temp;
            }

            if (screenEndY < screenStartY) {
                int temp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = temp;
            }

            float[] ids = pickingTexture.readPixels(new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY));
            Set<Integer> uniqueIds = new HashSet<>();
            for (float id : ids) {
                uniqueIds.add((int) id);
            }

            for (Integer entityId : uniqueIds) {
                Entity entity = GameWindow.getScene().getEntityById(entityId);
                if (entity != null && entity.getComponent(NonPickable.class) == null) {
                    GameWindow.getImGuiApp().getPropertiesPanel().addSelectedEntityToEntities(entity);
                }
            }
        }
    }

    private boolean blockExistsInSquare(float x, float y) {
        PropertiesPanel propertiesPanel = GameWindow.getImGuiApp().getPropertiesPanel();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Constants.GRID_WIDTH, Constants.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf = MouseListener.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int) startScreenf.x + 2, (int) startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int) endScreenf.x - 2, (int) endScreenf.y - 2);
        float[] entityIds = propertiesPanel.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < entityIds.length; i++) {
            if (entityIds[i] >= 0) {
                Entity entity = GameWindow.getScene().getEntityById((int) entityIds[i]);
                if (entity.getComponent(NonPickable.class) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasSelectedEntity() {
        return this.selectedEntity != null;
    }
}
