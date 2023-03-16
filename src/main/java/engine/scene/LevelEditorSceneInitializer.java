package engine.scene;

import components.NonPickable;
import components.rendering.SpriteRenderer;
import engine.Entity;
import engine.camera.LevelEditorCameraController;
import engine.input.EngineKeyShortcuts;
import engine.input.MouseControl;
import engine.rendering.SpriteSheet;
import engine.ui.Grid2d;
import engine.ui.gizmos.GizmoManager;
import engine.utility.AssetPool;
import imgui.ImGui;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private Entity editorEntity;

    @Override
    public void init(Scene scene) {
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
        AssetPool.addSpriteSheet("src/main/resources/spritesheets/gizmos.png", new SpriteSheet(
                AssetPool.getTexture("src/main/resources/spritesheets/gizmos.png"),
                24, 48, 3, 0)
        );

        AssetPool.getShader(new String[]{
                        "src/main/resources/basicShader.vertex",
                        "src/main/resources/basicShader.fragment"
                }
        );

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

    }

    @Override
    public void imgui() {
        //  TODO implement basic selection of entities (like in Unreal where you select cube and sphere whatnot)
        ImGui.begin("Default prefabs");

//        ImVec2 buttonSize = new ImVec2(16, 16);
//        ImGuiStyle style = ImGui.getStyle();
//
//        int numberOfButtons = EntityGenerator.getNumberOfTemplatesAvailable();
//        float windowVisibleX2 = ImGui.getWindowPosX() + ImGui.getWindowContentRegionMaxX();
//
//        int id = 0;
//        Texture texture = AssetPool.getTexture("src/main/resources/icons/wooden-crate.png");
//
//        ImGui.pushID(id++);
//        int texId = texture.getTextureId();
//
//        if (ImGui.imageButton(texId, buttonSize.x, buttonSize.y)) {
//            Entity entity = EntityGenerator.generateEmptyEntity(0.25f, 0.25f);
//            editorEntity.getComponent(MouseControl.class).pickUpEntity(entity);
//        }
//
//        ImGui.popID();
//        ImGui.sameLine();

        ImGui.end();
    }
}
