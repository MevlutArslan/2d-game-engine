package engine.utility;

import components.Transform;
import components.VariableConnectionTestClass;
import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.GameWindow;
import engine.physics.components.Box2dCollider;
import engine.physics.components.RigidBody2d;
import engine.rendering.Sprite;
import engine.rendering.SpriteSheet;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class EntityGenerator {
//    We can generate a sprite object and a floor block object
    private static final int numberOfTemplatesAvailable = 2;
//    private static Class[] generatableEntities = new Class[numberOfGeneratableEntites];

    // TODO keep track of all possible Templates to avoid repetitive code in Template tab
//    public static Entity[] getGeneratableEntityList(){
//        generatableEntities[0] = SpriteRenderer.class;
//        generatableEntities[1] =
//        return EntityGenerator.generatableEntities;
//    }

    public static int getNumberOfTemplatesAvailable(){
        return numberOfTemplatesAvailable;
    }

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

    public static Entity generatePlayerEntity(){
        SpriteSheet spriteSheetIdle = AssetPool.getSpriteSheet("src/main/resources/spritesheets/Biker_idle.png");
        Entity entity = generateSpriteObject(spriteSheetIdle.getSprite(0),0.5f,0.7f);

        RigidBody2d rigidBody = new RigidBody2d();
        rigidBody.setBodyType(BodyType.DYNAMIC);
        entity.addComponent(rigidBody);

        Box2dCollider collider = new Box2dCollider();
        collider.setHalfSize(entity.transform.scale);
        collider.setOffset(new Vector2f(0f, -0.100f));
        collider.setHalfSize(new Vector2f(0.4f, 0.5f));
        entity.addComponent(collider);

        return entity;
    }

    public static Entity generateFloorEntity(){
        SpriteSheet spriteSheet = AssetPool.getSpriteSheet("src/main/resources/spritesheets/Tiles.png");
        Entity entity = generateSpriteObject(spriteSheet.getSprite(2),0.25f,0.25f);

        RigidBody2d rigidBody = new RigidBody2d();
        rigidBody.setBodyType(BodyType.STATIC);
        entity.addComponent(rigidBody);

        Box2dCollider collider = new Box2dCollider();
        collider.setHalfSize(entity.transform.scale);
        entity.addComponent(collider);

        return entity;
    }

    // not working
    public static Entity generateEmptyBlock(){
        SpriteSheet spriteSheet = AssetPool.getSpriteSheet("src/main/resources/spritesheets/Tiles.png");
        Entity entity = generateSpriteObject(spriteSheet.getSprite(0), 0.25f, 0.25f);

        RigidBody2d rigidBody = new RigidBody2d();
        rigidBody.setBodyType(BodyType.STATIC);
        entity.addComponent(rigidBody);

        Box2dCollider collider = new Box2dCollider();
        collider.setHalfSize(entity.transform.scale);
        entity.addComponent(collider);

        entity.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,1,0,1f));
        return entity;
    }
}
