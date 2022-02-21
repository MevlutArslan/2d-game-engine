package engine.utility;

import components.Transform;
import components.VariableConnectionTestClass;
import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.GameWindow;
import engine.rendering.Sprite;
import org.joml.Vector2f;

public class EntityGenerator {
    public static Entity generateSpriteObject(Sprite sprite, float width, float height){
        Entity entity = GameWindow.getScene().createEntity("Generated_Entity");
        entity.transform.scale.x = width;
        entity.transform.scale.y = height;
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setSprite(sprite);
        entity.addComponent(spriteRenderer);

        return entity;
    }

    public static Entity generate(Sprite sprite, float width, float height, int zIndex){
        Entity entity = GameWindow.getScene().createEntity("Generated_Entity");
        entity.transform.scale.x = width;
        entity.transform.scale.y = height;
        entity.transform.zIndex = zIndex;
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setSprite(sprite);

        entity.addComponent(spriteRenderer);

        return entity;
    }
}
