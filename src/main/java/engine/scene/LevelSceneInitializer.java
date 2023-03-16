package engine.scene;

import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.camera.Camera;
import engine.camera.GameCamera;
import engine.rendering.SpriteSheet;
import engine.utility.AssetPool;

public class LevelSceneInitializer extends SceneInitializer {

    @Override
    public void init(Scene scene) {
        Entity cameraEntity = scene.createEntity("camera");
        cameraEntity.addComponent(new GameCamera(scene.getCamera()));
        cameraEntity.start();
        scene.addEntityToScene(cameraEntity);
    }

    @Override
    public void loadResources(Scene scene) {
        for (Entity entity : scene.getEntities()) {
            if (entity.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
                if (spriteRenderer.getTexture() != null) {
                    spriteRenderer.setTexture(
                            AssetPool.getTexture(spriteRenderer.getTexture().getFilepath())
                    );
                }
            }
        }

        // load audio files
        AssetPool.addSound("src/main/resources/audio/mixkit-player-jumping-in-a-video-game-2043.ogg", false);
    }

    @Override
    public void imgui() {

    }
}
