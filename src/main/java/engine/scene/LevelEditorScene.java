package engine.scene;

import components.Entity;
import components.Transform;
import components.rendering.SpriteRenderer;
import engine.camera.Camera;
import engine.utility.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
        this.camera = new Camera(new Vector2f(-100,0));
    }

    @Override
    public void init() {
        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;
        float padding = 3;

        for (int x=0; x < 100; x++) {
            for (int y=0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX) + (padding * x);
                float yPos = yOffset + (y * sizeY) + (padding * y);

                Entity entity = new Entity("Obj" + x + "" + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                entity.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                this.addEntityToScene(entity);
            }
        }

        loadResources();
    }

    public void loadResources(){
        AssetPool.getShader(new String[]{
                "src/main/resources/basicShader.vertex",
                "src/main/resources/basicShader.fragment"
                }
        );
    }

    @Override
    public void update(float deltaTime) {

        for(Entity entity : this.entities){
            entity.update(deltaTime);
        }

        this.renderer.render();
    }
}
