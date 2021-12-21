package engine.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.Component;
import engine.Entity;
import engine.camera.Camera;
import engine.rendering.Renderer;
import engine.utility.gson_adapter.ComponentGsonAdapter;
import engine.utility.gson_adapter.EntityGsonAdapter;
import imgui.ImGui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

public abstract class Scene {

    protected Camera camera;

    protected ArrayList<Entity> entities = new ArrayList<>();

    private boolean isRunning;

    protected Renderer renderer = new Renderer();

    protected Entity selectedEntity = null;

    protected boolean levelIsLoaded = false;

    private Gson gson;

    public Scene(){
        gson = new GsonBuilder().
                registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
                registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
                setPrettyPrinting().create();
    }

    public void init(){

    }

    public void start(){
        for(Entity entity : entities){
            entity.start();
            this.renderer.add(entity);
        }
        isRunning = true;
    }

    // Give credit for the method
    public void addEntityToScene(Entity entity){
        if(!isRunning) {
            entities.add(entity);
        }else{
            entities.add(entity);
            entity.start();
            this.renderer.add(entity);
        }
    }

    public abstract void update(float deltaTime);

    public abstract void render();

    public Camera getCamera(){
        return this.camera;
    }

    public void imguiScene(){
        if(selectedEntity != null){
            ImGui.begin("Inspect");
            selectedEntity.imgui();
            ImGui.end();
        }

        imgui();
    }

    public void imgui(){

    }

    public void save(){

        try{
            FileWriter fileWriter = new FileWriter("level.json");
            fileWriter.write(gson.toJson(this.entities));
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void load(){
        String loadedText = "";
        try{
            loadedText = new String(Files.readAllBytes(Paths.get("level.json")));
        }catch (IOException exception){
            exception.printStackTrace();
        }

        if(!loadedText.equals("")){
            long maxEntityCount = -1;
            long maxComponentCount = -1;

            Entity[] entities = gson.fromJson(loadedText, Entity[].class);
            // IF FACING ANY PROBLEMS RELATED TO LOADING ENTITIES LOOK HERE
            // Switching from a for-each loop to a regular for loop might be better
            // need to read more about the difference
            for(Entity entity : entities){
                addEntityToScene(entity);

                for(Component component : entity.getAllComponents()){
                    if(component.getComponentId() > maxComponentCount){
                        maxComponentCount = component.getComponentId();
                    }
                }

                if(entity.getEntityId() > maxEntityCount){
                    maxEntityCount = entity.getEntityId();
                }
            }

            // https://www.youtube.com/watch?v=bRha19j-gB8&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=24
            maxEntityCount++;
            maxComponentCount++;

            Entity.init(maxEntityCount);
            Component.init(maxComponentCount);
            this.levelIsLoaded = true;
        }

    }

    public void selectEntity(Entity entity){
        this.selectedEntity = entity;
    }

    public Entity getEntityById(long id){
        Optional<Entity> entity = entities.stream().filter(ent -> ent.getEntityId() == id).findAny();
        return entity.orElse(null);
    }
}
