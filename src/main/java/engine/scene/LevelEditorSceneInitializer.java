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
        SpriteSheet gizmos = AssetPool.getSpriteSheet("src/main/resources/textures/gizmos.png");
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

        AssetPool.addSpriteSheet("src/main/resources/textures/gizmos.png", new SpriteSheet(
                AssetPool.getTexture("src/main/resources/textures/gizmos.png"),
                24, 48, 3, 0)
        );

        // load audio files
        AssetPool.addSound("src/main/resources/audio/mixkit-player-jumping-in-a-video-game-2043.ogg", false);
    }

    @Override
    public void imgui() {
        ImGui.begin("Templates");

        ImVec2 buttonSize = new ImVec2();
        ImGuiStyle style = ImGui.getStyle();

        int numberOfButtons = EntityGenerator.getNumberOfTemplatesAvailable();
        float windowVisibleX2 = ImGui.getWindowPosX() + ImGui.getWindowContentRegionMaxX();

//        for (int i = 0; i < numberOfButtons; i++) {
        int id = 0;
        Sprite sprite = sprites.getSprite(2);
        ImGui.pushID(id++);
        // I need the textureId, sprite's width, height, uv coordinates
        int texId = sprite.getTexId();
        float spriteHeight = sprite.getHeight();
        float spriteWidth = sprite.getWidth();
        Vector2f[] texCoords = sprite.getTextureCoords();

        if (ImGui.imageButton(texId, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
            Entity entity = EntityGenerator.generateBuildingBlocks(sprite);
            editorEntity.getComponent(MouseControl.class).pickUpEntity(entity);
        }

        ImGui.popID();
        ImGui.sameLine();

        ImGui.pushID(id++);

        sprite = playerIdleSprites.getSprite(0);
        texId = sprite.getTexId();
        spriteHeight = sprite.getHeight();
        spriteWidth = sprite.getWidth();
        texCoords = sprite.getTextureCoords();

        if (ImGui.imageButton(texId, spriteWidth, 32, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
            Entity entity = EntityGenerator.generatePlayerEntity();
            editorEntity.getComponent(MouseControl.class).pickUpEntity(entity);
        }

        ImGui.popID();
        ImGui.sameLine();
//            float lastButtonX2 = ImGui.getItemRectMaxX();
//            float nextButtonX2 = lastButtonX2 + style.getItemSpacingX() + buttonSize.x;
//
//            if (i + 1 < numberOfButtons && nextButtonX2 < windowVisibleX2) {

//            }


//        }

        ImGui.end();
    }


}
