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
                // the ratio is different
                selectedEntity.transform.position.x -= MouseListener.getWorldDx();
//                System.out.println(MouseListener.getWorldCoordinateX()/100);
            } else if (verticalGizmoActive && !omniDirectionalGizmoActive) {
//                System.out.println(MouseListener.getWorldCoordinateY()/80.0f);
                selectedEntity.transform.position.y -= MouseListener.getWorldDy();
            } else if (omniDirectionalGizmoActive) {
                selectedEntity.transform.position.set(MouseListener.getWorldCoordinates());
            }
        }
        super.onUpdateEditor(deltaTime);
    }
}
