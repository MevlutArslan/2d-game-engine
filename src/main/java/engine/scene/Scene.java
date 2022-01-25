package engine.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.Component;
import engine.Entity;
import engine.camera.Camera;
import engine.physics.Physics2d;
import engine.rendering.Renderer;
import engine.utility.gson_adapter.ComponentGsonAdapter;
import engine.utility.gson_adapter.EntityGsonAdapter;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// A lot of things of the Scene system are taken from The Cherno, GamesWithGabe and the 'Game Engine Architecture' book.
public class Scene {
    private String sceneName;
    private Camera camera;
    private ArrayList<Entity> entities;
    private Renderer renderer;
    private boolean isRunning;
    private Physics2d physics2d;

    private Entity selectedEntity = null;

    private SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneName = "defaultScene";
        this.sceneInitializer = sceneInitializer;
        this.renderer = new Renderer();
        this.entities = new ArrayList<>();
        this.isRunning = false;
        this.physics2d = new Physics2d();
    }

    public void init() {
        this.camera = new Camera(new Vector2f(-250, 0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start() {
        // using this for loop to avoid concurrent modification errors.
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.start();
            this.renderer.add(entity);
            this.physics2d.add(entity);
        }
        isRunning = true;
    }

    // Give credit for the method
    public void addEntityToScene(Entity entity) {
        if (!isRunning) {
            entities.add(entity);
        } else {
            entities.add(entity);
            entity.start();
            this.renderer.add(entity);
            this.physics2d.add(entity);
        }
    }

    public void render() {
        this.renderer.render();
    }

    public Camera getCamera() {
        return this.camera;
    }


    public void imgui() {
        this.sceneInitializer.imgui();
    }

    public void onUpdateRuntime(float deltaTime) {

    }

    public void onUpdateEditor(float deltaTime) {
        camera.adjustProjection();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.onUpdateEditor(deltaTime);

            if(entity.isDead()){
                entities.remove(i);
                // TODO implement destroy entity methods for renderer and physics2d
                this.renderer.destroyEntity(entity);
                this.physics2d.destroyEntity(entity);
                i--;
            }
        }
    }

    public void update(float deltaTime){
        camera.adjustProjection();
        this.physics2d.update(deltaTime);

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.update(deltaTime);
//            physics2d.onUpdateEditor(deltaTime);

            if(entity.isDead()){
                entities.remove(i);
                // TODO implement destroy entity methods for renderer and physics2d
                this.renderer.destroyEntity(entity);
                this.physics2d.destroyEntity(entity);
                i--;
            }
        }
    }

    public void save() {
        Gson gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
                registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
                create();
        try {
            FileWriter fileWriter = new FileWriter("level.json");
            List<Entity> toSerialize = new ArrayList<>();
            for (Entity entity : entities) {
                if (entity.getShouldSerialize()) {
                    toSerialize.add(entity);
                }
            }
            fileWriter.write(gson.toJson(toSerialize));
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
                registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
                create();
        String loadedText = "";

        try {
            loadedText = new String(Files.readAllBytes(Paths.get("level.json")));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if (!loadedText.equals("")) {
            long maxEntityCount = -1;
            long maxComponentCount = -1;

            Entity[] entities = gson.fromJson(loadedText, Entity[].class);
            // IF FACING ANY PROBLEMS RELATED TO LOADING ENTITIES LOOK HERE
            // Switching from a for-each loop to a regular for loop might be better
            // need to read more about the difference
            for (Entity entity : entities) {
                addEntityToScene(entity);

                for (Component component : entity.getAllComponents()) {
                    if (component.getComponentId() > maxComponentCount) {
                        maxComponentCount = component.getComponentId();
                    }
                }

                if (entity.getEntityId() > maxEntityCount) {
                    maxEntityCount = entity.getEntityId();
                }
            }

            // https://www.youtube.com/watch?v=bRha19j-gB8&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=24
            maxEntityCount++;
            maxComponentCount++;

            Entity.init(maxEntityCount);
            Component.init(maxComponentCount);
        }

    }

    public void selectEntity(Entity entity) {
        this.selectedEntity = entity;
    }

    public Entity getSelectedEntity() {
        return this.selectedEntity;
    }

    public Entity getEntityById(long id) {
        Optional<Entity> entity = entities.stream().filter(ent -> ent.getEntityId() == id).findAny();
        return entity.orElse(null);
    }

    public void destroy(){
        for( Entity entity : entities){
            entity.destroy();
        }
    }

    public List<Entity> getEntities(){
        return this.entities;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
}
