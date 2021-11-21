package components.rendering;

import components.Component;
import components.Entity;
import engine.rendering.Sprite;
import engine.rendering.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

public class SpriteRenderer extends Component {

    private Vector4f color;

    private List<Vector2f> textureCoords;

    private Sprite sprite;

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        // white
        this.color = new Vector4f(1,1,1,1);
    }

    public SpriteRenderer(Vector4f color){
        this.color = color;
        this.sprite = new Sprite(null);
    }

    public Texture getTexture(){
        return sprite.getTexture();
    }

    public Vector2f[] getTextureCoords(){
       return sprite.getTextureCoords();
    }

    @Override
    public void start() {

    }

    @Override
    public void update(Entity entity, float deltaTime) {

    }

    public Vector4f getColor(){
        return this.color;
    }
}
