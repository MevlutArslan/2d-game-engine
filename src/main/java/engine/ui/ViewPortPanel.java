package engine.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.Component;
import engine.Entity;
import engine.GameWindow;
import engine.input.MouseControl;
import engine.input.MouseListener;
import engine.observers.Event;
import engine.observers.EventSystem;
import engine.observers.EventType;
import engine.rendering.Sprite;
import engine.scene.LevelEditorSceneInitializer;
import engine.scene.Scene;
import engine.ui.editor.Console;
import engine.utility.AssetPool;
import engine.utility.Constants;
import engine.utility.gson_adapter.ComponentGsonAdapter;
import engine.utility.gson_adapter.EntityGsonAdapter;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ViewPortPanel {

    private static float leftX, rightX, topY, bottomY;

    private static boolean isPlaying = false;

    public static void imgui() {
        ImGui.begin("ViewPort", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();

        Sprite sprite = new Sprite();
        sprite.setTexture( AssetPool.getTexture("src/main/resources/icons/play-button.png"));
        int spriteTexId = sprite.getTexId();
        float spriteHeight = sprite.getHeight();
        float spriteWidth = sprite.getWidth();
        Vector2f[] texCoords = sprite.getTextureCoords();

        if (ImGui.imageButton(spriteTexId, 16, 16, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
            if(!isPlaying){
                isPlaying = true;
                EventSystem.notify(null, new Event(EventType.GAME_ENGINE_START_PLAY));
            }
        }

        sprite.setTexture(AssetPool.getTexture("src/main/resources/icons/pause-button.png"));
        spriteTexId = sprite.getTexId();
        spriteHeight = sprite.getHeight();
        spriteWidth = sprite.getWidth();
        texCoords = sprite.getTextureCoords();

        if (ImGui.imageButton(spriteTexId, 16, 16, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
            if(isPlaying){
                isPlaying = false;
                EventSystem.notify(null, new Event(EventType.GAME_ENGINE_STOP_PLAY));
            }
        }

        ImGui.endMenuBar();
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());

        ImVec2 windowSize = getLargestViewportSize();
        ImVec2 windowPos = getWindowCenterPosition(windowSize);
//        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);

        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

        int textureId = GameWindow.getFramebuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        MouseListener.setViewPortPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setViewPortSize(new Vector2f(windowSize.x, windowSize.y));

        if (ImGui.beginDragDropTarget()) {
            int targetFlags = 0;

            String path = ImGui.acceptDragDropPayload("VIEWPORT_DROPABLE");
            if (path != null) {
                if (isScene(path)) {
                    // TODO refactor this to be reusable across the engine
                    Scene scene = new Scene(new LevelEditorSceneInitializer());
                    scene.load(path);
                    GameWindow.setScene(scene);
                    GameWindow.getScene().init();
                    GameWindow.getScene().start();
                } else if(isPrefab(path)){
                    loadEntity(path);
                }
            }
            ImGui.endDragDropTarget();
        }
        ImGui.end();
    }

    private static void loadEntity(String path){
        Gson gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
                registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
                create();

        String loadedText = "";

        try {
            loadedText = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if(loadedText.equals("")){
            Console.getInstance().addMessage("Prefab is empty!");
            return;
        }

        Entity entity = gson.fromJson(loadedText, Entity.class);
        GameWindow.getScene().getEntityWithComponent(MouseControl.class).getComponent(MouseControl.class).pickUpEntity(entity);
    }


    public static boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

    private static ImVec2 getWindowCenterPosition(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewPortY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewPortY + ImGui.getCursorPosY());
    }

    private static ImVec2 getLargestViewportSize() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / (16.0f / 9.0f);

        if (aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * (16.0f / 9.0f);
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private static boolean isScene(String path) {
        return path.endsWith("." + Constants.SCENE_FILE_EXTENSION);
    }

    private static boolean isPrefab(String path) {
        return path.endsWith("." + Constants.PREFAB_FILE_EXTENSION);
    }

}
