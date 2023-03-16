package engine.ui.panels;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.rendering.SpriteRenderer;
import engine.Component;
import engine.Entity;
import engine.ToolboxEditor;
import engine.rendering.PickingTexture;
import engine.scene.Scene;
import engine.ui.editor.Console;
import engine.ui.editor.CustomImGuiController;
import engine.utility.Constants;
import engine.utility.gson_adapter.ComponentGsonAdapter;
import engine.utility.gson_adapter.EntityGsonAdapter;
import imgui.ImGui;
import imgui.type.ImString;
import org.joml.Vector4f;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PropertiesPanel {
    private Entity selectedEntity = null;
    private List<Entity> selectedEntities;
    private List<Vector4f> selectedEntititiesOriginalColor;
    private PickingTexture pickingTexture;

    private boolean shouldAskForName = false;
    private ImString prefabName = new ImString();

    private Console console;

    public PropertiesPanel(PickingTexture pickingTexture) {
        this.selectedEntities = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.selectedEntititiesOriginalColor = new ArrayList<>();

        this.console = Console.getInstance();
    }

    public void update(float deltaTime, Scene currentScene) {

    }

    public void imgui() {
        // TODO allow for collective editing of selected entities
        ImGui.begin("Properties");
        if (selectedEntities.size() == 1 && selectedEntities.get(0) != null) {
            selectedEntity = selectedEntities.get(0);

            if (selectedEntity != null) {
                selectedEntity.imgui();

                if (ImGui.button("Create Prefab from Entity")) {
                    // Create and save prefab to location
                    shouldAskForName = true;
                }

                if (shouldAskForName) {
                    CustomImGuiController.drawInputText("Prefab name", prefabName);

                    if (ImGui.button("Save")) {
                        try {
                            saveEntityAsPrefab(selectedEntity, prefabName.get());
                        } catch (Exception exception) {
                            console.addMessage(exception.getMessage());
                        } finally {
                            shouldAskForName = false;
                        }
                    }
                }
            }
        }
        ImGui.end();
    }

    public void saveEntityAsPrefab(Entity selectedEntity, String prefabName) throws Exception {
        File currentDirectory = ToolboxEditor.getImGuiApp().getCurrentDirectory();
        String fileName = prefabName+ "." + Constants.PREFAB_FILE_EXTENSION;

        for(File file : Objects.requireNonNull(currentDirectory.listFiles())){
            if(file.getName().equals(fileName)){
                throw new FileAlreadyExistsException("A Prefab with name : " + prefabName + " exists already!");
            }
        }

        File prefabFile = new File(currentDirectory, fileName);
        if(prefabFile.createNewFile()){
            try{
                FileWriter writer = new FileWriter(prefabFile);
                Gson gson = new GsonBuilder().
                        setPrettyPrinting().
                        registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
                        registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
                        create();

                if(!selectedEntity.getShouldSerialize()){
                    throw new Exception("Entity is marked as should not Serialize!");
                }

                writer.write(gson.toJson(selectedEntity));
                writer.close();
            }catch (Exception e){
                console.addMessage(e.getMessage());
            }
        }
    }

    public Entity getSelectedEntity() {
        return selectedEntities.size() == 1 ? this.selectedEntities.get(0) : null;
    }

    public List<Entity> getSelectedEntities() {
        return this.selectedEntities;
    }

    public void clearSelected() {
        if (selectedEntititiesOriginalColor.size() > 0) {
            int i = 0;
            for (Entity entity : selectedEntities) {
                SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
                if (spriteRenderer != null) {
                    spriteRenderer.setColor(selectedEntititiesOriginalColor.get(i));
                }
                i++;
            }
        }
        this.selectedEntities.clear();
        this.selectedEntititiesOriginalColor.clear();
    }

    public void addSelectedEntityToEntities(Entity entity) {
        SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            this.selectedEntititiesOriginalColor.add(new Vector4f(spriteRenderer.getColor()));
            spriteRenderer.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.selectedEntititiesOriginalColor.add(new Vector4f());
        }
        this.selectedEntities.add(entity);
    }

    public void setSelectedEntity(Entity selectedEntity) {
        if (selectedEntity != null) {
            clearSelected();
            this.selectedEntities.add(selectedEntity);
        }
        this.selectedEntity = selectedEntity;
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}
