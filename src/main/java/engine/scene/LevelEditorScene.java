package engine.scene;

import components.Entity;
import components.Transform;
import components.rendering.SpriteRenderer;
import engine.camera.Camera;
import engine.rendering.Sprite;
import engine.rendering.SpriteSheet;
import engine.utility.AssetPool;
import org.joml.Vector2f;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-100,0));

        loadResources();

        SpriteSheet sprites = AssetPool.getSpriteSheet("src/main/resources/textures/spritesheet.png");

        Entity entity_1 = new Entity("Object_1",
                new Transform(new Vector2f(100,100),
                              new Vector2f(256,256)));
        entity_1.addComponent(
                new SpriteRenderer(
                       sprites.getSprite(0)));
        this.addEntityToScene(entity_1);

        Entity entity_2 = new Entity("Object_2",
                new Transform(new Vector2f(400,100),
                        new Vector2f(256,256)));
        entity_2.addComponent(
                new SpriteRenderer(
                        sprites.getSprite(1)));
        this.addEntityToScene(entity_2);
        loadResources();
    }

    public void loadResources(){
        AssetPool.getShader(new String[]{
                "src/main/resources/basicShader.vertex",
                "src/main/resources/basicShader.fragment"
                }
        );
        AssetPool.addSpriteSheet("src/main/resources/textures/spritesheet.png",
                                 new SpriteSheet(AssetPool.getTexture("src/main/resources/textures/spritesheet.png"),
                                         16, 16, 26, 0));
    }

    @Override
    public void update(float deltaTime) {

        for(Entity entity : this.entities){
            entity.update(deltaTime);
        }

        this.renderer.render();
    }
}
