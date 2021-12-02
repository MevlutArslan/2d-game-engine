package engine.rendering;

import components.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Sprite {

    private Texture texture = null;
    private Vector2f[] textureCoords = {
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    public Texture getTexture(){
        return this.texture;
    }

    public Vector2f[] getTextureCoords(){
        return this.textureCoords;
    }

    public void setTexture(Texture texture){
        this.texture = texture;
    }

    public void setTextureCoords(Vector2f[] textureCoords){
        this.textureCoords = textureCoords;
    }

}
