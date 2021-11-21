package components.rendering;

import components.Component;
import components.Entity;
import engine.rendering.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

public class SpriteRenderer extends Component {

    private Vector4f color;

    private List<Vector2f> textureCoords;
    private Texture texture;

    public SpriteRenderer(Texture texture){
        this.texture = texture;
        // white
        this.color = new Vector4f(1,1,1,1);
    }


    public SpriteRenderer(Vector4f color){
        this.color = color;
        this.texture = null;
    }

    public Texture getTexture(){
        return this.texture;
    }

    public Vector2f[] getTextureCoords(){
        Vector2f[] texCoords = {
                new Vector2f(1,1),
                new Vector2f(1,0),
                new Vector2f(0,0),
                new Vector2f(0,1)
        };
        return texCoords;
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
