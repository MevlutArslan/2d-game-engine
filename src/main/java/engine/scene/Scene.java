package engine.scene;

import engine.Entity;
import engine.camera.Camera;
import engine.rendering.Renderer;
import imgui.ImGui;

import java.util.ArrayList;

public abstract class Scene {

    protected Camera camera;

    protected ArrayList<Entity> entities = new ArrayList<>();

    private boolean isRunning;

    protected Renderer renderer = new Renderer();

    protected Entity selectedEntity = null;

    public Scene(){

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
            System.out.println("here");
        }
    }

    public abstract void update(float deltaTime);

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
}
