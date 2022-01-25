package engine.ui.gizmos;

import engine.input.MouseListener;
import engine.rendering.Sprite;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite sprite, Sprite omniDirectionalGizmoSprite) {
        super(sprite, omniDirectionalGizmoSprite);
    }

    @Override
    public void onUpdateEditor(float deltaTime) {
        if (selectedEntity != null) {
            if (horizontalGizmoActive && !verticalGizmoActive && !omniDirectionalGizmoActive) {
                selectedEntity.transform.position.x -= MouseListener.getWorldDx();
            } else if (verticalGizmoActive && !omniDirectionalGizmoActive) {
                selectedEntity.transform.position.y -= MouseListener.getWorldDy();
            } else if (omniDirectionalGizmoActive) {
                selectedEntity.transform.position.set(MouseListener.getWorldCoordsX(), MouseListener.getWorldCoordsY());
            }
        }


        super.onUpdateEditor(deltaTime);
    }
}
