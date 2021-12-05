package engine.scene;

import components.VariableConnectionTestClass;
import engine.Entity;
import components.Transform;
import components.rendering.SpriteRenderer;
import engine.camera.Camera;
import engine.rendering.SpriteSheet;
import engine.utility.AssetPool;
import org.joml.Vector2f;

public class LevelEditorScene extends Scene {

    private Entity entity_1;
    private SpriteSheet sprites;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-100,0));
        loadResources();

        if(levelIsLoaded){
            selectedEntity = entities.get(0);
            return;
        }

        sprites = AssetPool.getSpriteSheet("src/main/resources/textures/spritesheet.png");
        entity_1 = new Entity("Object_1",
                new Transform(new Vector2f(100,100),
                              new Vector2f(256,256)), 3);
        entity_1.addComponent(new SpriteRenderer());
        entity_1.addComponent(new VariableConnectionTestClass());
        entity_1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(0));
        this.addEntityToScene(entity_1);
        this.selectedEntity = entity_1;

        Entity entity_2 = new Entity("Object_2",
                new Transform(new Vector2f(400,100),
                        new Vector2f(256,256)), -1);
        entity_2.addComponent(
                new SpriteRenderer());
        entity_2.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(1));

        this.addEntityToScene(entity_2);
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
