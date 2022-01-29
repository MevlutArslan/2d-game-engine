package engine.scene;

import components.NonPickable;
import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.GameWindow;
import engine.camera.LevelEditorCameraController;
import engine.input.MouseControl;
import engine.physics.components.Box2dCollider;
import engine.physics.components.CircleCollider;
import engine.physics.components.RigidBody2d;
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

    private Entity editorEntity = new Entity("Editor Entity");
    private SpriteSheet sprites;

    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpriteSheet("src/main/resources/textures/spritesheet.png");
        SpriteSheet gizmos = AssetPool.getSpriteSheet("src/main/resources/textures/gizmos.png");

        editorEntity.setNoSerialize();
        editorEntity.addComponent(new MouseControl());
        editorEntity.addComponent(new Grid2d());
        editorEntity.addComponent(new LevelEditorCameraController());
        editorEntity.addComponent(new GizmoManager(gizmos));
        editorEntity.addComponent(new NonPickable());

        scene.addEntityToScene(editorEntity);
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
    }

    @Override
    public void imgui() {
        ImGui.begin("Assets");

        ImVec2 buttonSize = new ImVec2();
        ImGuiStyle style = ImGui.getStyle();

        int numberOfButtons = sprites.length();
        float windowVisibleX2 = ImGui.getWindowPosX() + ImGui.getWindowContentRegionMaxX();

        for (int i = 0; i < numberOfButtons; i++) {
            Sprite sprite = sprites.getSprite(i);
            ImGui.pushID(i);

            // I need the textureId, sprite's width, height, uv coordinates
            int texId = sprite.getTexId();
            // TODO : Properly set sprite height and width in spritesheet class
            float spriteHeight = sprite.getHeight() * 2;
            float spriteWidth = sprite.getWidth() * 2;
            Vector2f[] texCoords = sprite.getTextureCoords();

            if (ImGui.imageButton(texId, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
                Entity entity = EntityGenerator.generate(sprite, spriteWidth, spriteHeight);
                editorEntity.getComponent(MouseControl.class).pickUpEntity(entity);
            }

            float lastButtonX2 = ImGui.getItemRectMaxX();
            float nextButtonX2 = lastButtonX2 + style.getItemSpacingX() + buttonSize.x;

            if (i + 1 < numberOfButtons && nextButtonX2 < windowVisibleX2) {
                ImGui.sameLine();
            }

            ImGui.popID();
        }

        ImGui.end();

    }
}