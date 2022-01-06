package engine.ui.gizmos;

import engine.input.MouseListener;
import engine.rendering.Sprite;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite sprite) {
        super(sprite);
    }

    @Override
    public void update(float deltaTime){
        if(selectedEntity != null){
            if(horizontalGizmoActive && !verticalGizmoActive){
                selectedEntity.transform.position.x -= MouseListener.getWorldDx();
            }else if(verticalGizmoActive){
                selectedEntity.transform.position.y -= MouseListener.getWorldDy();
            }
        }

        super.update(deltaTime);
    }
}
