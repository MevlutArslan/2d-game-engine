package engine.ui.gizmos;

import components.NonPickable;
import components.rendering.SpriteRenderer;
import engine.Component;
import engine.Entity;
import engine.Window;
import engine.input.MouseListener;
import engine.rendering.Sprite;
import engine.utility.Constants;
import engine.utility.EntityGenerator;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {

    private Entity horizontalGizmoEntity;
    private Entity verticalGizmoEntity;

    private SpriteRenderer verticalGizmoSprite;
    private SpriteRenderer horizontalGizmoSprite;

    // Red
    private final Vector4f verticalGizmoColor = new Vector4f(1, 0, 0, 1);
    // Green
    private final Vector4f horizontalGizmoColor = new Vector4f(0, 1, 0, 1);

    private final Vector4f verticalGizmoColorOnHover = new Vector4f(1, 0, 0, 0.5f);
    private final Vector4f horizontalGizmoColorOnHover = new Vector4f(0, 1, 0, 0.5f);

    private final Vector2f verticalGizmoPositionOffset = new Vector2f(20,61);
    private final Vector2f horizontalGizmoPositionOffset = new Vector2f(64,8);

    // in world units
    private final int gizmoHeight = 48;
    private final int gizmoWidth = 16;

    protected Entity selectedEntity = null;

    protected boolean verticalGizmoActive = false;
    protected boolean horizontalGizmoActive = false;

    private boolean isUsing = false;

    public Gizmo(Sprite sprite) {
        this.horizontalGizmoEntity = EntityGenerator.generate(sprite, 16, 48, Constants.LEVEL_EDITOR_UI_LAYER);
        this.verticalGizmoEntity = EntityGenerator.generate(sprite, 16, 48, Constants.LEVEL_EDITOR_UI_LAYER);

        this.horizontalGizmoEntity.addComponent(new NonPickable());
        this.verticalGizmoEntity.addComponent(new NonPickable());

        this.horizontalGizmoSprite = this.horizontalGizmoEntity.getComponent(SpriteRenderer.class);
        this.verticalGizmoSprite = this.verticalGizmoEntity.getComponent(SpriteRenderer.class);

        Window.getScene().addEntityToScene(horizontalGizmoEntity);
        Window.getScene().addEntityToScene(verticalGizmoEntity);
    }

    @Override
    public void start() {
        this.horizontalGizmoEntity.transform.rotation = 90;
        this.verticalGizmoEntity.transform.rotation = 180;

        this.horizontalGizmoEntity.setNoSerialize();
        this.verticalGizmoEntity.setNoSerialize();
    }

    @Override
    public void update(float deltaTime) {
        if(!isUsing){
            return;
        }
        this.selectedEntity = Window.getScene().getSelectedEntity();
        if (this.selectedEntity != null) {
            this.setActive();
        } else {
            this.setInactive();
            return;
        }

        boolean horizontalTranslateInFocus = checkXHoverState();
        boolean verticalTranslateInFocus = checkYHoverState();

        if((horizontalTranslateInFocus || horizontalGizmoActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            horizontalGizmoActive = true;
            verticalGizmoActive = false;
        }else if((verticalTranslateInFocus || verticalGizmoActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT )){
            verticalGizmoActive = true;
            horizontalGizmoActive = false;
        }else{
            horizontalGizmoActive = false;
            verticalGizmoActive = false;
        }

        if (this.selectedEntity != null) {
            this.horizontalGizmoEntity.transform.position.set(this.selectedEntity.transform.position);
            this.verticalGizmoEntity.transform.position.set(this.selectedEntity.transform.position);

            this.horizontalGizmoEntity.transform.position.add(this.horizontalGizmoPositionOffset);
            this.verticalGizmoEntity.transform.position.add(this.verticalGizmoPositionOffset);
        }
    }

    private boolean checkXHoverState(){
        Vector2f mousePos = new Vector2f(MouseListener.getWorldCoordsX(), MouseListener.getWorldCoordsY());
        if(mousePos.x <= horizontalGizmoEntity.transform.position.x &&
                mousePos.x >= horizontalGizmoEntity.transform.position.x - gizmoHeight &&
                mousePos.y >= horizontalGizmoEntity.transform.position.y &&
                mousePos.y <= horizontalGizmoEntity.transform.position.y + gizmoWidth){
            horizontalGizmoSprite.setColor(horizontalGizmoColorOnHover);
            return true;
        }else{
            horizontalGizmoSprite.setColor(horizontalGizmoColor);
            return false;
        }
    }

    private boolean checkYHoverState(){
        Vector2f mousePos = new Vector2f(MouseListener.getWorldCoordsX(), MouseListener.getWorldCoordsY());
        if(mousePos.x <= verticalGizmoEntity.transform.position.x &&
                mousePos.x >= verticalGizmoEntity.transform.position.x - gizmoWidth &&
                mousePos.y <= verticalGizmoEntity.transform.position.y &&
                mousePos.y >= verticalGizmoEntity.transform.position.y - gizmoHeight){
            verticalGizmoSprite.setColor(verticalGizmoColorOnHover);
            return true;
        }else{
            verticalGizmoSprite.setColor(verticalGizmoColor);
            return false;
        }
    }

    public void setActive() {
        this.horizontalGizmoSprite.setColor(horizontalGizmoColor);
        this.verticalGizmoSprite.setColor(verticalGizmoColor);
    }

    private void setInactive() {
        this.selectedEntity = null;

        this.horizontalGizmoSprite.setColor(new Vector4f(0, 0, 0, 0));
        this.verticalGizmoSprite.setColor(new Vector4f(0, 0, 0, 0));
    }

    public void setUsing(){
        this.isUsing = true;
    }

    public void setNotUsing(){
        this.isUsing = false;
        this.setInactive();
    }
}
