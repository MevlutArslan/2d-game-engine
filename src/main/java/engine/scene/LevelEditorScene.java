package engine.scene;

import components.VariableConnectionTestClass;
import engine.Entity;
import components.Transform;
import components.rendering.SpriteRenderer;
import engine.camera.Camera;
import engine.camera.LevelEditorCameraController;
import engine.input.KeyListener;
import engine.input.MouseControl;
import engine.input.MouseListener;
import engine.rendering.DebugDraw;
import engine.rendering.Sprite;
import engine.rendering.SpriteSheet;
import engine.ui.Grid2d;
import engine.ui.ViewPortWindow;
import engine.utility.AssetPool;
import engine.utility.EntityGenerator;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    private final Entity editorEntity = new Entity("Editor Entity");
    private SpriteSheet sprites;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-250, 0));

        editorEntity.addComponent(new MouseControl());
        editorEntity.addComponent(new Grid2d());
        editorEntity.addComponent(new LevelEditorCameraController());

        loadResources();

        sprites = AssetPool.getSpriteSheet("src/main/resources/textures/spritesheet.png");
        if (levelIsLoaded) {
            if (entities.size() > 0) {
                selectedEntity = entities.get(0);
            }
            return;
        }
    }

    public void loadResources() {
        AssetPool.getShader(new String[]{
                        "src/main/resources/basicShader.vertex",
                        "src/main/resources/basicShader.fragment"
                }
        );
        AssetPool.addSpriteSheet("src/main/resources/textures/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("src/main/resources/textures/spritesheet.png"),
                        16, 16, 26, 0));

        for (Entity entity : entities) {
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

    int angle = 0;
    Vector2f pos = new Vector2f(200, 200);

    @Override
    public void update(float deltaTime) {
        camera.adjustProjection();
        editorEntity.update(deltaTime);

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            pos.y++;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            pos.y--;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            angle++;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            angle--;
        }

        DebugDraw.drawSquare(pos, new

                Vector2f(64, 32), angle, new

                Vector3f(0, 0, 1), 1);
        DebugDraw.drawCircle(pos, 5f, new

                Vector3f(0, 0, 1), 1);


        for (
                Entity entity : this.entities) {
            entity.update(deltaTime);
        }

    }

    @Override
    public void render() {
        this.renderer.render();
    }


    //https://github.com/ocornut/imgui/issues/1977
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
