package engine.ui.gizmos;

import engine.input.MouseListener;
import engine.rendering.Sprite;

public class ScaleGizmo extends Gizmo{
    public ScaleGizmo(Sprite sprite) {
        super(sprite);
    }

    @Override
    public void onUpdateEditor(float deltaTime){
        if(selectedEntity != null){
            if(horizontalGizmoActive && !verticalGizmoActive){
                selectedEntity.transform.scale.x -= MouseListener.getWorldDx();
            }else if(verticalGizmoActive){
                selectedEntity.transform.scale.y -= MouseListener.getWorldDy();
            }
        }

        super.onUpdateEditor(deltaTime);
    }
}
