package engine.ui.gizmos;

import engine.input.MouseListener;
import engine.rendering.Sprite;
import engine.ui.panels.PropertiesPanel;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite sprite, Sprite omniDirectionalGizmoSprite, PropertiesPanel propertiesPanel) {
        super(sprite, omniDirectionalGizmoSprite, propertiesPanel);
    }

    @Override
    public void onUpdateEditor(float deltaTime) {
        if (selectedEntity != null) {
            if (horizontalGizmoActive && !verticalGizmoActive && !omniDirectionalGizmoActive) {
                selectedEntity.transform.position.x -= MouseListener.getWorldCoordinateX();
            } else if (verticalGizmoActive && !omniDirectionalGizmoActive) {
                selectedEntity.transform.position.y -= MouseListener.getWorldCoordinateY();
            } else if (omniDirectionalGizmoActive) {
                selectedEntity.transform.position.set(MouseListener.getWorldCoordinates());
            }
        }
        super.onUpdateEditor(deltaTime);
    }
}
