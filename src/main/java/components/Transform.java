package components;

import components.rendering.SpriteRenderer;
import engine.Component;
import engine.ui.editor.CustomImGuiController;
import engine.utility.Constants;
import org.joml.Vector2f;

public class Transform extends Component {

    public Vector2f position;
    public Vector2f scale;
    public int zIndex;
    public float rotation = 0;

    public Transform() {
        this.position = new Vector2f();
        this.scale = new Vector2f();
        this.zIndex = Constants.GAME_LAYER;
        this.allowForRemoval = false;
    }

    public Transform(Vector2f position) {
        this.position = position;
        this.scale = new Vector2f();
        this.zIndex = Constants.GAME_LAYER;
        this.allowForRemoval = false;
    }

    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
        this.zIndex = Constants.GAME_LAYER;
        this.allowForRemoval = false;
    }

    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    public void copyTo(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform) o;
        return t.position.equals(this.position) && t.scale.equals(this.scale);
    }

    @Override
    public void imgui() {
        CustomImGuiController.drawVec2Control("Position", this.position, 0);
        CustomImGuiController.drawVec2Control("Scale", this.scale, 0);
        float newRotation = CustomImGuiController.dragFloat("Rotation", this.rotation);
        int newZindex = CustomImGuiController.dragInt("Z-Index", this.zIndex);
        if(this.rotation != newRotation){
            this.rotation = newRotation;
            this.parent.getComponent(SpriteRenderer.class).setHasChanged();
        }
        if(this.zIndex != newZindex){
            this.zIndex = newZindex;
            this.parent.getComponent(SpriteRenderer.class).setHasChanged();
        }
    }
}
