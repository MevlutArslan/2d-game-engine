package engine.scene;

import components.Entity;
import engine.camera.Camera;

import java.util.ArrayList;

public abstract class Scene {

    protected Camera camera;

    protected ArrayList<Entity> entities = new ArrayList<>();

    private boolean isRunning;

    public Scene(){

    }

    public void init(){

    }

    public void start(){
        for(Entity entity : entities){
            entity.start();
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
        }
    }

    public abstract void update(float deltaTime);


}
