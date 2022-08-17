package engine.utility;

import components.Ground;
import components.physics.BoxCollider;
import components.physics.RigidBody;
import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.GameWindow;
import engine.rendering.DebugDraw;
import engine.rendering.Sprite;
import engine.rendering.SpriteSheet;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class EntityGenerator {
//    We can generate a sprite object and a floor block object
    private static final int numberOfTemplatesAvailable = 3;
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
        Entity entity = generateSpriteObject(spriteSheetIdle.getSprite(0),0.35f,0.45f);


        // Built a custom collider because the box collider gets stuck on some of the blocks

        return entity;
    }

    public static Entity generateBuildingBlocks(Sprite sprite){
        // we first need an entity with our sprite assigned.
        Entity entity = generateSpriteObject(sprite,0.25f,0.25f);

        // we add a rigidbody and a collider to allow our player to stand on our blocks.
        RigidBody rigidBody = new RigidBody();
        rigidBody.setBodyType(BodyType.DYNAMIC);

        BoxCollider circleCollider = new BoxCollider();
        circleCollider.setHalfSize(new Vector2f(0.25f, 0.25f));

        entity.addComponent(rigidBody);
        entity.addComponent(circleCollider);
        entity.addComponent(new Ground());

        return entity;
    }

    // not working
    public static Entity generateEmptyBlock(){
        SpriteSheet spriteSheet = AssetPool.getSpriteSheet("src/main/resources/spritesheets/Tiles.png");
        Entity entity = generateSpriteObject(spriteSheet.getSprite(0), 0.25f, 0.25f);


        entity.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,1,0,1f));
        return entity;
    }

    public static Entity generateEmptyEntity(){
        Entity entity = generateSpriteObject(null, 0.25f, 0.25f);
        DebugDraw.drawSquare(entity.transform.position, entity.transform.scale, 0);

        return entity;
    }
}
