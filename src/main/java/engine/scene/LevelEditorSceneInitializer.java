package engine.scene;

import components.NonPickable;
import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.camera.LevelEditorCameraController;
import engine.input.EngineKeyShortcuts;
import engine.input.MouseControl;
import engine.input.MouseListener;
import engine.rendering.Sprite;
import engine.rendering.SpriteSheet;
import engine.ui.Grid2d;
import engine.ui.gizmos.GizmoManager;
import engine.utility.AssetPool;
import engine.utility.EntityGenerator;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import org.joml.Vector2f;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private Entity editorEntity;
    private SpriteSheet sprites;
    private SpriteSheet playerIdleSprites;

    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpriteSheet("src/main/resources/spritesheets/Tiles.png");
        playerIdleSprites = AssetPool.getSpriteSheet("src/main/resources/spritesheets/Biker_idle.png");
        SpriteSheet gizmos = AssetPool.getSpriteSheet("src/main/resources/spritesheets/gizmos.png");
        editorEntity = scene.createEntity("Level editor stuff");

        editorEntity.setNoSerialize();
        editorEntity.addComponent(new MouseControl());
        editorEntity.addComponent(new Grid2d());
        editorEntity.addComponent(new LevelEditorCameraController());
        editorEntity.addComponent(new GizmoManager(gizmos));
        editorEntity.addComponent(new NonPickable());
        editorEntity.addComponent(new EngineKeyShortcuts());

        scene.addEntityToScene(editorEntity);
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader(new String[]{
                        "src/main/resources/basicShader.vertex",
                        "src/main/resources/basicShader.fragment"
                }
        );

        AssetPool.getTexture("src/main/resources/icons/play-button.png");
        AssetPool.getTexture("src/main/resources/icons/pause-button.png");
        AssetPool.getTexture("src/main/resources/icons/cube.png");

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
        //  TODO implement basic selection of entities (like in Unreal where you select cube and sphere whatnot)
        ImGui.begin("Default prefabs");

        ImGui.end();
    }


}
