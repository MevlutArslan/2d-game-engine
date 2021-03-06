package components.rendering;

import engine.Component;
import components.Transform;
import engine.rendering.Sprite;
import engine.rendering.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1,1,1,1);

    private List<Vector2f> textureCoords;

    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean hasChanged = true;

    @Override
    public void start() {
        this.allowForRemoval = false;
        this.lastTransform = parent.transform.copy();
    }

    @Override
    public void update(float deltaTime) {
        if(!this.lastTransform.equals(parent.transform)){
            this.parent.transform.copyTo(lastTransform);
            hasChanged = true;

        }
    }

    @Override
    public void onUpdateEditor(float deltaTime){
        if(!this.lastTransform.equals(parent.transform)){
            this.parent.transform.copyTo(lastTransform);
            hasChanged = true;
        }
    }

    public boolean hasChanged(){
        return this.hasChanged;
    }

    public Texture getTexture(){
        return sprite.getTexture();
    }

    public Vector2f[] getTextureCoords(){
        return sprite.getTextureCoords();
    }

    public Vector4f getColor(){
        return this.color;
    }

    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        this.hasChanged = true;
    }

    public void setColor(Vector4f color){
        if(!this.color.equals(color)){
            this.color.set(color);
            this.hasChanged = true;
        }
    }

    public void setHasChangedToFalse(){
        this.hasChanged = false;
    }

    public void setTexture(Texture texture){
        this.sprite.setTexture(texture);
    }

    public void setHasChanged() {
        this.hasChanged = true;
    }

}
