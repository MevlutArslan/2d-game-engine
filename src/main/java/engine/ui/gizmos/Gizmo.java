package engine.ui.gizmos;

import components.NonPickable;
import components.Transform;
import components.rendering.SpriteRenderer;
import engine.Component;
import engine.Entity;
import engine.GameWindow;
import engine.input.KeyListener;
import engine.input.MouseListener;
import engine.rendering.Sprite;
import engine.ui.panels.PropertiesPanel;
import engine.utility.Constants;
import engine.utility.EntityGenerator;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component {

    private Entity horizontalGizmoEntity;
    private Entity verticalGizmoEntity;
    private Entity omniDirectionalGizmoEntity;

    private SpriteRenderer verticalGizmoSprite;
    private SpriteRenderer horizontalGizmoSprite;
    private SpriteRenderer omniDirectionalGizmoSprite;

    // Red
    private final Vector4f verticalGizmoColor = new Vector4f(1, 0, 0, 0.7f);
    // Green
    private final Vector4f horizontalGizmoColor = new Vector4f(0, 0.7f, 0, 1);
    // Combination of both
    private final Vector4f omniDirectionalGizmoColor = new Vector4f(0, 0, 0.7f, 1);

    private final Vector4f verticalGizmoColorOnHover = new Vector4f(1, 0, 0, 1f);
    private final Vector4f horizontalGizmoColorOnHover = new Vector4f(0, 1, 0, 1f);
    private final Vector4f omniDirectionalGizmoColorOnHover = new Vector4f(0, 0, 1, 1f);

    private final Vector2f verticalGizmoPositionOffset = new Vector2f(-7f / 80f, 21f / 80f);
    private final Vector2f horizontalGizmoPositionOffset = new Vector2f(24f / 80f, -6f / 80f);
    private final Vector2f omniDirectionalGizmoPositionOffset = new Vector2f(18/80f, 2/80f);

    // in world units
    private final float gizmoHeight = 48 / 80f;
    private final float gizmoWidth = 16 / 80f;

    private final float omniDirectionalGizmoHeight = 32 / 80f;
    private final float omniDirectionalGizmoWidth = 16 / 80f;

    protected Entity selectedEntity = null;

    protected boolean verticalGizmoActive = false;
    protected boolean horizontalGizmoActive = false;
    protected boolean omniDirectionalGizmoActive = false;

    private PropertiesPanel propertiesPanel;


    private boolean isUsing = false;

    public Gizmo(Sprite sprite, PropertiesPanel propertiesPanel) {
        this.horizontalGizmoEntity = EntityGenerator.generate(sprite, gizmoWidth, gizmoHeight, Constants.LEVEL_EDITOR_UI_LAYER);
        this.verticalGizmoEntity = EntityGenerator.generate(sprite, gizmoWidth, gizmoHeight, Constants.LEVEL_EDITOR_UI_LAYER);

        this.propertiesPanel = propertiesPanel;
        this.horizontalGizmoEntity.addComponent(new NonPickable());
        this.verticalGizmoEntity.addComponent(new NonPickable());

        this.horizontalGizmoSprite = this.horizontalGizmoEntity.getComponent(SpriteRenderer.class);
        this.verticalGizmoSprite = this.verticalGizmoEntity.getComponent(SpriteRenderer.class);

        GameWindow.getScene().addEntityToScene(horizontalGizmoEntity);
        GameWindow.getScene().addEntityToScene(verticalGizmoEntity);
    }

    public Gizmo(Sprite sprite, Sprite omniDirectionalGizmoSprite, PropertiesPanel propertiesPanel) {
        this.horizontalGizmoEntity = EntityGenerator.generate(sprite, gizmoWidth, gizmoHeight, Constants.LEVEL_EDITOR_UI_LAYER);
        this.verticalGizmoEntity = EntityGenerator.generate(sprite, gizmoWidth, gizmoHeight, Constants.LEVEL_EDITOR_UI_LAYER);
        this.omniDirectionalGizmoEntity = EntityGenerator.generate(omniDirectionalGizmoSprite, omniDirectionalGizmoWidth, omniDirectionalGizmoHeight, Constants.LEVEL_EDITOR_UI_LAYER);

        this.propertiesPanel = propertiesPanel;

        this.horizontalGizmoEntity.addComponent(new NonPickable());
        this.verticalGizmoEntity.addComponent(new NonPickable());
        this.omniDirectionalGizmoEntity.addComponent(new NonPickable());

        this.horizontalGizmoSprite = this.horizontalGizmoEntity.getComponent(SpriteRenderer.class);
        this.verticalGizmoSprite = this.verticalGizmoEntity.getComponent(SpriteRenderer.class);
        this.omniDirectionalGizmoSprite = this.omniDirectionalGizmoEntity.getComponent(SpriteRenderer.class);

        GameWindow.getScene().addEntityToScene(horizontalGizmoEntity);
        GameWindow.getScene().addEntityToScene(verticalGizmoEntity);
        GameWindow.getScene().addEntityToScene(omniDirectionalGizmoEntity);
    }

    @Override
    public void start() {
        this.horizontalGizmoEntity.transform.rotation = 90;
        this.verticalGizmoEntity.transform.rotation = 180;

        this.horizontalGizmoEntity.setNoSerialize();
        this.verticalGizmoEntity.setNoSerialize();

        if (omniDirectionalGizmoEntity != null) {
            this.omniDirectionalGizmoEntity.setNoSerialize();
        }
    }

    @Override
    public void update(float deltaTime){
        if(isUsing){
            setInactive();
        }
    }

    @Override
    public void onUpdateEditor(float deltaTime) {
        if (!isUsing) {
            return;
        }
        this.selectedEntity = this.propertiesPanel.getSelectedEntity();
        if (this.selectedEntity != null) {
            this.setActive();

            if(KeyListener.keyBeginPress(GLFW_KEY_BACKSPACE)){
                selectedEntity.destroy();
                this.setInactive();
                propertiesPanel.setSelectedEntity(null);
            }

        } else {
            this.setInactive();
            return;
        }

        boolean verticalGizmoInFocus = checkVerticalGizmoHoverState();
        boolean horizontalGizmoInFocus = checkHorizontalGizmoHoverState();
        boolean omniDirectionalGizmoInFocus = checkOmniDirectionalGizmoHoverState();


        if ((horizontalGizmoInFocus || horizontalGizmoActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            verticalGizmoActive = false;
            omniDirectionalGizmoActive = false;
            horizontalGizmoActive = true;
        } else if ((verticalGizmoInFocus || verticalGizmoActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            horizontalGizmoActive = false;
            omniDirectionalGizmoActive = false;
            verticalGizmoActive = true;
        } else {
            horizontalGizmoActive = false;
            verticalGizmoActive = false;
            omniDirectionalGizmoActive = false;
        }

        if (this.selectedEntity != null) {
            this.horizontalGizmoEntity.transform.position.set(this.selectedEntity.transform.position);
            this.verticalGizmoEntity.transform.position.set(this.selectedEntity.transform.position);

            if (omniDirectionalGizmoEntity != null) {
                this.omniDirectionalGizmoEntity.transform.position.set(this.selectedEntity.transform.position);
                this.omniDirectionalGizmoEntity.transform.position.add(this.omniDirectionalGizmoPositionOffset);
//                DebugDraw.drawSquare(this.omniDirectionalGizmoEntity.transform.position, this.omniDirectionalGizmoEntity.transform.scale, 0, new Vector3f(0,0,1),1);
            }

            this.horizontalGizmoEntity.transform.position.add(this.horizontalGizmoPositionOffset);
            this.verticalGizmoEntity.transform.position.add(this.verticalGizmoPositionOffset);

        }
    }

    private boolean checkHorizontalGizmoHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldCoordinates());
        if (mousePos.x <= horizontalGizmoEntity.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= horizontalGizmoEntity.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y >= horizontalGizmoEntity.transform.position.y - (gizmoHeight / 2.0f) &&// bug here dont know why 3 works
                mousePos.y <= horizontalGizmoEntity.transform.position.y + (gizmoWidth / 2.0f)) {
            horizontalGizmoSprite.setColor(horizontalGizmoColorOnHover);
            return true;
        } else {
            horizontalGizmoSprite.setColor(horizontalGizmoColor);
            return false;
        }
    }

    private boolean checkVerticalGizmoHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldCoordinates());
        if (mousePos.x <= verticalGizmoEntity.transform.position.x + (gizmoWidth / 2.0f)&&
                mousePos.x >= verticalGizmoEntity.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= verticalGizmoEntity.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePos.y >= verticalGizmoEntity.transform.position.y - (gizmoHeight / 2.0f)) {
            verticalGizmoSprite.setColor(verticalGizmoColorOnHover);
            return true;
        } else {
            verticalGizmoSprite.setColor(verticalGizmoColor);
            return false;
        }
    }

    // TODO Sizing to Box2D standards broke it, needs fixing
    private boolean checkOmniDirectionalGizmoHoverState() {
        if (omniDirectionalGizmoEntity == null) return false;

        Vector2f mousePos = new Vector2f(MouseListener.getWorldCoordinates());
        if (mousePos.x >= omniDirectionalGizmoEntity.transform.position.x &&
                mousePos.x <= omniDirectionalGizmoEntity.transform.position.x + omniDirectionalGizmoWidth &&
                mousePos.y >= omniDirectionalGizmoEntity.transform.position.y + omniDirectionalGizmoWidth &&
                mousePos.y <= omniDirectionalGizmoEntity.transform.position.y + omniDirectionalGizmoHeight) {
            omniDirectionalGizmoSprite.setColor(omniDirectionalGizmoColorOnHover);
            return true;

        } else {
            omniDirectionalGizmoSprite.setColor(omniDirectionalGizmoColor);
            return false;
        }
    }

    public void setActive() {
        this.horizontalGizmoSprite.setColor(horizontalGizmoColor);
        this.verticalGizmoSprite.setColor(verticalGizmoColor);
        if (omniDirectionalGizmoEntity != null) {
            this.omniDirectionalGizmoSprite.setColor(omniDirectionalGizmoColor);
        }
    }

    private void setInactive() {
        this.selectedEntity = null;

        this.horizontalGizmoSprite.setColor(new Vector4f(0, 0, 0, 0));
        this.verticalGizmoSprite.setColor(new Vector4f(0, 0, 0, 0));
        if (omniDirectionalGizmoEntity != null) {
            this.omniDirectionalGizmoSprite.setColor(new Vector4f(0, 0, 0, 0));
        }
    }

    public void setUsing() {
        this.isUsing = true;
    }

    public void setNotUsing() {
        this.isUsing = false;
        this.setInactive();
    }

}
