package engine.rendering;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {

    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numberOfSprites, int spacing){
        this.sprites = new ArrayList<>();
        this.texture = texture;

        // starting at top left. So top left is our 1st sprite
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;
        for(int i = 0; i < numberOfSprites; i++){
            // normalized sprite coordinates
            // NOTE : ADD FORMULA
            float topY = (currentY + spriteHeight) / (float)texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float)texture.getWidth();
            float leftX = currentX / (float)texture.getWidth();
            float bottomY = currentY / (float)texture.getHeight();

            Vector2f[] texCoords = new Vector2f[] {
                    new Vector2f(rightX,topY),
                    new Vector2f(rightX,bottomY),
                    new Vector2f(leftX,bottomY),
                    new Vector2f(leftX,topY)
            };

            Sprite sprite = new Sprite();
            sprite.setTexture(this.texture);
            sprite.setTextureCoords(texCoords);
            sprite.setHeight(spriteHeight);
            sprite.setWidth(spriteWidth);
            this.sprites.add(sprite);
            currentX+= spriteWidth + spacing;
            if(currentX >= texture.getWidth()){
                currentX = 0;
                currentY-= (spriteHeight + spacing);
            }
        }
    }

    public Sprite getSprite(int index){
        return this.sprites.get(index);
    }

    public int length(){
        return this.sprites.size();
    }
}
