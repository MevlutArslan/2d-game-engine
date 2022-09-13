package engine.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Transform;
import engine.Component;
import engine.Entity;
import engine.ToolboxEditor;
import engine.camera.Camera;
import engine.input.MouseListener;
import engine.physics.PhysicsEngine;
import engine.rendering.DebugDraw;
import engine.rendering.Renderer;
import engine.utility.gson_adapter.ComponentGsonAdapter;
import engine.utility.gson_adapter.EntityGsonAdapter;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;


// A lot of things of the Scene system are taken from The Cherno, GamesWithGabe and the 'Game Engine Architecture' book.
public class Scene {

    private Camera camera;
    private ArrayList<Entity> entities;
    private Renderer renderer;
    private PhysicsEngine physicsEngine;
    private boolean isRunning;

    private SceneInitializer sceneInitializer;

    private String path;

    private Gson gson;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.renderer = new Renderer();
        this.entities = new ArrayList<>();
        this.isRunning = false;
        this.physicsEngine = new PhysicsEngine();
        this.gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
                registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
                create();
    }

    public void init() {
        this.camera = new Camera(new Vector2f(0, 0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start() {
        // using the traditional for loop to avoid concurrent modification errors.
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.start();
            this.renderer.add(entity);
            // TODO : make this optional
            this.physicsEngine.add(entity);
        }
        isRunning = true;
    }

    // Give credit for the method
    public void addEntityToScene(Entity entity) {
        if (!isRunning) {
            entities.add(entity);
        } else {
            entities.add(entity);
            // we are calling the start method to make sure the dynamically added entities also start
            entity.start();

            this.renderer.add(entity);
            this.physicsEngine.add(entity);
        }
    }

    public void render() {
        this.renderer.render();
    }

    public void imgui() {
        this.sceneInitializer.imgui();
    }

    public void onUpdateEditor(float deltaTime) {
        camera.adjustProjection();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.onUpdateEditor(deltaTime);

            if (entity.isDead()) {
                entities.remove(i);
                this.renderer.destroyEntity(entity);
                this.physicsEngine.destroyEntity(entity);
                i--;
            }
        }
    }

    public void update(float deltaTime) {
        this.camera.adjustProjection();
        this.physicsEngine.update(deltaTime);

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.update(deltaTime);

            if (entity.isDead()) {
                entities.remove(i);
                this.renderer.destroyEntity(entity);
                this.physicsEngine.destroyEntity(entity);
                i--;
            }
        }
    }

    public void save() {
        try {
            // TODO connect to project setting's asset directory
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

    public void saveAs(String fileName) {
        try {
            // TODO connect to project setting's asset directory
            FileWriter fileWriter = new FileWriter(fileName);
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

    public void load(String sceneSource) {
        Gson gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Component.class, new ComponentGsonAdapter()).
                registerTypeAdapter(Entity.class, new EntityGsonAdapter()).
                create();
        String loadedText = "";

        try {
            loadedText = new String(Files.readAllBytes(Paths.get(sceneSource)));
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

    public Entity getEntityById(long id) {
        Optional<Entity> entity = entities.stream().filter(ent -> ent.getEntityId() == id).findAny();
        return entity.orElse(null);
    }

    public void destroy() {
        for (Entity entity : entities) {
            entity.destroy();
        }
    }

    public List<Entity> getEntities() {
        return this.entities;
    }

    public SceneInitializer getSceneInitializer() {
        return this.sceneInitializer;
    }

    public Entity createEntity(String name) {
        Entity entity = new Entity(name);

        entity.addComponent(new Transform());
        entity.transform = entity.getComponent(Transform.class);

        return entity;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public <T extends Component> Entity getEntityWithComponent(Class<T> cls) {
        for (Entity entity : entities) {
            if (entity.getComponent(cls) != null) {
                return entity;
            }
        }
        return null;
    }

    public PhysicsEngine getPhysicsEngine() {
        return physicsEngine;
    }


}
