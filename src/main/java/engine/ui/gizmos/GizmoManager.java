package engine.ui.gizmos;

import engine.Component;
import engine.input.KeyListener;
import engine.rendering.Sprite;
import engine.rendering.SpriteSheet;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;


public class GizmoManager extends Component {
    private enum GizmoType {
        TRANSLATE_GIZMO,
        SCALE_GIZMO,
        ROTATE_GIZMO
    }

    SpriteSheet spriteSheet;
    GizmoType activeGizmo = GizmoType.TRANSLATE_GIZMO;

    public GizmoManager(SpriteSheet spriteSheet) {
        this.spriteSheet = spriteSheet;
    }

    @Override
    public void start() {
        parent.addComponent(new TranslateGizmo(spriteSheet.getSprite(1)));
        parent.addComponent(new ScaleGizmo(spriteSheet.getSprite(2)));
    }

    @Override
    public void update(float deltaTime) {
        if (activeGizmo == GizmoType.TRANSLATE_GIZMO) {
            parent.getComponent(TranslateGizmo.class).setUsing();
            parent.getComponent(ScaleGizmo.class).setNotUsing();
        } else if (activeGizmo == GizmoType.SCALE_GIZMO) {
            parent.getComponent(TranslateGizmo.class).setNotUsing();
            parent.getComponent(ScaleGizmo.class).setUsing();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            this.activeGizmo = GizmoType.TRANSLATE_GIZMO;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_E)) {
            this.activeGizmo = GizmoType.SCALE_GIZMO;
        }

        System.out.println("Active gizmo is : " + this.activeGizmo.name());
    }
}
