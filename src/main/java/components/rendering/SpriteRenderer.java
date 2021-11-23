package components.rendering;

import components.Component;
import components.Entity;
import components.Transform;
import engine.rendering.Sprite;
import engine.rendering.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

public class SpriteRenderer extends Component {

    private Vector4f color;

    private List<Vector2f> textureCoords;

    private Sprite sprite;
    private Transform lastTransform;

    private boolean hasChanged = false;

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        // white
        this.color = new Vector4f(1,1,1,1);
    }

    public SpriteRenderer(Vector4f color){
        this.color = color;
        this.sprite = new Sprite(null);
    }

    @Override
    public void start() {
        this.lastTransform = parent.transform.copy();
    }

    @Override
    public void update(float deltaTime) {
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
}
