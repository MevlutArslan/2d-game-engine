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
        AssetPool.getShader(new String[]{
                        "src/main/resources/basicShader.vertex",
                        "src/main/resources/basicShader.fragment"
                }
        );
        AssetPool.addSpriteSheet("src/main/resources/textures/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("src/main/resources/textures/spritesheet.png"),
                        16, 16, 26, 0));
        AssetPool.addSpriteSheet("src/main/resources/spritesheets/Tiles.png", new SpriteSheet(
                AssetPool.getTexture("src/main/resources/spritesheets/Tiles.png"), 32, 32, 81, 0
        ));
        AssetPool.addSpriteSheet("src/main/resources/spritesheets/Biker_idle.png", new SpriteSheet(
                AssetPool.getTexture("src/main/resources/spritesheets/Biker_idle.png"), 24, 48, 4, 0
        ));

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

        AssetPool.addSpriteSheet("src/main/resources/spritesheets/gizmos.png", new SpriteSheet(
                AssetPool.getTexture("src/main/resources/spritesheets/gizmos.png"),
                24, 48, 3, 0)
        );

        // load audio files
        AssetPool.addSound("src/main/resources/audio/mixkit-player-jumping-in-a-video-game-2043.ogg", false);
    }

    @Override
    public void imgui() {

    }
}
