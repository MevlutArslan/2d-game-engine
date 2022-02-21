package engine.ui.gizmos;

import engine.input.MouseListener;
import engine.rendering.Sprite;
import engine.ui.panels.PropertiesPanel;

public class ScaleGizmo extends Gizmo{
    public ScaleGizmo(Sprite sprite, PropertiesPanel propertiesPanel) {
        super(sprite, propertiesPanel);
    }

    @Override
    public void onUpdateEditor(float deltaTime){
        if(selectedEntity != null){
            if(horizontalGizmoActive && !verticalGizmoActive){
                selectedEntity.transform.scale.x -= MouseListener.getWorldCoordinateX();
            }else if(verticalGizmoActive){
                selectedEntity.transform.scale.y -= MouseListener.getWorldCoordinateY();
            }
        }

        super.onUpdateEditor(deltaTime);
    }
}
