package engine.rendering;

import org.joml.Vector2f;

public class Sprite {

    private Texture texture = null;
    private Vector2f[] textureCoords = {
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    private float width;
    private float height;

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

    public float getHeight(){
        return this.height;
    }

    public float getWidth(){
        return this.width;
    }

    public int getTexId(){
        return this.texture.getTextureId();
    }

    public void setWidth(int spriteWidth) {
        this.width = spriteWidth;
    }

    public void setHeight(int spriteHeight){
        this.height = spriteHeight;
    }
}
