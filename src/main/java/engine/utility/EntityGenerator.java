package engine.utility;

import components.Transform;
import components.VariableConnectionTestClass;
import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.rendering.Sprite;
import org.joml.Vector2f;

public class EntityGenerator {
    public static Entity generate(Sprite sprite, float width, float height){
        Entity entity = new Entity("",
                new Transform(new Vector2f(), new Vector2f(width, height)), 0);
        entity.addComponent(new VariableConnectionTestClass());
        entity.addComponent(new SpriteRenderer());
        entity.getComponent(SpriteRenderer.class).setSprite(sprite);

        return entity;
    }

    public static Entity generate(Sprite sprite, float width, float height, int zIndex){
        Entity entity = new Entity("",
                new Transform(new Vector2f(), new Vector2f(width, height)), zIndex);
        entity.addComponent(new VariableConnectionTestClass());
        entity.addComponent(new SpriteRenderer());
        entity.getComponent(SpriteRenderer.class).setSprite(sprite);

        return entity;
    }
}
